package cookie.textureswap;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.stitcher.TextureRegistry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cookie.textureswap.TextureSwap.LOGGER;

public class TextureJsonLoader {
	public static final List<TextureEntry> textureEntryFiles = new ArrayList<>();
	private static final Minecraft mc = Minecraft.getMinecraft(Minecraft.class);

	public static void loadTexturesFromJson() {
		String jsonLoc = "/assets/textureswap/textures/textures.json";
		textureEntryFiles.clear();

		try {
			JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(mc.texturePackList.getResourceAsStream(jsonLoc))));
			Textures textures = new Gson().fromJson(reader, Textures.class);

			textureEntryFiles.addAll(Arrays.asList(textures.textures));

			for (TextureEntry entry : textureEntryFiles) {
				try {
					LOGGER.info("Added {}.", entry.itemName);
					TextureRegistry.getTexture("textureswap:item/" + entry.itemName);
				} catch (RuntimeException e) {
					LOGGER.warn("The folder for {} has changed! Ignoring...", entry.itemName);
				}
			}
		} catch (NullPointerException ignored){
			LOGGER.warn("No Texture Swap json found. Skipping.");
		}
	}
}

class Textures {
	public TextureEntry[] textures;

	public Textures() {
		textures = new TextureEntry[] {};
	}
}
