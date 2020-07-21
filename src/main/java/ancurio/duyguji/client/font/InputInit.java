package ancurio.duyguji.client.font;

import ancurio.duyguji.client.input.api.InputApiInitializer;
import ancurio.duyguji.client.input.api.ShortcodeKey;
import ancurio.duyguji.client.input.api.ShortcodeKeyProvider;

public class InputInit implements InputApiInitializer {
    public static ShortcodeKey key = null;

    @Override
    public void onInitialize(final ShortcodeKeyProvider provider) {
        key = provider.registerKey("duyguji-font", "color");

        CommonShortcodes.init();
    }

    public static void updateShortcodes() {
        if (key == null) {
            // input module not present
            return;
        }

        CommonShortcodes.applyToKey(key, EmojiFontStorage.getGlyphs());
    }
}
