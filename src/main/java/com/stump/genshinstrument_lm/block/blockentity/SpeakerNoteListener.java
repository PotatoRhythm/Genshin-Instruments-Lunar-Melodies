package com.stump.genshinstrument_lm.block.blockentity;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.event.HeldNoteSoundPlayedEvent;
import com.stump.genshinstrument_lm.event.InstrumentPlayedEvent;
import com.stump.genshinstrument_lm.event.NoteSoundPlayedEvent;
import com.stump.genshinstrument_lm.util.SpeakerUtil;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.Optional;

/**
 * Listens to instrument played events and immediately relays them
 * through any speaker paired to the playing instrument.
 */
@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID)
public class SpeakerNoteListener {

    @SubscribeEvent
    public static void onNoteSoundPlayed(final NoteSoundPlayedEvent event) {
        getMatchingSpeaker(event).ifPresent((speakerBE) ->
            speakerBE.playNote(event.sound(), event.soundMeta())
        );
    }

    @SubscribeEvent
    public static void onHeldNoteSoundPlayed(final HeldNoteSoundPlayedEvent event) {
        getMatchingSpeaker(event).ifPresent((speakerBE) ->
            speakerBE.playHeldNote(event.sound(), event.soundMeta(), event.phase)
        );
    }


    /**
     * @return The speaker paired to the instrument that produced the provided event.
     * Only matches player-initiated events - this both ties speaker playback to
     * an actual player performance, and prevents a speaker's own relayed sound
     * (which is not player-initiated) from re-triggering itself.
     */
    private static Optional<SpeakerBlockEntity> getMatchingSpeaker(final InstrumentPlayedEvent<?> event) {
        if (!event.isByPlayer() || event.level().isClientSide)
            return Optional.empty();

        return Optional.ofNullable(SpeakerUtil.getFromEvent(event));
    }
}
