package com.stump.songcraft_instruments.criteria;

import com.stump.songcraft_instruments.SCInstrumentMod;
import com.stump.songcraft_instruments.event.InstrumentPlayedEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;
import static net.minecraft.advancements.CriteriaTriggers.register;

//NOTE: There to make it load on setup too
@EventBusSubscriber(bus = Bus.FORGE, modid = SCInstrumentMod.MODID)
public class ModCriteria {
    public static void load() {}

    public static final InstrumentPlayedTrigger INSTRUMENT_PLAYED_TRIGGER = register(new InstrumentPlayedTrigger());

    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent<?> event) {
        if (event.level().isClientSide)
            return;

        // Only get player events
        if (!event.isByPlayer())
            return;

        final Item instrument = ForgeRegistries.ITEMS.getValue(event.soundMeta().instrumentId());
        // Perhaps troll packets
        if (instrument == null)
            return;

        INSTRUMENT_PLAYED_TRIGGER.trigger(
            (ServerPlayer) event.entityInfo().get().entity,
            new ItemStack(instrument)
        );
    }


    public static final RecordInjectedTrigger RECORD_INJECTED_TRIGGER = register(new RecordInjectedTrigger());
}