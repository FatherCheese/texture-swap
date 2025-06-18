package cookie.textureswap.extra.mixin;

import cookie.textureswap.TextureSwapClient;
import cookie.textureswap.core.TextureEntry;
import cookie.textureswap.core.TextureJSON;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.MobRendererPlayer;
import net.minecraft.client.render.model.ModelBase;
import net.minecraft.client.render.model.ModelBiped;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemArmor;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(value = MobRendererPlayer.class, remap = false)
public abstract class PlayerRendererMixin extends MobRenderer<Player> {

	@Shadow
	private ModelBiped modelBipedMain;

	@Shadow
	@Final
	private ModelBiped modelArmorChestplate;

	@Shadow
	@Final
	private ModelBiped modelArmor;

	public PlayerRendererMixin(ModelBase model, float shadowSize) {
		super(model, shadowSize);
	}

	/**
	 * A mixin method to prepare the new armor texture, based on JSON location.
	 * @param entity Entity wearing armor
	 * @param layer Armor layer (1 or 2)
	 * @param partialTick Partial Tick
	 * @param cir Mixin return.
	 */
	@Inject(method = "prepareArmor(Lnet/minecraft/core/entity/player/Player;IF)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/IArmorItem;getArmorMaterial()Lnet/minecraft/core/item/material/ArmorMaterial;", shift = At.Shift.AFTER, ordinal = 0), cancellable = true)
	private void textureswap_prepareArmor(Player entity, int layer, float partialTick, CallbackInfoReturnable<Boolean> cir) {
		// First, we make a bunch of vars to use.
		// We get the MC client, the armor in the slot, and the armor.
		Minecraft mc = Minecraft.getMinecraft();
		ItemStack stack = entity.inventory.armorItemInSlot(3 - layer);
		ItemArmor armor = (ItemArmor) stack.getItem();

		// Now we check a bunch of if statements! First we check for
		// a texture pack. Then we check if the armor material is null.
		// Now we check if it has a custom name, then finally, we check
		// the texture JSON and make a list of its entries.
		if (mc.textureManager.texturePacks.selectedPacks.isEmpty()) return;
		if (armor.getArmorMaterial() == null) return;
		if (!stack.hasCustomName()) return;
		if (TextureJSON.getInstance().getEntries().isEmpty()) return;
		List<TextureEntry> textureEntryFiles = TextureJSON.getInstance().getEntries();

		// If the list is empty, we return to use normal armor textures.
		if (textureEntryFiles.isEmpty()) return;

		try {
			// Now we try to get the armor path! If it has a custom name, we
			// get an armor path.
			String armorTexture = null;
			for (TextureEntry entry : textureEntryFiles) {
				for (String itemName : entry.getTextures().keySet()) {
					if (itemName.equalsIgnoreCase(stack.getCustomName())) {
						armorTexture = entry.getItem().getArmor_texture();;
						break;
					}
				}

				// If it IS NOT null, we break the loop since we found it.
				if (armorTexture != null) break;
			}

			// Now check if the armor path is empty or null. If either is
			//  true, we return again. Otherwise, bind the texture.
			if (armorTexture == null || armorTexture.isEmpty()) return;
			bindTexture(String.format("/assets/textureswap/textures/armor/%s_%d.png",
				armorTexture,
				layer != 2 ? 1 : 2));

			ModelBiped modelBiped = layer != 2 ? modelArmorChestplate : modelArmor;
			modelBiped.head.visible = layer == 0;
			modelBiped.hair.visible = layer == 0;
			modelBiped.body.visible = layer == 1 || layer == 2;
			modelBiped.armRight.visible = layer == 1;
			modelBiped.armLeft.visible = layer == 1;
			modelBiped.legRight.visible = layer == 2 || layer == 3;
			modelBiped.legLeft.visible = layer == 2 || layer == 3;
			setArmorModel(modelBiped);
			cir.setReturnValue(true);
		} catch (RuntimeException e) {
			TextureSwapClient.LOGGER.error("Error loading the armor texture for {}.\n{}", stack.getCustomName(), e.getLocalizedMessage());
		}

		cir.cancel();
	}
}
