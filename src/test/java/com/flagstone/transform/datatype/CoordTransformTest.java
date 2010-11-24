/*
 * CoordTransformTest.java
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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class CoordTransformTest {

    private static final float SCALEX = 2.0f;
    private static final float SCALEY = 3.0f;
    private static final float SHEARX = 2.0f;
    private static final float SHEARY = 3.0f;
    private static final int XCOORD = 2;
    private static final int YCOORD = 3;

    @Test
    public void checkProduct() {
        final float[][] left = {
                {1.0f, 1.0f, 1.0f},
                {2.0f, 2.0f, 2.0f},
                {3.0f, 3.0f, 3.0f}};

        final float[][] right = {
                {1.0f, 2.0f, 3.0f},
                {1.0f, 2.0f, 3.0f},
                {1.0f, 2.0f, 3.0f}};

        final float[][] expected = {
                {3.0f, 6.0f, 9.0f},
                {6.0f, 12.0f, 18.0f},
                {9.0f, 18.0f, 27.0f} };

        assertTrue(compare(expected, CoordTransform.product(left, right)));
    }

    @Test
    public void checkScale() {
        final float[][] expected = {
                {SCALEX, 0.0f, 0.0f},
                {0.0f, SCALEY, 0.0f},
                {0.0f, 0.0f, 1.0f}};

        assertTrue(compare(expected,
                CoordTransform.scale(SCALEX, SCALEY).getMatrix()));
    }

    @Test
    public void checkShear() {
        final float[][] expected = {
                {1.0f, SHEARY, 0.0f},
                {SHEARX, 1.0f, 0.0f},
                {0.0f, 0.0f, 1.0f }};

        assertTrue(compare(expected,
                CoordTransform.shear(SHEARX, SHEARY).getMatrix()));
    }

    @Test
    public void checkTranslate() {
        final float[][] expected = {
                {1.0f, 0.0f, XCOORD},
                {0.0f, 1.0f, YCOORD},
                {0.0f, 0.0f, 1.0f}};

        assertTrue(compare(expected,
                CoordTransform.translate(XCOORD, YCOORD).getMatrix()));
    }

    @Test
    public void checkRotate() {
        /*
         * Values are stored as 16-bit fixed point numbers so the precision
         * of the expected result needs to be limited so the comparison passes.
         */
        final double radians = Math.toRadians(60);
        final float cos = ((int) (Math.cos(radians)
                * CoordTransform.SCALE_FACTOR)) / CoordTransform.SCALE_FACTOR;
        final float sin = ((int) (Math.sin(radians)
                * CoordTransform.SCALE_FACTOR)) / CoordTransform.SCALE_FACTOR;

        final float[][] expected = {
                {cos,  -sin, 0.0f},
                {sin,   cos, 0.0f},
                {0.0f, 0.0f, 1.0f}};

        assertTrue(compare(expected, CoordTransform.rotate(60).getMatrix()));
    }

    @Test
    public void checkConstructorSetsOnlyTranslation() {

        final CoordTransform fixture = CoordTransform.translate(1, 2);

        final float[][] expected = {
                {1.0f, 0.0f, 1.0f},
                {0.0f, 1.0f, 2.0f},
                {0.0f, 0.0f, 1.0f}};

        assertTrue(compare(expected, fixture.getMatrix()));
    }

    private boolean compare(final float[][] left, final float[][] right) {
        boolean result = true;

        for (int i = 0; i < left.length; i++) {
            for (int j = 0; j < left[i].length; j++) {
                if (left[i][j] != right[i][j]) {
                    result = false;
                }
            }
        }
        return result;
    }
}
