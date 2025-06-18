package cookie.textureswap.extra.mixin;

import cookie.textureswap.client.JSONDeserializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.TextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(value = TextureManager.class, remap = false)
public abstract class TextureManagerMixin {

	@Inject(method = "refreshTextures", at = @At("HEAD"))
	private void textureswap_refreshTextures(List<Throwable> errors, CallbackInfo ci) {
		JSONDeserializer.preloadTextures();
	}
}
