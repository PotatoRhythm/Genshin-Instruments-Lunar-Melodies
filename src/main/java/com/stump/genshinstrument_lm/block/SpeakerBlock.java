package com.stump.genshinstrument_lm.block;

import com.stump.genshinstrument_lm.block.blockentity.ModBlockEntities;
import com.stump.genshinstrument_lm.block.blockentity.SpeakerBlockEntity;
import com.stump.genshinstrument_lm.item.InstrumentItem;
import com.stump.genshinstrument_lm.util.SpeakerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

/**
 * once a speaker is paired to an instrument, the speaker immediately plays that instrument's notes from its own position.
 * Block instruments are paired via the SpeakerCableItem, while held instruments are paired
 * by right-clicking the speaker with the instrument in hand, and unpaired by shift + right-clicking it.
 */
public class SpeakerBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    /**
     * whether at least one instrument is paired to this speaker; switches the front texture.
     * managed by SpeakerBlockEntity's pair count.
     */
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public SpeakerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(CONNECTED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, CONNECTED);
    }
    /**
     * places the speaker with its front facing the player
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    // keep the front pointing the right way when structures are rotated/mirrored
    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }
    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof SpeakerBlockEntity sbe)
            sbe.resetPairings();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SpeakerBlockEntity(pPos, pState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
            BlockEntityType<T> pBlockEntityType) {

        return (!pLevel.isClientSide && pBlockEntityType == ModBlockEntities.SPEAKER.get())
            ? (level, pos, state, be) -> ((SpeakerBlockEntity)(be)).tick(level)
            : null;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {
        final ItemStack heldStack = pPlayer.getItemInHand(pHand);
        if (!(heldStack.getItem() instanceof InstrumentItem))
            return InteractionResult.PASS;

        // Consume on the client too, so the instrument's screen doesn't open
        if (pLevel.isClientSide)
            return InteractionResult.SUCCESS;

        if (!(pLevel.getBlockEntity(pPos) instanceof SpeakerBlockEntity sbe))
            return InteractionResult.FAIL;

        // shift + right-click unpairs this speaker, right-click pairs it
        if (pPlayer.isShiftKeyDown()) {
            final boolean removed = SpeakerUtil.unpair(pLevel, heldStack, sbe);
            SpeakerUtil.sendUnpairMessage(pPlayer, removed, SpeakerUtil.speakerCount(pLevel, heldStack), sbe);
        } else {
            final SpeakerUtil.PairResult result = SpeakerUtil.pair(pLevel, heldStack, sbe);
            SpeakerUtil.sendPairMessage(pPlayer, result, SpeakerUtil.speakerCount(pLevel, heldStack));
        }

        return InteractionResult.SUCCESS;
    }
}
