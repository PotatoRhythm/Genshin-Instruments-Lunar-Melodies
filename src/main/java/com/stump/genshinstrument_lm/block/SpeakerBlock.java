package com.stump.genshinstrument_lm.block;

import com.stump.genshinstrument_lm.block.blockentity.SpeakerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A block that, once paired to an instrument via the LooperAdapterItem,
 * immediately plays that instrument's notes from its own position.
 */
public class SpeakerBlock extends Block implements EntityBlock {

    public SpeakerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SpeakerBlockEntity(pPos, pState);
    }
}
