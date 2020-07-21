package ancurio.duyguji.client.font.mixin;

import ancurio.duyguji.client.font.ext.ExtTextureFont;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.font.TextureFont;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextureFont.class)
public abstract class MixinTextureFont implements ExtTextureFont {
    private boolean isEmoset = false;
    private String name = "[unnamed]";

    // ExtTextureFont
    public void markAsEmoset() {
        isEmoset = true;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isEmoset() {
        return isEmoset;
    }

    public String getName() {
        return name;
    }

    @Shadow
    public abstract IntSet method_27442();
}
