/*
 * ColorTransformTest.java 
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class CoordTransformTest {

    private static final float SCALE_X = 1.0f;
    private static final float SCALE_Y = 2.0f;

    private static final float SHEAR_X = 1.0f;
    private static final float SHEAR_Y = 2.0f;

    private static final int X_COORD = 1;
    private static final int Y_COORD = 2;

    private static final float[][] ROTATE_MATRIX = {
        {1.0f, 4.0f, 0.0f},
        {3.0f, 2.0f, 0.0f},
        {0.0f, 0.0f, 1.0f}};

    private static final byte[] SCALE = {-52, -128, 0, 32, 0, 0, 64};
    private static final byte[] SHEAR = {102, 64, 0, 16, 0, 0, 64};
    private static final byte[] TRANSLATE = {6, 80};
    private static final byte[] ROTATE = {-52, -128, 0, 32, 0, 13, 12, 0, 1, 0, 0, 2, 0};

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
        final float scaleX = 2.0f;
        final float scaleY = 3.0f;
        final float[][] expected = {
                {scaleX, 0.0f, 0.0f},
                {0.0f, scaleY, 0.0f},
                {0.0f, 0.0f, 1.0f}};

        assertTrue(compare(expected,
                CoordTransform.scale(scaleX, scaleY).getMatrix()));
    }

    @Test
    public void checkShear() {
        final float shearX = 2.0f;
        final float shearY = 3.0f;
        final float[][] expected = {
                {1.0f, shearY, 0.0f},
                {shearX, 1.0f, 0.0f},
                {0.0f, 0.0f, 1.0f }};

        assertTrue(compare(expected,
                CoordTransform.shear(shearX, shearY).getMatrix()));
    }

    @Test
    public void checkTranslate() {
        final int xCoord = 2;
        final int yCoord = 3;
        final float[][] expected = {
                {1.0f, 0.0f, xCoord},
                {0.0f, 1.0f, yCoord},
                {0.0f, 0.0f, 1.0f}};

        assertTrue(compare(expected,
                CoordTransform.translate(xCoord, yCoord).getMatrix()));
    }

    @Test
    public void checkRotate() {
        /*
         * Values are stored as 16-bit fixed point numbers so the precision 
         * of the expected result needs to be limited so the comparison passes.
         */
        final int angle = 60;
        final double radians = Math.toRadians(angle);
        final float cos = (float)((int)(Math.cos(radians) * 
                CoordTransform.SCALE_FACTOR))/CoordTransform.SCALE_FACTOR;
        final float sin = (float)((int)(Math.sin(radians) * 
                CoordTransform.SCALE_FACTOR))/CoordTransform.SCALE_FACTOR;

        final float[][] expected = {
                {cos,  -sin, 0.0f},
                {sin,   cos, 0.0f},
                {0.0f, 0.0f, 1.0f}};

        assertTrue(compare(expected, CoordTransform.rotate(angle).getMatrix()));
    }

    @Test
    public void checkConstructorSetsOnlyTranslation() {

        final CoordTransform fixture = CoordTransform.translate(X_COORD, Y_COORD);

        final float[][] expected = {
                {1.0f, 0.0f, X_COORD},
                {0.0f, 1.0f, Y_COORD},
                {0.0f, 0.0f, 1.0f}};

        assertTrue(compare(expected, fixture.getMatrix()));
    }

    @Test
    public void checkEncodeTranslation() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(TRANSLATE.length);
        final Context context = new Context();

        final CoordTransform fixture = CoordTransform.translate(X_COORD, Y_COORD);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(length, TRANSLATE.length);
        assertArrayEquals(TRANSLATE, encoder.getData());
    }

    @Test
    public void checkEncodeScale() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(SCALE.length);
        final Context context = new Context();

        final CoordTransform fixture = CoordTransform.scale(SCALE_X, SCALE_Y);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(length, SCALE.length);
        assertArrayEquals(SCALE, encoder.getData());
    }

    @Test
    public void checkEncodeShear() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(SHEAR.length);
        final Context context = new Context();

        final CoordTransform fixture = CoordTransform.shear(SHEAR_X, SHEAR_Y);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(length, SHEAR.length);
        assertArrayEquals(SHEAR, encoder.getData());
    }

    @Test
    public void checkEncodeRotate() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(ROTATE.length);
        final Context context = new Context();

        final CoordTransform fixture = new CoordTransform(ROTATE_MATRIX);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(length, ROTATE.length);
        assertArrayEquals(ROTATE, encoder.getData());
    }

    @Test
    public void checkDecodeTranslation() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(TRANSLATE);

        final CoordTransform fixture = new CoordTransform(decoder);

        assertTrue(decoder.eof());
        assertEquals(X_COORD, fixture.getMatrix()[0][2]);
        assertEquals(Y_COORD, fixture.getMatrix()[1][2]);
    }

    @Test
    public void checkDecodeScale() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(SCALE);

        final CoordTransform fixture = new CoordTransform(decoder);

        assertTrue(decoder.eof());
        assertEquals(SCALE_X, fixture.getMatrix()[0][0]);
        assertEquals(SCALE_Y, fixture.getMatrix()[1][1]);
    }

    @Test
    public void checkDecodeShear() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(SHEAR);

        final CoordTransform fixture = new CoordTransform(decoder);

        assertTrue(decoder.eof());
        assertEquals(SHEAR_X, fixture.getMatrix()[1][0]);
        assertEquals(SHEAR_Y, fixture.getMatrix()[0][1]);
    }

    @Test
    public void checkDecodeRotate() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(ROTATE);

        final CoordTransform fixture = new CoordTransform(decoder);

        assertTrue(decoder.eof());
        assertTrue(compare(ROTATE_MATRIX, fixture.getMatrix()));
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
