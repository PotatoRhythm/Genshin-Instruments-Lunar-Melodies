package com.stump.genshinstrument_lm.block;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.block.blockentity.DummyBlockEntity;
import com.stump.genshinstrument_lm.block.blockentity.MicrophoneStandBlockEntity;
import com.stump.genshinstrument_lm.block.partial.InstrumentBlockEntity;
import com.stump.genshinstrument_lm.block.partial.MultiblockInstrumentBlock;
import com.stump.genshinstrument_lm.item.ModItems;
import com.stump.genshinstrument_lm.networking.packet.instrument.util.InstrumentPacketUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MicrophoneStandBlock extends MultiblockInstrumentBlock {

    private static final BlockPos[] SHAPE = {
            // Controller is (0,0,0)
            new BlockPos(0, 1, 0)
    };

    private static final VoxelShape STAND_BOTTOM =
            Block.box(5, 0, 5, 11, 16, 11);

    private static final VoxelShape STAND_TOP =
            Block.box(5, 0, 5, 11, 9, 11);


    public MicrophoneStandBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected BlockPos[] getLayoutOffsets() {
        return SHAPE;
    }


    @Override
    protected Item getDropItem() {
        return ModItems.MICROPHONE_STAND.get();
    }

    @Override
    protected BlockState dummyState(BlockState controllerState) {
        return ModBlocks.MICROPHONE_STAND.get().defaultBlockState()
                .setValue(FACING, controllerState.getValue(FACING))
                .setValue(PART, Part.DUMMY);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(PART) == Part.CONTROLLER ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {

        return state.getValue(PART) == Part.CONTROLLER ? STAND_BOTTOM : STAND_TOP;
    }


    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return getShape(state, level, pos, ctx);
    }


    @Override
    public InstrumentBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == MultiblockInstrumentBlock.Part.CONTROLLER
                ? new MicrophoneStandBlockEntity(pos, state)
                : new DummyBlockEntity(pos, state);
    }


    @Override
    protected void onInstrumentOpen(ServerPlayer player) {
        InstrumentPacketUtil.sendOpenPacket(player, new ResourceLocation(GInstrumentMod.MODID, "microphone_stand"));
    }
}