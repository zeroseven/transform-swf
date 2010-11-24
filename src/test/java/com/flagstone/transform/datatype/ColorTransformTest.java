/*
 * ColorTransformTest.java
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

@SuppressWarnings({"PMD.TooManyMethods" })
public final class ColorTransformTest {

    private static final int ARED = 1;
    private static final int AGREEN = 2;
    private static final int ABLUE = 3;
    private static final int AALPHA = 4;

    private static final float MRED = 1.0f;
    private static final float MGREEN = 2.0f;
    private static final float MBLUE = 3.0f;
    private static final float MALPHA = 4.0f;

    @Test
    public void checkConstructorSetsAddRed() {
        assertEquals(ARED, new ColorTransform(
                ARED, AGREEN, ABLUE, AALPHA).getAddRed());
    }

    @Test
    public void checkConstructorSetsAddGreen() {
        assertEquals(AGREEN, new ColorTransform(
                ARED, AGREEN, ABLUE, AALPHA).getAddGreen());
    }

    @Test
    public void checkConstructorSetsAddBlue() {
        assertEquals(ABLUE, new ColorTransform(
                ARED, AGREEN, ABLUE, AALPHA).getAddBlue());
    }

    @Test
    public void checkConstructorSetsAddAlpha() {
        assertEquals(AALPHA, new ColorTransform(
                ARED, AGREEN, ABLUE, AALPHA).getAddAlpha());
    }

    @Test
    public void checkConstructorSetsMultiplyRed() {
        assertEquals(Float.valueOf(MRED),
                Float.valueOf(new ColorTransform(
                        MRED, MGREEN, MBLUE, MALPHA).getMultiplyRed()));
    }

    @Test
    public void checkConstructorSetsMultiplyGreen() {
        assertEquals(Float.valueOf(MGREEN),
                Float.valueOf(new ColorTransform(
                        MRED, AGREEN, ABLUE, MALPHA).getMultiplyGreen()));
    }

    @Test
    public void checkConstructorSetsMultiplyBlue() {
        assertEquals(Float.valueOf(MBLUE),
                Float.valueOf(new ColorTransform(
                        MRED, MGREEN, MBLUE, MALPHA).getMultiplyBlue()));
    }

    @Test
    public void checkConstructorSetsMultiplyAlpha() {
        assertEquals(Float.valueOf(MALPHA),
                Float.valueOf(new ColorTransform(
                        MRED, MGREEN, MBLUE, MALPHA).getMultiplyAlpha()));
    }

    @Test
    public void checkString() {
        assertNotNull(new ColorTransform(
                ARED, AGREEN, ABLUE, AALPHA).toString());
    }

    @Test
    public void checkHashCode() {
        assertEquals(-679687358, new ColorTransform(
                ARED, AGREEN, ABLUE, AALPHA).hashCode());
    }

    @Test
    public void checkSameObjectIsEqual() {
        final ColorTransform color = new ColorTransform(
                ARED, AGREEN, ABLUE, AALPHA);

        assertEquals(color, color);
    }

    @Test
    public void checkSameColorTransformIsEqual() {
        final ColorTransform color = new ColorTransform(
                ARED, AGREEN, ABLUE, AALPHA);
        final ColorTransform other = new ColorTransform(
                ARED, AGREEN, ABLUE, AALPHA);

        assertEquals(color, other);
    }

    @Test
    public void checkDifferentColorTransformIsNotEqual() {
        final ColorTransform color = new ColorTransform(
                ARED, AGREEN, ABLUE, AALPHA);
        final ColorTransform other = new ColorTransform(
                MRED, MGREEN, MBLUE, MALPHA);

        assertFalse(color.equals(other));
    }

    @Test
    public void checkObjectIsNotEqual() {
        final ColorTransform color = new ColorTransform(
                ARED, AGREEN, ABLUE, AALPHA);
        final Object other = new Object();

        assertFalse(color.equals(other));
    }
}
