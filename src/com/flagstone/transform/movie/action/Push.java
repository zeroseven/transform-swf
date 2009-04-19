/*
 * Push.java
 * Transform
 * 
 * Copyright (c) 2001-2008 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.movie.action;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Strings;


//TODO(doc) Review
/**
 * Push is used to push values on the Flash Player's internal stack.
 * 
 * <p>
 * Push supports the full range of data types supported by Flash:
 * </p>
 * 
 * <table class="datasheet">
 * <tr>
 * <td valign="top" nowrap width="20%">Boolean</td>
 * <td>A boolean value, 1 (true) or 0 (false).</td>
 * </tr>
 * <tr>
 * <td valign="top" nowrap width="20%">Integer</td>
 * <td>A signed 32-bit integer, range -2,147,483,648 to 2,147,483,647.</td>
 * </tr>
 * <tr>
 * <td valign="top" nowrap width="20%">Double</td>
 * <td>A double-precision (64-bit) floating-point number, range approximately
 * +/- 1.79769313486231570E+308.</td>
 * </tr>
 * <tr>
 * <td valign="top" nowrap width="20%">String</td>
 * <td>A String. The string is encoded as using the UTF-8 encoding which is
 * backward compatible with ASCII encoding supported in Flash 5.</td>
 * </tr>
 * <tr>
 * <td valign="top" nowrap width="20%">Register Index</td>
 * <td>The number (0..255) of one of the Flash player's internal registers.</td>
 * </tr>
 * <tr>
 * <td valign="top" nowrap width="20%">Table Index</td>
 * <td>An index into a table of string literals defined using the Table action.</td>
 * </tr>
 * <tr>
 * <td valign="top" nowrap width="20%">Null</td>
 * <td>A null value.</td>
 * </tr>
 * <tr>
 * <td valign="top" nowrap width="20%">Void</td>
 * <td>A void value.</td>
 * </tr>
 * <tr>
 * <td valign="top" nowrap width="20%">Movie Clip Property</td>
 * <td>A reserved number used to identify a specific property of a movie clip.</td>
 * </tr>
 * <tr>
 * <td valign="top" nowrap width="20%">Player Property</td>
 * <td>A reserved number used to identify a specific property of the Flash
 * Player.</td>
 * </tr>
 * </table>
 * 
 * @see Push.Null
 * @see Push.Property
 * @see Push.Register
 * @see Push.TableIndex
 * @see Push.Void
 * 
 */
//TODO(api) Will Java autoboxing work with a single add() method.
public final class Push implements Action {
	
	private static final String FORMAT = "Push: { values=%s }";

	private List<Object> values;

	private transient int length;

	//TODO(doc)
	public Push(final SWFDecoder coder, final SWFContext context) throws CoderException {
		
		coder.readByte();
		length = coder.readWord(2, false);
		values = new ArrayList<Object>();

		int valuesLength = length;

		while (valuesLength > 0) {
			int dataType = coder.readWord(1, false);

			switch (dataType) {
			case 0:
				int start = coder.getPointer();
				int strlen = 0;

				while (coder.readWord(1, false) != 0) {
					strlen += 1;
				}

				coder.setPointer(start);
				values.add(coder.readString(strlen));
				coder.adjustPointer(8);
				valuesLength -= strlen + 2;
				break;
			case 1: // Pre version 5 property
				values.add(Property.fromInt(coder.readWord(4, false)));
				valuesLength -= 5;
				break;
			case 2:
				values.add(Null.getInstance());
				valuesLength -= 1;
				break;
			case 3:
				values.add(Void.getInstance());
				valuesLength -= 1;
				break;
			case 4:
				values.add(new RegisterIndex(coder.readByte()));
				valuesLength -= 2;
				break;
			case 5:
				values.add(coder.readByte() != 0);
				valuesLength -= 2;
				break;
			case 6:
				values.add(coder.readDouble());
				valuesLength -= 9;
				break;
			case 7:
				values.add(coder.readWord(4, false));
				valuesLength -= 5;
				break;
			case 8:
				values.add(new TableIndex(coder.readWord(1, false)));
				valuesLength -= 2;
				break;
			case 9:
				values.add(new TableIndex(coder.readWord(2, false)));
				valuesLength -= 3;
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Creates a Push action that will push the values in the array onto the
	 * stack.
	 * 
	 * @param anArray
	 *            an array of values to be pushed onto the stack. The values in
	 *            the array must be one of the following classes: Boolean,
	 *            Integer, Double, String, RegisterIndex or TableIndex. Must not
	 *            be null.
	 */
	public Push(List<Object> anArray) {
		setValues(anArray);
	}

	//TODO(doc)
	public Push(Push object) {
		values = new ArrayList<Object>(object.values);
	}

	/**
	 * Adds a boolean value to the array of values that will be pushed onto the
	 * stack.
	 * 
	 * @param value
	 *            a boolean value.
	 */
	public final Push add(boolean value) {
		values.add(value);
		return this;
	}

	/**
	 * Adds an integer value to the array of values that will be pushed onto the
	 * stack.
	 * 
	 * @param value
	 *            an integer (int) value.
	 */
	public final Push add(int value) {
		values.add(value);
		return this;
	}

	/**
	 * Adds a double value to the array of values that will be pushed onto the
	 * stack.
	 * 
	 * @param value
	 *            a double-precision floating-point value.
	 */
	public final Push add(double value) {
		values.add(new Double(value));
		return this;
	}

	/**
	 * Adds a null value to the array of values that will be pushed onto the
	 * stack.
	 * 
	 * @param value
	 *            a lightweight Null object.
	 * 
	 * @throws IllegalArgumentException
	 *             is the argument is null.
	 */
	public final Push add(Null value) {
		if (value == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		values.add(value);
		return this;
	}

	/**
	 * Adds a void value to the array of values that will be pushed onto the
	 * stack.
	 * 
	 * @param value
	 *            a lightweight Void object.
	 * 
	 * @throws IllegalArgumentException
	 *             is the argument is null.
	 */
	public final Push add(Void value) {
		if (value == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		values.add(value);
		return this;
	}

	/**
	 * Adds a String to the array of values that will be pushed onto the stack.
	 * 
	 * @param value
	 *            a String. Must not be null.
	 */
	public final Push add(String value) {
		if (value == null) {
			throw new IllegalArgumentException(Strings.STRING_CANNOT_BE_NULL);
		}
		values.add(value);
		return this;
	}

	/**
	 * Adds an TableIndex to the array of values that will be pushed onto the
	 * stack.
	 * 
	 * @param value
	 *            a TableIndex referencing an entry in a table of literals. Must
	 *            not be null.
	 */
	public final Push add(TableIndex value) {
		if (value == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		values.add(value);
		return this;
	}

	/**
	 * Adds an RegisterIndex to the array of values that will be pushed onto the
	 * stack.
	 * 
	 * @param value
	 *            a RegisterIndex referencing one of the Flash Player's internal
	 *            registers. Must not be null.
	 */
	public final Push add(RegisterIndex value) {
		if (value == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		values.add(value);
		return this;
	}

	/**
	 * Returns the array of values that will be pushed onto the Flash Player's
	 * stack.
	 */
	public List<Object> getValues() {
		return values;
	}

	/**
	 * Sets the array of values.
	 * 
	 * @param anArray
	 *            replaces the existing array of value with anArray. The values
	 *            in the array must be one of the following classes: Boolean,
	 *            Integer, Double, String, Null, Void, RegisterIndex or
	 *            TableIndex. Must not be null.
	 */
	public void setValues(List<Object> anArray) {
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		values = anArray;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public Push copy() {
		return new Push(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, values);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context) {
		
		length = 0;

		for (Object anObject : values) {
			if (anObject instanceof Boolean) {
				length += 2;
			} else if (anObject instanceof Property) {
				length += 5;
			} else if (anObject instanceof Integer) {
				length += 5;
			} else if (anObject instanceof Double) {
				length += 9;
			} else if (anObject instanceof String) {
				length += 1 + coder.strlen(anObject.toString());
			} else if (anObject instanceof Null) {
				length += 1;
			} else if (anObject instanceof Void) {
				length += 1;
			} else if (anObject instanceof TableIndex) {
				if (((TableIndex) anObject).getIndex() < 256) {
					length += 2;
				} else {
					length += 3;
				}
			} else if (anObject instanceof RegisterIndex) {
				length += 2;
			}
		}

		return length + 3;
	}

	//TODO(code) throw a Coder exception if an unexpected object is found
	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException {
		coder.writeByte(ActionTypes.PUSH);
		coder.writeWord(length, 2);

		for (Object anObject : values) {
			if (anObject instanceof Boolean) {
				coder.writeWord(5, 1);
				coder.writeWord(((Boolean) anObject).booleanValue() ? 1 : 0, 1);
			} else if (anObject instanceof Integer) {
				coder.writeWord(7, 1);
				coder.writeWord(((Integer) anObject).intValue(), 4);
			} else if (anObject instanceof Property) {
				coder.writeWord(1, 1);
				coder.writeWord(((Property) anObject).getValue(coder
						.getContext().getVersion()), 4);
			} else if (anObject instanceof Double) {
				coder.writeWord(6, 1);
				coder.writeDouble(((Double) anObject).doubleValue());
			} else if (anObject instanceof String) {
				coder.writeWord(0, 1);
				coder.writeString(anObject.toString());
			} else if (anObject instanceof Null) {
				coder.writeWord(2, 1);
			} else if (anObject instanceof Void) {
				coder.writeWord(3, 1);
			} else if (anObject instanceof TableIndex) {
				if (((TableIndex) anObject).getIndex() < 256) {
					coder.writeWord(8, 1);
					coder.writeWord(((TableIndex) anObject).getIndex(), 1);
				} else {
					coder.writeWord(9, 1);
					coder.writeWord(((TableIndex) anObject).getIndex(), 2);
				}
			} else if (anObject instanceof RegisterIndex) {
				coder.writeWord(4, 1);
				coder.writeWord(((RegisterIndex) anObject).getIndex(), 1);
			}
		}
	}
}
