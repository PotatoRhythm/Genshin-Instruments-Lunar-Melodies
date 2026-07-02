package com.stump.genshinstrument_lm.event;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.client.colorSet.ColorSetManager;
import com.stump.genshinstrument_lm.util.ParticleShareUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GInstrumentMod.MODID)
public class ColorSharedEvent {

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        String msg = event.getMessage().getString();
        ServerPlayer sender = event.getPlayer();

        if (!msg.startsWith(ParticleShareUtil.PREFIX + ":")) { return; }

        event.setCanceled(true);

        var set = ParticleShareUtil.decode(msg);
        if (set == null) { return; }

        ColorSetManager.broadcastShare(sender, set, msg);
    }
}