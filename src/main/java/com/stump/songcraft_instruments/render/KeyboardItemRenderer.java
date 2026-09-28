package com.stump.songcraft_instruments.render;

import com.stump.songcraft_instruments.item.KeyboardBlockItem;
import com.stump.songcraft_instruments.model.KeyboardItemModel;

public class KeyboardItemRenderer
        extends AbstractDyeableItemRenderer<KeyboardBlockItem> {

    public KeyboardItemRenderer() {
        super(new KeyboardItemModel());
    }
}