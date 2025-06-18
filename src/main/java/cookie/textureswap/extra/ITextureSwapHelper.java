package cookie.textureswap.extra;

import cookie.textureswap.TextureSwapClient;
import cookie.textureswap.core.ComparisonType;
import cookie.textureswap.core.ConditionalTexture;
import cookie.textureswap.core.TextureEntry;
import cookie.textureswap.core.TextureJSON;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.*;
import net.minecraft.core.util.helper.DyeColor;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

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
		} catch (RuntimeException ignored) {
		}
	}

	/**
	 * A method handling getting the new icon
	 * @param stack ItemStack
	 * @param entity Entity holding the item
	 * @return Returns a new IconCoordinate.
	 */
	static IconCoordinate getNewIcon(ItemStack stack, Entity entity) {
		TextureJSON json = TextureJSON.getInstance();
		if (json == null) return null;

		String customName = stack.getCustomName();

		for (TextureEntry entry : json.getEntries()) {
			if (!entry.getItem().matchesKey(stack)) continue;

			if (entry.getConditional_textures() != null) {
				for (ConditionalTexture condition : entry.getConditional_textures()) {
					if (condition.getTextures().containsKey(customName)) {
						if (checkStackConditions(stack, condition.getMeta(), condition.getStack_size())) {
							String texturePath = "textureswap:item/" + condition.getTextures().get(customName);
							String finalTexturePath = addVariantSuffix(stack, entity, texturePath);

							try {
								return TextureRegistry.getTexture(finalTexturePath);
							} catch (RuntimeException ignored) {
								TextureSwapClient.LOGGER.error("Failed to find {} texture for {} in the texture pack.", customName, customName);
							}
						}
					}
				}
			}

			// Fallback to regular textures if no conditional texture matched
			for (String itemName : entry.getTextures().keySet()) {
				if (itemName.equalsIgnoreCase(customName)) {
					String texturePath = "textureswap:item/" + entry.getTextures().get(itemName);
					String finalTexturePath = addVariantSuffix(stack, entity, texturePath);

					try {
						return TextureRegistry.getTexture(finalTexturePath);
					} catch (RuntimeException ignored) {
						TextureSwapClient.LOGGER.error("Failed to find {} texture for {} in the texture pack.", itemName, customName);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Helper method to check if an ItemStack matches the metadata conditions
	 */
	static boolean checkStackConditions(ItemStack stack, Map<ComparisonType, Integer> metaConditions, Map<ComparisonType, Integer> sizeConditions) {
		boolean hasMetaConditions = metaConditions != null && !metaConditions.isEmpty();
		boolean hasSizeConditions = sizeConditions != null && !sizeConditions.isEmpty();
		if (!hasMetaConditions && !hasSizeConditions) return true;

		int stackMeta = stack.getMetadata();
		int stackSize = stack.stackSize;

		if (hasMetaConditions) {
			for (ComparisonType comp : metaConditions.keySet()) {
				Integer targetMeta = metaConditions.get(comp);
				if (targetMeta == null) {
					TextureSwapClient.LOGGER.warn("Null metadata value for comparison type: {}", comp);
					return false;
				}

				if (!comp.compare(stackMeta, targetMeta)) return false;
			}
		}

		if (hasSizeConditions) {
			for (ComparisonType comp : sizeConditions.keySet()) {
				Integer targetSize = sizeConditions.get(comp);
				if (targetSize == null) {
					TextureSwapClient.LOGGER.warn("Null stack size value for comparison type: {}", comp);
					return false;
				}

				if (!comp.compare(stackSize, targetSize)) return false;
			}
		}

		return true;
	}

	/**
	 * A method to construct a path based on an item key and the custom name.
	 */
	static String addVariantSuffix(ItemStack stack, Entity entity, String basePath) {
		if (stack.getItem() instanceof ItemFishingRod) {
			if (entity instanceof Player && ((Player) entity).bobberEntity != null && stack == ((Player) entity).getHeldItem()) return basePath + "_cast";
		} else if (stack.getItem() instanceof ItemMap) {
			if (!ItemMap.hasInitialized(stack))  return basePath + "_blank";
		} else if (stack.getItem() instanceof ItemQuiver) {
			if (stack.getMetadata() >= stack.getMaxDamage())  return basePath + "_empty";
		} else if (stack.getItem() instanceof ItemDye || stack.getItem() instanceof ItemSignPainted || stack.getItem() instanceof ItemDoorPainted) {
			return basePath + "_" + DyeColor.colorFromItemMeta(stack.getMetadata()).name().toLowerCase();
		} else if (stack.getItem() instanceof ItemPaintBrush) if (ItemPaintBrush.getColor(stack) != null)
			return basePath + "_" + ItemPaintBrush.getColor(stack).name().toLowerCase();

		return basePath;
	}
}
