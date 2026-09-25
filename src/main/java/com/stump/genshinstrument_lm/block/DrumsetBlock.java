package com.stump.genshinstrument_lm.block;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.block.blockentity.DrumsetBlockEntity;
import com.stump.genshinstrument_lm.block.blockentity.DummyBlockEntity;
import com.stump.genshinstrument_lm.block.partial.MultiblockInstrumentBlock;
import com.stump.genshinstrument_lm.block.partial.InstrumentBlockEntity;
import com.stump.genshinstrument_lm.item.ModItems;
import com.stump.genshinstrument_lm.networking.packet.instrument.util.InstrumentPacketUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DrumsetBlock extends MultiblockInstrumentBlock {

    // Block layout
    private static final BlockPos[] SHAPE = {
            // Controller block is (0, 0, 0)
            new BlockPos(-1, 0, 0),
            new BlockPos( 1, 0, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos( 0, 1, 0),
            new BlockPos( 1, 1, 0)
    };

    // Collision shapes
    private static final VoxelShape FULL = Block.box(0, 0, 0, 16, 16, 16);
    private static final VoxelShape HALF = Block.box(0, 0, 0, 16, 8, 16);

    public DrumsetBlock(Properties props) {
        super(props);
    }

    @Override
    protected BlockPos[] getLayoutOffsets() {
        return SHAPE;
    }

    @Override
    protected Item getDropItem() {
        return ModItems.DRUMSET.get();
    }

    @Override
    protected BlockState dummyState(BlockState controllerState) {
        return ModBlocks.DRUMSET.get().defaultBlockState()
                .setValue(FACING, controllerState.getValue(FACING))
                .setValue(PART, Part.DUMMY);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(PART) == Part.CONTROLLER
                ? RenderShape.ENTITYBLOCK_ANIMATED
                : RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext ctx) {

        if (state.getValue(PART) == Part.CONTROLLER) {
            return FULL;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof DummyBlockEntity dummy)) return FULL;

        BlockPos controller = dummy.getControllerPos();
        if (controller == null) return FULL;

        BlockPos local = pos.subtract(controller);

        return local.getY() == 1 ? HALF : FULL;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext ctx) {
        return getShape(state, level, pos, ctx);
    }

    @Override
    public InstrumentBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == Part.CONTROLLER
                ? new DrumsetBlockEntity(pos, state)
                : new DummyBlockEntity(pos, state);
    }

    @Override
    protected void onInstrumentOpen(ServerPlayer player) {
        InstrumentPacketUtil.sendOpenPacket(player, new ResourceLocation(GInstrumentMod.MODID, "drumset"));
    }
}