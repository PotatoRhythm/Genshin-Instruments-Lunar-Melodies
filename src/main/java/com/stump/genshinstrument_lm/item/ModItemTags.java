package com.stump.genshinstrument_lm.item;

import com.stump.genshinstrument_lm.GInstrumentMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModItemTags {

    public static final TagKey<Item> GUILD_WARS_INSTRUMENTS =
            ItemTags.create(
                    new ResourceLocation(
                            GInstrumentMod.MODID,
                            "guild_wars/instruments"
                    )
            );

    private ModItemTags() {}
}