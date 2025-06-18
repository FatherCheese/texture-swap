package cookie.textureswap.core;

import cookie.textureswap.TextureSwapClient;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;

/**
 * A class to handle Item Keys in the textures JSON.
 */
public class ItemKey {
		private final String id;
		private final String armor_texture;

	/**
	 * Class constructor
	 * @param id Item ID (minecraft:item/dye)
	 */
	public ItemKey(String id) {
		this.id = id;
		this.armor_texture = null;
	}

	/**
	 * Class constructor
	 * @param id Item ID (minecraft:item/dye)
	 * @param armor_texture Armor path for a mixin
	 */
	public ItemKey(String id, String armor_texture) {
		this.id = id;
		this.armor_texture = armor_texture;
	}

	/**
	 * A getter method for the ID.
	 * @return Returns an item ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * A getter method for the armor path.
	 * @return Returns an armor path string.
	 */
	public String getArmor_texture() {
		return armor_texture;
	}

	/**
	 * A getter for the Namespace ID.
	 * @return Returns the first part of the key ("minecraft")
	 */
	public String getNamespaceID() {
		String[] parts = id.split(":");
		return parts.length > 1 ? parts[0] : "minecraft";
	}

	/**
	 * A getter for the Namespace Key.
	 * @return Returns the second part of the key ("item/dye")
	 */
	public String getNamespaceKey() {
		String[] parts = id.split(":");
		return parts.length > 1 ? parts[1] : parts[0];
	}

	/**
	 * A method to check if the key matches an item stack.
	 * @param stack Item Stack to check.
	 * @return Returns true if the namespace ID matches
	 */
	public boolean matchesKey(ItemStack stack) {
		try {
			if (!stack.getItem().namespaceID.equals(NamespaceID.getTemp(id))) {
				return false;
			}
		} catch (HardIllegalArgumentException ignored) {
			TextureSwapClient.LOGGER.warn("ItemKey: {} does not match stack: {}", id, stack.getItem().namespaceID);
		}

		return true;
	}
}
