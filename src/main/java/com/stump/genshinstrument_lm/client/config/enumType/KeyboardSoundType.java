package com.stump.genshinstrument_lm.client.config.enumType;

import com.stump.genshinstrument_lm.sound.GISounds;
import com.stump.genshinstrument_lm.sound.NoteSound;
import com.stump.genshinstrument_lm.sound.SoundOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public enum KeyboardSoundType implements SoundType {
    EMI(() -> new SoundOption(GISounds.KEYBOARD)),
    GW2(() -> new SoundOption(GISounds.KEYBOARD_GW2)),
    YAMAHA_C5(() -> new SoundOption(GISounds.KEYBOARD_YAMAHA_C5)),
    HEARTOPIA(() -> new SoundOption(GISounds.HEARTOPIA)),
    ELECTRIC(() -> new SoundOption(GISounds.KEYBOARD_ELECTRIC)),
    HARPSICHORD(() -> new SoundOption(GISounds.KEYBOARD_HARPSICHORD));

    private final Supplier<SoundOption> soundArr;
    private KeyboardSoundType(final Supplier<SoundOption> soundType) {
        this.soundArr = soundType;
    }

    @Override
    public Supplier<SoundOption> getSoundArr() {
        return soundArr;
    }
}