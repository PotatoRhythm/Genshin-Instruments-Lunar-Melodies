package com.stump.songcraft_instruments.model;

import com.stump.songcraft_instruments.SCInstrumentMod;
import com.stump.songcraft_instruments.item.MicrophoneStandBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MicrophoneStandItemModel extends GeoModel<MicrophoneStandBlockItem> {

    @Override
    public ResourceLocation getModelResource(MicrophoneStandBlockItem animatable) {
        return new ResourceLocation(SCInstrumentMod.MODID, "geo/microphone_stand.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MicrophoneStandBlockItem animatable) {
        return new ResourceLocation(SCInstrumentMod.MODID, "textures/block/microphone_stand.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MicrophoneStandBlockItem animatable) {
        return new ResourceLocation(SCInstrumentMod.MODID, "animations/microphone_stand.animation.json");
    }
}
