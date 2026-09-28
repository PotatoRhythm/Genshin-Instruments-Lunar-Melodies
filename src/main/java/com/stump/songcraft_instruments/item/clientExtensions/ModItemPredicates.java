package com.stump.songcraft_instruments.item.clientExtensions;

import com.stump.songcraft_instruments.SCInstrumentMod;
import com.stump.songcraft_instruments.capability.instrumentOpen.InstrumentOpen;
import com.stump.songcraft_instruments.capability.instrumentOpen.InstrumentOpenProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModItemPredicates {

    public static void register() {
        ItemProperties.registerGeneric(new ResourceLocation(SCInstrumentMod.MODID, "instrument_open"),
            ModItemPredicates::instrumentOpenPredicate
        );
    }

    /**
     * Derives {@link InstrumentOpen} capability as item model predicate
     */
    public static float instrumentOpenPredicate(ItemStack pStack, ClientLevel pLevel, LivingEntity pEntity, int pSeed) {
        if (!(pEntity instanceof Player player))
            return 0;

        if (!InstrumentOpenProvider.isOpen(player) || !InstrumentOpenProvider.isItem(player))
            return 0;

        return ItemStack.matches(pStack, player.getItemInHand(InstrumentOpenProvider.getHand(player))) ? 1 : 0;
    }

}
