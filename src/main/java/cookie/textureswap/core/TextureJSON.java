package cookie.textureswap.core;

import java.util.List;

/**
 * A class to handle the textures JSON format.
 */
public class TextureJSON {
	private final List<TextureEntry> entries;
	private static TextureJSON instance;

	/**
	 * Class constructor
	 * @param entries A list of texture entries
	 */
	public TextureJSON(List<TextureEntry> entries) {
		this.entries = entries;
	}

	/**
	 * A getter for texture entries.
	 * @return Returns a list of texture entries
	 */
	public List<TextureEntry> getEntries() {
		return entries;
	}

	/**
	 * A getter for the class instance.
	 * @return Returns the class instance
	 */
	public static TextureJSON getInstance() {
		return instance;
	}

	/**
	 * A setter for the class instance.
	 * @param instance A new TextureJSON instance
	 */
	public static void setInstance(TextureJSON instance) {
		TextureJSON.instance = instance;
	}
}
