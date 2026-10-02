package com.stump.songcraft_instruments.client.config.enumType;

import com.stump.songcraft_instruments.sound.SCSounds;
import com.stump.songcraft_instruments.sound.SoundOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public enum DrumsetSoundType implements SoundType {
    GW2(() -> new SoundOption(SCSounds.DRUMSET_GW2)),
    STANDARD(() -> new SoundOption(SCSounds.DRUMSET_STANDARD));

    private final Supplier<SoundOption> soundArr;
    private DrumsetSoundType(final Supplier<SoundOption> soundType) {
        this.soundArr = soundType;
    }

    @Override
    public Supplier<SoundOption> getSoundArr() {
        return soundArr;
    }
}