package com.stump.genshinstrument_lm;

import com.stump.genshinstrument_lm.item.ModItems;
import com.stump.genshinstrument_lm.networking.GIPacketHandler;
import com.stump.genshinstrument_lm.networking.buttonidentifier.DjemDjemDrumNoteIdentifier;
import com.stump.genshinstrument_lm.networking.buttonidentifier.GloriousDrumNoteIdentifier;
import com.stump.genshinstrument_lm.networking.buttonidentifier.NoteButtonIdentifiers;
import com.stump.genshinstrument_lm.networking.buttonidentifier.NoteGridButtonIdentifier;
import com.stump.genshinstrument_lm.particle.ModParticles;
import com.stump.genshinstrument_lm.sound.GISounds;
import com.stump.genshinstrument_lm.util.CommonUtil;
import com.stump.genshinstrument_lm.block.ModBlocks;
import com.stump.genshinstrument_lm.block.blockentity.ModBlockEntities;
import com.stump.genshinstrument_lm.criteria.ModCriteria;
import com.stump.genshinstrument_lm.gamerule.ModGameRules;
import com.stump.genshinstrument_lm.item.crafting.ModRecipeSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class of the Genshin Instruments: Lunar Melodies mod
 * 
 * Original mod author: tavWasPlayZ
 * Lunar Melodies fork by: Stump
 */
@Mod(GInstrumentMod.MODID)
public class GInstrumentMod
{
    public static final String MODID = "genshinstrument_lm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static CompoundTag modTag(final ItemStack item) {
        return item.getOrCreateTagElement(MODID);
    }
    public static CompoundTag modTag(final BlockEntity be) {
        return CommonUtil.getOrCreateElementTag(be.getPersistentData(), MODID);
    }

    public GInstrumentMod()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        GIPacketHandler.registerPackets();
        NoteButtonIdentifiers.register(
            NoteGridButtonIdentifier.class,
            GloriousDrumNoteIdentifier.class,
            DjemDjemDrumNoteIdentifier.class
        );

        ModGameRules.load();
        ModCriteria.load();

        ModItems.register(bus);
        ModBlocks.register(bus);
        ModBlockEntities.register(bus);
        ModRecipeSerializers.register(bus);
        ModParticles.register(bus);

        GISounds.register(bus);
        GICreativeModeTabs.regsiter(bus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
