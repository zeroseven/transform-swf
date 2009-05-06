/*
 * ColorTransformTest.java 
 * Transform
 *
 * Copyright (c) 2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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

    private static float scaleX = 1.0f;
    private static float scaleY = 2.0f;

    private static float shearX = 1.0f;
    private static float shearY = 2.0f;

    private static int xCoord = 1;
    private static int yCoord = 2;

    private static float[][] rotateMatrix = {
        {1.0f, 4.0f, 0.0f},
        {3.0f, 2.0f, 0.0f}, 
        {0.0f, 0.0f, 1.0f}};

    private static byte[] scale = {-52, -128, 0, 32, 0, 0, 64};
    private static byte[] shear = {102, 64, 0, 16, 0, 0, 64};
    private static byte[] translate = {6, 80};
    private static byte[] rotate = {-52, -128, 0, 32, 0, 13, 12, 0, 1, 0, 0, 2, 0};

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
                {2.0f, 0.0f, 0.0f},
                {0.0f, 3.0f, 0.0f}, 
                {0.0f, 0.0f, 1.0f}};

        assertTrue(compare(expected, CoordTransform.scale(2.0f, 3.0f).getMatrix()));
    }

    @Test
    public void checkShear() {
        final float[][] expected = {
                {1.0f, 3.0f, 0.0f},
                {2.0f, 1.0f, 0.0f},
                {0.0f, 0.0f, 1.0f }};

        assertTrue(compare(expected, CoordTransform.shear(2.0f, 3.0f).getMatrix()));
    }

    @Test
    public void checkTranslate() {
        final float[][] expected = {
                {1.0f, 0.0f, 2.0f},
                {0.0f, 1.0f, 3.0f},
                {0.0f, 0.0f, 1.0f}};

        assertTrue(compare(expected, CoordTransform.translate(2, 3).getMatrix()));
    }

    @Test
    public void checkRotate() {
        final float[][] expected = {
                {0.5f, -0.8660126f, 0.0f},
                {0.8660126f, 0.5f, 0.0f},
                {0.0f, 0.0f, 1.0f}};

        assertTrue(compare(expected, CoordTransform.rotate(60).getMatrix()));
    }

    @Test
    public void checkConstructorSetsOnlyTranslation() {

        final CoordTransform fixture = CoordTransform.translate(xCoord, yCoord);

        final float[][] expected = {
                {1.0f, 0.0f, xCoord},
                {0.0f, 1.0f, yCoord}, 
                {0.0f, 0.0f, 1.0f}};

        assertTrue(compare(expected, fixture.getMatrix()));
    }

    @Test
    public void checkEncodeTranslation() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(translate.length);
        final Context context = new Context();

        final CoordTransform fixture = CoordTransform.translate(xCoord, yCoord);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(length, translate.length);
        assertArrayEquals(translate, encoder.getData());
    }

    @Test
    public void checkEncodeScale() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(scale.length);
        final Context context = new Context();

        final CoordTransform fixture = CoordTransform.scale(scaleX, scaleY);
        final int length = fixture.prepareToEncode(encoder, context);
        
        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(length, scale.length);
        assertArrayEquals(scale, encoder.getData());
    }

    @Test
    public void checkEncodeShear() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(shear.length);
        final Context context = new Context();

        final CoordTransform fixture = CoordTransform.shear(shearX, shearY);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(length, shear.length);
        assertArrayEquals(shear, encoder.getData());
    }

    @Test
    public void checkEncodeRotate() throws CoderException {

        final SWFEncoder encoder = new SWFEncoder(rotate.length);
        final Context context = new Context();

        final CoordTransform fixture = new CoordTransform(rotateMatrix);
        final int length = fixture.prepareToEncode(encoder, context);

        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertEquals(length, rotate.length);
        assertArrayEquals(rotate, encoder.getData());
    }

    @Test
    public void checkDecodeTranslation() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(translate);

        final CoordTransform fixture = new CoordTransform(decoder);

        assertTrue(decoder.eof());
        assertEquals(xCoord, fixture.getMatrix()[0][2]);
        assertEquals(yCoord, fixture.getMatrix()[1][2]);
    }

    @Test
    public void checkDecodeScale() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(scale);

        final CoordTransform fixture = new CoordTransform(decoder);

        assertTrue(decoder.eof());
        assertEquals(scaleX, fixture.getMatrix()[0][0]);
        assertEquals(scaleY, fixture.getMatrix()[1][1]);
    }

    @Test
    public void checkDecodeShear() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(shear);

        final CoordTransform fixture = new CoordTransform(decoder);

        assertTrue(decoder.eof());
        assertEquals(shearX, fixture.getMatrix()[1][0]);
        assertEquals(shearY, fixture.getMatrix()[0][1]);
    }

    @Test
    public void checkDecodeRotate() throws CoderException {

        final SWFDecoder decoder = new SWFDecoder(rotate);

        final CoordTransform fixture = new CoordTransform(decoder);

        assertTrue(decoder.eof());
        assertTrue(compare(rotateMatrix, fixture.getMatrix()));
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
