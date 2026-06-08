package com.stump.genshinstrument_lm.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public class SoundJsonGenerator extends SoundDefinitionsProvider {

    public SoundJsonGenerator(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, "genshinstrument_lm", fileHelper);
    }

    @Override
    public void registerSounds() {
        registerInstrument("windsong_lyre", 20, 0.8f, false, true);

        registerInstrument("vintage_lyre", 20, 0.90f, false, false);

        registerInstrument("floral_zither_new", 20, 0.85f, false, false);
        registerInstrument("floral_zither_old", 20, 0.95f, false, false);

        registerInstrument("ukulele", 20, 0.85f, false, false);

        registerInstrument("djem_djem_drum", 7, 1f, false, false);

        registerInstrument("nightwind_horn", 13, 0.85f, true, true);

        registerInstrument("shamisen", 20, 0.8f, false, true);

        registerInstrument("koto", 20, 0.6f, false, false);

        registerInstrument("pipa_regular", 20, 0.9f, false, false);
        registerInstrument("pipa_tremolo", 20, 1f, false, false);

        registerInstrument("keyboard", 20, 1f, false, true);
        registerInstrument("keyboard_gw2", 20, 0.60f, false, true);
        registerInstrument("keyboard_yamaha_c5", 20, 0.45f, false, true);
        registerInstrument("keyboard_heartopia", 20, 0.7f, false, true);
        registerInstrument("keyboard_electric", 20, 1f, false, true);
        registerInstrument("keyboard_harpsichord", 20, 0.6f, false, true);

        registerInstrument("trombone", 20, 0.85f, false, false);
        registerInstrument("trumpet_westgate_studios", 20, 0.65f, true, true);
        registerInstrument("trombone_phgm", 20, 0.85f, true, true);

        registerInstrument("saxophone", 20, 1f, false, false);
        registerInstrument("saxophone_baritone", 20, 0.60f, true, true);
        registerInstrument("saxophone_tenor", 20, 0.25f, true, true);

        registerInstrument("guitar", 20, 0.9f, false, false);
        registerInstrument("guitar_jazz", 20, 0.6f, false, true);
        registerInstrument("bass_acoustic", 20, 1.0f, false, true);
        registerInstrument("bass_finger", 20, 1.0f, false, true);
        registerInstrument("bass_slap", 20, 0.9f, false, true);

        registerInstrument("violin_slow", 20, 0.8f, true, true);
        registerInstrument("violin_fast", 20, 0.5f, true, true);
        registerInstrument("violin_pizzicato", 20, 0.7f, false, true);

        registerInstrument("microphone_irina_brochin", 20, 0.3f, true, true);
        registerInstrument("microphone_bass_choir", 20, 0.7f, true, true);
        registerInstrument("microphone_not_miku", 20, 0.85f, true, true);

        registerInstrument("gw2_bass", 13, 1f, false, true);
        registerInstrument("gw2_bell", 13, 0.7f, false, true);
        registerInstrument("gw2_harp", 20, 0.8f, false, true);
        registerInstrument("gw2_lute", 20, 0.9f, false, true);
        registerInstrument("gw2_minstrel", 20, 0.8f, false, true);
        registerInstrument("gw2_pell", 20, 0.8f, false, true);
        registerInstrument("gw2_drum", 9, 0.95f, false, true);
        registerInstrument("gw2_horn", 20, 0.95f, true, true);
        registerInstrument("gw2_flute", 13, 0.95f, true, true);
        registerInstrument("gw2_verdarach", 20, 0.5f, true, true);
        registerInstrument("gw2_organ", 20, 0.9f, true, true);
        registerInstrument("gw2_quaggan_organ", 20, 0.9f, true, true);

        add("glorious_drum_don", definition().with(sound("genshinstrument_lm:glorious_drum/don").volume(1f)));
        add("glorious_drum_ka", definition().with(sound("genshinstrument_lm:glorious_drum/ka").volume(1f)));
        add("glorious_drum_ka_stereo", definition().with(sound("genshinstrument_lm:glorious_drum/ka.stereo").volume(1f)));
    }

    private static final int MONO_DISTANCE = 64;
    private static final float STEREO_VOLUME_ADJUSTMENT = 0.2f;

    /**
     * @param instrumentName    Instrument name (gw2_quaggan_organ, nightwind_horn, etc)
     * @param maxNotes          Max note index
     * @param volume            Adjusted volume level
     * @param heldSound         If true, generates attack + hold variants
     * @param supportsStereo    If true, generates .stereo variants
     */
    private void registerInstrument(String instrumentName, int maxNotes, float volume, boolean heldSound, boolean supportsStereo) {
        for (int i = 0; i <= maxNotes; i++) {
            if (!heldSound) {
                String basePath = "genshinstrument_lm:" + instrumentName + "/" + i;
                String event = instrumentName + "_note_" + i;

                // MONO
                add(event, definition().with(
                        sound(basePath)
                                .volume(volume)
                                .attenuationDistance(MONO_DISTANCE)
                ));

                if (supportsStereo) {
                    String stereoPath = "genshinstrument_lm:" + instrumentName + "/" + i + ".stereo";
                    String stereoEvent = instrumentName + "_note_" + i + "_stereo";

                    // STEREO (no attenuation)
                    add(stereoEvent, definition().with(
                            sound(stereoPath).volume(volume - STEREO_VOLUME_ADJUSTMENT)
                    ));
                }
            }
            else {
                String holdPath = "genshinstrument_lm:" + instrumentName + "/hold/" + i;
                String holdEvent = instrumentName + "_hold_note_" + i;

                // MONO
                add(holdEvent, definition().with(
                        sound(holdPath)
                                .volume(volume)
                                .attenuationDistance(MONO_DISTANCE)
                ));

                if (supportsStereo) {
                    String holdStereoPath = "genshinstrument_lm:" + instrumentName + "/hold/" + i + ".stereo";
                    String holdStereoEvent = instrumentName + "_hold_note_" + i + "_stereo";

                    add(holdStereoEvent, definition().with(
                            sound(holdStereoPath).volume(volume - STEREO_VOLUME_ADJUSTMENT)
                    ));
                }

                String attackPath = "genshinstrument_lm:" + instrumentName + "/attack/" + i;
                String attackEvent = instrumentName + "_attack_note_" + i;

                // MONO
                add(attackEvent, definition().with(
                        sound(attackPath)
                                .volume(volume)
                                .attenuationDistance(MONO_DISTANCE)
                ));

                if (supportsStereo) {
                    String attackStereoPath = "genshinstrument_lm:" + instrumentName + "/attack/" + i + ".stereo";
                    String attackStereoEvent = instrumentName + "_attack_note_" + i + "_stereo";

                    add(attackStereoEvent, definition().with(
                            sound(attackStereoPath).volume(volume - STEREO_VOLUME_ADJUSTMENT)
                    ));
                }
            }
        }
    }

}