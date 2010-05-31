/*
 * ColorTest.java
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
package com.flagstone.transform.datatype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.flagstone.transform.exception.IllegalArgumentRangeException;

public final class ColorTest {

    @Test
    public void checkConstructorSetsRed() {
        assertEquals(1, new Color(1, 0, 0).getRed());
    }

    @Test(expected = IllegalArgumentRangeException.class)
    public void checkRedValueBelowRangeThrowsException() {
        new Color(-1, 0, 0);
    }

    @Test(expected = IllegalArgumentRangeException.class)
    public void checkRedValueAboveRangeThrowsException() {
        new Color(256, 0, 0);
    }

    @Test
    public void checkConstructorSetsGreen() {
        assertEquals(1, new Color(0, 1, 0).getGreen());
    }

    @Test(expected = IllegalArgumentRangeException.class)
    public void checkGreenValueBelowRangeThrowsException() {
        new Color(0, -1, 0);
    }

    @Test(expected = IllegalArgumentRangeException.class)
    public void checkGreenValueAboveRangeThrowsException() {
        new Color(0, 256, 0);
    }

    @Test
    public void checkConstructorSetsBlue() {
        assertEquals(1, new Color(0, 0, 1).getBlue());
    }

    @Test(expected = IllegalArgumentRangeException.class)
    public void checkBlueValueBelowRangeThrowsException() {
        new Color(0, 0, -1);
    }

    @Test(expected = IllegalArgumentRangeException.class)
    public void checkBlueValueAboveRangeThrowsException() {
        new Color(0, 0, 256);
    }

    @Test
    public void checkColorIsOpaque() {
        assertEquals(255, new Color(0, 0, 0).getAlpha());
    }

    @Test
    public void checkConstructorSetsAlpha() {
        assertEquals(1, new Color(0, 0, 0, 1).getAlpha());
    }

    @Test(expected = IllegalArgumentRangeException.class)
    public void checkAlphaValueBelowRangeThrowsException() {
        new Color(0, 0, 0, -1);
    }

    @Test(expected = IllegalArgumentRangeException.class)
    public void checkAlphaValueAboveRangeThrowsException() {
        new Color(0, 0, 0, 256);
    }

    @Test
    public void checkString() {
        assertNotNull(new Color(0, 0, 0, 0).toString());
    }

    @Test
    public void checkHashCode() {
        assertEquals(new Color(0, 0, 0, 0).hashCode(),
				new Color(0, 0, 0, 0).hashCode());
    }

    @Test
    public void checkSameObjectIsEqual() {
        final Color color = new Color(0, 0, 0, 0);
        assertEquals(color, color);
    }

    @Test
    public void checkSameColorIsEqual() {
        assertEquals(new Color(0, 1, 2, 3), new Color(0, 1, 2, 3));
    }

    @Test
    public void checkDifferentColorIsNotEqual() {
        assertFalse(new Color(1, 2, 3).equals(new Color(3, 2, 1)));
    }

    @Test
    public void checkObjectIsNotEqual() {
        assertFalse(new Color(0, 0, 0).equals(new Object()));
    }

    @Test
    public void checkNullIsNotEqual() {
        assertFalse(new Color(0, 0, 0).equals(null));
    }
}
