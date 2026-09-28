package com.stump.songcraft_instruments.item;

import com.stump.songcraft_instruments.networking.SCPacketHandler;
import com.stump.songcraft_instruments.networking.packet.OpenNoteBlockInstrumentPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class NoteBlockInstrumentItem extends InstrumentItem {
    public static final String NOTEBLOCK_INSTRUMENT_SUFFIX = "_note_block_instrument";

    public final NoteBlockInstrument instrument;
    public NoteBlockInstrumentItem(NoteBlockInstrument instrument) {
        super((player) -> SCPacketHandler.sendToClient(
            new OpenNoteBlockInstrumentPacket(instrument), player
        ));

        this.instrument = instrument;
    }
    

    @Override
    public Component getName(ItemStack pStack) {
        return Component.translatable("item.songcraft_instruments.note_block_instrument", getInstrumentName());
    }

    private String getInstrumentName() {
        final String[] words = instrument.getSerializedName().split("_");

        final StringBuilder result = new StringBuilder();
        for (final String word : words) {
            result.append(Character.toUpperCase(word.charAt(0)));
            result.append(word.substring(1));
            result.append(" ");
        }

        return result.toString().trim();
    }

    public static String getId(final NoteBlockInstrument instrument) {
        return instrument.getSerializedName() + NOTEBLOCK_INSTRUMENT_SUFFIX;
    }

}
