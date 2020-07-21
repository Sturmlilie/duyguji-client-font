package ancurio.duyguji.client.font.mixin;

import ancurio.duyguji.client.font.ClientInit;
import ancurio.duyguji.client.font.ext.ExtTextureFont;
import ancurio.duyguji.client.font.ext.ExtTextureFontLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.TextureFont;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextureFont.Loader.class)
public class MixinTextureFontLoader implements ExtTextureFontLoader {
    @Shadow
    @Final
    private Identifier filename;

    int version = -1;
    String name = "[unnamed]";

    @Inject(at = @At("RETURN"), method = "fromJson(Lcom/google/gson/JsonObject;)Lnet/minecraft/client/font/TextureFont$Loader;")
    private static void modifyFromJson(final JsonObject json, final CallbackInfoReturnable<TextureFont.Loader> cir) {
        final TextureFont.Loader loader = cir.getReturnValue();
        final ExtTextureFontLoader extLoader = ExtTextureFontLoader.from(loader);

        try {
            parseExtraFields(json, extLoader);
        } catch (final Exception exc) {
            ClientInit.log("Invalid duyguji fields found in: {}", extLoader.getStreamFilename());
        }
    }

    @Inject(at = @At("RETURN"), method = "load(Lnet/minecraft/resource/ResourceManager;)Lnet/minecraft/client/font/Font;")
    public void modifyLoad(final ResourceManager manager, final CallbackInfoReturnable<Font> cir) {
        if (version == -1) {
            // No Duyguji-specific fields were parsed.
            return;
        }

        final ExtTextureFont ext = ExtTextureFont.from(cir.getReturnValue());
        ext.markAsEmoset();
        ext.setName(name);
    }

    private static void parseExtraFields(final JsonObject json, final ExtTextureFontLoader extLoader) {
        final JsonElement jsonVersion = json.get("duyguji_version");

        if (jsonVersion == null) {
            // No duyguji-relevant fields, must be a vanilla pack
            return;
        }

        final int version = jsonVersion.getAsInt();
        String name = "";
        final JsonElement jsonName = json.get("duyguji_name");

        if (jsonName != null) {
            name = jsonName.getAsString();
        }

        ClientInit.log("Found emoji font \"{}\", version {}", name, version);
        extLoader.setVersion(version);
        extLoader.setName(name);
    }

    // ExtTextureFontLoader
    public void setVersion(final int version) {
        this.version = version;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getStreamFilename() {
        return filename.toString();
    }
}
