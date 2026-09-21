package com.stump.genshinstrument_lm.util;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.block.blockentity.SpeakerBlockEntity;
import com.stump.genshinstrument_lm.block.partial.IDoubleBlock;
import com.stump.genshinstrument_lm.event.InstrumentPlayedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Handles pairing an instrument to a SpeakerBlockEntity and resolving
 * that pairing when the instrument is played. Mirrors link LooperUtil's
 * looper-pairing mechanism, but a given instrument may be paired to a speaker
 * independently of (and simultaneously with) a looper, since the two use separate tags.
 */
public class SpeakerUtil {
    public static final String SPEAKER_TAG = "speaker", POS_TAG = "pos";


    // Handle instrument's speaker tag
    public static boolean hasSpeakerTag(final ItemStack instrument) {
        return hasSpeakerTag(GInstrumentMod.modTag(instrument));
    }
    public static boolean hasSpeakerTag(final BlockEntity instrument) {
        return hasSpeakerTag(GInstrumentMod.modTag(instrument));
    }
    private static boolean hasSpeakerTag(final CompoundTag modTag) {
        return modTag.contains(SPEAKER_TAG, CompoundTag.TAG_COMPOUND) && !modTag.getCompound(SPEAKER_TAG).isEmpty();
    }

    public static void remSpeakerTag(final ItemStack instrument) {
        GInstrumentMod.modTag(instrument).remove(SPEAKER_TAG);
    }
    public static void remSpeakerTag(final BlockEntity instrument) {
        GInstrumentMod.modTag(instrument).remove(SPEAKER_TAG);
    }

    public static void createSpeakerTag(final ItemStack instrument, final BlockPos speakerPos) {
        GInstrumentMod.modTag(instrument).put(SPEAKER_TAG, new CompoundTag());
        constructSpeakerTag(speakerTag(instrument), speakerPos);
    }
    public static void createSpeakerTag(final BlockEntity instrument, final BlockPos speakerPos) {
        GInstrumentMod.modTag(instrument).put(SPEAKER_TAG, new CompoundTag());
        constructSpeakerTag(speakerTag(instrument), speakerPos);
    }
    private static void constructSpeakerTag(final CompoundTag speakerTag, final BlockPos speakerPos) {
        speakerTag.put(POS_TAG, NbtUtils.writeBlockPos(speakerPos));
    }

    public static CompoundTag speakerTag(final ItemStack instrument) {
        return speakerTag(GInstrumentMod.modTag(instrument));
    }
    public static CompoundTag speakerTag(final BlockEntity instrument) {
        return speakerTag(GInstrumentMod.modTag(instrument));
    }
    public static CompoundTag speakerTag(final CompoundTag parentTag) {
        return parentTag.contains(SPEAKER_TAG, CompoundTag.TAG_COMPOUND)
            ? parentTag.getCompound(SPEAKER_TAG)
            : new CompoundTag();
    }


    @Nullable
    public static SpeakerBlockEntity getFromEvent(final InstrumentPlayedEvent<?> event) {
        if (!event.isByPlayer())
            return null;

        final InstrumentPlayedEvent<?>.EntityInfo entityInfo = event.entityInfo().get();
        final Player player = (Player) entityInfo.entity;
        final Level level = event.level();

        if (entityInfo.isItemInstrument())
            return getFromItemInstrument(level, player.getItemInHand(entityInfo.hand.get()));
        else if (entityInfo.isBlockInstrument())
            return getFromBlockInstrument(level, level.getBlockEntity(event.soundMeta().pos()));

        return null;
    }

    @Nullable
    public static SpeakerBlockEntity getFromItemInstrument(final Level level, final ItemStack instrument) {
        return getFromInstrument(level, speakerTag(instrument), () -> remSpeakerTag(instrument));
    }
    @Nullable
    public static SpeakerBlockEntity getFromBlockInstrument(final Level level, final BlockEntity instrument) {
        return getFromInstrument(level, speakerTag(instrument), () -> {
            remSpeakerTag(instrument);

            final BlockPos pos = instrument.getBlockPos();
            final BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof IDoubleBlock doubleBlock)
                remSpeakerTag(level.getBlockEntity(doubleBlock.getOtherBlock(state, pos, level)));
        });
    }
    /**
     * Attempts to get the speaker pointed out by {@code speakerData}. Removes its reference if not found.
     * @return The speaker's block entity as pointed in the {@code instrument}'s data.
     * Null if not found
     */
    @Nullable
    private static SpeakerBlockEntity getFromInstrument(Level level, CompoundTag speakerData, Runnable onInvalid) {
        if (speakerData.isEmpty())
            return null;

        final SpeakerBlockEntity speakerBE = getFromPos(level, getSpeakerPos(speakerData));

        if (speakerBE == null)
            onInvalid.run();

        return speakerBE;
    }

    public static SpeakerBlockEntity getFromPos(final Level level, final BlockPos pos) {
        return (level.getBlockEntity(pos) instanceof SpeakerBlockEntity sbe) ? sbe : null;
    }

    @Nullable
    public static BlockPos getSpeakerPos(final CompoundTag speakerTag) {
        final CompoundTag speakerPosTag = speakerTag.getCompound(POS_TAG);
        return (speakerPosTag == null) ? null : NbtUtils.readBlockPos(speakerPosTag);
    }
}
