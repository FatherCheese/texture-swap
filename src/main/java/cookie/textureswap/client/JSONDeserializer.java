package cookie.textureswap.client;

import com.google.gson.Gson;
import cookie.textureswap.TextureSwapClient;
import cookie.textureswap.core.ConditionalTexture;
import cookie.textureswap.core.TextureEntry;
import cookie.textureswap.core.TextureJSON;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.item.*;
import net.minecraft.core.util.helper.DyeColor;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * A client-side class that deserializes the textures JSON.
 */
@Environment(EnvType.CLIENT)
public class JSONDeserializer {
	private static final Minecraft mc = Minecraft.getMinecraft();

	/**
	 * A static method to preload the textures. Used in a mixin.
	 */
	public static void preloadTextures() {
		// Some vars and a texture pack check. If the texturepacks are
		//  null, we return.
		if (mc.texturePackList.selectedPacks.isEmpty()) return;
		Gson gson = new Gson();
		String jsonLoc = "/assets/textureswap/textures/textures.json";

		// Now we try to get the JSON location from the selected texturepacks.
		// We also read the JSON if it's found and check if it even exists.
		// We also check whether the config entries are empty.
		try (InputStream inputStream = mc.texturePackList.getResourceAsStream(jsonLoc);
			 InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
			TextureJSON config = gson.fromJson(reader, TextureJSON.class);

			if (config == null) {
				TextureSwapClient.LOGGER.warn("Failed to find a textures.json file. Skipping.");
				return;
			}

			if (config.getEntries().isEmpty()) {
				return;
			}

			// If the entries ARE NOT null, we set an instance and run some
			// for loops. We check through the entries and texture values,
			// then try to register the textures.
			TextureJSON.setInstance(config);

			for (TextureEntry entry : config.getEntries()) {
				if (entry.getTextures() != null) {
					for (String texture : entry.getTextures().values()) {
						TextureSwapClient.LOGGER.info("Preloading texture '{}'.", texture);

						try {
							TextureRegistry.getTexture(String.format("textureswap:item/%s", texture));
							TextureSwapClient.LOGGER.info("Successfully loaded texture '{}'.", texture);
							compareToItems(texture, entry);
						} catch (Exception e) {
							TextureSwapClient.LOGGER.error("Failed to find texture 'textureswap:item/{}' in the texture pack.", texture);
						}
					}
				}

				// Now we try to get the conditional textures, checking for both
				// item metadata and stack size. If EITHER EXISTS we compare it for
				// some special cases.
				if (entry.getConditional_textures() != null) {
					try {
						for (ConditionalTexture condition : entry.getConditional_textures()) {
							for (String texture : condition.getTextures().values()) {
								TextureSwapClient.LOGGER.info("Preloading conditional texture '{}'.", texture);
								try {
									TextureRegistry.getTexture(String.format("textureswap:item/%s", texture));
									TextureSwapClient.LOGGER.info("Successfully loaded conditional texture '{}'.", texture);
									compareToItems(texture, entry);
								} catch (Exception ignored) {
									TextureSwapClient.LOGGER.error("Failed to find conditional texture 'textureswap:item/{}' in the texture pack.", texture);
								}
							}
						}
					} catch (NullPointerException ignored) {
						TextureSwapClient.LOGGER.warn("Failed to find conditional textures for 'textureswap:item/{}'. Skipping.", entry.getItem().getNamespaceKey());
					}
				}
			}
		} catch (IOException e) {
			TextureSwapClient.LOGGER.error("Failed to read a textures.json file.\n{}", e.getLocalizedMessage());
		}
	}

	/**
	 * A method to compare the items, for special cases.
	 * @param textureName The name of the texture that was loaded
	 * @param entry The texture entry this texture belongs to
	 */
	private static void compareToItems(String textureName, TextureEntry entry) {
		String namespaceKey = entry.getItem().getNamespaceKey();
		if (namespaceKey == null) return;

		String expectedKey = namespaceKey.replace("_", ".").replace("/", ".");

		// Go through every item in the game's list.
		// If any are null, continue. Otherwise, we check if
		// it matches the namespace key in this entry.
		// Finally, register special textures for this item.
		for (Item item : Item.itemsList) {
			if (item == null) continue;

			if (!item.getKey().equals(expectedKey)) continue;
			registerSpecialTextures(item, textureName);
			break;
		}
	}

	/**
	 * A method to register "special case" item textures
	 * @param item Item to check
	 * @param baseTexture Texture to check
	 */
	private static void registerSpecialTextures(Item item, String baseTexture) {
		// We check for fishing rods, maps, quivers, dye-ables, and the paintbrush.
		if (item instanceof ItemFishingRod) {
			registerTextureVariant(baseTexture, "cast");
		} else if (item instanceof ItemMap) {
			registerTextureVariant(baseTexture, "blank");
		} else if (item instanceof ItemQuiver) {
			registerTextureVariant(baseTexture, "empty");
		} else if (item instanceof ItemDye || item instanceof ItemSignPainted || item instanceof ItemDoorPainted || item instanceof ItemPaintBrush) {
			for (DyeColor color : DyeColor.values()) {
				registerTextureVariant(baseTexture, color.name().toLowerCase());
				break;
			}
		}
	}

	/**
	 * A method to register texture variants.
	 * @param baseTexture The base texture to get
	 * @param variant The variant to add to the texture
	 */
	private static void registerTextureVariant(String baseTexture, String variant) {
		String texturePath = String.format("textureswap:item/%s_%s", baseTexture, variant).toLowerCase();
		try {
			TextureRegistry.getTexture(texturePath);
			TextureSwapClient.LOGGER.info("Successfully registered texture variant '{}'.", texturePath);
		} catch (RuntimeException ignored) {
			TextureSwapClient.LOGGER.warn("Failed to find {} texture for {} in the texture pack.", variant, baseTexture);
		}
	}
}
