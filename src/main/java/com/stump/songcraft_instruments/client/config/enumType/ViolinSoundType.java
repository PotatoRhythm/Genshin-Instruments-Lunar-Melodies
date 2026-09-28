package com.stump.songcraft_instruments.client.config.enumType;

import com.stump.songcraft_instruments.sound.SCSounds;
import com.stump.songcraft_instruments.sound.SoundOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public enum ViolinSoundType implements SoundType {
    SLOW(() -> new SoundOption(SCSounds.VIOLIN_SLOW)),
    FAST(() -> new SoundOption(SCSounds.VIOLIN_FAST)),
    PIZZ(() -> new SoundOption(SCSounds.VIOLIN_PIZZICATO));

    private final Supplier<SoundOption> soundArr;
    private ViolinSoundType(final Supplier<SoundOption> soundType) {
        this.soundArr = soundType;
    }

    @Override
    public Supplier<SoundOption> getSoundArr() {
        return soundArr;
    }
}