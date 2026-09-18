package com.stump.genshinstrument_lm.client.gui.instrument.electricguitar;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.InstrumentThemeLoader;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.grid.GridInstrumentScreen;
import com.stump.genshinstrument_lm.client.gui.options.partial.InstrumentOptionsScreen;
import com.stump.genshinstrument_lm.sound.SoundOption;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElectricGuitarScreen extends GridInstrumentScreen {
    public static final ResourceLocation INSTRUMENT_ID = new ResourceLocation(GInstrumentMod.MODID, "electric_guitar");

    @Override
    public SoundOption getSoundOption() {
        return ((ElectricGuitarOptionsScreen) optionsScreen)
                .getPreferredSoundType()
                .getSoundArr()
                .get();
    }

    @Override
    protected InstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new ElectricGuitarOptionsScreen(this);
    }

    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }
    @Override
    public boolean isGenshinInstrument() {
        return false;
    }

    public static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}