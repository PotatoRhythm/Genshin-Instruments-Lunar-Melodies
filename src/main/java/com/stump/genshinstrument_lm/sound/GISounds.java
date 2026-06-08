package com.stump.genshinstrument_lm.sound;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.grid.GridInstrumentScreen;
import com.stump.genshinstrument_lm.sound.held.HeldNoteSound;
import com.stump.genshinstrument_lm.sound.registrar.HeldNoteSoundRegistrar;
import com.stump.genshinstrument_lm.sound.registrar.NoteSoundRegistrar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

public class GISounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GInstrumentMod.MODID);
    public static void register(final IEventBus bus) {
        SOUNDS.register(bus);
    }

    static {
        NOTEBLOCK_SOUNDS = new HashMap<>();
        registerNoteBlockSounds();
    }

    public static final NoteSound[]
        WINDSONG_LYRE_NOTE_SOUNDS = nsr(loc("windsong_lyre")).stereo().registerGrid(),
        VINTAGE_LYRE_NOTE_SOUNDS = nsr(loc("vintage_lyre")).registerGrid(),

        ZITHER_NEW_NOTE_SOUNDS = nsr(loc("floral_zither_new")).registerGrid(),
        ZITHER_OLD_NOTE_SOUNDS = nsr(loc("floral_zither_old")).registerGrid(),

        GLORIOUS_DRUM = nsr(loc("glorious_drum"))
            .chain(loc("glorious_drum_don")).add()
            .chain(loc("glorious_drum_ka")).stereo().add()
        .registerAll(),

        UKULELE = nsr(loc("ukulele")).registerGrid(),
        DJEM_DJEM_DRUM = nsr(loc("djem_djem_drum")).registerGrid(2, 4),

        KEYBOARD = nsr(loc("keyboard")).stereo().registerGrid(),
        KEYBOARD_GW2 = nsr(loc("keyboard_gw2")).stereo().registerGrid(),
        KEYBOARD_YAMAHA_C5 = nsr(loc("keyboard_yamaha_c5")).stereo().registerGrid(),
        HEARTOPIA = nsr(loc("keyboard_heartopia")).stereo().registerGrid(),
        KEYBOARD_ELECTRIC = nsr(loc("keyboard_electric")).stereo().registerGrid(),
        KEYBOARD_HARPSICHORD = nsr(loc("keyboard_harpsichord")).stereo().registerGrid(),


        TROMBONE = nsr(loc("trombone")).registerGrid(),
        SAXOPHONE = nsr(loc("saxophone")).registerGrid(),

        GUITAR = nsr(loc("guitar")).registerGrid(),
        GUITAR_JAZZ = nsr(loc("guitar_jazz")).stereo().registerGrid(),
        BASS_ACOUSTIC = nsr(loc("bass_acoustic")).stereo().registerGrid(),
        BASS_FINGER = nsr(loc("bass_finger")).stereo().registerGrid(),
        BASS_SLAP = nsr(loc("bass_slap")).stereo().registerGrid(),

        SHAMISEN = nsr(loc("shamisen")).stereo().registerGrid(),
        KOTO = nsr(loc("koto")).registerGrid(),

        PIPA_REGULAR = nsr(loc("pipa_regular")).registerGrid(),
        PIPA_TERMOLO = nsr(loc("pipa_tremolo")).registerGrid(),

        VIOLIN_PIZZICATO = nsr(loc("violin_pizzicato")).stereo().registerGrid(),

        GW2_BASS = nsr(loc("gw2_bass")).stereo().registerGrid(),
        GW2_BELL = nsr(loc("gw2_bell")).stereo().registerGrid(),
        GW2_HARP = nsr(loc("gw2_harp")).stereo().registerGrid(),
        GW2_LUTE = nsr(loc("gw2_lute")).stereo().registerGrid(),
        GW2_MINSTREL = nsr(loc("gw2_minstrel")).stereo().registerGrid(),
        GW2_PELL = nsr(loc("gw2_pell")).stereo().registerGrid(),
        GW2_DRUM = nsr(loc("gw2_drum")).stereo().registerGrid(2, 5)
    ;
    
    private static final float
        HOLD_DURATION = 3f,
        FADE_TIME = .25f
    ;

    public static final HeldNoteSound[]
        NIGHTWIND_HORN = hnsr(loc("nightwind_horn"))
            .holdBuilder(GISounds::twoOctaveSoundBuilder)
            .attackBuilder(GISounds::twoOctaveSoundBuilder)

            // Test for release sound:
//            .releaseBuilder((builder) -> builder
//                .chain(SoundEvents.COW_DEATH.getLocation())
//                .alreadyRegistered()
//                .add(GridInstrumentScreen.DEF_ROWS * 2)
//                .registerAll()
//            )

            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 10)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(HOLD_DURATION),

        VIOLIN_SLOW = hnsr(loc("violin_slow"))
            .holdBuilder(GISounds::threeOctaveSoundBuilder)
            .attackBuilder(GISounds::threeOctaveSoundBuilder)
            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 10)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(HOLD_DURATION),
        VIOLIN_FAST = hnsr(loc("violin_fast"))
            .holdBuilder(GISounds::threeOctaveSoundBuilder)
            .attackBuilder(GISounds::threeOctaveSoundBuilder)
            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 10)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(HOLD_DURATION),

        IRINA_BROCHIN = hnsr(loc("microphone_irina_brochin"))
            .holdBuilder(GISounds::threeOctaveSoundBuilder)
            .attackBuilder(GISounds::threeOctaveSoundBuilder)
            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 8)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(HOLD_DURATION),
        BASS_CHOIR = hnsr(loc("microphone_bass_choir"))
            .holdBuilder(GISounds::threeOctaveSoundBuilder)
            .attackBuilder(GISounds::threeOctaveSoundBuilder)
            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 8)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(HOLD_DURATION),
        NOT_MIKU = hnsr(loc("microphone_not_miku"))
            .holdBuilder(GISounds::threeOctaveSoundBuilder)
            .attackBuilder(GISounds::threeOctaveSoundBuilder)
            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 6)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(HOLD_DURATION),
        SAXOPHONE_BARITONE = hnsr(loc("saxophone_baritone"))
            .holdBuilder(GISounds::threeOctaveSoundBuilder)
            .attackBuilder(GISounds::threeOctaveSoundBuilder)
            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 10)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(HOLD_DURATION),
        SAXOPHONE_TENOR = hnsr(loc("saxophone_tenor"))
            .holdBuilder(GISounds::threeOctaveSoundBuilder)
            .attackBuilder(GISounds::threeOctaveSoundBuilder)
            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 10)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(HOLD_DURATION),
        TRUMPET_WESTGATE_STUDIOS = hnsr(loc("trumpet_westgate_studios"))
            .holdBuilder(GISounds::threeOctaveSoundBuilder)
            .attackBuilder(GISounds::threeOctaveSoundBuilder)
            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 8)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(HOLD_DURATION),
        TROMBONE_PHGM = hnsr(loc("trombone_phgm"))
            .holdBuilder(GISounds::threeOctaveSoundBuilder)
            .attackBuilder(GISounds::threeOctaveSoundBuilder)
            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 8)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(HOLD_DURATION),

        GW2_HORN = hnsr(loc("gw2_horn"))
            .holdBuilder(GISounds::threeOctaveSoundBuilder)
            .attackBuilder(GISounds::threeOctaveSoundBuilder)
            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 10)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(2.5f),
        GW2_FLUTE = hnsr(loc("gw2_flute"))
            .holdBuilder(GISounds::twoOctaveSoundBuilder)
            .attackBuilder(GISounds::twoOctaveSoundBuilder)
            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 10)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(HOLD_DURATION),
        GW2_VERDARACH = hnsr(loc("gw2_verdarach"))
            .holdBuilder(GISounds::threeOctaveSoundBuilder)
            .attackBuilder(GISounds::threeOctaveSoundBuilder)
            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 10)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(1f),
        GW2_ORGAN = hnsr(loc("gw2_organ"))
            .holdBuilder(GISounds::threeOctaveSoundBuilder)
            .attackBuilder(GISounds::threeOctaveSoundBuilder)
            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 10)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(2.5f),
        GW2_QUAGGAN_ORGAN = hnsr(loc("gw2_quaggan_organ"))
            .holdBuilder(GISounds::threeOctaveSoundBuilder)
            .attackBuilder(GISounds::threeOctaveSoundBuilder)
            .holdDelay(.03f)
            .chainedHoldDelay(-FADE_TIME * 2)
            .releaseFadeOut(FADE_TIME / 10)
            .fullHoldFadeoutTime(2)
            .decays(7)
            .register(HOLD_DURATION)
    ;
    
    private static NoteSound[] twoOctaveSoundBuilder(final NoteSoundRegistrar builder) {
        return builder.stereo().registerGrid(GridInstrumentScreen.DEF_ROWS, 2);
    }

    private static NoteSound[] threeOctaveSoundBuilder(final NoteSoundRegistrar builder) {
        return builder.stereo().registerGrid(GridInstrumentScreen.DEF_ROWS, 3);
    }

    /**
     * Shorthand for {@code new ResourceLocation(Main.MODID, name)}
     */
    private static ResourceLocation loc(final String name) {
        return new ResourceLocation(GInstrumentMod.MODID, name);
    }
    /**
     * Shorthand for {@code new NoteSoundRegistrar(soundRegistrar, instrumentId)}
     */
    private static NoteSoundRegistrar nsr(ResourceLocation instrumentId) {
        return new NoteSoundRegistrar(GISounds.SOUNDS, instrumentId);
    }
    /**
     * Shorthand for {@code new HeldNoteSoundRegistrar(soundRegistrar, instrumentId)}
     */
    private static HeldNoteSoundRegistrar hnsr(ResourceLocation instrumentId) {
        return new HeldNoteSoundRegistrar(GISounds.SOUNDS, instrumentId);
    }

    private static final HashMap<NoteBlockInstrument, NoteSound> NOTEBLOCK_SOUNDS;
    public static NoteSound[] getNoteblockSounds(final NoteBlockInstrument instrumentType) {
        return new NoteSound[] {NOTEBLOCK_SOUNDS.get(instrumentType)};
    }

    private static void registerNoteBlockSounds() {
        final NoteSoundRegistrar registrar = nsr(loc("note_block_instrument"));

        for (NoteBlockInstrument noteSound : NoteBlockInstrument.values()) {
            registrar.chain(noteSound.getSoundEvent().get().getLocation())
                    .alreadyRegistered()
                    .add();
            NOTEBLOCK_SOUNDS.put(noteSound, registrar.peek());
        }

        registrar.registerAll();
    }
}
