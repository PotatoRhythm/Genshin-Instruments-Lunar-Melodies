package com.stump.genshinstrument_lm.client;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.block.blockentity.ModBlockEntities;
import com.stump.genshinstrument_lm.render.DrumsetRenderer;
import com.stump.genshinstrument_lm.render.KeyboardRenderer;
import com.stump.genshinstrument_lm.render.KeyboardStandRenderer;
import com.stump.genshinstrument_lm.render.MicrophoneStandRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = GInstrumentMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(ModBlockEntities.DRUMSET.get(), DrumsetRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.KEYBOARD.get(), KeyboardRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.KEYBOARD_STAND.get(), KeyboardStandRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.MICROPHONE_STAND.get(), MicrophoneStandRenderer::new);
    }
}