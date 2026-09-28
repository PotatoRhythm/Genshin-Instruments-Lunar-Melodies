package com.stump.songcraft_instruments.model;

import com.stump.songcraft_instruments.SCInstrumentMod;
import com.stump.songcraft_instruments.item.KeyboardBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KeyboardItemModel extends GeoModel<KeyboardBlockItem> {

    @Override
    public ResourceLocation getModelResource(KeyboardBlockItem animatable) {
        return new ResourceLocation(SCInstrumentMod.MODID, "geo/keyboard.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KeyboardBlockItem animatable) {
        return new ResourceLocation(SCInstrumentMod.MODID, "textures/block/keyboard.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KeyboardBlockItem animatable) {
        return new ResourceLocation(SCInstrumentMod.MODID, "animations/keyboard.animation.json");
    }
}
