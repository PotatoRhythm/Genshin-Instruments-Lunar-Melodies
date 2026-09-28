package com.stump.songcraft_instruments.client.config.enumType;

import com.stump.songcraft_instruments.sound.SCSounds;
import com.stump.songcraft_instruments.sound.SoundOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public enum ZitherSoundType implements SoundType {
    OLD(() -> new SoundOption(SCSounds.ZITHER_OLD_NOTE_SOUNDS)),
    NEW(() -> new SoundOption(SCSounds.ZITHER_NEW_NOTE_SOUNDS));

    private final Supplier<SoundOption> soundArr;
    private ZitherSoundType(final Supplier<SoundOption> soundType) {
        this.soundArr = soundType;
    }

    @Override
    public Supplier<SoundOption> getSoundArr() {
        return soundArr;
    }
}