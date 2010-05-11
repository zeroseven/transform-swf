/*
 * Push.java
 * Transform
 *
 * Copyright (c) 2001-2010 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.action;

import java.util.ArrayList;
import java.util.List;


import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

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
 * <td>
 * The number (0..255) of one of the Flash player's internal registers.
 * </td>
 * </tr>
 * <tr>
 * <td valign="top" nowrap width="20%">Table Index</td>
 * <td>
 * An index into a table of string literals defined using the Table action.
 * </td>
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
 * <td>
 * A reserved number used to identify a specific property of a movie clip.
 * </td>
 * </tr>
 * <tr>
 * <td valign="top" nowrap width="20%">Player Property</td>
 * <td>A reserved number used to identify a specific property of the Flash
 * Player.</td>
 * </tr>
 * </table>
 *
 * @see Null
 * @see Property
 * @see RegisterIndex
 * @see TableIndex
 * @see Void
 *
 */
public final class Push implements Action {

    /** Number of last internal register in the Flash Player. */
    private static final int LAST_REGISTER = 255;

    /**
     * The Builder class is used to generate a new Push object.
     */
    public static final class Builder {
        /** The list of values to push onto the stack. */
        private final transient List<Object>objects = new ArrayList<Object>();

        /**
         * Adds a value to the list.
         *
         * @param value
         *            a value that will be pushed onto the Flash Player's stack
         *            when the action is executed.
         * @return this object.
         */
        public Builder add(final Object value) {
            if (value == null) {
                throw new IllegalArgumentException();
            }
            objects.add(value);
            return this;
        }

        /**
         * Generate a Push using the set of values defined in the Builder.
         * @return an initialized Push object.
         */
        public Push build() {
            return new Push(objects);
        }
    }

    /** Format string used in toString() method. */
    private static final String FORMAT = "Push: { values=%s }";
    /** Type identifying Strings. */
    private static final int TYPE_STRING = 0;
    /** Type identifying Properties. */
    private static final int TYPE_PROPERTY = 1;
    /** Type identifying Null values. */
    private static final int TYPE_NULL = 2;
    /** Type identifying Void values. */
    private static final int TYPE_VOID = 3;
    /** Type identifying RegisterIndex object. */
    private static final int TYPE_RINDEX = 4;
    /** Type identifying Boolean values. */
    private static final int TYPE_BOOLEAN = 5;
    /** Type identifying Double values. */
    private static final int TYPE_DOUBLE = 6;
    /** Type identifying Integer values. */
    private static final int TYPE_INTEGER = 7;
    /** Type identifying indices into Tables with up to 255 entries. */
    private static final int TYPE_TINDEX = 8;
    /** Type identifying indices into Tables with more than 255 entries. */
    private static final int TYPE_LARGE_TINDEX = 9;

    /** Length of encoded Properties. */
    private static final int LENGTH_PROPERTY = 5;
    /** Length of encoded Null values. */
    private static final int LENGTH_NULL = 1;
    /** Length of encoded Void values. */
    private static final int LENGTH_VOID = 1;
    /** Length of encoded RegisterIndex object. */
    private static final int LENGTH_RINDEX = 2;
    /** Length of encoded Boolean values. */
    private static final int LENGTH_BOOLEAN = 2;
    /** Length of encoded Double values. */
    private static final int LENGTH_DOUBLE = 9;
    /** Length of encoded Integer values. */
    private static final int LENGTH_INTEGER = 5;
    /** Length of encoded indices for Tables with up to 255 entries. */
    private static final int LENGTH_TINDEX = 2;
    /** Length of encoded indices for Tables with more than 255 entries. */
    private static final int LENGTH_LTINDEX = 3;

    /** The list of values that will be pushed onto the Flash Player's stack. */
    private final transient List<Object> values;

    /** The length of the encoded object. */
    private transient int length;

    /**
     * Creates and initialises a Push action using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public Push(final SWFDecoder coder) throws CoderException {

        coder.readByte();
        length = coder.readWord(2, false);
        values = new ArrayList<Object>();

        int valuesLength = length;

        while (valuesLength > 0) {
            final int dataType = coder.readByte();

            switch (dataType) {
            case TYPE_STRING:
                final int start = coder.getPointer();
                int strlen = 0;

                while (coder.readWord(1, false) != 0) {
                    strlen += 1;
                }

                coder.setPointer(start);
                values.add(coder.readString(strlen));
                coder.adjustPointer(Coder.BITS_PER_BYTE);
                valuesLength -= strlen + 2;
                break;
            case TYPE_PROPERTY:
                values.add(new Property(coder.readWord(
                        Coder.BYTES_PER_WORD, false)));
                valuesLength -= LENGTH_PROPERTY;
                break;
            case TYPE_NULL:
                values.add(Null.getInstance());
                valuesLength -= LENGTH_NULL;
                break;
            case TYPE_VOID:
                values.add(Void.getInstance());
                valuesLength -= LENGTH_VOID;
                break;
            case TYPE_RINDEX:
                values.add(new RegisterIndex(coder.readByte()));
                valuesLength -= LENGTH_RINDEX;
                break;
            case TYPE_BOOLEAN:
                values.add(coder.readByte() != 0);
                valuesLength -= LENGTH_BOOLEAN;
                break;
            case TYPE_DOUBLE:
                values.add(coder.readDouble());
                valuesLength -= LENGTH_DOUBLE;
                break;
            case TYPE_INTEGER:
                values.add(coder.readWord(Coder.BYTES_PER_WORD, false));
                valuesLength -= LENGTH_INTEGER;
                break;
            case TYPE_TINDEX:
                values.add(new TableIndex(coder.readWord(1, false)));
                valuesLength -= LENGTH_TINDEX;
                break;
            case TYPE_LARGE_TINDEX:
                values.add(new TableIndex(coder.readWord(2, false)));
                valuesLength -= LENGTH_LTINDEX;
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
    public Push(final List<Object> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        values = new ArrayList<Object>(anArray);
    }

    /**
     * Creates and initialises a Push action using the values
     * copied from another Push action.
     *
     * @param object
     *            a Push action from which the values will be
     *            copied. References to immutable objects will be shared.
     */
    public Push(final Push object) {
        values = new ArrayList<Object>(object.values);
    }


    /**
     * Get the array of values that will be pushed onto the Flash Player's
     * stack.
     *
     * @return a copy of the array of values.
     */
    public List<Object> getValues() {
        return new ArrayList<Object>(values);
    }

    /** {@inheritDoc} */
    public Push copy() {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, values);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {

        length = 0;

        for (final Object obj : values) {
            if (obj instanceof Boolean) {
                length += LENGTH_BOOLEAN;
            } else if (obj instanceof Property) {
                length += LENGTH_PROPERTY;
            } else if (obj instanceof Integer) {
                length += LENGTH_INTEGER;
            } else if (obj instanceof Double) {
                length += LENGTH_DOUBLE;
            } else if (obj instanceof String) {
                length += 1 + coder.strlen(obj.toString());
            } else if (obj instanceof Null) {
                length += LENGTH_NULL;
            } else if (obj instanceof Void) {
                length += LENGTH_VOID;
            } else if (obj instanceof TableIndex) {
                if (((TableIndex) obj).getIndex() <= LAST_REGISTER) {
                    length += LENGTH_TINDEX;
                } else {
                    length += LENGTH_LTINDEX;
                }
            } else if (obj instanceof RegisterIndex) {
                length += 2;
            }
        }

        return SWFEncoder.ACTION_HEADER + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {

        coder.writeByte(ActionTypes.PUSH);
        coder.writeWord(length, 2);

        for (final Object obj : values) {
            if (obj instanceof Boolean) {
                coder.writeWord(TYPE_BOOLEAN, 1);
                if (((Boolean) obj).booleanValue()) {
                    coder.writeByte(1);
                } else {
                    coder.writeByte(0);
                }
            } else if (obj instanceof Integer) {
                coder.writeWord(TYPE_INTEGER, 1);
                coder.writeWord(((Integer) obj).intValue(),
                        Coder.BYTES_PER_WORD);
            } else if (obj instanceof Property) {
                coder.writeWord(TYPE_PROPERTY, 1);
                coder.writeWord(((Property) obj).getValue(),
                        Coder.BYTES_PER_WORD);
            } else if (obj instanceof Double) {
                coder.writeWord(TYPE_DOUBLE, 1);
                coder.writeDouble(((Double) obj).doubleValue());
            } else if (obj instanceof String) {
                coder.writeWord(TYPE_STRING, 1);
                coder.writeString(obj.toString());
            } else if (obj instanceof Null) {
                coder.writeWord(TYPE_NULL, 1);
            } else if (obj instanceof Void) {
                coder.writeWord(TYPE_VOID, 1);
            } else if (obj instanceof TableIndex) {
                if (((TableIndex) obj).getIndex() <= LAST_REGISTER) {
                    coder.writeWord(TYPE_TINDEX, 1);
                    coder.writeWord(((TableIndex) obj).getIndex(), 1);
                } else {
                    coder.writeWord(TYPE_LARGE_TINDEX, 1);
                    coder.writeWord(((TableIndex) obj).getIndex(), 2);
                }
            } else if (obj instanceof RegisterIndex) {
                coder.writeWord(TYPE_RINDEX, 1);
                coder.writeWord(((RegisterIndex) obj).getNumber(), 1);
            } else {
                throw new CoderException(getClass().getName(), 0, 0, 0,
                        "Unsupported value");
            }
        }
    }
}
