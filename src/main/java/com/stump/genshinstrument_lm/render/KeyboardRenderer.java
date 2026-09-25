package com.stump.genshinstrument_lm.render;

import com.stump.genshinstrument_lm.block.blockentity.KeyboardBlockEntity;
import com.stump.genshinstrument_lm.model.KeyboardModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class KeyboardRenderer extends AbstractDyeableBlockRenderer<KeyboardBlockEntity> {

    public KeyboardRenderer(BlockEntityRendererProvider.Context context) {
        super(new KeyboardModel());
    }
}