package cookie.textureswap.mixin;

import cookie.textureswap.TextureSwapClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.MobRendererPlayer;
import net.minecraft.client.render.model.ModelBase;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemArmor;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(value = MobRendererPlayer.class, remap = false)
public abstract class PlayerRendererMixin extends MobRenderer<Player> {

	public PlayerRendererMixin(ModelBase model, float shadowSize) {
		super(model, shadowSize);
	}

	@Inject(method = "prepareArmor(Lnet/minecraft/core/entity/player/Player;IF)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/IArmorItem;getArmorMaterial()Lnet/minecraft/core/item/material/ArmorMaterial;", shift = At.Shift.AFTER, ordinal = 0), cancellable = true)
	private void textureswap_prepareArmor(Player entity, int layer, float partialTick, CallbackInfoReturnable<Boolean> cir) {
		Minecraft mc = Minecraft.getMinecraft();
		ItemStack stack = entity.inventory.armorItemInSlot(3 - layer);
		ItemArmor armor = (ItemArmor) stack.getItem();

		if (mc.textureManager.texturePacks.selectedPacks.isEmpty()) return;
		if (armor.getArmorMaterial() == null) return;
		if (!stack.hasCustomName()) return;

		try {
			bindTexture(String.format("/assets/textureswap/textures/armor/%s_%s_%d.png",
				armor.getArmorMaterial().identifier.namespace(),
				armor.getArmorMaterial().identifier.value(),
				layer != 2 ? 1 : 2));
		} catch (RuntimeException e) {
			TextureSwapClient.LOGGER.error("Error loading the armor texture for {}.", stack.getCustomName(), e);
		}

		cir.cancel();
	}
}
