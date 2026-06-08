package com.stump.genshinstrument_lm.block.blockentity;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.capability.recording.RecordingCapabilityProvider;
import com.stump.genshinstrument_lm.util.LooperUtil;
import com.stump.genshinstrument_lm.event.HeldNoteSoundPlayedEvent;
import com.stump.genshinstrument_lm.event.InstrumentPlayedEvent;
import com.stump.genshinstrument_lm.event.NoteSoundPlayedEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.Optional;

import static com.stump.genshinstrument_lm.item.emirecord.EMIRecordItem.INSTRUMENT_ID_TAG;

/**
 * Listens to instrument played events
 * and writes it to a matching looper.
 */
@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID)
public class LooperNoteListener {

    @SubscribeEvent
    public static void onNoteSoundPlayed(final NoteSoundPlayedEvent event) {
        getMatchingLooper(event).ifPresent(looperBE -> {
            Player player = (Player) event.entityInfo().get().entity;
            int particleSet = RecordingCapabilityProvider.getParticleSet(player);
            looperBE.writeNote(
                    event.sound(),
                    event.soundMeta(),
                    looperBE.getTicks(),
                    particleSet
            );
        });
    }

    @SubscribeEvent
    public static void onHeldNoteSoundPlayed(final HeldNoteSoundPlayedEvent event) {
        getMatchingLooper(event).ifPresent(looperBE -> {
            Player player = (Player) event.entityInfo().get().entity;
            int particleSet = RecordingCapabilityProvider.getParticleSet(player);
            looperBE.writeHeldNote(
                    event.sound(),
                    event.phase,
                    event.soundMeta(),
                    looperBE.getTicks(),
                    particleSet
            );
        });
    }


    /**
     * @return The looper matching the provided event
     */
    private static Optional<LooperBlockEntity> getMatchingLooper(final InstrumentPlayedEvent<?> event) {
        // Only get player events
        if (!event.isByPlayer())
            return Optional.empty();

        final Player player = (Player) event.entityInfo().get().entity;

        if (event.level().isClientSide || !LooperUtil.isRecording(player))
            return Optional.empty();


        final Level level = player.level();

        final LooperBlockEntity looperBE = LooperUtil.getFromEvent(event);
        if (looperBE == null || looperBE.isCapped(level))
            return Optional.empty();

        // Omit if record is not writable
        if (!looperBE.isWritable())
            return Optional.empty();


        if (looperBE.isLocked()) {
            if (!looperBE.isRecording() || !looperBE.isAllowedToRecord(player))
                return Optional.empty();
        } else {
            looperBE.setLockedBy(player);
            looperBE.setRecording(true);
            looperBE.getChannel().putString(INSTRUMENT_ID_TAG, event.soundMeta().instrumentId().toString());
        }

        return Optional.of(looperBE);
    }
}
