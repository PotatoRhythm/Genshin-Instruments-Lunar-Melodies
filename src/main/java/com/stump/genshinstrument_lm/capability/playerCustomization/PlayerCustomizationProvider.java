package com.stump.genshinstrument_lm.capability.playerCustomization;

import com.stump.genshinstrument_lm.particle.ColorSet;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerCustomizationProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<PlayerCustomization> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerCustomization instance;
    private final LazyOptional<PlayerCustomization> optional = LazyOptional.of(this::getInstance);

    private PlayerCustomization getInstance() {
        if (instance == null){
            instance = new PlayerCustomization();
            instance.initDefaults();
        }
        return instance;
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CAPABILITY.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        getInstance().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        getInstance().loadNBTData(nbt);
    }

    public static int getActiveSetIndex(Player player) {
        return player.getCapability(CAPABILITY)
                .map(PlayerCustomization::getActiveColorSet)
                .orElse(0);
    }

    public static void setActiveSetIndex(Player player, int set) {
        player.getCapability(CAPABILITY)
                .ifPresent(data -> data.setActiveColorSet(set));
    }

    public static PlayerCustomization get(Player player) {
        return player.getCapability(CAPABILITY).orElse(null);
    }

    public static ColorSet getActiveColorSet(Player player) {
        return player.getCapability(CAPABILITY)
                .map(data -> {
                    int index = data.getActiveColorSet();
                    if (index < 0 || index >= data.getColorSets().size())
                        return null;
                    return data.getColorSets().get(index);
                })
                .orElse(null);
    }
}
