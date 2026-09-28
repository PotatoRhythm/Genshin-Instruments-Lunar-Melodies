package com.stump.songcraft_instruments.datagen;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {

        var generator = event.getGenerator();
        var output = generator.getPackOutput();

        generator.addProvider(
                event.includeClient(),
                new SoundJsonGenerator(output, event.getExistingFileHelper())
        );
    }
}