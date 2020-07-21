package ancurio.duyguji.client.font.mixin;

import ancurio.duyguji.client.font.EmojiFontStorage;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(at = @At("HEAD"), method = "reloadResources()Ljava/util/concurrent/CompletableFuture;")
    public void onReloadResources(final CallbackInfoReturnable ci) {
        EmojiFontStorage.clear();
    }
}
