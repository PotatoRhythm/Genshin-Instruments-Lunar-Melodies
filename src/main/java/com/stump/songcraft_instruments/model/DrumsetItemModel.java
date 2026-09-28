package com.stump.songcraft_instruments.model;

import com.stump.songcraft_instruments.SCInstrumentMod;
import com.stump.songcraft_instruments.item.DrumsetBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DrumsetItemModel extends GeoModel<DrumsetBlockItem> {

    @Override
    public ResourceLocation getModelResource(DrumsetBlockItem animatable) {
        return new ResourceLocation(SCInstrumentMod.MODID, "geo/drumset.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DrumsetBlockItem animatable) {
        return new ResourceLocation(SCInstrumentMod.MODID, "textures/block/drumset.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DrumsetBlockItem animatable) {
        return new ResourceLocation(SCInstrumentMod.MODID, "animations/drumset.animation.json");
    }
}
