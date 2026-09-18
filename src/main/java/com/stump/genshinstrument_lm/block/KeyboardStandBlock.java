package com.stump.genshinstrument_lm.block;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.block.blockentity.ModInstrumentBlockEntity;
import com.stump.genshinstrument_lm.item.ModItems;
import com.stump.genshinstrument_lm.block.partial.AbstractInstrumentBlock;
import com.stump.genshinstrument_lm.block.partial.InstrumentBlockEntity;
import com.stump.genshinstrument_lm.networking.packet.instrument.util.InstrumentPacketUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.stump.genshinstrument_lm.block.blockentity.KeyboardStandBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public class KeyboardStandBlock extends AbstractInstrumentBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty HAS_KEYBOARD = BooleanProperty.create("has_keyboard");


    public static final VoxelShape BASE_SHAPE =
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);

    private static final VoxelShape KEYBOARD_NS =
            Shapes.or(
                    Block.box(0.0D, 9.0D, 4.0D, 15.65D, 13.4D, 12.8D),
                    Block.box(0.3D, 9.0D, 4.0D, 16.0D, 13.4D, 12.8D)
            );

    private static final VoxelShape KEYBOARD_EW =
            Shapes.or(
                    Block.box(3.5D, 9.0D, 0.0D, 12.0D, 13.4D, 15.65D),
                    Block.box(3.5D, 9.0D, 0.3D, 12.0D, 13.4D, 16.0D)
            );

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (!pState.getValue(HAS_KEYBOARD))
            return BASE_SHAPE;

        Direction facing = pState.getValue(FACING);

        return facing == Direction.NORTH || facing == Direction.SOUTH ? KEYBOARD_NS : KEYBOARD_EW;
    }

    public KeyboardStandBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(HAS_KEYBOARD, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {

        ItemStack stack = player.getItemInHand(hand);

        InteractionResult dyeResult = tryApplyDye(level, pos, player, hand);

        if (dyeResult != null)
            return dyeResult;

        // place a keyboard onto the stand
        if (!state.getValue(HAS_KEYBOARD) && stack.is(ModItems.KEYBOARD.get())) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);

                if (be instanceof KeyboardStandBlockEntity stand) {
                    CompoundTag tag = stack.getTag();
                    if (tag != null && tag.contains("DyeColor")) {
                        stand.setDyeColor(tag.getInt("DyeColor"));
                    } else {
                        stand.setDyeColor(0xFFFFFF);
                    }
                    level.setBlock(pos, state.setValue(HAS_KEYBOARD, true), 3);

                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // empty stand
        if (!state.getValue(HAS_KEYBOARD)) {
            return InteractionResult.FAIL;
        }

        // keyboard interaction
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos,
            Player player, boolean willHarvest, FluidState fluid) {

        if (!level.isClientSide && state.getValue(HAS_KEYBOARD) && !player.isCreative()) {

            ItemStack keyboard = new ItemStack(ModItems.KEYBOARD.get());
            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof KeyboardStandBlockEntity stand) {
                keyboard.getOrCreateTag().putInt("DyeColor", stand.getDyeColor());
            }

            level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), keyboard));
        }

        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }


    @Override
    protected void onInstrumentOpen(ServerPlayer player) {
        InstrumentPacketUtil.sendOpenPacket(player, new ResourceLocation(GInstrumentMod.MODID, "keyboard"));
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public InstrumentBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KeyboardStandBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, HAS_KEYBOARD);
    }

}