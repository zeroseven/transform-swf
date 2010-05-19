/*
 * IllegalArgumentRangeException.java
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

package com.flagstone.transform.exception;

/**
 * IllegalArgumentValueException is thrown when a value is used that is not
 * within a pre-defined range.
 */
public final class IllegalArgumentRangeException
        extends IllegalArgumentException {

    /** Serial number identifying the version of the object. */
    private static final long serialVersionUID = -9208368642722953411L;
    /** The lower value of the range. */
    private final transient int lower;
    /** The upper value of the range. */
    private final transient int upper;
    /** The actual value used. */
    private final transient int value;

    /**
     * Creates an IllegalArgumentRangeException with the range of expected
     * values and the actual value used.
     *
     * @param start the start of the expected range.
     * @param end the end of the expected range inclusive.
     * @param actual the actual value used.
     */
    public IllegalArgumentRangeException(final int start, final int end,
            final int actual) {
        super("Lower Bound: " + start + " Upper Bound: " + end
                + " Value: " + actual);
        lower = start;
        upper = end;
        value = actual;
    }
    /**
     * Get the lower value of the expected range.
     * @return the range's lower value.
     */
    public int getLower() {
        return lower;
    }
    /**
     * Get the upper value of the expected range.
     * @return the range's upper value.
     */
    public int getUpper() {
        return upper;
    }
    /**
     * Get the actual value that triggered the exception.
     * @return the actual value used.
     */
    public int getValue() {
        return value;
    }
}
