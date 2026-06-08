package com.stump.genshinstrument_lm.client.gui.instrument.gw2_drum;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.InstrumentScreen;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.InstrumentThemeLoader;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.NoteButton;
import com.stump.genshinstrument_lm.client.gui.options.partial.InstrumentOptionsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LinearLayout.Orientation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class Gw2DrumScreen extends InstrumentScreen {
    public static final ResourceLocation INSTRUMENT_ID = new ResourceLocation(GInstrumentMod.MODID, "gw2_drum");

    public static final String[] NOTE_LAYOUT = {
        "C", "D", "E", "F", "G",
        "C", "D", "E", "F", "G"
    };

    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }


    /**
     * Maps keycodes to their respected note button
     */
    private final HashMap<Key, NoteButton> notes = new HashMap<>();
    @Override
    public Map<Key, NoteButton> getNoteMap() {
        return notes;
    }

    @Override
    protected InstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new Gw2DrumOptionsScreen(this);
    }

    @Override
    protected void init() {
        initControlBar(height / 2 + 5);

        final LinearLayout rowTop = createRow(1);
        final LinearLayout rowBottom = createRow(0);

        rowTop.arrangeElements();
        rowBottom.arrangeElements();

        rowTop.setPosition((width - rowTop.getWidth()) / 2, (int)(height * 0.6f));
        rowBottom.setPosition((width - rowBottom.getWidth()) / 2,
                rowTop.getY() + rowTop.getHeight() + 10);

        rowTop.arrangeElements();
        rowBottom.arrangeElements();

        rowTop.visitWidgets(this::addRenderableWidget);
        rowBottom.visitWidgets(this::addRenderableWidget);

        notesIterable().forEach(NoteButton::init);

        super.init();
    }

    private LinearLayout createRow(int row) {
        final LinearLayout layout = new LinearLayout(
                0, 0,
                (int)(width/2f),
                getNoteSize(),
                Orientation.HORIZONTAL
        );

        for (int column = 0; column < 5; column++) {
            createButton(layout, row, column);
        }

        return layout;
    }
    private Gw2DrumNoteButton createButton(LinearLayout container, int row, int column) {
        final Gw2DrumNoteButton btn = new Gw2DrumNoteButton(this, row, column);

        container.addChild(btn);
        notes.put(btn.getKey(), btn);

        return btn;
    }


    @Override
    public String[] noteLayout() {
        return NOTE_LAYOUT;
    }

    public static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }

}