package com.stump.genshinstrument_lm.client.keyMaps;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.InstrumentScreen;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.grid.GridInstrumentScreen;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.MOD, modid = GInstrumentMod.MODID, value = Dist.CLIENT)
public class InstrumentKeyMappings {
    public static final String CATEGORY = GInstrumentMod.MODID+".keymaps";

    public static final IKeyConflictContext INSTRUMENT_KEY_CONFLICT_CONTEXT = new IKeyConflictContext() {

        @SuppressWarnings("resource")
        @Override
        public boolean isActive() {
            return Minecraft.getInstance().screen instanceof InstrumentScreen;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return this == other;
        }

    };

    public static final Lazy<KeyMapping> VOLUME_UP = Lazy.of(
            () -> new KeyMapping(CATEGORY+".volume_up",
                    INSTRUMENT_KEY_CONFLICT_CONTEXT,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT_BRACKET
                    , CATEGORY)
    );
    public static final Lazy<KeyMapping> VOLUME_DOWN = Lazy.of(
            () -> new KeyMapping(CATEGORY+".volume_down",
                    INSTRUMENT_KEY_CONFLICT_CONTEXT,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_LEFT_BRACKET
                    , CATEGORY)
    );

    public static final Lazy<KeyMapping> TRANSPOSE_UP_MODIFIER = Lazy.of(
        () -> new KeyMapping(CATEGORY+".transpose_up_modifier",
            INSTRUMENT_KEY_CONFLICT_CONTEXT,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT
        , CATEGORY)
    );
    public static final Lazy<KeyMapping> TRANSPOSE_DOWN_MODIFIER = Lazy.of(
        () -> new KeyMapping(CATEGORY+".transpose_down_modifier",
            INSTRUMENT_KEY_CONFLICT_CONTEXT,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT
        , CATEGORY)
    );

    public static final Lazy<KeyMapping>
            INSTRUMENT_TYPE_MODIFIER = Lazy.of(
            () -> new KeyMapping(
                    CATEGORY+".instrument_type_modifier",
                    InstrumentKeyMappings.INSTRUMENT_KEY_CONFLICT_CONTEXT,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT_ALT,
                    CATEGORY
            )
    ),
            RECORD = Lazy.of(
                    () -> new KeyMapping(
                            CATEGORY+".record",
                            InstrumentKeyMappings.INSTRUMENT_KEY_CONFLICT_CONTEXT,
                            InputConstants.Type.KEYSYM,
                            GLFW.GLFW_KEY_GRAVE_ACCENT,
                            CATEGORY
                    )
            )
    ;


    // Key mappings for octave mode
    private static final String OCTAVE_CATEGORY = "genshinstrument_lm.keymaps.octave_mode";
    public static final Lazy<KeyMapping> OCTAVE_DOWN = Lazy.of(
            () -> new KeyMapping(
                    OCTAVE_CATEGORY + ".octave_down", // translation key
                    INSTRUMENT_KEY_CONFLICT_CONTEXT,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_9, // default key
                    OCTAVE_CATEGORY // category
            )
    );
    public static final Lazy<KeyMapping> OCTAVE_UP = Lazy.of(
            () -> new KeyMapping(
                    OCTAVE_CATEGORY + ".octave_up",
                    INSTRUMENT_KEY_CONFLICT_CONTEXT,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_0, // default key
                    OCTAVE_CATEGORY
            )
    );
    public static final Lazy<KeyMapping>
            NOTE_C = Lazy.of(() -> new KeyMapping(OCTAVE_CATEGORY + ".note_c", INSTRUMENT_KEY_CONFLICT_CONTEXT, Type.KEYSYM, GLFW.GLFW_KEY_1, OCTAVE_CATEGORY)),
            NOTE_CS = Lazy.of(() -> new KeyMapping(OCTAVE_CATEGORY + ".note_csharp", INSTRUMENT_KEY_CONFLICT_CONTEXT, Type.KEYSYM, GLFW.GLFW_KEY_F1, OCTAVE_CATEGORY)),
            NOTE_D = Lazy.of(() -> new KeyMapping(OCTAVE_CATEGORY + ".note_d", INSTRUMENT_KEY_CONFLICT_CONTEXT, Type.KEYSYM, GLFW.GLFW_KEY_2, OCTAVE_CATEGORY)),
            NOTE_DS = Lazy.of(() -> new KeyMapping(OCTAVE_CATEGORY + ".note_dsharp", INSTRUMENT_KEY_CONFLICT_CONTEXT, Type.KEYSYM, GLFW.GLFW_KEY_F2, OCTAVE_CATEGORY)),
            NOTE_E = Lazy.of(() -> new KeyMapping(OCTAVE_CATEGORY + ".note_e", INSTRUMENT_KEY_CONFLICT_CONTEXT, Type.KEYSYM, GLFW.GLFW_KEY_3, OCTAVE_CATEGORY)),
            NOTE_F = Lazy.of(() -> new KeyMapping(OCTAVE_CATEGORY + ".note_f", INSTRUMENT_KEY_CONFLICT_CONTEXT, Type.KEYSYM, GLFW.GLFW_KEY_4, OCTAVE_CATEGORY)),
            NOTE_FS = Lazy.of(() -> new KeyMapping(OCTAVE_CATEGORY + ".note_fsharp", INSTRUMENT_KEY_CONFLICT_CONTEXT, Type.KEYSYM, GLFW.GLFW_KEY_F4, OCTAVE_CATEGORY)),
            NOTE_G = Lazy.of(() -> new KeyMapping(OCTAVE_CATEGORY + ".note_g", INSTRUMENT_KEY_CONFLICT_CONTEXT, Type.KEYSYM, GLFW.GLFW_KEY_5, OCTAVE_CATEGORY)),
            NOTE_GS = Lazy.of(() -> new KeyMapping(OCTAVE_CATEGORY + ".note_gsharp", INSTRUMENT_KEY_CONFLICT_CONTEXT, Type.KEYSYM, GLFW.GLFW_KEY_F5, OCTAVE_CATEGORY)),
            NOTE_A = Lazy.of(() -> new KeyMapping(OCTAVE_CATEGORY + ".note_a", INSTRUMENT_KEY_CONFLICT_CONTEXT, Type.KEYSYM, GLFW.GLFW_KEY_6, OCTAVE_CATEGORY)),
            NOTE_AS = Lazy.of(() -> new KeyMapping(OCTAVE_CATEGORY + ".note_asharp", INSTRUMENT_KEY_CONFLICT_CONTEXT, Type.KEYSYM, GLFW.GLFW_KEY_F6, OCTAVE_CATEGORY)),
            NOTE_B = Lazy.of(() -> new KeyMapping(OCTAVE_CATEGORY + ".note_b", INSTRUMENT_KEY_CONFLICT_CONTEXT, Type.KEYSYM, GLFW.GLFW_KEY_7, OCTAVE_CATEGORY)),
            NOTE_HIGH_C = Lazy.of(() -> new KeyMapping(OCTAVE_CATEGORY + ".note_high_c", INSTRUMENT_KEY_CONFLICT_CONTEXT, Type.KEYSYM, GLFW.GLFW_KEY_8, OCTAVE_CATEGORY));
    public static final Map<KeyMapping, Integer> HOTKEY_TO_PITCH = Map.ofEntries(
            Map.entry(NOTE_C.get(), 0),
            Map.entry(NOTE_CS.get(), 1),
            Map.entry(NOTE_D.get(), 2),
            Map.entry(NOTE_DS.get(), 3),
            Map.entry(NOTE_E.get(), 4),
            Map.entry(NOTE_F.get(), 5),
            Map.entry(NOTE_FS.get(), 6),
            Map.entry(NOTE_G.get(), 7),
            Map.entry(NOTE_GS.get(), 8),
            Map.entry(NOTE_A.get(), 9),
            Map.entry(NOTE_AS.get(), 10),
            Map.entry(NOTE_B.get(), 11),
            Map.entry(NOTE_HIGH_C.get(), 12)
    );

    @SubscribeEvent
    public static void registerKeybinds(final RegisterKeyMappingsEvent event) {
        event.register(VOLUME_UP.get());
        event.register(VOLUME_DOWN.get());
        event.register(TRANSPOSE_UP_MODIFIER.get());
        event.register(TRANSPOSE_DOWN_MODIFIER.get());
        event.register(INSTRUMENT_TYPE_MODIFIER.get());
        event.register(RECORD.get());

        event.register(OCTAVE_DOWN.get());
        event.register(OCTAVE_UP.get());
        event.register(NOTE_C.get());
        event.register(NOTE_CS.get());
        event.register(NOTE_D.get());
        event.register(NOTE_DS.get());
        event.register(NOTE_E.get());
        event.register(NOTE_F.get());
        event.register(NOTE_FS.get());
        event.register(NOTE_G.get());
        event.register(NOTE_GS.get());
        event.register(NOTE_A.get());
        event.register(NOTE_AS.get());
        event.register(NOTE_B.get());
        event.register(NOTE_HIGH_C.get());
    }
    

    /* --------------- Builtin Keys --------------- */

    public static final Key[][] GRID_INSTRUMENT_MAPPINGS = createInstrumentMaps(new int[][] {
        {81, 87, 69, 82, 84, 89, 85, 73},
        {65, 83, 68, 70, 71, 72, 74, 75},
        {90, 88, 67, 86, 66, 78, 77, 44}
    });


    // Glorious drum
    public static final GloriousDrumKeys
        DON = new GloriousDrumKeys(83, 75),
        KA = new GloriousDrumKeys(65, 76)
    ;

    @OnlyIn(Dist.CLIENT)
    public static final class GloriousDrumKeys {
        public final Key left, right;

        private GloriousDrumKeys(final int left, final int right) {
            this.left = create(left);
            this.right = create(right);
        }

        public Key getKey(final boolean isRight) {
            return isRight ? right : left;
        }
    }



    /**
     * Creates a grid of keys.
     * used by {@link GridInstrumentScreen} for managing keyboard input.
     * @param keyCodes A 2D array representing a key grid. Each cell should correspond to a note.
     * @return A 2D key array as described in {@code keyCodes}.
     */
    public static Key[][] createInstrumentMaps(final int[][] keyCodes) {
        final int rows = keyCodes[0].length, columns = keyCodes.length;

        final Key[][] result = new Key[columns][rows];
        for (int i = 0; i < columns; i++)
            for (int j = 0; j < rows; j++)
                result[i][j] = create(keyCodes[i][j]);

        return result;
    }

    private static Key create(final int keyCode) {
        return Type.KEYSYM.getOrCreate(keyCode);
    }

}
