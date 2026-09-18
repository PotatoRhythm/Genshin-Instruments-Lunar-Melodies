package com.stump.genshinstrument_lm.client.gui.instrument.partial.note.held;

import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.NoteButton;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.render.NoteRing;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.animation.RingAnimationController;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A note ring designed for use with a {@link IHoldableNoteButton}.
 * If it is a consecutive ring, will shade it slightly.
 */
@OnlyIn(Dist.CLIENT)
public class HeldNoteRing extends NoteRing {
    final boolean isConsecutive;
    public HeldNoteRing(NoteButton note, boolean isForeign, boolean isConsecutive) {
        super(note, isForeign);
        this.isConsecutive = isConsecutive;
    }

    @Override
    public void playAnim() {
        final float alpha;
        if (isForeign && isConsecutive)
            alpha = -.2f;
        else if (isConsecutive)
            alpha = -.3f;
        else if (isForeign)
            alpha = -.4f;
        else
            alpha = RingAnimationController.INIT_ALPHA;

        ringAnimation.play(alpha);
    }

}
