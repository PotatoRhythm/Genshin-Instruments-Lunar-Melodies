package com.stump.genshinstrument_lm.render;

import com.stump.genshinstrument_lm.item.DrumsetBlockItem;
import com.stump.genshinstrument_lm.model.DrumsetItemModel;
import com.stump.genshinstrument_lm.model.DrumsetModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DrumsetItemRenderer
        extends AbstractDyeableItemRenderer<DrumsetBlockItem> {

    public DrumsetItemRenderer() {
        super(new DrumsetItemModel());
    }
}