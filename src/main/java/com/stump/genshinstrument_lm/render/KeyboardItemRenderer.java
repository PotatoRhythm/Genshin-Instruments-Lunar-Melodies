package com.stump.genshinstrument_lm.render;

import com.stump.genshinstrument_lm.item.KeyboardBlockItem;
import com.stump.genshinstrument_lm.model.KeyboardItemModel;

public class KeyboardItemRenderer
        extends AbstractDyeableItemRenderer<KeyboardBlockItem> {

    public KeyboardItemRenderer() {
        super(new KeyboardItemModel());
    }
}