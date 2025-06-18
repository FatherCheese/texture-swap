package cookie.textureswap.core;

import java.util.Map;

/**
 * A class for conditional textures, such as stack size or metadata.
 */
public class ConditionalTexture {
	private final Map<ComparisonType, Integer> stack_size;
	private final Map<ComparisonType, Integer> meta;
	private final Map<String, String> textures;

	/**
	 * Class constructor
	 * @param stack_size Comparison Enum: Integer map for stack size
	 * @param meta Comparison Enum: Integer map for metadata
	 * @param textures String: String map for textures
	 */
	public ConditionalTexture(Map<ComparisonType, Integer> stack_size, Map<ComparisonType, Integer> meta, Map<String, String> textures) {
		this.stack_size = stack_size;
		this.meta = meta;
		this.textures = textures;
	}

	/**
	 * A getter method for the stack size.
	 * @return Returns the stack size.
	 */
	public Map<ComparisonType, Integer> getStack_size() {
		return stack_size;
	}

	/**
	 * A getter method for the metadata.
	 * @return Returns the metadata.
	 */
	public Map<ComparisonType, Integer> getMeta() {
		return meta;
	}

	/**
	 * A getter method for a map of textures.
	 * @return Returns the texture map.
	 */
	public Map<String, String> getTextures() {
		return textures;
	}
}
