package com.stump.songcraft_instruments.networking.packet.instrument.util;

import com.stump.songcraft_instruments.client.colorSet.ColorSet;
import com.stump.songcraft_instruments.client.colorSet.ColorSetManager;
import com.stump.songcraft_instruments.client.gui.options.ColorImportConfirmationScreen;
import com.stump.songcraft_instruments.util.ParticleShareUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class ColorSetPacketUtil {

    private ColorSetPacketUtil() {}

    public static void openColorImportScreen(String encoded) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) { return; }

        ColorSet set = ParticleShareUtil.decode(encoded);
        if (set == null) { return; }

        mc.pushGuiLayer(new ColorImportConfirmationScreen(encoded, set));
    }

    public static void addColorSet(String encoded) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) { return;}

        ColorSet set = ParticleShareUtil.decode(encoded);
        if (set == null) { return; }

        var sets = ColorSetManager.getSets();
        sets.add(set);

        ColorSetManager.setActiveSet(sets.size() - 1);
        ColorSetManager.save();

        mc.player.sendSystemMessage(Component.literal("Imported color set: " + set.getName()));
    }
}