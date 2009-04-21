package com.flagstone.transform.action;

import com.flagstone.transform.Strings;

/**
 * RegisterIndex is used references a value stored in one of the Flash Player's
 * internal registers. The value will be pushed onto the top of the Flash
 * Player's stack.
 * 
 * @see RegisterCopy
 * @see Push
 */
public final class RegisterIndex {
	
	private final static String FORMAT = "Register: { index=%d }";
	
	private final int index;

	/**
	 * Creates a RegisterIndex object referencing the value stored in one of the
	 * Flash Player's internal registers.
	 * 
	 * @param anIndex
	 *            the register number. Must be in the range 0..255.
	 */
	public RegisterIndex(final int anIndex) {
		if (anIndex < 0 || anIndex > 255) {
			throw new IllegalArgumentException(Strings.REGISTER_OUT_OF_RANGE);
		}
		index = anIndex;
	}

	/**
	 * Returns the number of the register that will be accessed and the value
	 * pushed onto the Flash Player's stack.
	 */
	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return String.format(FORMAT, index);
	}
	
	@Override
	public boolean equals(Object other) {
		boolean result;
		
		if (other == null) {
			result = false;
		} else if (other == this) {
			result = true;
		} else if (other instanceof RegisterIndex) {
			result = index == ((RegisterIndex)other).index;
		} else {
			result = false;
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		return 31*index;
	}
}

