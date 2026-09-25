package com.stump.genshinstrument_lm.block.partial;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class InstrumentBlockEntity extends BlockEntity {

    public Set<UUID> users = new HashSet<UUID>();

    public InstrumentBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    private int dyeColor = 0xFFFFFF;

    public int getDyeColor() {
        return dyeColor;
    }

    public void setDyeColor(int color) {
        dyeColor = color;

        setChanged();

        if (level != null) {
            level.sendBlockUpdated(worldPosition,
                    getBlockState(),
                    getBlockState(),
                    Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putInt("DyeColor", dyeColor);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        dyeColor = tag.getInt("DyeColor");
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    @Override
    public AABB getRenderBoundingBox() {

        // Only expand controller block entities
        if (level != null &&
                getBlockState().getBlock() instanceof MultiblockInstrumentBlock multiblock) {

            return calculateRenderBounds(multiblock);
        }

        return super.getRenderBoundingBox();
    }


    private AABB calculateRenderBounds(MultiblockInstrumentBlock multiblock) {

        BlockPos min = worldPosition;
        BlockPos max = worldPosition;

        for (BlockPos offset : multiblock.getLayoutOffsets()) {

            BlockPos part = worldPosition.offset(
                    multiblock.rotate(
                            getBlockState().getValue(MultiblockInstrumentBlock.FACING),
                            offset
                    )
            );

            min = new BlockPos(
                    Math.min(min.getX(), part.getX()),
                    Math.min(min.getY(), part.getY()),
                    Math.min(min.getZ(), part.getZ())
            );

            max = new BlockPos(
                    Math.max(max.getX(), part.getX()),
                    Math.max(max.getY(), part.getY()),
                    Math.max(max.getZ(), part.getZ())
            );
        }


        return new AABB(min, max.offset(1,1,1));
    }
}
