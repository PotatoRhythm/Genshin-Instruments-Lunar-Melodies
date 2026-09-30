package com.stump.songcraft_instruments.client.gui.instrument.bassguitar;

import com.stump.songcraft_instruments.client.gui.instrument.partial.SoundTypeOption;
import com.stump.songcraft_instruments.client.config.ModClientConfigs;
import com.stump.songcraft_instruments.client.config.enumType.BassGuitarSoundType;
import com.stump.songcraft_instruments.SCInstrumentMod;
import com.stump.songcraft_instruments.client.gui.instrument.partial.InstrumentThemeLoader;
import com.stump.songcraft_instruments.client.gui.instrument.partial.grid.GridInstrumentScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BassGuitarScreen extends GridInstrumentScreen {
    public static final ResourceLocation INSTRUMENT_ID = new ResourceLocation(SCInstrumentMod.MODID, "bass_guitar");

    @Override
    public SoundTypeOption<BassGuitarSoundType> soundTypeOption() {
        return new SoundTypeOption<>(BassGuitarSoundType.values(), ModClientConfigs.BASS_GUITAR_SOUND_TYPE,
            "button.songcraft_instruments.bass_guitar.soundType");
    }

    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }


    public static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}