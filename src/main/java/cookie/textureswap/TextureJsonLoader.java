package cookie.textureswap;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cookie.textureswap.TextureSwapClient.LOGGER;

public class TextureJsonLoader {
	public static final List<TextureEntry> textureEntryFiles = new ArrayList<>();
	private static final Minecraft mc = Minecraft.getMinecraft();

	public static void loadTexturesFromJson() {
		String jsonLoc = "/assets/textureswap/textures/textures.json";
		textureEntryFiles.clear();

		try (JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(mc.texturePackList.getResourceAsStream(jsonLoc))))) {
			Textures textures = new Gson().fromJson(reader, Textures.class);

			textureEntryFiles.addAll(Arrays.asList(textures.textures));

			for (TextureEntry entry : textureEntryFiles) {
				try {
					LOGGER.info("Added {} in directory {}.", entry.itemName, entry.texturePath.substring(0, entry.texturePath.lastIndexOf('/')));
					TextureRegistry.getTexture(String.format("%s%s_%s",
						entry.texturePath,
						entry.itemKey
							.replace(".", "_"),
						entry.itemName
							.replace(" ", "_")
							.replaceAll("[^a-zA-Z0-9_]", "_")).toLowerCase());
				} catch (RuntimeException e) {
					LOGGER.warn("The folder for {} has changed! Ignoring...", entry.itemName);
				}
			}
		} catch (NullPointerException ignored) {
			LOGGER.warn("No Texture Swap JSON found. Skipping.");
		} catch (IOException e) {
			LOGGER.error("Error loading the texture JSON.", e);
		}
	}
}

class Textures {
	public TextureEntry[] textures;

	public Textures() {
		textures = new TextureEntry[] {};
	}
}
