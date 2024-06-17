package cookie.textureswap.mixin;

import cookie.textureswap.TextureJsonLoader;
import net.minecraft.client.render.RenderEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = RenderEngine.class, remap = false)
public abstract class RenderEngineMixin {

	@Inject(method = "refreshTextures", at = @At("HEAD"))
	private void textureswap_refreshTextures(List<Throwable> errors, CallbackInfo ci) {
		TextureJsonLoader.loadTexturesFromJson();
	}
}
