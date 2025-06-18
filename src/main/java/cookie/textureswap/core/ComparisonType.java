package cookie.textureswap.core;

/**
 * An enum for comparisons.
 */
public enum ComparisonType {
	GREATER,
	LESSER,
	EQUAL,
	GREATER_OR_EQUAL,
	LESSER_OR_EQUAL,
	NOT_EQUAL;

	/**
	 * A method to compare two values.
	 * @param toCompare An integer value to compare
	 * @param compared The compared integer value
	 * @return Returns true if the compared value compares right
	 */
	public boolean compare(int toCompare, int compared) {
		switch (this) {
			case GREATER:
				return toCompare > compared;
			case LESSER:
				return toCompare < compared;
			case EQUAL:
				return toCompare == compared;
			case GREATER_OR_EQUAL:
				return toCompare >= compared;
			case LESSER_OR_EQUAL:
				return toCompare <= compared;
			case NOT_EQUAL:
				return toCompare != compared;
			default:
				return false;
		}
	}
}
