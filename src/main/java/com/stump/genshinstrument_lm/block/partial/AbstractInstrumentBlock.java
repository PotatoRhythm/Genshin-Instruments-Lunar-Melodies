package com.stump.genshinstrument_lm.block.partial;

import com.stump.genshinstrument_lm.block.partial.client.IClientArmPoseProvider;
import com.stump.genshinstrument_lm.capability.instrumentOpen.InstrumentOpenProvider;
import com.stump.genshinstrument_lm.client.ModArmPose;
import com.stump.genshinstrument_lm.networking.GIPacketHandler;
import com.stump.genshinstrument_lm.networking.packet.instrument.s2c.NotifyInstrumentOpenPacket;
import com.stump.genshinstrument_lm.networking.packet.instrument.util.InstrumentPacketUtil;
import com.stump.genshinstrument_lm.render.util.InstrumentDyeColors;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Consumer;

public abstract class AbstractInstrumentBlock extends BaseEntityBlock {

    public AbstractInstrumentBlock(Properties pProperties) {
        super(pProperties);

        if (!FMLEnvironment.dist.isDedicatedServer())
            initClientBlockUseAnim((pose) -> clientBlockArmPose = pose);
    }

    
    // Abstract implementations

    /**
     * A server-side event fired when the player has interacted with the instrument.
     * It should send a packet to the given player for opening this instrument's screen.
     */
    protected abstract void onInstrumentOpen(final ServerPlayer player);
    @Override
    public abstract InstrumentBlockEntity newBlockEntity(BlockPos pPos, BlockState pState);

    
    /**
     * An instance of {@link IClientArmPoseProvider} as defined in {@link AbstractInstrumentBlock#initClientBlockUseAnim}
     */
    private Object clientBlockArmPose;
    protected void initClientBlockUseAnim(final Consumer<ArmPose> consumer) {
        consumer.accept(ModArmPose.PLAYING_BLOCK_INSTRUMENT);
    }

    @OnlyIn(Dist.CLIENT)
    public ArmPose getClientBlockArmPose() {
        return (ArmPose)clientBlockArmPose;
    }


    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
                                 BlockHitResult pHit) {
        InteractionResult dyeResult =
                tryApplyDye(pLevel, pPos, pPlayer, pHand);

        if (dyeResult != null)
            return dyeResult;

        if (pLevel.isClientSide)
            return InteractionResult.CONSUME;

        final BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof InstrumentBlockEntity))
            return InteractionResult.FAIL;

        if (InstrumentPacketUtil.sendOpenPacket((ServerPlayer)pPlayer, this::onInstrumentOpen, pPos)) {
            ((InstrumentBlockEntity)be).users.add(pPlayer.getUUID());
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        final BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof InstrumentBlockEntity)) {
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
            return;
        }


        final InstrumentBlockEntity ibe = (InstrumentBlockEntity)be;
        
        for (final Player player : pLevel.players()) {
            ibe.users.forEach((user) -> {
                InstrumentOpenProvider.setClosed(pLevel.getPlayerByUUID(user));
                GIPacketHandler.sendToClient(new NotifyInstrumentOpenPacket(user), (ServerPlayer)player);
            });
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    protected InteractionResult tryApplyDye(Level level, BlockPos pos, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (!(stack.getItem() instanceof DyeItem dye))
            return null;

        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof InstrumentBlockEntity instrument) {
                DyeColor dyeColor = dye.getDyeColor();
                int rgb = InstrumentDyeColors.getColor(dyeColor);

                instrument.setDyeColor(rgb);

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
