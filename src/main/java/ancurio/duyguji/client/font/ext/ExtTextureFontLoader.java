package ancurio.duyguji.client.font.ext;

import net.minecraft.client.font.TextureFont;

public interface ExtTextureFontLoader {
    static ExtTextureFontLoader from(final TextureFont.Loader self) {
        assert(self instanceof ExtTextureFontLoader);
        return (ExtTextureFontLoader) self;
    }

    void setVersion(final int version);
    void setName(final String name);

    // For debug purposes
    String getStreamFilename();
}
