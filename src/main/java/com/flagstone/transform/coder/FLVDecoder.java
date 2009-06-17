package com.flagstone.transform.coder;

/** TODO(class). */
public final class FLVDecoder extends Decoder {

    /** TODO(method). */
    public FLVDecoder(final byte[] data) {
        super(data);
    }

    /**
     * Read an unsigned short integer without changing the internal pointer.
     */
    public int scanUnsignedShort() {
        return ((data[index] & 0x00FF) << 8) + (data[index + 1] & 0x00FF);
    }

    /**
     * Read a word.
     *
     * @param numberOfBytes
     *            the number of bytes read in the range 1..4.
     *
     * @param signed
     *            indicates whether the value read is signed (true) or unsigned
     *            (false).
     *
     * @return the decoded value.
     */
    public int readWord(final int numberOfBytes, final boolean signed) {
        int value = 0;

        for (int i = 0; i < numberOfBytes; i++) {
            value <<= 8;
            value += data[index++] & 255;
        }

        if (signed) {
            value <<= 32 - (numberOfBytes << 3);
            value >>= 32 - (numberOfBytes << 3);
        }

        return value;
    }

    /**
     * Read a double-precision floating point number.
     *
     * @return the decoded value.
     */
    public double readDouble() {
        long longValue = (long) readWord(4, false) << 32;
        longValue |= readWord(4, false) & 0x00000000FFFFFFFFL;

        return Double.longBitsToDouble(longValue);
    }

    /**
     * Searches for a word and advances the pointer to the location where it was
     * found, returning true to signal a successful search. If word cannot be
     * found then the method returns false and the position of the internal
     * pointer is not changed.
     *
     * @param value
     *            the value to search for.
     *
     * @param numberOfBytes
     *            the number of bytes from the value to compare.
     *
     * @param step
     *            the number of bytes to step between searches.
     *
     * @return true if the pattern was found, false otherwise.
     */
    public boolean findWord(final int value, final int numberOfBytes,
            final int step) {

        boolean found;
        final int mark = getPointer();

        while (index + numberOfBytes <= data.length) {

            if (readWord(numberOfBytes, false) == value) {
                index -= numberOfBytes;
                break;
            }
            index = index - numberOfBytes + step;
        }

        if (index + numberOfBytes > data.length) {
            found = false;
            setPointer(mark);
        } else {
            found = true;
        }

        return found;
    }
}
