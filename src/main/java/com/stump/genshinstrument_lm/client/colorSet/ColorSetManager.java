package com.stump.genshinstrument_lm.client.colorSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ColorSetManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ColorSetData data;

    private ColorSetManager() {}

    private static Path getFile() {
        return Minecraft.getInstance().gameDirectory.toPath()
                .resolve("config")
                .resolve("genshininstrument_lm_colors.json");
    }

    public static List<ColorSet> getSets() {
        ensureLoaded();
        return data.getSets();
    }

    public static int getActiveSet() {
        ensureLoaded();
        return data.getActiveSet();
    }

    public static void setActiveSet(int index) {
        ensureLoaded();

        int clamped = Mth.clamp(index, 0, data.getSets().size() - 1);
        data.setActiveSet(clamped);
    }

    public static ColorSet getActiveColorSet() {
        ensureLoaded();

        List<ColorSet> sets = data.getSets();
        if (sets.isEmpty()) return null;

        int index = Mth.clamp(data.getActiveSet(), 0, sets.size() - 1);
        return sets.get(index);
    }

    public static void save() {
        ensureLoaded();
        try {
            Path file = getFile();
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save color sets.", e);
        }
    }

    private static void ensureLoaded() {
        if (data != null) return;

        Path file = getFile();

        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file)) {
                data = GSON.fromJson(reader, ColorSetData.class);
            } catch (Exception ignored) {
                data = null;
            }
        }
        if (data == null) {
            data = createDefaults();
            save();
            return;
        }

        if (data.getSets() == null || data.getSets().isEmpty()) {
            data = createDefaults();
            save();
            return;
        }

        data.setActiveSet(
                Mth.clamp(data.getActiveSet(), 0, data.getSets().size() - 1)
        );
    }

    private static ColorSetData createDefaults() {
        ColorSetData data = new ColorSetData();
        List<ColorSet> sets = new ArrayList<>();

        sets.add(new ColorSet("Default", new int[]{
                0x162C57, 0x366CD9, 0x00D900,
                0xD9D900, 0xD93636, 0xD93687
        }));

        sets.add(new ColorSet("Pastel", new int[]{
                0xA0C4FF, 0x9BF6FF, 0xCAFFBF,
                0xFDFFB6, 0xFFADAD, 0xFFC7EC
        }));

        sets.add(new ColorSet("Monochrome", new int[]{
                0x000000, 0x333333, 0x666666,
                0x999999, 0xCCCCCC, 0xFFFFFF
        }));

        sets.add(new ColorSet("Ocean", new int[]{
                0x001F3F, 0x005F99, 0x00A6C6,
                0x48CAE4, 0x90E0EF, 0xCAF0F8
        }));

        sets.add(new ColorSet("Forest", new int[]{
                0x1B4332, 0x2D6A4F, 0x40916C,
                0x74C69D, 0x95D5B2, 0xD8F3DC
        }));

        sets.add(new ColorSet("Oak", new int[]{
                0x0F0A08, 0x2A1A14, 0x4A2A20,
                0x6A4A3A, 0x8A6A52, 0xBFAF9A
        }));

        sets.add(new ColorSet("Ember", new int[]{
                0x330000, 0x8B0000, 0xD62828,
                0xF77F00, 0xFCBF49, 0xFFF3B0
        }));

        sets.add(new ColorSet("Blossom", new int[]{
                0x6D214F, 0xB33771, 0xF78FB3,
                0xF8A5C2, 0xFADADD, 0xFFF0F5
        }));

        sets.add(new ColorSet("Amethyst", new int[]{
                0x2B193D, 0x5F4B8B, 0x9D79BC,
                0xD8B4E2, 0xF7CAD0, 0xFFF1F2
        }));

        data.setSets(sets);
        data.setActiveSet(0);

        return data;
    }


    public static void broadcastShare(ServerPlayer sender, ColorSet set, String encoded) {
        Component line1 = Component.empty()
                .append(sender.getDisplayName())
                .append(Component.literal(" shared a particle color set: \"" + set.getName() + "\"")
                        .withStyle(ChatFormatting.GRAY));

        Component line2 = Component.literal("[Add Color Set]")
                .withStyle(style -> style
                        .withColor(ChatFormatting.DARK_GREEN)
                        .withBold(true)
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/gicolorimport " + encoded
                        ))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.literal("Import this color set")
                        )));

        sender.server.getPlayerList().getPlayers().forEach(player -> {
            player.sendSystemMessage(line1);
            player.sendSystemMessage(line2);
        });
    }
}