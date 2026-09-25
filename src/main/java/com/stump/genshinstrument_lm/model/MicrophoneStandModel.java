package com.stump.genshinstrument_lm.model;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.block.blockentity.MicrophoneStandBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MicrophoneStandModel extends GeoModel<MicrophoneStandBlockEntity> {

    @Override
    public ResourceLocation getModelResource(MicrophoneStandBlockEntity animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "geo/microphone_stand.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MicrophoneStandBlockEntity animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "textures/block/microphone_stand.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MicrophoneStandBlockEntity animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "animations/microphone_stand.animation.json");
    }
}