package com.stump.genshinstrument_lm.model;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.block.blockentity.KeyboardStandBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KeyboardStandModel extends GeoModel<KeyboardStandBlockEntity> {

    @Override
    public ResourceLocation getModelResource(KeyboardStandBlockEntity animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "geo/keyboard.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KeyboardStandBlockEntity animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "textures/block/keyboard.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KeyboardStandBlockEntity animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "animations/keyboard.animation.json");
    }
}