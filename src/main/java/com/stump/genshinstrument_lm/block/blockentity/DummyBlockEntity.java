package com.stump.genshinstrument_lm.block.blockentity;

import com.stump.genshinstrument_lm.block.partial.InstrumentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class DummyBlockEntity extends InstrumentBlockEntity {

    private BlockPos controllerPos;

    public DummyBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUMMY.get(), pos, state);
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public void setControllerPos(BlockPos pos) {
        controllerPos = pos;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (controllerPos != null) {
            tag.putLong("Controller", controllerPos.asLong());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("Controller")) {
            controllerPos = BlockPos.of(tag.getLong("Controller"));
        }
    }
}