package com.flagstone.transform.movie.action;

import com.flagstone.transform.movie.Strings;

//TODO(doc) Review
/**
 * TableIndex is used with an Push action to push a reference to an entry in a
 * table of string literals onto the stack.
 * 
 * <p>
 * In the Macromedia Flash (SWF) File Format Specification all literals used in
 * calculations are stored as strings. When performing a series of actions each
 * time a literal value is used in a calculation it must be pushed onto the
 * stack. Rather than repeatedly pushing the value explicitly using an Push
 * action, all the literals are added to a table in the Flash Player's memory
 * using the Table action. To retrieve a literal from the table a reference to
 * an entry in the table is pushed onto the stack using an TableIndex. The
 * reduces the number of bytes required to perform a given calculation when the
 * values are used repeatedly.
 * </p>
 * 
 * @see Table
 * @see Push
 */
public final class TableIndex {
	
	private final static String FORMAT = "TableIndex: { index=%d }";

	private final int index;

	/**
	 * Creates a TableIndex object referencing the value stored in the literal
	 * table.
	 * 
	 * @param anIndex
	 *            the index into the literal table. Must be in the range
	 *            0..65535.
	 */
	public TableIndex(int anIndex) {
		if (anIndex < 0 || anIndex > 65535) {
			throw new IllegalArgumentException(
					Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
		}
		index = anIndex;
	}

	/**
	 * Returns the index in the table of string literals.
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
		} else if (other instanceof TableIndex) {
			result = index == ((TableIndex)other).index;
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

