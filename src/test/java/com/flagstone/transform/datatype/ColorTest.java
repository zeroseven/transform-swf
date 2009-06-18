/*
 * ColorTest.java
 * Transform
 *
 * Copyright (c) 2009 Flagstone Software Ltd. All rights reserved.
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

    private static final int RED = 1;
    private static final int GREEN = 2;
    private static final int BLUE = 3;
    private static final int ALPHA = 4;

    private static final int TOO_LOW = Color.MIN_LEVEL-1;
    private static final int TOO_HIGH = Color.MAX_LEVEL+1;

    @Test
    public void checkConstructorSetsRed() {
        assertEquals(RED, new Color(RED, GREEN, BLUE, ALPHA).getRed());
    }

    @Test(expected=IllegalArgumentRangeException.class)
    public void checkRedValueBelowRangeThrowsException() {
        new Color(TOO_LOW, GREEN, BLUE, ALPHA);    
    }

    @Test(expected=IllegalArgumentRangeException.class)
    public void checkRedValueAboveRangeThrowsException() {
        new Color(TOO_HIGH, GREEN, BLUE, ALPHA);    
    }

    @Test
    public void checkConstructorSetsGreen() {
        assertEquals(GREEN, new Color(RED, GREEN, BLUE, ALPHA).getGreen());
    }

    @Test(expected=IllegalArgumentRangeException.class)
    public void checkGreenValueBelowRangeThrowsException() {
        new Color(RED, TOO_LOW, BLUE, ALPHA);    
    }

    @Test(expected=IllegalArgumentRangeException.class)
    public void checkGreenValueAboveRangeThrowsException() {
        new Color(RED, TOO_HIGH, BLUE, ALPHA);    
    }

    @Test
    public void checkConstructorSetsBlue() {
        assertEquals(BLUE, new Color(RED, GREEN, BLUE, ALPHA).getBlue());
    }

    @Test(expected=IllegalArgumentRangeException.class)
    public void checkBlueValueBelowRangeThrowsException() {
        new Color(RED, GREEN, TOO_LOW, ALPHA);    
    }

    @Test(expected=IllegalArgumentRangeException.class)
    public void checkBlueValueAboveRangeThrowsException() {
        new Color(RED, GREEN, TOO_HIGH, ALPHA);    
    }

    @Test
    public void checkConstructorSetsAlpha() {
        assertEquals(ALPHA, new Color(RED, GREEN, BLUE, ALPHA).getAlpha());
    }

    @Test(expected=IllegalArgumentRangeException.class)
    public void checkAlphaValueBelowRangeThrowsException() {
        new Color(RED, GREEN, BLUE, TOO_LOW);    
    }

    @Test(expected=IllegalArgumentRangeException.class)
    public void checkAlphaValueAboveRangeThrowsException() {
        new Color(RED, GREEN, BLUE, TOO_HIGH);    
    }

    @Test
    public void checkString() {
        assertNotNull(new Color(RED, GREEN, BLUE, ALPHA).toString());
    }

    @Test
    public void checkHashCode() {
        assertEquals(31810, new Color(RED, GREEN, BLUE, ALPHA).hashCode());
    }

    @Test
    public void checkSameObjectIsEqual() {
        final Color color = new Color(RED, GREEN, BLUE, ALPHA);

        assertEquals(color, color);
    }

    @Test
    public void checkSameColorIsEqual() {
        final Color color = new Color(RED, GREEN, BLUE, ALPHA);
        final Color other = new Color(RED, GREEN, BLUE, ALPHA);

        assertEquals(color, other);
    }

    @Test
    public void checkDifferentColorIsNotEqual() {
        final Color color = new Color(RED, GREEN, BLUE, ALPHA);
        final Color other = new Color(RED, GREEN, BLUE);

        assertFalse(color.equals(other));
    }

    @Test
    public void checkObjectIsNotEqual() {
        final Color color = new Color(RED, GREEN, BLUE, ALPHA);
        final Object other = new Object();

        assertFalse(color.equals(other));
    }

    @Test
    public void checkNullIsNotEqual() {
        final Color color = new Color(RED, GREEN, BLUE, ALPHA);
        final Color other = null;

        assertFalse(color.equals(other));
    }
}
