package com.stump.genshinstrument_lm.util;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.block.blockentity.SpeakerBlockEntity;
import com.stump.genshinstrument_lm.block.partial.IDoubleBlock;
import com.stump.genshinstrument_lm.event.InstrumentPlayedEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * handles pairing an instrument to up to MAX_SPEAKERS SpeakerBlockEntities and resolving
 * those pairings when the instrument is played. mirrors LooperUtil's
 * looper-pairing mechanism, but a given instrument may be paired to speakers
 * independently of (and simultaneously with) a looper, since the two use separate tags.
 */
public class SpeakerUtil {
    public static final String SPEAKER_TAG = "speaker", POSITIONS_TAG = "positions",
        LEGACY_POS_TAG = "pos";
    public static final int MAX_SPEAKERS = 8;

    public enum PairResult { PAIRED, ALREADY_PAIRED, FULL }


    // Handle instrument's speaker tag
    public static boolean hasSpeakerTag(final ItemStack instrument) {
        return speakerCount(GInstrumentMod.modTag(instrument)) > 0;
    }
    public static boolean hasSpeakerTag(final BlockEntity instrument) {
        return speakerCount(GInstrumentMod.modTag(instrument)) > 0;
    }

    public static void remSpeakerTag(final ItemStack instrument) {
        GInstrumentMod.modTag(instrument).remove(SPEAKER_TAG);
    }
    public static void remSpeakerTag(final BlockEntity instrument) {
        GInstrumentMod.modTag(instrument).remove(SPEAKER_TAG);
    }

    public static PairResult addSpeaker(final ItemStack instrument, final BlockPos speakerPos) {
        return addSpeaker(GInstrumentMod.modTag(instrument), speakerPos);
    }
    public static PairResult addSpeaker(final BlockEntity instrument, final BlockPos speakerPos) {
        return addSpeaker(GInstrumentMod.modTag(instrument), speakerPos);
    }
    private static PairResult addSpeaker(final CompoundTag modTag, final BlockPos speakerPos) {
        final List<BlockPos> positions = getSpeakerPositions(modTag);

        if (positions.contains(speakerPos))
            return PairResult.ALREADY_PAIRED;
        if (positions.size() >= MAX_SPEAKERS)
            return PairResult.FULL;

        positions.add(speakerPos);
        setSpeakerPositions(modTag, positions);
        return PairResult.PAIRED;
    }

    /**
     * return whether the speaker was paired to the instrument
     */
    public static boolean removeSpeaker(final ItemStack instrument, final BlockPos speakerPos) {
        return removeSpeaker(GInstrumentMod.modTag(instrument), speakerPos);
    }
    public static boolean removeSpeaker(final BlockEntity instrument, final BlockPos speakerPos) {
        return removeSpeaker(GInstrumentMod.modTag(instrument), speakerPos);
    }
    private static boolean removeSpeaker(final CompoundTag modTag, final BlockPos speakerPos) {
        final List<BlockPos> positions = getSpeakerPositions(modTag);
        if (!positions.remove(speakerPos))
            return false;

        setSpeakerPositions(modTag, positions);
        return true;
    }

    public static int speakerCount(final ItemStack instrument) {
        return speakerCount(GInstrumentMod.modTag(instrument));
    }
    public static int speakerCount(final BlockEntity instrument) {
        return speakerCount(GInstrumentMod.modTag(instrument));
    }
    private static int speakerCount(final CompoundTag modTag) {
        return getSpeakerPositions(modTag).size();
    }

    /**
     * reads the paired speaker positions from the instrument's mod tag.
     * also understands the older single-speaker layout ({pos: {...}}).
     */
    public static List<BlockPos> getSpeakerPositions(final CompoundTag modTag) {
        final List<BlockPos> positions = new ArrayList<>();
        if (!modTag.contains(SPEAKER_TAG, CompoundTag.TAG_COMPOUND))
            return positions;

        final CompoundTag speakerTag = modTag.getCompound(SPEAKER_TAG);

        if (speakerTag.contains(POSITIONS_TAG, CompoundTag.TAG_LIST)) {
            final ListTag list = speakerTag.getList(POSITIONS_TAG, CompoundTag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++)
                positions.add(NbtUtils.readBlockPos(list.getCompound(i)));
        } else if (speakerTag.contains(LEGACY_POS_TAG, CompoundTag.TAG_COMPOUND)) {
            positions.add(NbtUtils.readBlockPos(speakerTag.getCompound(LEGACY_POS_TAG)));
        }

        return positions;
    }
    private static void setSpeakerPositions(final CompoundTag modTag, final List<BlockPos> positions) {
        if (positions.isEmpty()) {
            modTag.remove(SPEAKER_TAG);
            return;
        }

        final ListTag list = new ListTag();
        positions.forEach((pos) -> list.add(NbtUtils.writeBlockPos(pos)));

        final CompoundTag speakerTag = new CompoundTag();
        speakerTag.put(POSITIONS_TAG, list);
        modTag.put(SPEAKER_TAG, speakerTag);
    }


    /**
     * shows the player the outcome of a pairing attempt, e.g. "1 of 8 speakers connected!"
     */
    public static void sendPairMessage(final Player player, final PairResult result, final int count) {
        final Component message = switch (result) {
            case PAIRED -> Component.translatable("genshinstrument_lm.speaker.paired", count, MAX_SPEAKERS)
                .withStyle(ChatFormatting.GREEN);
            case ALREADY_PAIRED -> Component.translatable("genshinstrument_lm.speaker.already_paired", count, MAX_SPEAKERS)
                .withStyle(ChatFormatting.YELLOW);
            case FULL -> Component.translatable("genshinstrument_lm.speaker.full", MAX_SPEAKERS)
                .withStyle(ChatFormatting.RED);
        };
        player.displayClientMessage(message, true);
    }
    public static void sendUnpairMessage(final Player player, final boolean removed, final int count) {
        player.displayClientMessage(removed
            ? Component.translatable("genshinstrument_lm.speaker.unpaired", count, MAX_SPEAKERS)
                .withStyle(ChatFormatting.GREEN)
            : Component.translatable("genshinstrument_lm.speaker.not_paired")
                .withStyle(ChatFormatting.YELLOW)
        , true);
    }


    /**
     * return every speaker paired to the instrument that produced the event.
     * speakers that no longer exist are unpaired along the way.
     */
    public static List<SpeakerBlockEntity> getFromEvent(final InstrumentPlayedEvent<?> event) {
        if (!event.isByPlayer())
            return List.of();

        final InstrumentPlayedEvent<?>.EntityInfo entityInfo = event.entityInfo().get();
        final Player player = (Player) entityInfo.entity;
        final Level level = event.level();

        if (entityInfo.isItemInstrument())
            return getFromItemInstrument(level, player.getItemInHand(entityInfo.hand.get()));
        else if (entityInfo.isBlockInstrument())
            return getFromBlockInstrument(level, level.getBlockEntity(event.soundMeta().pos()));

        return List.of();
    }

    public static List<SpeakerBlockEntity> getFromItemInstrument(final Level level, final ItemStack instrument) {
        return getFromInstrument(level, GInstrumentMod.modTag(instrument), (pos) -> removeSpeaker(instrument, pos));
    }
    public static List<SpeakerBlockEntity> getFromBlockInstrument(final Level level, final BlockEntity instrument) {
        return getFromInstrument(level, GInstrumentMod.modTag(instrument), (pos) -> {
            removeSpeaker(instrument, pos);
            instrument.setChanged();

            final BlockPos instrumentPos = instrument.getBlockPos();
            final BlockState state = level.getBlockState(instrumentPos);
            if (state.getBlock() instanceof IDoubleBlock doubleBlock) {
                final BlockEntity other = level.getBlockEntity(doubleBlock.getOtherBlock(state, instrumentPos, level));
                if (other != null) {
                    removeSpeaker(other, pos);
                    other.setChanged();
                }
            }
        });
    }
    /**
     * resolves every speaker position in the instrument's data. positions that no longer
     * hold a speaker are handed to onInvalid for removal.
     */
    private static List<SpeakerBlockEntity> getFromInstrument(Level level, CompoundTag modTag,
                                                              Consumer<BlockPos> onInvalid) {
        final List<SpeakerBlockEntity> speakers = new ArrayList<>();

        for (final BlockPos pos : getSpeakerPositions(modTag)) {
            final SpeakerBlockEntity speakerBE = getFromPos(level, pos);
            if (speakerBE == null)
                onInvalid.accept(pos);
            else
                speakers.add(speakerBE);
        }

        return speakers;
    }

    @Nullable
    public static SpeakerBlockEntity getFromPos(final Level level, final BlockPos pos) {
        return (level.getBlockEntity(pos) instanceof SpeakerBlockEntity sbe) ? sbe : null;
    }
}
