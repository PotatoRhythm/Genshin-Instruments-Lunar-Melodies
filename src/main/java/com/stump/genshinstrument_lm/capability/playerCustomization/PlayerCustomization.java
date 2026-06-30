package com.stump.genshinstrument_lm.capability.playerCustomization;

import com.stump.genshinstrument_lm.particle.ColorSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.ArrayList;
import java.util.List;

@AutoRegisterCapability
public class PlayerCustomization {
    public static final String PARTICLE_COLOR_TAG = "InstrumentParticleColor";
    private List<ColorSet> colorSets = new ArrayList<>();
    private int activeColorSet = 0;

    public int getActiveColorSet() {
        return activeColorSet;
    }

    public void setActiveColorSet(int set) {
        this.activeColorSet = set;
    }

    public List<ColorSet> getColorSets() {
        return colorSets;
    }

    public void setColorSets(List<ColorSet> sets) {
        this.colorSets = sets;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("ActiveSet", activeColorSet);

        ListTag list = new ListTag();

        for (ColorSet set : colorSets) {
            CompoundTag tag = new CompoundTag();

            tag.putString("Name", set.getName());

            int[] colors = set.getColors();
            tag.putInt("Count", colors.length);

            for (int i = 0; i < colors.length; i++) {
                tag.putInt("C" + i, colors[i]);
            }

            list.add(tag);
        }

        nbt.put("ColorSets", list);
    }

    public void loadNBTData(CompoundTag nbt) {
        activeColorSet = nbt.getInt("ActiveSet");
        colorSets.clear();
        ListTag list = nbt.getList("ColorSets", Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);

            String name = tag.getString("Name");
            int count = tag.getInt("Count");

            int[] colors = new int[count];
            for (int c = 0; c < count; c++) {
                colors[c] = tag.getInt("C" + c);
            }

            colorSets.add(new ColorSet(name, colors));
        }

        initDefaults();
    }

    public void initDefaults() {
        if (!colorSets.isEmpty()) return;

        colorSets.add(new ColorSet("Default", new int[] {
                0x162C57, 0x366CD9, 0x00D900, 0xD9D900, 0xD93636, 0xD93687
        }));

        colorSets.add(new ColorSet("Pastel", new int[] {
                0xA0C4FF, 0x9BF6FF, 0xCAFFBF, 0xFDFFB6, 0xFFADAD, 0xFFC7EC
        }));

        colorSets.add(new ColorSet("Monochrome", new int[] {
                0x000000, 0x333333, 0x666666, 0x999999, 0xCCCCCC, 0xFFFFFF
        }));

        colorSets.add(new ColorSet("Ocean", new int[] {
                0x001F3F, 0x005F99, 0x00A6C6, 0x48CAE4, 0x90E0EF, 0xCAF0F8
        }));

        colorSets.add(new ColorSet("Forest", new int[] {
                0x1B4332, 0x2D6A4F, 0x40916C, 0x74C69D, 0x95D5B2, 0xD8F3DC
        }));

        colorSets.add(new ColorSet("Oak", new int[] {
                0x0F0A08, 0x2A1A14, 0x4A2A20, 0x6A4A3A, 0x8A6A52, 0xBFAF9A
        }));

        colorSets.add(new ColorSet("Ember", new int[] {
                0x330000, 0x8B0000, 0xD62828, 0xF77F00, 0xFCBF49, 0xFFF3B0
        }));

        colorSets.add(new ColorSet("Blossom", new int[] {
                0x6D214F, 0xB33771, 0xF78FB3, 0xF8A5C2, 0xFADADD, 0xFFF0F5
        }));

        colorSets.add(new ColorSet("Amethyst", new int[] {
                0x2B193D, 0x5F4B8B, 0x9D79BC, 0xD8B4E2, 0xF7CAD0, 0xFFF1F2
        }));
    }
}
