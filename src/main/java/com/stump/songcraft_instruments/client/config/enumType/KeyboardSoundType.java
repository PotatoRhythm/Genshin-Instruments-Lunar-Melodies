package com.stump.songcraft_instruments.client.config.enumType;

import com.stump.songcraft_instruments.sound.SCSounds;
import com.stump.songcraft_instruments.sound.SoundOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public enum KeyboardSoundType implements SoundType {
    EMI(() -> new SoundOption(SCSounds.KEYBOARD)),
    YAMAHA_C5(() -> new SoundOption(SCSounds.KEYBOARD_YAMAHA_C5)),
    HEARTOPIA(() -> new SoundOption(SCSounds.HEARTOPIA)),
    ELECTRIC(() -> new SoundOption(SCSounds.KEYBOARD_ELECTRIC)),
    HARPSICHORD(() -> new SoundOption(SCSounds.KEYBOARD_HARPSICHORD));

    private final Supplier<SoundOption> soundArr;
    private KeyboardSoundType(final Supplier<SoundOption> soundType) {
        this.soundArr = soundType;
    }

    @Override
    public Supplier<SoundOption> getSoundArr() {
        return soundArr;
    }
}