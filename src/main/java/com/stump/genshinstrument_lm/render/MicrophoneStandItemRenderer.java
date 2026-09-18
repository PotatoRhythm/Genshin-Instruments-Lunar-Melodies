package com.stump.genshinstrument_lm.render;

import com.stump.genshinstrument_lm.item.MicrophoneStandBlockItem;
import com.stump.genshinstrument_lm.model.MicrophoneStandItemModel;

public class MicrophoneStandItemRenderer
        extends AbstractDyeableItemRenderer<MicrophoneStandBlockItem> {

    public MicrophoneStandItemRenderer() {
        super(new MicrophoneStandItemModel());
    }
}