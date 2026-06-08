package com.stump.genshinstrument_lm.networking.packet.instrument.util;

import com.stump.genshinstrument_lm.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class LooperParticlePacketUtil {
    public static void spawnLooperParticle(BlockPos pos, double color, int colorSet) {
        Level level = Minecraft.getInstance().level;
        if (level == null)
            return;

        double xOffset = (level.random.nextDouble() - 0.5) * 0.30;
        double yOffset = (level.random.nextDouble() - 0.5) * 0.20;
        double zOffset = (level.random.nextDouble() - 0.5) * 0.30;

        level.addParticle(
                ModParticles.CUSTOM_NOTE.get(),
                pos.getX() + 0.5 + xOffset,
                pos.getY() + 1.25 + yOffset,
                pos.getZ() + 0.5 + zOffset,
                color,
                0.2,
                colorSet
        );
    }
}