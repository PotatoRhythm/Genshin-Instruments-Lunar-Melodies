package com.stump.genshinstrument_lm.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.networking.GIPacketHandler;
import com.stump.genshinstrument_lm.networking.packet.instrument.s2c.S2CColorSetConfirmationPacket;
import com.stump.genshinstrument_lm.util.ParticleShareUtil;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GInstrumentMod.MODID)
public class ColorSetImportCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("gicolorimport")
                    .then(Commands.argument("encoded", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                CommandSourceStack source = ctx.getSource();
                                ServerPlayer player = source.getPlayer();
                                if (player == null) { return 0; }

                                String encoded = StringArgumentType.getString(ctx, "encoded");
                                if (!encoded.startsWith(ParticleShareUtil.PREFIX + ":")) { return 0; }

                                GIPacketHandler.sendToClient(new S2CColorSetConfirmationPacket(encoded), player);

                                return 1;
                            })
                    )
        );
    }
}