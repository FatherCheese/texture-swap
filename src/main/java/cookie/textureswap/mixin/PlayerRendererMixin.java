package cookie.textureswap.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.LivingRenderer;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.model.ModelBase;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemArmor;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerRenderer.class, remap = false)
public abstract class PlayerRendererMixin extends LivingRenderer<EntityPlayer> {
	public PlayerRendererMixin(ModelBase model, float shadowSize) {
		super(model, shadowSize);
	}

	@Inject(method = "setArmorModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerRenderer;loadTexture(Ljava/lang/String;)V", shift = At.Shift.AFTER, ordinal = 3))
	private void textureswap_setArmorModel(EntityPlayer entity, int renderPass, float partialTick, CallbackInfoReturnable<Boolean> cir) {
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		ItemStack stack = entity.inventory.armorItemInSlot(3 - renderPass);
		ItemArmor itemArmor = (ItemArmor) stack.getItem();

		if (!mc.texturePackList.selectedPacks.isEmpty() && stack.hasCustomName()) {
			loadTexture(
				String.format(
					"/assets/textureswap/textures/armor/%s_%s_%d.png",
					itemArmor.material.identifier.value,
					stack.getCustomName().replace(" ", "_").toLowerCase(),
					renderPass != 2 ? 1 : 2
				)
			);
		}
	}
}
