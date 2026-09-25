package com.stump.genshinstrument_lm.model;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.block.blockentity.DrumsetBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DrumsetModel extends GeoModel<DrumsetBlockEntity> {

    @Override
    public ResourceLocation getModelResource(DrumsetBlockEntity animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "geo/drumset.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DrumsetBlockEntity animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "textures/block/drumset.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DrumsetBlockEntity animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "animations/drumset.animation.json");
    }
}