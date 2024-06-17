package cookie.textureswap.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.stitcher.IconCoordinate;
import net.minecraft.client.render.stitcher.TextureRegistry;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemModelStandard.class, remap = false)
public abstract class ItemModelStandardMixin extends ItemModel {

	@Shadow
	public IconCoordinate icon;

	@Shadow
	@Final
	Minecraft mc;

	public ItemModelStandardMixin(Item item) {
		super(item);
	}

	@Inject(method = "getIcon", at = @At("HEAD"), cancellable = true)
	private void textureswap_swapIcon(Entity entity, ItemStack stack, CallbackInfoReturnable<IconCoordinate> cir) {
		if (stack.hasCustomName()) {
			if (!mc.renderEngine.texturePacks.selectedPacks.isEmpty()) {
			IconCoordinate newIcon = TextureRegistry.getTexture(
				String.format(
					"textureswap:item/%s_%s",
					stack.getItem().getKey().substring("item.".length()).replace(".", "_"),
					stack.getCustomName().replace(" ", "_").toLowerCase()
				)
			);
			cir.setReturnValue(newIcon);
			}
		} else cir.setReturnValue(icon);
	}
}
