package com.stump.genshinstrument_lm.client.config.enumType;

import com.stump.genshinstrument_lm.sound.GISounds;
import com.stump.genshinstrument_lm.sound.SoundOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public enum MicrophoneSoundType implements SoundType {
    IRINA(() -> new SoundOption(GISounds.IRINA_BROCHIN)),
    BASS(() -> new SoundOption(GISounds.BASS_CHOIR)),
    MIKU(() -> new SoundOption(GISounds.NOT_MIKU)),
    TETO(() -> new SoundOption(GISounds.NOT_TETO)),
    TETO_SNEAKY(() -> new SoundOption(GISounds.NOT_TETO_SNEAKY));

    private final Supplier<SoundOption> soundArr;
    private MicrophoneSoundType(final Supplier<SoundOption> soundType) {
        this.soundArr = soundType;
    }

    @Override
    public Supplier<SoundOption> getSoundArr() {
        return soundArr;
    }
}