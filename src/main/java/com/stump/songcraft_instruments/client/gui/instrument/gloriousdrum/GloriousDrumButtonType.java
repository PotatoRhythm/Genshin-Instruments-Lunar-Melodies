package com.stump.songcraft_instruments.client.gui.instrument.gloriousdrum;

import com.stump.songcraft_instruments.client.gui.instrument.partial.note.label.INoteLabel;
import com.stump.songcraft_instruments.client.keyMaps.InstrumentKeyMappings;
import com.stump.songcraft_instruments.client.keyMaps.InstrumentKeyMappings.GloriousDrumKeys;
import com.stump.songcraft_instruments.sound.SCSounds;
import com.stump.songcraft_instruments.sound.NoteSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// This class is also used by the server as an identifier for the drum
public enum GloriousDrumButtonType {
    DON(SCSounds.GLORIOUS_DRUM[0], "glorious_drum.don"),
    KA(SCSounds.GLORIOUS_DRUM[1], "glorious_drum.ka");

    private final String transKey;
    private final NoteSound sound;

    GloriousDrumButtonType(NoteSound sound, String transKey) {
        this.sound = sound;
        this.transKey = INoteLabel.TRANSLATABLE_PATH + transKey;
    }

    public NoteSound getSound() {
        return sound;
    }
    public String getTransKey() {
        return transKey;
    }


    // Seperated for server compatibility
    @OnlyIn(Dist.CLIENT)
    public GloriousDrumKeys getKeys() {
        return (this == DON) ? InstrumentKeyMappings.DON : InstrumentKeyMappings.KA;
    }

}