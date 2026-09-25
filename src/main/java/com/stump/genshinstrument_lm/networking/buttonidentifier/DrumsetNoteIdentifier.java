package com.stump.genshinstrument_lm.networking.buttonidentifier;

import com.stump.genshinstrument_lm.client.gui.instrument.drumset.DrumsetNoteButton;
import com.stump.genshinstrument_lm.client.gui.instrument.gw2_drumset.Gw2DrumsetNoteButton;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DrumsetNoteIdentifier extends NoteButtonIdentifier {

    public final int index;

    @OnlyIn(Dist.CLIENT)
    public DrumsetNoteIdentifier(final DrumsetNoteButton note) {
        index = note.index;
    }
    @OnlyIn(Dist.CLIENT)
    public DrumsetNoteIdentifier(final Gw2DrumsetNoteButton note) {
        index = note.index;
    }

    public DrumsetNoteIdentifier(FriendlyByteBuf buf) {
        index = buf.readInt();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        super.writeToNetwork(buf);
        buf.writeInt(index);
    }

    @Override
    public boolean matches(NoteButtonIdentifier other) {
        return MatchType.forceMatch(other, this::drumMatch);
    }
    private boolean drumMatch(final DrumsetNoteIdentifier other) {
        return index == other.index;
    }
}
