package com.stump.genshinstrument_lm.client.config.enumType;

import com.stump.genshinstrument_lm.sound.GISounds;
import com.stump.genshinstrument_lm.sound.SoundOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public enum GuitarSoundType implements SoundType {
    EMI(() -> new SoundOption(GISounds.GUITAR)),
    JAZZ(() -> new SoundOption(GISounds.GUITAR_JAZZ)),
    ACOUSTIC(() -> new SoundOption(GISounds.BASS_ACOUSTIC)),
    FINGER(() -> new SoundOption(GISounds.BASS_FINGER)),
    SLAP(() -> new SoundOption(GISounds.BASS_SLAP));

    private final Supplier<SoundOption> soundArr;
    private GuitarSoundType(final Supplier<SoundOption> soundType) {
        this.soundArr = soundType;
    }

    @Override
    public Supplier<SoundOption> getSoundArr() {
        return soundArr;
    }
}