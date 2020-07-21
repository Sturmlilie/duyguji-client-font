package ancurio.duyguji.client.font;

import ancurio.duyguji.client.font.ext.ExtTextureFont;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.RenderableGlyph;

public class EmojiFontStorage {
    private static Collection<ExtTextureFont> fonts = new ArrayList<ExtTextureFont>();

    public static void clear() {
        ClientInit.log("Clearing emoji font storage");
        fonts.clear();
    }

    // Sorting eventually needs to happen here; probably via the set name at first
    public static void add(final ExtTextureFont font) {
        fonts.add(font);
    }

    public static RenderableGlyph getGlyph(int codePoint) {
        for (final Font font : fonts) {
            final RenderableGlyph glyph = font.getGlyph(codePoint);

            if (glyph != null) {
                return glyph;
            }
        }

        return null;
    }

    public static IntSet getGlyphs() {
        final IntSet codepoints = new IntOpenHashSet();

        for (final ExtTextureFont font : fonts) {
            codepoints.addAll(font.method_27442());
        }

        return codepoints;
    }

    public static void removeFromSet(final Collection<Font> set) {
        for (final Font font : fonts) {
            set.remove(font);
        }
    }
}
