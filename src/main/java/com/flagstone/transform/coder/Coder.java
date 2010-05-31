package com.flagstone.transform.coder;

/**
 * Coder contains constants and utility functions used by the various classes
 * for encoding and decoding.
 */
public final class Coder {
    /** Mask for getting and setting bit 0 of a byte. */
    public static final int BIT0 = 1;
    /** Mask for getting and setting bit 1 of a byte. */
    public static final int BIT1 = 2;
    /** Mask for getting and setting bit 2 of a byte. */
    public static final int BIT2 = 4;
    /** Mask for getting and setting bit 3 of a byte. */
    public static final int BIT3 = 8;
    /** Mask for getting and setting bit 4 of a byte. */
    public static final int BIT4 = 16;
    /** Mask for getting and setting bit 5 of a byte. */
    public static final int BIT5 = 32;
    /** Mask for getting and setting bit 6 of a byte. */
    public static final int BIT6 = 64;
    /** Mask for getting and setting bit 7 of a byte. */
    public static final int BIT7 = 128;
    /**
     * Bit mask for extracting the length field from the header word.
     */
    public static final int LENGTH_FIELD = 0x3F;
    /**
     * The number of bits used to encode the length field when the length is
     * less than the maximum length of 62.
     */
    public static final int LENGTH_FIELD_SIZE = 6;
    /**
     * Value used to indicate that the length of an object has been encoded
     * as a 32-bit integer following the header for the MovieTag.
     */
    public static final int IS_EXTENDED = 63;
    /**
     * Number of bytes occupied by the header when the size of the encoded
     * object is 62 bytes or less.
     */
    public static final int SHORT_HEADER = 2;
    /**
     * The maximum length in bytes of an encoded object before the length must
     * be encoded using a 32-bit integer.
     */
    public static final int SHORT_HEADER_LIMIT = 62;
    /**
     * Number of bytes occupied by the header when the size of the encoded
     * object is greater than 62 bytes.
     */
    public static final int LONG_HEADER = 6;
    /**
     * Length, in bytes, of type and length fields of an encoded action.
     */
    public static final int ACTION_HEADER = 3;


    /** Number of bits in an int. */
    private static final int BITS_PER_INT = 32;
    /** Bit mask with most significant bit of a 32-bit integer set. */
    private static final int MSB_MASK = 0x80000000;

    /**
     * Calculates the minimum number of bits required to encoded an unsigned
     * integer in a bit field.
     *
     * @param value
     *            the unsigned value to be encoded.
     *
     * @return the number of bits required to encode the value.
     */
    public static int unsignedSize(final int value) {

        final int val = (value < 0) ? -value - 1 : value;
        int counter = BITS_PER_INT;
        int mask = MSB_MASK;

        while (((val & mask) == 0) && (counter > 0)) {
            mask >>>= 1;
            counter -= 1;
        }
        return counter;
    }

    /**
     * Calculates the minimum number of bits required to encoded a signed
     * integer in a bit field.
     *
     * @param value
     *            the signed value to be encoded.
     *
     * @return the number of bits required to encode the value.
     */
    public static int size(final int value) {
        int counter = BITS_PER_INT;
        int mask = MSB_MASK;
        final int val = (value < 0) ? -value - 1 : value;

        while (((val & mask) == 0) && (counter > 0)) {
            mask >>>= 1;
            counter -= 1;
        }
        return counter + 1;
    }

    /**
     * Returns the minimum number of bits required to encode all the signed
     * values in an array as a set of bit fields with the same size.
     *
     * @param values
     *            an array of signed integers.
     *
     * @return the minimum number of bits required to encode each of the values.
     */
    public static int maxSize(final int... values) {

        int max = 0;
        int size;

        for (final int value : values) {
            size = size(value);
            max = (max > size) ? max : size;
        }
        return max;
    }

    /**
     * Calculate minimum number of bytes a 32-bit unsigned integer can be
     * encoded in.
     *
     * @param value
     *            an integer containing the value to be written.
     * @return the number of bytes required to encode the integer.
     */
    public static int sizeVariableU32(final int value) {

        int val = value;
        int size = 1;

        while (val > 127) {
            size += 1;
            val = val >>> 7;
        }
        return size;
    }

    /** Private constructor. */
    private Coder() {
        // Private Constructor
    }
}
