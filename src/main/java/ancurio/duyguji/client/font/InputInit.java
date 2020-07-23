package ancurio.duyguji.client.font;

import ancurio.duyguji.client.input.api.InputApiInitializer;
import ancurio.duyguji.client.input.api.ShortcodeList;
import ancurio.duyguji.client.input.api.ShortcodeListRegistry;

public class InputInit implements InputApiInitializer {
    public static ShortcodeList list = null;

    @Override
    public void onInitialize(final ShortcodeListRegistry registry) {
        list = registry.register("duyguji-font", "color");

        CommonShortcodes.init();
    }

    public static void updateShortcodes() {
        if (list == null) {
            // input module not present
            return;
        }

        CommonShortcodes.applyToList(list, EmojiFontStorage.getGlyphs());
    }
}
