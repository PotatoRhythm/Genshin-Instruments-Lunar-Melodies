package com.stump.songcraft_instruments.util;

import com.stump.songcraft_instruments.SCInstrumentMod;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;


public abstract class CommonUtil {
    
    /**
     * @return What the default level should've returned, but without any conditions
     */
    public static List<Player> getPlayersInArea(final Level level, final AABB area) {
        final List<Player> list = Lists.newArrayList();

        for (Player player : level.players()) {
            if (area.contains(player.getX(), player.getY(), player.getZ()))
                list.add(player);
        }

        return list;
    }
    
    
    /**
     * @param dir The directory location at which to grab the specified resource
     * @param path The desired path to obtain from the {@code dir}
     * @return The resource contained in the specified directory
     */
    public static ResourceLocation getResourceFrom(final ResourceLocation dir, final String path) {
        return new ResourceLocation(
            dir.getNamespace(),
            dir.getPath() + "/" + path
        );
    }


    /**
     * Retrieves a constructor from the provided {@code clazz}.
     * Failure will result in a {@link RuntimeException}.
     * @param clazz The class to reflect the constructor from
     * @param paramTypes The parameter types the function should accept
     * @return The parameterless constructor of the provided class
     * @param <T> The class type
     */
    public static <T> Constructor<T> getExpectedConstructor(final Class<T> clazz, Class<?>... paramTypes) {
        try {
            return clazz.getDeclaredConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find a matching constructor for " + clazz.getName(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error getting constructor for " + clazz.getName(), e);
        }
    }


    /**
     * Provides a similar behaviour to Python's indexing,
     * where negatives are counted backwards.
     */
    public static int pyWrap(int index, final int arrLength) {
        while (index < 0)
            index += arrLength;

        return index;
    }
    /**
     * Wraps the index around an array
     */
    public static int wrapAround(int index, final int arrLength) {
        return index % arrLength;
    }
    /**
     * Performs both {@link CommonUtil#pyWrap} and {@link CommonUtil#wrapAround}
     */
    public static int doublyPyWrap(int index, final int arrLength) {
        return wrapAround(pyWrap(index, arrLength), arrLength);
    }


    public static void loadClasses(final Class<?>[] classes) {
        for (final Class<?> loadMe : classes) {

            try {
                Class.forName(loadMe.getName());
            } catch (ClassNotFoundException e) {
                SCInstrumentMod.LOGGER.error("Failed to load class "+ loadMe.getSimpleName() +": class not found", e);
            }

        }
    }


    /**
     * @return The given {@code value} rounded by the provided {@code places}.
     */
    public static double round(double value, int places) {
        return BigDecimal.valueOf(value)
            .setScale(places, RoundingMode.HALF_UP)
            .doubleValue();
    }

    public static CompoundTag getOrCreateElementTag(final ItemStack item, final String key) {
        return getOrCreateElementTag(SCInstrumentMod.modTag(item), key);
    }
    public static CompoundTag getOrCreateElementTag(final CompoundTag parent, final String key) {
        return getOrCreateTag(parent, key, CompoundTag.TAG_COMPOUND, CompoundTag::new);
    }

    public static ListTag getOrCreateListTag(final CompoundTag parent, final String key) {
        return getOrCreateTag(parent, key, CompoundTag.TAG_LIST, ListTag::new);
    }

    public static <T extends Tag> T getOrCreateTag(ItemStack item, String key, int type, Supplier<T> orElse) {
        return getOrCreateTag(SCInstrumentMod.modTag(item), key, type, orElse);
    }
    @SuppressWarnings("unchecked")
    public static <T extends Tag> T getOrCreateTag(CompoundTag parent, String key, int type, Supplier<T> orElse) {
        if (parent.contains(key, type))
            return (T) parent.get(key);

        final T tag = orElse.get();
        parent.put(key, tag);
        return tag;
    }

    public static InteractionHand getOffhand(final InteractionHand hand) {
        return (hand == InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }


    public static CompoundTag deepConvertCompound(final CompoundTag compound, final Map<String, String> oldToNewMapper) {
        final CompoundTag result = new CompoundTag();

        compound.getAllKeys().forEach((key) -> {
            final String newKey = oldToNewMapper.get(key);
            if (newKey == null)
                return; // idk why this is even a case..

            if (compound.contains(key, Tag.TAG_COMPOUND))
                result.put(newKey, deepConvertCompound(compound.getCompound(key), oldToNewMapper));
            else if (compound.contains(key, Tag.TAG_LIST))
                result.put(newKey, deepConvertList((ListTag) compound.get(key), oldToNewMapper));
            else
                result.put(newKey, compound.get(key).copy());
        });

        return result;
    }
    /**
     * Converts all compound keys within this list
     */
    public static ListTag deepConvertList(final ListTag list, final Map<String, String> oldToNewMapper) {
        final ListTag result = new ListTag();

        list.forEach((tag) -> {
            if (tag instanceof ListTag lt)
                result.add(deepConvertList(lt, oldToNewMapper));
            else if (tag instanceof CompoundTag ct)
                result.add(deepConvertCompound(ct, oldToNewMapper));
            else
                result.add(tag.copy());
        });

        return result;
    }

    public static void moveTags(final CompoundTag source, final CompoundTag dest, final String key) {
        final Tag value = source.get(key);
        dest.put(key, value);
        source.remove(key);
    }

    public static Optional<ItemStack> getItemInBothHands(final Player player, final Item item) {
        ItemStack result = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (result.is(item))
            return Optional.of(result);

        result = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (result.is(item))
            return Optional.of(result);

        return Optional.empty();
    }
}
