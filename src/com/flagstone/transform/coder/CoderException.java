package com.flagstone.transform.coder;

import java.io.IOException;

/**
 * <p>
 * This exception is thrown when errors occur while encoding or decoding
 * objects. Errors are reported as either underflow or overflow errors
 * indicating that the class used to encode/decode a particular data structure
 * did not encode or decode the expected number of bytes. This allows the
 * majority of software errors and errors due to improperly encoded flash files
 * to be detected.
 * </p>
 */
public final class CoderException extends IOException {

    private static final long serialVersionUID = 1;

    private static final String FORMAT = "CoderException: { name=%s; location=%d; length=%d; delta=%d; message=%s }";

    public static final String UNDERFLOW = "Underflow";
    public static final String OVERFLOW = "Overflow";

    private transient final String name;
    private transient final int start;
    private transient final int length;
    private transient final int delta;

    /**
     * Creates a CoderException to report where a problem occurred when encoding
     * or decoding a Flash (.swf) file.
     * 
     * @param name
     *            the name of the object or action that was being encoded or
     *            decoded when the problem occurred.
     * 
     * @param start
     *            the address in the file where the data structure being
     *            encoded/decoded is located. This is only valid for files being
     *            decoded since the encoded file will not be written if an
     *            exception occurs.
     * 
     * @param length
     *            the number of bytes that were expected to be encoded or
     *            decoded.
     * 
     * @param delta
     *            the difference between the expected number of bytes and the
     *            actual number encoded or decoded.
     * 
     * @param message
     *            a message indicating type of object that caused the error and
     *            and whether the difference was more (overflow) or less
     *            (underflow) than expected.
     */
    public CoderException(final String name, final int start, final int length,
            final int delta, final String message) {
        super(message);
        this.name = name;
        this.start = start;
        this.length = length;
        this.delta = delta;
    }

    /**
     * Creates a CoderException to report where a problem occurred when encoding
     * or decoding a Flash (.swf) file.
     * 
     * @param name
     *            the name of the object or action that was being encoded or
     *            decoded when the problem occurred.
     * 
     * @param start
     *            the address in the file where the data structure being
     *            encoded/decoded is located. This is only valid for files being
     *            decoded since the encoded file will not be written if an
     *            exception occurs.
     * 
     * @param length
     *            the number of bytes that were expected to be encoded or
     *            decoded.
     * 
     * @param delta
     *            the difference between the expected number of bytes and the
     *            actual number encoded or decoded.
     */
    public CoderException(final String name, final int start, final int length,
            final int delta) {
        super(delta > 0 ? OVERFLOW : UNDERFLOW);
        this.name = name;
        this.start = start;
        this.length = length;
        this.delta = delta;
    }

    /**
     * Returns the name of the class of the object that caused the error.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the byte address of the start of the object that caused the
     * error.
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns number of bytes the object was expected to occupy when encoded.
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns the difference between the expected number of bytes and the
     * actual number of bytes encoded or decoded.
     */
    public int getDelta() {
        return delta;
    }

    /**
     * Return a string representation of the error.
     */
    @Override
    public String toString() {
        return String.format(FORMAT, name, start, length, delta, getMessage());
    }
}
