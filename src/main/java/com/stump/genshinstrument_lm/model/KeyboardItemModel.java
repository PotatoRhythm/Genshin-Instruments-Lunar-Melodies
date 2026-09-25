package com.stump.genshinstrument_lm.model;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.item.KeyboardBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KeyboardItemModel extends GeoModel<KeyboardBlockItem> {

    @Override
    public ResourceLocation getModelResource(KeyboardBlockItem animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "geo/keyboard.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KeyboardBlockItem animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "textures/block/keyboard.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KeyboardBlockItem animatable) {
        return new ResourceLocation(GInstrumentMod.MODID, "animations/keyboard.animation.json");
    }
}
