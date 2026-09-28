package com.stump.songcraft_instruments.render;

import com.stump.songcraft_instruments.block.blockentity.KeyboardBlockEntity;
import com.stump.songcraft_instruments.model.KeyboardModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class KeyboardRenderer extends AbstractDyeableBlockRenderer<KeyboardBlockEntity> {

    public KeyboardRenderer(BlockEntityRendererProvider.Context context) {
        super(new KeyboardModel());
    }
}