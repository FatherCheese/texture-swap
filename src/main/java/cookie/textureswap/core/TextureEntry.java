package cookie.textureswap.core;

import java.util.List;
import java.util.Map;

/**
 * A class for the Texture Entries in the JSON.
 */
public class TextureEntry {
	private final ItemKey item;
	private final Map<String, String> textures;
	private List<ConditionalTexture> conditional_textures;

	/**
	 * A class constructor.
	 *
	 * @param item     ItemKey Class
	 * @param textures Textures Map
	 */
	public TextureEntry(ItemKey item, Map<String, String> textures) {
		this.item = item;
		this.textures = textures;
	}

	/**
	 * A class constructor.
	 *
	 * @param item                 ItemKey Class
	 * @param textures             Textures Map
	 * @param conditional_textures Conditional textures Map
	 */
	public TextureEntry(ItemKey item, Map<String, String> textures, List<ConditionalTexture> conditional_textures) {
		this.item = item;
		this.textures = textures;
		this.conditional_textures = conditional_textures;
	}

	/**
	 * A getter method for the ItemKey.
	 *
	 * @return Returns an ItemKey
	 */
	public ItemKey getItem() {
		return item;
	}

	/**
	 * A getter method for the textures.
	 *
	 * @return Returns a String: String map
	 */
	public Map<String, String> getTextures() {
		return textures;
	}

	/**
	 * A getter method for the conditional textures.
	 *
	 * @return Returns a list of conditional textures
	 */
	public List<ConditionalTexture> getConditional_textures() {
		return conditional_textures;
	}
}
