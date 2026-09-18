package com.stump.genshinstrument_lm.model;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.block.blockentity.KeyboardBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KeyboardModel extends GeoModel<KeyboardBlockEntity> {

    @Override
    public ResourceLocation getModelResource(KeyboardBlockEntity animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "geo/keyboard.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KeyboardBlockEntity animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "textures/block/keyboard.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KeyboardBlockEntity animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "animations/keyboard.animation.json");
    }
}