package ancurio.duyguji.client.font.ext;

import net.minecraft.client.font.Font;

public interface ExtTextureFont extends Font {
    static ExtTextureFont from(final Font self) {
        assert(self instanceof ExtTextureFont);
        return (ExtTextureFont) self;
    }

    void markAsEmoset();
    void setName(final String name);

    boolean isEmoset();
    String getName();
}
