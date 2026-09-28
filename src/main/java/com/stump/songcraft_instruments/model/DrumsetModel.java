package com.stump.songcraft_instruments.model;

import com.stump.songcraft_instruments.SCInstrumentMod;
import com.stump.songcraft_instruments.block.blockentity.DrumsetBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DrumsetModel extends GeoModel<DrumsetBlockEntity> {

    @Override
    public ResourceLocation getModelResource(DrumsetBlockEntity animatable) {
        return new ResourceLocation(SCInstrumentMod.MODID, "geo/drumset.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DrumsetBlockEntity animatable) {
        return new ResourceLocation(SCInstrumentMod.MODID, "textures/block/drumset.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DrumsetBlockEntity animatable) {
        return new ResourceLocation(SCInstrumentMod.MODID, "animations/drumset.animation.json");
    }
}