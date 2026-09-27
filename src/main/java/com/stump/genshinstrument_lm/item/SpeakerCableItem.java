package com.stump.genshinstrument_lm.item;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.block.LooperBlock;
import com.stump.genshinstrument_lm.block.SpeakerBlock;
import com.stump.genshinstrument_lm.block.blockentity.LooperBlockEntity;
import com.stump.genshinstrument_lm.block.blockentity.SpeakerBlockEntity;
import com.stump.genshinstrument_lm.block.partial.AbstractInstrumentBlock;
import com.stump.genshinstrument_lm.block.partial.InstrumentBlockEntity;
import com.stump.genshinstrument_lm.util.CommonUtil;
import com.stump.genshinstrument_lm.util.SpeakerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.List;

/**
 * pairs sources (block instruments and loopers) to speakers. right-click a source and a speaker
 * (in either order) to pair them. a speaker accepts at most one looper.
 * shift + right-click a source to unpair all of its speakers,
 * or only the speaker selected with this cable beforehand.
 * held instruments don't need a cable: they pair by right-clicking a speaker directly (see SpeakerBlock).
 */
public class SpeakerCableItem extends Item {
    private static final String CABLE_TAG = "speakerCable",
        SOURCE_POS_TAG = "source",
        SPEAKER_POS_TAG = "speaker";

    public SpeakerCableItem(Properties pProperties) {
        super(pProperties);
    }


    /**
     * Runs before the clicked block's own right-click action (the looper toggling playback,
     * an instrument block opening its screen), so the cable works with or without sneaking.
     */
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext pContext) {
        final Level level = pContext.getLevel();
        final BlockPos clickedPos = pContext.getClickedPos();
        final Block block = level.getBlockState(clickedPos).getBlock();

        final Player player = pContext.getPlayer();
        if (player == null)
            return InteractionResult.PASS;

        final boolean isCableTarget = block instanceof AbstractInstrumentBlock
            || block instanceof LooperBlock
            || block instanceof SpeakerBlock;
        if (!isCableTarget)
            return InteractionResult.PASS;
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        final CompoundTag cableTag = CommonUtil.getOrCreateElementTag(
                GInstrumentMod.modTag(pContext.getItemInHand()), CABLE_TAG);

        final BlockPos sourcePos;
        final String selectMessage;
        if (block instanceof AbstractInstrumentBlock instrumentBlock) {
            sourcePos = instrumentBlock.getInstrumentPos(level, clickedPos);
            selectMessage = "item.genshinstrument_lm.speaker_cable.instrument.select";
        } else if (block instanceof LooperBlock) {
            sourcePos = clickedPos;
            selectMessage = "item.genshinstrument_lm.speaker_cable.looper.select";
        } else if (block instanceof SpeakerBlock) {
            final boolean succeed = player.isShiftKeyDown()
                ? unpairFromSpeaker(clickedPos, cableTag, player)
                : handleSpeakerBlock(clickedPos, cableTag, player);
            return succeed ? InteractionResult.SUCCESS : InteractionResult.CONSUME_PARTIAL;
        } else {
            return InteractionResult.FAIL;
        }

        final boolean succeed = player.isShiftKeyDown()
            ? unpairSpeakersFromSource(sourcePos, cableTag, player)
            : handleSourceBlock(sourcePos, cableTag, player, selectMessage);

        return succeed ? InteractionResult.SUCCESS : InteractionResult.CONSUME_PARTIAL;
    }

    private static boolean handleSourceBlock(BlockPos sourcePos, CompoundTag cableTag, Player player, String selectMessage) {
        if (cableTag.contains(SPEAKER_POS_TAG, Tag.TAG_COMPOUND))
            return pairSpeakerToSource(cableTag, NbtUtils.readBlockPos(cableTag.getCompound(SPEAKER_POS_TAG)), sourcePos, player);

        clearCable(cableTag);
        cableTag.put(SOURCE_POS_TAG, NbtUtils.writeBlockPos(sourcePos));
        player.displayClientMessage(
            Component.translatable(selectMessage).withStyle(ChatFormatting.GREEN)
        , true);
        return true;
    }
    private static boolean handleSpeakerBlock(BlockPos speakerPos, CompoundTag cableTag, Player player) {
        if (!(player.level().getBlockEntity(speakerPos) instanceof SpeakerBlockEntity))
            return false;

        if (cableTag.contains(SOURCE_POS_TAG, Tag.TAG_COMPOUND))
            return pairSpeakerToSource(cableTag, speakerPos, NbtUtils.readBlockPos(cableTag.getCompound(SOURCE_POS_TAG)), player);

        clearCable(cableTag);
        cableTag.put(SPEAKER_POS_TAG, NbtUtils.writeBlockPos(speakerPos));
        player.displayClientMessage(
            Component.translatable("item.genshinstrument_lm.speaker_cable.speaker.select").withStyle(ChatFormatting.GREEN)
        , true);
        return true;
    }


    /**
     * return the block instrument or looper at the given position, or null if there is neither
     */
    @Nullable
    private static BlockEntity getSource(Level level, BlockPos sourcePos) {
        final BlockEntity be = level.getBlockEntity(sourcePos);
        return (be instanceof InstrumentBlockEntity || be instanceof LooperBlockEntity) ? be : null;
    }

    private static boolean pairSpeakerToSource(CompoundTag cableTag, BlockPos speakerPos, BlockPos sourcePos, Player player) {
        final Level level = player.level();
        clearCable(cableTag);

        final BlockEntity source = getSource(level, sourcePos);
        if (!(level.getBlockEntity(speakerPos) instanceof SpeakerBlockEntity sbe) || source == null)
            return false;

        // also mirrors the pairing onto the other half of linked blocks (like the Keyboard), and syncs it to clients
        final SpeakerUtil.PairResult result = SpeakerUtil.pair(level, source, sbe);
        SpeakerUtil.sendPairMessage(player, result, SpeakerUtil.speakerCount(level, source));

        return true;
    }

    /**
     * shift + right-click on a source. If a speaker was selected with this cable first,
     * only that speaker is unpaired, otherwise every speaker paired to the source is.
     */
    private static boolean unpairSpeakersFromSource(BlockPos sourcePos, CompoundTag cableTag, Player player) {
        final Level level = player.level();
        final BlockEntity source = getSource(level, sourcePos);
        if (source == null)
            return false;

        final BlockPos selectedSpeakerPos = cableTag.contains(SPEAKER_POS_TAG, Tag.TAG_COMPOUND)
            ? NbtUtils.readBlockPos(cableTag.getCompound(SPEAKER_POS_TAG))
            : null;
        clearCable(cableTag);

        if (selectedSpeakerPos != null && (level.getBlockEntity(selectedSpeakerPos) instanceof SpeakerBlockEntity sbe)) {
            final boolean removed = SpeakerUtil.unpair(level, source, sbe);
            SpeakerUtil.sendUnpairMessage(player, removed, SpeakerUtil.speakerCount(level, source), sbe);
        } else {
            SpeakerUtil.sendUnpairAllMessage(player, SpeakerUtil.unpairAll(level, source));
        }

        return true;
    }

    /**
     * shift + right-click on a speaker. If a source (block instrument or looper) was selected with this cable first,
     * only that source is unpaired, otherwise everything paired to the speaker is (including held instruments).
     */
    private static boolean unpairFromSpeaker(BlockPos speakerPos, CompoundTag cableTag, Player player) {
        final Level level = player.level();
        if (!(level.getBlockEntity(speakerPos) instanceof SpeakerBlockEntity sbe))
            return false;

        final BlockEntity selectedSource = cableTag.contains(SOURCE_POS_TAG, Tag.TAG_COMPOUND)
            ? getSource(level, NbtUtils.readBlockPos(cableTag.getCompound(SOURCE_POS_TAG)))
            : null;
        clearCable(cableTag);

        if (selectedSource != null) {
            final boolean removed = SpeakerUtil.unpair(level, selectedSource, sbe);
            SpeakerUtil.sendUnpairMessage(player, removed, SpeakerUtil.speakerCount(level, selectedSource), sbe);
        } else {
            SpeakerUtil.sendSpeakerUnpairAllMessage(player, SpeakerUtil.unpairAllFromSpeaker(level, sbe));
        }

        return true;
    }

    private static void clearCable(CompoundTag cableTag) {
        for (final String key : List.copyOf(cableTag.getAllKeys()))
            cableTag.remove(key);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        if (!Screen.hasShiftDown()) {
            tooltipComponents.add(
                Component.translatable("item.shift.hint.show")
                    .withStyle(ChatFormatting.YELLOW)
            );
            return;
        } else {
            tooltipComponents.add(
                Component.translatable("item.shift.hint.hide")
                    .withStyle(ChatFormatting.YELLOW)
            );
        }

        tooltipComponents.add(
            Component.translatable("item.genshinstrument_lm.speaker_cable.pair.description")
                .withStyle(ChatFormatting.GRAY)
        );
        tooltipComponents.add(
            Component.translatable("item.genshinstrument_lm.speaker_cable.unpair.description")
                .withStyle(ChatFormatting.GRAY)
        );
        tooltipComponents.add(
            Component.translatable("item.genshinstrument_lm.speaker_cable.held.description")
                .withStyle(ChatFormatting.GRAY)
        );

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

}
