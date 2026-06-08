package com.stump.genshinstrument_lm.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class CustomNoteParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private static final int[][] COLOR_SETS = new int[][] {
            // 0) Vanilla Colors
            { 0x162C57, 0x366CD9, 0x00D900, 0xD9D900, 0xD93636, 0xD93687 },
            // 1) Pastel Colors
            { 0xA0C4FF, 0x9BF6FF, 0xCAFFBF, 0xFDFFB6, 0xFFADAf, 0xFFC7EC },
            // 2) Black/White
            { 0x2B2B2B, 0xFFFFFF }
    };

    /**
     * Custom Version of Minecraft's Note Particle
     * @param dx color index
     * @param dy size (+/- 25%)
     * @param dz color set
     */
    protected CustomNoteParticle(ClientLevel level, double x, double y, double z,
                                 double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z, dx, dy, dz);

        this.sprites = sprites;
        this.friction = 0.85F;
        this.gravity = 0.0F;

        this.xd = 0;
        this.yd = 0.12;
        this.zd = 0;

        this.lifetime = 10;
        this.quadSize = (float) (dy * (0.75 + level.random.nextDouble() * 0.50));

        this.setSpriteFromAge(sprites);

        int setIndex = (int) dz;
        if (setIndex < 0 || setIndex >= COLOR_SETS.length) {
            setIndex = 0;
        }
        int[] colors = COLOR_SETS[setIndex];

        float colorPos = (float) dx;
        applyGradientColor(colorPos, colors);
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

    private void applyGradientColor(float t, int[] colors) {
        t = Mth.clamp(t, 0.0F, 1.0F);

        float scaled = t * (colors.length - 1);
        int idx = (int) scaled;
        idx = Mth.clamp(idx, 0, colors.length - 2);
        float localT = scaled - idx;

        int c1 = colors[idx];
        int c2 = colors[idx + 1];

        float r1 = ((c1 >> 16) & 0xFF) / 255F;
        float g1 = ((c1 >> 8) & 0xFF) / 255F;
        float b1 = (c1 & 0xFF) / 255F;

        float r2 = ((c2 >> 16) & 0xFF) / 255F;
        float g2 = ((c2 >> 8) & 0xFF) / 255F;
        float b2 = (c2 & 0xFF) / 255F;

        float r = r1 + (r2 - r1) * localT;
        float g = g1 + (g2 - g1) * localT;
        float b = b1 + (b2 - b1) * localT;

        this.setColor(r, g, b);
    }
}