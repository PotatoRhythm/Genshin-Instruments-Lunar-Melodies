package com.stump.songcraft_instruments.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.stump.songcraft_instruments.SCInstrumentMod;
import com.stump.songcraft_instruments.networking.SCPacketHandler;
import com.stump.songcraft_instruments.networking.packet.instrument.s2c.S2CColorSetConfirmationPacket;
import com.stump.songcraft_instruments.util.ParticleShareUtil;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SCInstrumentMod.MODID)
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

                                SCPacketHandler.sendToClient(new S2CColorSetConfirmationPacket(encoded), player);

                                return 1;
                            })
                    )
        );
    }
}