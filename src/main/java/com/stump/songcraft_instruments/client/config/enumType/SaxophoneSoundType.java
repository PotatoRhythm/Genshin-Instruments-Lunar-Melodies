package com.stump.songcraft_instruments.client.config.enumType;

import com.stump.songcraft_instruments.sound.SCSounds;
import com.stump.songcraft_instruments.sound.SoundOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public enum SaxophoneSoundType implements SoundType {
    EMI(() -> new SoundOption(SCSounds.SAXOPHONE)),
    BARITONE(() -> new SoundOption(SCSounds.SAXOPHONE_BARITONE)),
    TENOR(() -> new SoundOption(SCSounds.SAXOPHONE_TENOR));

    private final Supplier<SoundOption> soundArr;
    private SaxophoneSoundType(final Supplier<SoundOption> soundType) {
        this.soundArr = soundType;
    }

    @Override
    public Supplier<SoundOption> getSoundArr() {
        return soundArr;
    }
}