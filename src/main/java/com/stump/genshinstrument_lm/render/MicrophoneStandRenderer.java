package com.stump.genshinstrument_lm.render;

import com.stump.genshinstrument_lm.block.blockentity.MicrophoneStandBlockEntity;
import com.stump.genshinstrument_lm.model.MicrophoneStandModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class MicrophoneStandRenderer extends AbstractDyeableBlockRenderer<MicrophoneStandBlockEntity> {

    public MicrophoneStandRenderer(BlockEntityRendererProvider.Context context) {
        super(new MicrophoneStandModel());
    }
}