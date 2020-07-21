package ancurio.duyguji.client.font.mixin;

import ancurio.duyguji.client.font.EmojiFontStorage;
import ancurio.duyguji.client.font.ext.ExtTextureFont;
import java.util.List;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.RenderableGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontStorage.class)
public class MixinFontStorage {
    @Inject(at = @At("HEAD"), method = "setFonts(Ljava/util/List;)V")
    public void modifySetFonts(List<Font> fontsToAdd, CallbackInfo ci) {
        for (final Font font : fontsToAdd) {
            if (!(font instanceof ExtTextureFont)) {
                continue;
            }

            final ExtTextureFont extFont = ExtTextureFont.from(font);

            if (!extFont.isEmoset()) {
                continue;
            }

            // Intercept any of our own fonts here
            EmojiFontStorage.add(extFont);
        }

        // And remove them so vanilla doesn't get confused
        EmojiFontStorage.removeFromSet(fontsToAdd);
    }

    @Inject(at = @At("HEAD"), method = "getRenderableGlyph(I)Lnet/minecraft/client/font/RenderableGlyph;", cancellable = true)
    public void modifyGetRenderableGlyph(int codepoint, CallbackInfoReturnable cir) {
        if (((codepoint >> 16) == 0)) {
            // Don't source anything in plane 0 from emosets for now
            return;
        }

        final RenderableGlyph glyph = EmojiFontStorage.getGlyph(codepoint);

        if (glyph != null) {
            cir.setReturnValue(glyph);
        }
    }
}
