package com.stump.songcraft_instruments.client.config.enumType;

import com.stump.songcraft_instruments.sound.SCSounds;
import com.stump.songcraft_instruments.sound.SoundOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public enum MicrophoneSoundType implements SoundType {
    IRINA(() -> new SoundOption(SCSounds.IRINA_BROCHIN)),
    BASS(() -> new SoundOption(SCSounds.BASS_CHOIR)),
    MIKU(() -> new SoundOption(SCSounds.NOT_MIKU)),
    TETO(() -> new SoundOption(SCSounds.NOT_TETO)),
    TETO_SNEAKY(() -> new SoundOption(SCSounds.NOT_TETO_SNEAKY));

    private final Supplier<SoundOption> soundArr;
    private MicrophoneSoundType(final Supplier<SoundOption> soundType) {
        this.soundArr = soundType;
    }

    @Override
    public Supplier<SoundOption> getSoundArr() {
        return soundArr;
    }
}