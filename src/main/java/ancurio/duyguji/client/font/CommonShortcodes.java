package ancurio.duyguji.client.font;

import ancurio.duyguji.client.input.api.Shortcode;
import ancurio.duyguji.client.input.api.ShortcodeList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.fabricmc.loader.launch.common.FabricLauncher;
import net.fabricmc.loader.launch.common.FabricLauncherBase;

import java.util.ArrayList;

public class CommonShortcodes {
    private static final String SHORTCODE_LOCATION = "assets/duyguji/font/shortcodes.txt";
    private static Int2ObjectMap<Shortcode> shortcodes;

    public static void init() {
        ClientInit.log("Initializing shortcodes..");

        final InputStream stream = FabricLauncherBase.getLauncher().getResourceAsStream(SHORTCODE_LOCATION);
        final InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        final BufferedReader bufferedReader = new BufferedReader(streamReader);

        shortcodes = new Int2ObjectOpenHashMap<Shortcode>();

        Shortcode.readPairList(bufferedReader, '/',
            (symbol, code) -> shortcodes.put(Character.codePointAt(symbol, 0), new Shortcode(symbol, code)),
            ClientInit.commonLogger);

        ClientInit.log("done.");
    }

    public static void applyToList(final ShortcodeList list, final IntSet glyphs) {
        list.beginUpdate();
        int addedCount = 0;

        for (final int cp : glyphs) {
            final Shortcode code = shortcodes.get(cp);

            if (code != null) {
                list.putEntry(code.symbol, code.code);
                addedCount++;
            }
        }

        list.endUpdate();
        ClientInit.log("Applied {} shortcodes.", addedCount);
    }
}
