package com.stump.genshinstrument_lm.particle;

import com.stump.genshinstrument_lm.GInstrumentMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, GInstrumentMod.MODID);

    public static final RegistryObject<SimpleParticleType> CUSTOM_NOTE =
            PARTICLES.register("custom_note", () -> new SimpleParticleType(false));

    public static void register(final IEventBus bus) {
        PARTICLES.register(bus);
    }
}
