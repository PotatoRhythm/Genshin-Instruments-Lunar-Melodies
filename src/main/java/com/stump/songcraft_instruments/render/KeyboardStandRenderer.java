package com.stump.songcraft_instruments.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stump.songcraft_instruments.block.KeyboardStandBlock;
import com.stump.songcraft_instruments.block.blockentity.KeyboardStandBlockEntity;
import com.stump.songcraft_instruments.model.KeyboardStandModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class KeyboardStandRenderer extends AbstractDyeableBlockRenderer<KeyboardStandBlockEntity> {

    private static final double KEYBOARD_Y_OFFSET = 0.55D;

    public KeyboardStandRenderer(BlockEntityRendererProvider.Context context) {
        super(new KeyboardStandModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, KeyboardStandBlockEntity animatable, BakedGeoModel model,
            RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
            float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (!animatable.getBlockState().getValue(KeyboardStandBlock.HAS_KEYBOARD)) {
            return;
        }

        poseStack.pushPose();

        Direction facing = animatable.getBlockState().getValue(KeyboardStandBlock.FACING);

        switch (facing) {
            case NORTH -> poseStack.translate(0.5D, KEYBOARD_Y_OFFSET, 0.0D);
            case EAST  -> poseStack.translate(0.0D, KEYBOARD_Y_OFFSET, 0.5D);
            case SOUTH -> poseStack.translate(-0.5D, KEYBOARD_Y_OFFSET, 0.0D);
            case WEST  -> poseStack.translate(0.0D, KEYBOARD_Y_OFFSET, -0.5D);
        }

        super.actuallyRender(poseStack, animatable, model,
                renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        poseStack.popPose();
    }
}
