package com.stump.songcraft_instruments.render;

import com.stump.songcraft_instruments.item.MicrophoneStandBlockItem;
import com.stump.songcraft_instruments.model.MicrophoneStandItemModel;

public class MicrophoneStandItemRenderer
        extends AbstractDyeableItemRenderer<MicrophoneStandBlockItem> {

    public MicrophoneStandItemRenderer() {
        super(new MicrophoneStandItemModel());
    }
}