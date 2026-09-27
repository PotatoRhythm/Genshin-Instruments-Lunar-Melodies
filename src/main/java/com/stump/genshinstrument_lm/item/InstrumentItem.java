package com.stump.genshinstrument_lm.item;

import com.stump.genshinstrument_lm.block.SpeakerBlock;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.InstrumentScreen;
import com.stump.genshinstrument_lm.item.clientExtensions.InstrumentItemClientExt;
import com.stump.genshinstrument_lm.networking.OpenInstrumentPacketSender;
import com.stump.genshinstrument_lm.networking.packet.instrument.util.InstrumentPacketUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * An item responsible for opening an {@link InstrumentScreen}.
 */
public class InstrumentItem extends Item {

    protected final OpenInstrumentPacketSender onOpenRequest;
    /**
     * @param onOpenRequest A server-side event fired when the player has requested to interact
     * with the instrument.
     * It should send a packet to the given player for opening this instrument's screen.
     */
    public InstrumentItem(final OpenInstrumentPacketSender onOpenRequest) {
        this(onOpenRequest, new Properties());
    }
    /**
     * @param onOpenRequest A server-side event fired when the player has requested to interact
     * with the instrument.
     * It should send a packet to the given player for opening this instrument's screen.
     * @param properties The properties of this instrument item. {@link Properties#stacksTo stack size}
     * will always be set to 1.
     */
    public InstrumentItem(final OpenInstrumentPacketSender onOpenRequest, final Properties properties) {
        super(properties.stacksTo(1));
        this.onOpenRequest = onOpenRequest;
    }
    

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        final ItemStack item = pPlayer.getItemInHand(pUsedHand);

        if (pLevel.isClientSide)
            return InteractionResultHolder.success(item);

        if (InstrumentPacketUtil.sendOpenPacket((ServerPlayer)pPlayer, pUsedHand, onOpenRequest)) {
            return InteractionResultHolder.success(item);
        } else {
            return InteractionResultHolder.fail(item);
        }
    }

    /**
     * right-clicking a speaker pairs this instrument to it, and shift + right-clicking unpairs it.
     * handled here rather than in the speaker's own interaction, as this runs first and regardless of sneaking.
     */
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        final Player player = context.getPlayer();
        if ((player == null) || !(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof SpeakerBlock))
            return InteractionResult.PASS;

        return SpeakerBlock.onHeldInstrumentUse(context.getLevel(), context.getClickedPos(), player, stack);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.CUSTOM;
    }
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new InstrumentItemClientExt());
    }
    
}
