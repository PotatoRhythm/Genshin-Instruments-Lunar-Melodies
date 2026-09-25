package com.stump.genshinstrument_lm.model;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.block.blockentity.DrumsetBlockEntity;
import com.stump.genshinstrument_lm.item.DrumsetBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DrumsetItemModel extends GeoModel<DrumsetBlockItem> {

    @Override
    public ResourceLocation getModelResource(DrumsetBlockItem animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "geo/drumset.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DrumsetBlockItem animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "textures/block/drumset.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DrumsetBlockItem animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "animations/drumset.animation.json");
    }
}
