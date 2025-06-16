package cookie.textureswap;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.item.model.ItemModelPaintbrush;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.*;
import net.minecraft.core.util.helper.DyeColor;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A base interface handling custom textures, made for use in Mixins.
 */
@Environment(EnvType.CLIENT)
public interface ITextureSwapHelper {
	/**
	 * A method used by mixins to handle custom textures.
	 * @param stack ItemStack
	 * @param entity Entity (Player)
	 * @param cir Returnable state.
	 */
	static void handleCustomTexture(ItemStack stack, Entity entity, CallbackInfoReturnable<IconCoordinate> cir) {
		// First, we start by getting MC. This is fine since we're
		// a client class! Then we check for if the texture pack is null
		// or if the stack is null. (Also if it has a name!)
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.textureManager.texturePacks.selectedPacks.isEmpty()) return;
		if (stack == null || !stack.hasCustomName()) return;

		// If those pass, we get a new icon using the method below.
		// If the new icon IS NOT null, return it for the mixins.
		// Otherwise, log the error for the missing item texture.
		try {
			IconCoordinate newIcon = getNewIcon(stack, entity);
			if (newIcon != null) {
				cir.setReturnValue(newIcon);
			}
		} catch (RuntimeException e) {
			TextureSwapClient.LOGGER.error("Error loading the item texture for {}.", stack.getCustomName(), e);
		}
	}

	/**
	 * A method handling getting the new icon
	 * @param stack ItemStack
	 * @param entity Entity holding the item
	 * @return Returns a new IconCoordinate.
	 */
	static IconCoordinate getNewIcon(ItemStack stack, Entity entity) {
		// First, we make two variables; A IconCoordinate, and the texture path.
		// The texture pack is defaulted to "textureswap:item/"
		IconCoordinate newIcon;
		String texturePath = "textureswap:item/";

		// Now we go through each entry file in the JSON. If the JSON item name is
		// equal to the stack's custom name, we will load that and get the texture
		// path from it. If the path is invalid, we go back to the default path.
		for (TextureEntry entry : TextureJsonLoader.textureEntryFiles) {
			if (entry.itemName.equalsIgnoreCase(stack.getCustomName())) {
				if (entry.texturePath != null &&
					!entry.texturePath.trim().isEmpty() &&
					entry.texturePath.matches("^[a-zA-Z0-9_:/.]+$")) {
					texturePath = entry.texturePath;
				}
				break;
			}
		}

		// Now we get the "final" path using a method. We make sure the string
		// is system-safe, as well. Finally, we check if it's a valid texture.
		// If so, we return it. Otherwise, return null.
		String finalTexturePath = constructTexturePath(stack, entity, texturePath);
		if (finalTexturePath.isEmpty() ||
			!finalTexturePath.matches("^[a-zA-Z0-9_:/.]+$")) {
			return null;
		}

		try {
			return TextureRegistry.hasTexture(finalTexturePath) ? TextureRegistry.getTexture(finalTexturePath) : null;
		} catch (RuntimeException e) {
			return null;
		}
	}

	/**
	 * A method to construct a path based on an item key and the custom name.
	 */
	static String constructTexturePath(ItemStack stack, Entity entity, String basePath) {
		// First, we clean the custom name to make it safe.
		// Afterward we get the key without the "item." prefix
		// and replace dots with underscores.
		String cleanCustomName = stack.getCustomName()
			.replace(" ", "_")
			.replaceAll("[^a-zA-Z0-9_]", "_")
			.toLowerCase();

		String itemKey = stack.getItem().getKey().substring("item.".length()).replace(".", "_");

		// Now we handle special cases, such as fishing rods,
		// maps, quivers, and color-able items. After we're done,
		// we construct the final path so it can be used.
		String suffix = "";
		if (stack.getItem() instanceof ItemFishingRod) {
			if (entity instanceof Player && ((Player) entity).bobberEntity != null) suffix = "_cast";
		} else if (stack.getItem() instanceof ItemMap) {
			if (!ItemMap.hasInitialized(stack)) suffix = "_blank";
		} else if (stack.getItem() instanceof ItemQuiver) {
			if (stack.getMetadata() >= stack.getMaxDamage()) suffix = "_empty";
		} else if (stack.getItem() instanceof ItemDye || stack.getItem() instanceof ItemSignPainted || stack.getItem() instanceof ItemDoorPainted) {
			suffix = "_" + DyeColor.colorFromItemMeta(stack.getMetadata()).name().toLowerCase();
		} else if (stack.getItem() instanceof ItemPaintBrush) if (ItemPaintBrush.getColor(stack) != null)
			suffix = "_" + ItemPaintBrush.getColor(stack).name().toLowerCase();

		return basePath + itemKey + "_" + cleanCustomName + suffix;
	}
}
