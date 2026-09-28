package com.stump.songcraft_instruments.client.gui.instrument;

import com.stump.songcraft_instruments.SCInstrumentMod;
import com.stump.songcraft_instruments.client.keyMaps.InstrumentKeyMappings; //hmm
import com.stump.songcraft_instruments.mixins.required.ScreenAccessor;
import com.stump.songcraft_instruments.networking.SCPacketHandler;
import com.stump.songcraft_instruments.networking.packet.DoesLooperExistPacket;
import com.stump.songcraft_instruments.networking.packet.LooperRecordStatePacket;
import com.stump.songcraft_instruments.util.LooperUtil;
import com.stump.songcraft_instruments.capability.instrumentOpen.InstrumentOpenProvider;
import com.stump.songcraft_instruments.client.gui.instrument.partial.InstrumentScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.FORGE, modid = SCInstrumentMod.MODID, value = Dist.CLIENT)
public class LooperOverlayInjector {
    private static final int REC_BTN_WIDTH = 120;

    private static InstrumentScreen screen = null;
    private static boolean isRecording = false;
    public static Button recordBtn;

    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void onScreenInit(final ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof InstrumentScreen instrumentScreen))
            return;

        final Player player = Minecraft.getInstance().player;

        if (InstrumentOpenProvider.isItem(player)) {
            final InteractionHand hand = InstrumentOpenProvider.getHand(player);
            final ItemStack instrumentItem = player.getItemInHand(hand);

            if (!LooperUtil.hasLooperTag(instrumentItem))
                return;

            SCPacketHandler.sendToServer(new DoesLooperExistPacket(hand));
        } else {
            final BlockPos instrumentBlockPos = InstrumentOpenProvider.getBlockPos(player);
            final BlockEntity instrumentBE = player.level().getBlockEntity(instrumentBlockPos);

            if (!LooperUtil.hasLooperTag(instrumentBE))
                return;

            SCPacketHandler.sendToServer(new DoesLooperExistPacket());
        }

        LooperOverlayInjector.screen = instrumentScreen;

        event.addListener(
            recordBtn = Button.builder(
                    appendRecordKeyHint(Component.translatable("button.songcraft_instruments.record")),
                    LooperOverlayInjector::onRecordPress
                )
                .width(REC_BTN_WIDTH)
                .pos((instrumentScreen.width - REC_BTN_WIDTH) / 2, 5)
                .build()
        );
    }
    public static void handleLooperRemoved() {
        removeRecordButton();
        screen = null;
    }

    private static MutableComponent appendRecordKeyHint(final MutableComponent component) {
        return component
            .append(" (")
            .append(InstrumentKeyMappings.RECORD.get().getKey().getDisplayName())
            .append(")");
    }

    @SubscribeEvent
    public static void onKeyboardPress(final ScreenEvent.KeyPressed.Pre event) {
        if (InstrumentKeyMappings.RECORD.get().matches(event.getKeyCode(), event.getScanCode())) {

            if (recordBtn != null) {
                recordBtn.playDownSound(Minecraft.getInstance().getSoundManager());
                recordBtn.onPress();
            }

        }
    }

    @SubscribeEvent
    public static void onScreenClose(final ScreenEvent.Closing event) {
        if (event.getScreen() != screen)
            return;

        isRecording = false;
        LooperOverlayInjector.screen = null;
    }

    @SuppressWarnings("resource")
    private static void onRecordPress(final Button btn) {
        final LocalPlayer player = Minecraft.getInstance().player;

        final boolean isItem = InstrumentOpenProvider.isItem(player);
        final InteractionHand hand = isItem ?
            InstrumentOpenProvider.getHand(Minecraft.getInstance().player)
            : null;

        if (isRecording) {
            removeRecordButton();
            screen = null;
        } else
            btn.setMessage(appendRecordKeyHint(Component.translatable("button.songcraft_instruments.stop")));

        isRecording = !isRecording;
        SCPacketHandler.sendToServer(new LooperRecordStatePacket(isRecording, hand));
    }

//    private static BlockEntity getIBE(final Player player) {
//        final BlockPos instrumentPos = InstrumentOpenProvider.getBlockPos(player);
//
//        return (instrumentPos == null) ? null
//            : player.level().getBlockEntity(instrumentPos);
//    }


    public static void removeRecordButton() {
        if (screen == null)
            return;

        ((ScreenAccessor)screen).invokeRemoveWidget(recordBtn);
        recordBtn = null;
    }
}
