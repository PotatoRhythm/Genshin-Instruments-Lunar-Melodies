package com.stump.genshinstrument_lm.block.blockentity;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GInstrumentMod.MODID);
    public static void register(final IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }


    public static final RegistryObject<BlockEntityType<LooperBlockEntity>> LOOPER = BLOCK_ENTITIES.register("looper", () ->
        BlockEntityType.Builder.of(
            LooperBlockEntity::new, ModBlocks.LOOPER.get()
        ).build(null)
    );

    public static final RegistryObject<BlockEntityType<SpeakerBlockEntity>> SPEAKER = BLOCK_ENTITIES.register("speaker", () ->
        BlockEntityType.Builder.of(
            SpeakerBlockEntity::new, ModBlocks.SPEAKER.get()
        ).build(null)
    );
    
    // hmm
    public static final RegistryObject<BlockEntityType<ModInstrumentBlockEntity>> INSTRUMENT =
        BLOCK_ENTITIES.register(GInstrumentMod.MODID + "_instrument", () ->
                BlockEntityType.Builder.of(ModInstrumentBlockEntity::new,
                        ModBlocks.KOTO.get(), ModBlocks.MICROPHONE_STAND.get() ).build(null)
        );

    public static final RegistryObject<BlockEntityType<KeyboardStandBlockEntity>> KEYBOARD_STAND =
            BLOCK_ENTITIES.register("keyboard_stand", () ->
                    BlockEntityType.Builder.of(KeyboardStandBlockEntity::new,
                            ModBlocks.KEYBOARD_STAND.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<DrumsetBlockEntity>> DRUMSET =
            BLOCK_ENTITIES.register("drumset",
                    () -> BlockEntityType.Builder.of(DrumsetBlockEntity::new,
                            ModBlocks.DRUMSET.get()).build(null));

    public static final RegistryObject<BlockEntityType<KeyboardBlockEntity>> KEYBOARD =
            BLOCK_ENTITIES.register("keyboard",
                    () -> BlockEntityType.Builder.of(KeyboardBlockEntity::new,
                            ModBlocks.KEYBOARD.get()).build(null));

    public static final RegistryObject<BlockEntityType<MicrophoneStandBlockEntity>> MICROPHONE_STAND =
            BLOCK_ENTITIES.register("microphone_stand",
                    () -> BlockEntityType.Builder.of(MicrophoneStandBlockEntity::new,
                            ModBlocks.MICROPHONE_STAND.get()).build(null));

    public static final RegistryObject<BlockEntityType<DummyBlockEntity>> DUMMY =
            BLOCK_ENTITIES.register("dummy",
                    () -> BlockEntityType.Builder.of(
                            DummyBlockEntity::new,
                            ModBlocks.DRUMSET.get(),
                            ModBlocks.KEYBOARD.get()
                    ).build(null));
}