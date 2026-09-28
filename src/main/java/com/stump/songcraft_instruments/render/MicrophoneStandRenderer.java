package com.stump.songcraft_instruments.render;

import com.stump.songcraft_instruments.block.blockentity.MicrophoneStandBlockEntity;
import com.stump.songcraft_instruments.model.MicrophoneStandModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class MicrophoneStandRenderer extends AbstractDyeableBlockRenderer<MicrophoneStandBlockEntity> {

    public MicrophoneStandRenderer(BlockEntityRendererProvider.Context context) {
        super(new MicrophoneStandModel());
    }
}