package com.stump.genshinstrument_lm.render;

import com.stump.genshinstrument_lm.block.blockentity.DrumsetBlockEntity;
import com.stump.genshinstrument_lm.model.DrumsetModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class DrumsetRenderer extends AbstractDyeableBlockRenderer<DrumsetBlockEntity> {

    public DrumsetRenderer(BlockEntityRendererProvider.Context context) {
        super(new DrumsetModel());
    }
}