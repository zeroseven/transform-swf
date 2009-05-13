/*
 * RegisterIndex.java
 * Transform
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
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

    private static final String FORMAT = "Register: { number=%d }";

    private final transient int number;

    /**
     * Creates a RegisterIndex object referencing the value stored in one of the
     * Flash Player's internal registers.
     *
     * @param index
     *            the register number. Must be in the range 0..255.
     */
    public RegisterIndex(final int index) {
        if ((index < 0) || (index > 255)) {
            throw new IllegalArgumentException(Strings.REGISTER_RANGE);
        }
        number = index;
    }

    /**
     * Returns the number of the register that will be accessed and the value
     * pushed onto the Flash Player's stack.
     */
    public int getNumber() {
        return number;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, number);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object other) {
        boolean result;

        if (other == null) {
            result = false;
        } else if (other == this) {
            result = true;
        } else if (other instanceof RegisterIndex) {
            result = number == ((RegisterIndex) other).number;
        } else {
            result = false;
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return 31 * number;
    }
}
