package ancurio.duyguji.client.font;

import ancurio.duyguji.client.input.api.Shortcode;
import ancurio.duyguji.client.input.api.ShortcodeKey;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.InputStream;
import net.fabricmc.loader.launch.common.FabricLauncher;
import net.fabricmc.loader.launch.common.FabricLauncherBase;

import java.util.ArrayList;

public class CommonShortcodes {
    private static final String SHORTCODE_LOCATION = "assets/duyguji/font/shortcodes.txt";
    private static Int2ObjectMap<Shortcode> shortcodes;

    public static void init() {
        ClientInit.log("Initializing shortcodes..");

        InputStream stream = FabricLauncherBase.getLauncher().getResourceAsStream(SHORTCODE_LOCATION);

        shortcodes = new Int2ObjectOpenHashMap<Shortcode>();
        Shortcode.readPairList(stream, '/',
            shortcode -> shortcodes.put(Character.codePointAt(shortcode.symbol, 0), shortcode),
            ClientInit.commonLogger);

        ClientInit.log("done.");
    }

    public static void applyToKey(final ShortcodeKey key, final IntSet glyphs) {
        key.beginUpdate();
        int addedCount = 0;

        for (final int cp : glyphs) {
            final Shortcode code = shortcodes.get(cp);

            if (code != null) {
                key.putEntry(code);
                addedCount++;
            }
        }

        key.endUpdate();
        ClientInit.log("Applied {} shortcodes.", addedCount);
    }
}
