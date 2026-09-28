package com.stump.songcraft_instruments.block.partial;

import com.stump.songcraft_instruments.block.blockentity.ModInstrumentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.util.StringRepresentable;

public abstract class DoubleVerticalBlock extends AbstractInstrumentBlock implements IDoubleBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<BlockPart> PART = EnumProperty.create("part", BlockPart.class);

    public DoubleVerticalBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, BlockPart.BOTTOM)
        );
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InstrumentBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ModInstrumentBlockEntity(pos, state);
    }

    // Handle 2-block-tall placement
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide) return;

        final BlockPos topPos = pos.above();

        level.setBlock(topPos,
                state.setValue(PART, BlockPart.TOP),
                3
        );

        level.blockUpdated(pos, Blocks.AIR);
        level.blockUpdated(topPos, Blocks.AIR);
        state.updateNeighbourShapes(level, pos, 3);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (level.isClientSide) return;

        final BlockPos otherBlock = getOtherBlock(state, pos, level);
        if (otherBlock == null) return;

        level.setBlock(otherBlock, Blocks.AIR.defaultBlockState(), 1|2|4);
        level.blockUpdated(pos, Blocks.AIR);
        state.updateNeighbourShapes(level, pos, 1|2|4);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final BlockPos pos = context.getClickedPos();
        final Level level = context.getLevel();
        final BlockPos topPos = pos.above();

        if (level.getBlockState(topPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(topPos)
                && level.getBlockState(pos.below()).canOcclude()
        ) {
            return defaultBlockState()
                    .setValue(PART, BlockPart.BOTTOM)
                    .setValue(FACING, context.getHorizontalDirection().getOpposite());
        }

        return null;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    public static enum BlockPart implements StringRepresentable {
        BOTTOM, TOP;

        @Override
        public String getSerializedName() {
            return toString().toLowerCase();
        }
    }

    @Override
    public BlockPos getOtherBlock(final BlockState state, BlockPos pos, Level level) {
        final BlockPos other = (state.getValue(PART) == BlockPart.BOTTOM) ? pos.above() : pos.below();
        return (!level.getBlockState(other).is(state.getBlock())) ? null : other;
    }
}
