package com.stump.genshinstrument_lm.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class CustomNoteParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    /**
     * dx = RGB (0xRRGGBB)
     * dy = size
     * dz = unused
     */
    protected CustomNoteParticle(ClientLevel level, double x, double y, double z,
                                 double dx, double dy, double dz, SpriteSet sprites) {

        super(level, x, y, z, 0, 0, 0);

        this.sprites = sprites;

        this.friction = 0.85F;
        this.gravity = 0.0F;

        this.xd = 0;
        this.yd = 0.12;
        this.zd = 0;

        this.lifetime = 10;

        this.quadSize = (float) (dy * (0.75 + level.random.nextDouble() * 0.50));

        int color = ((int) dx) & 0xFFFFFF;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        this.setColor(r, g, b);

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
}