package com.stump.songcraft_instruments.client.config.enumType;

import com.stump.songcraft_instruments.sound.SCSounds;
import com.stump.songcraft_instruments.sound.SoundOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public enum PipaSoundType implements SoundType {
    REGULAR(() -> new SoundOption(SCSounds.PIPA_REGULAR)),
    TREMOLO(() -> new SoundOption(SCSounds.PIPA_TERMOLO));


    private final Supplier<SoundOption> soundArr;
    private PipaSoundType(final Supplier<SoundOption> soundType) {
        this.soundArr = soundType;
    }

    @Override
    public Supplier<SoundOption> getSoundArr() {
        return soundArr;
    }
}