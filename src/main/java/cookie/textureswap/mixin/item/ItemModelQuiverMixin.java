package cookie.textureswap.mixin.item;

import cookie.textureswap.ITextureSwapHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModelQuiver;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(value = ItemModelQuiver.class, remap = false)
public abstract class ItemModelQuiverMixin extends ItemModelStandard implements ITextureSwapHelper {
	public ItemModelQuiverMixin(Item item, String namespace) {
		super(item, namespace);
	}


	@Inject(method = "getIcon", at = @At("HEAD"), cancellable = true)
	private void textureswap_getIcon(Entity entity, ItemStack stack, CallbackInfoReturnable<IconCoordinate> cir) {
		ITextureSwapHelper.handleCustomTexture(stack, entity, cir);
	}
}
