package com.stump.genshinstrument_lm.model;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.item.MicrophoneStandBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MicrophoneStandItemModel extends GeoModel<MicrophoneStandBlockItem> {

    @Override
    public ResourceLocation getModelResource(MicrophoneStandBlockItem animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "geo/microphone_stand.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MicrophoneStandBlockItem animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "textures/block/microphone_stand.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MicrophoneStandBlockItem animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "animations/microphone_stand.animation.json");
    }
}
