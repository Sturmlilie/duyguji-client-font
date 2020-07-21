package ancurio.duyguji.client.font.mixin;

import ancurio.duyguji.client.font.InputInit;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Inject(at = @At("HEAD"), method = "reload()V")
    public void onReload(final CallbackInfo ci) {
        InputInit.updateShortcodes();
    }
}
