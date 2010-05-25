/*
 * CoderException.java
 * Transform
 *
 * Copyright (c) 2009-2010 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.coder;

import java.io.IOException;

/**
 * CoderException is thrown when errors occur while encoding or decoding
 * objects. Errors are reported as either underflow or overflow errors
 * indicating that the class used to encode/decode a particular data structure
 * did not encode or decode the expected number of bytes. This allows the
 * majority of software errors and errors due to improperly encoded flash files
 * to be detected.
 */
public final class CoderException extends IOException {

    /** Serial number identifying the version of the object. */
    private static final long serialVersionUID = 1;

    /** Format string used in toString() method. */
    private static final String FORMAT = "CoderException: { "
            + "name=%s; location=%d; length=%d; delta=%d; message=%s }";

    public static final String CODING_ERROR = "CodingError";

    public static final String STREAM_ERROR = "StreamError";

    /** The name of the class where the error occurred. */
    private final transient String name;
    /**
     * The location of the start of the object being encoded/decoded
     * when the error occurred.
     */
    private final transient int start;
    /** The expected length of the encoded object. */
    private final transient int length;
    /**
     * The difference between the expected and actual number of bytes encoded
     * or decoded.
     */
    private final transient int delta;

    public CoderException(final String className, Exception cause) {
        super(STREAM_ERROR, cause);
        name = className;
        start = 0;
        length = 0;
        delta = 0;
    }

    /**
     * Creates a CoderException to report where a problem occurred when encoding
     * or decoding a Flash (.swf) file.
     *
     * @param className
     *            the name of the object or action that was being encoded or
     *            decoded when the problem occurred.
     *
     * @param location
     *            the address in the file where the data structure being
     *            encoded/decoded is located. This is only valid for files being
     *            decoded since the encoded file will not be written if an
     *            exception occurs.
     *
     * @param message
     *            a short description of the error.
     */
    public CoderException(final String className, final int location,
            final String message) {
        super(message);
        name = className;
        start = location;
        length = 0;
        delta = 0;
    }

    /**
     * Creates a CoderException to report where a problem occurred when encoding
     * or decoding a Flash (.swf) file.
     *
     * @param className
     *            the name of the object or action that was being encoded or
     *            decoded when the problem occurred.
     *
     * @param location
     *            the address in the file where the data structure being
     *            encoded/decoded is located. This is only valid for files being
     *            decoded since the encoded file will not be written if an
     *            exception occurs.
     *
     * @param size
     *            the number of bytes that were expected to be encoded or
     *            decoded.
     *
     * @param difference
     *            the difference between the expected number of bytes and the
     *            actual number encoded or decoded.
     */
    public CoderException(final String className, final int location,
            final int size, final int difference) {
        super(CODING_ERROR);
        name = className;
        start = location;
        length = size;
        delta = difference;
    }

    /**
     * Get the name of the class of the object that caused the error.
     *
     * @return the name of the class that caused the error.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the byte address of the start of the object that caused the
     * error.
     *
     * @return the location of the start of the encoded object when the error
     * occurred.
     */
    public int getStart() {
        return start;
    }

    /**
     * Get number of bytes the object was expected to occupy when encoded.
     *
     * @return get the number of bytes expected to be encoded or decoded.
     */
    public int getLength() {
        return length;
    }

    /**
     * Get the difference between the expected number of bytes and the
     * actual number of bytes encoded or decoded.
     *
     * @return the difference from the expected number of bytes.
     */
    public int getDelta() {
        return delta;
    }

    /**
     * Get a string representation of the error.
     *
     * @return the string describing the error.
     */
    @Override
    public String toString() {
        return String.format(FORMAT, name, start, length, delta, getMessage());
    }
}
