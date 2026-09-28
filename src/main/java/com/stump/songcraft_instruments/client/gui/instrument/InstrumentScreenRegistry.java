package com.stump.songcraft_instruments.client.gui.instrument;

import com.stump.songcraft_instruments.SCInstrumentMod;
import com.stump.songcraft_instruments.client.gui.instrument.partial.InstrumentScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class InstrumentScreenRegistry {

    private static final HashMap<ResourceLocation, Supplier<? extends InstrumentScreen>> INSTRUMENT_SCREENS = new HashMap<>();

    public static void register(final ResourceLocation instrumentID, Supplier<? extends InstrumentScreen> screenInitiator) {
        INSTRUMENT_SCREENS.put(instrumentID, screenInitiator);
    }
    public static void register(final Map<ResourceLocation, Supplier<? extends InstrumentScreen>> instrumentMap) {
        instrumentMap.forEach(InstrumentScreenRegistry::register);
    }

    public static void setScreenByID(final ResourceLocation instrumentID) {
        try {
            Minecraft.getInstance().setScreen(instantiateById(instrumentID));
        } catch (Exception e) {
            SCInstrumentMod.LOGGER.error("Exception thrown trying to open an instrument screen " + instrumentID, e);
        }
    }
    public static InstrumentScreen instantiateById(final ResourceLocation instrumentID) {
        return INSTRUMENT_SCREENS.get(instrumentID).get();
    }

}
