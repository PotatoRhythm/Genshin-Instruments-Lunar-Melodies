package com.stump.genshinstrument_lm.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stump.genshinstrument_lm.block.partial.InstrumentBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public abstract class AbstractDyeableBlockRenderer<T extends InstrumentBlockEntity & GeoAnimatable>
        extends GeoBlockRenderer<T> {

    public AbstractDyeableBlockRenderer(GeoModel<T> model) {
        super(model);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone,
            RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
            float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if ("Dyeable".equals(bone.getName())) {
            int color = animatable.getDyeColor();

            red = ((color >> 16) & 255) / 255f;
            green = ((color >> 8) & 255) / 255f;
            blue = (color & 255) / 255f;
        }

        super.renderRecursively(poseStack, animatable, bone,
                renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}