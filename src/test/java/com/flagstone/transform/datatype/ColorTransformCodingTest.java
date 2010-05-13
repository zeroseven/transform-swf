/*
 * ColorTransformCodingTest.java
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.yaml.snakeyaml.Yaml;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

@RunWith(Parameterized.class)
public final class ColorTransformCodingTest {

    private static final String RESOURCE =
        "com/flagstone/transform/datatype/ColorTransform.yaml";

    private static final Float DEFAULT_MULTIPLY = 1.0f;
    private static final int DEFAULT_ADD = 0;

    private static final String MRED = "mred";
    private static final String MGREEN = "mgreen";
    private static final String MBLUE = "mblue";
    private static final String MALPHA = "malpha";
    private static final String ARED = "ared";
    private static final String AGREEN = "agreen";
    private static final String ABLUE = "ablue";
    private static final String AALPHA = "aalpha";
    private static final String DATA = "data";

    @Parameters
    public static Collection<Object[]>  patterns() {

        ClassLoader loader = ColorTransformCodingTest.class.getClassLoader();
        InputStream other = loader.getResourceAsStream(RESOURCE);
        Yaml yaml = new Yaml();

        Collection<Object[]> list = new ArrayList<Object[]>();

        for (Object data : yaml.loadAll(other)) {
            list.add(new Object[] {data });
        }

        return list;
    }

    private final Double mred;
    private final Double mgreen;
    private final Double mblue;
    private final Double malpha;
    private final Integer ared;
    private final Integer agreen;
    private final Integer ablue;
    private final Integer aalpha;
    private final byte[] data;

    private final Context context;

    public ColorTransformCodingTest(final Map<String, Object>values) {
        mred = (Double) values.get(MRED);
        mgreen = (Double) values.get(MGREEN);
        mblue = (Double) values.get(MBLUE);
        if (values.get(MALPHA) == null) {
            malpha = 1.0;
        } else {
            malpha = (Double) values.get(MALPHA);
        }
        ared = (Integer) values.get(ARED);
        agreen = (Integer) values.get(AGREEN);
        ablue = (Integer) values.get(ABLUE);
        if (values.get(AALPHA) == null) {
            aalpha = 0;
        } else {
            aalpha = (Integer) values.get(AALPHA);
        }
        data = (byte[]) values.get(DATA);

        context = new Context();

        if (malpha != null || aalpha != null) {
            context.put(Context.TRANSPARENT, 1);
        }
    }

    @Test
    public void checkSizeMatchesEncodedSizeForAdd() throws CoderException {

        Assume.assumeNotNull(ared);
        Assume.assumeTrue(mred == null);

        final ColorTransform transform = new ColorTransform(ared, agreen, ablue,
                aalpha);
        final SWFEncoder encoder = new SWFEncoder(data.length);

        assertEquals(data.length, transform.prepareToEncode(encoder, context));
    }

    @Test
    public void checkSizeMatchesEncodedSizeForMultiply() throws CoderException {

        Assume.assumeTrue(ared == null);
        Assume.assumeNotNull(mred);

        final ColorTransform transform = new ColorTransform(mred.floatValue(),
                mgreen.floatValue(), mblue.floatValue(), malpha.floatValue());
        final SWFEncoder encoder = new SWFEncoder(data.length);

        assertEquals(data.length, transform.prepareToEncode(encoder, context));
    }

    @Test
    public void checkSizeMatchesEncodedSize() throws CoderException {

        Assume.assumeNotNull(ared);
        Assume.assumeNotNull(mred);

        final ColorTransform transform = new ColorTransform(
                ared, agreen, ablue, aalpha,
                mred.floatValue(), mgreen.floatValue(), mblue.floatValue(),
                malpha.floatValue());
        final SWFEncoder encoder = new SWFEncoder(data.length);

        assertEquals(data.length, transform.prepareToEncode(encoder, context));
    }

    @Test
    public void checkAddIsEncoded() throws CoderException {

        Assume.assumeNotNull(ared);
        Assume.assumeTrue(mred == null);

        final ColorTransform transform = new ColorTransform(ared, agreen, ablue,
                aalpha);
        final SWFEncoder encoder = new SWFEncoder(data.length);

        transform.prepareToEncode(encoder, context);
        transform.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(data, encoder.getData());
    }

    @Test
    public void checkMultiplyIsEncoded() throws CoderException {

        Assume.assumeTrue(ared == null);
        Assume.assumeNotNull(mred);

        final ColorTransform transform = new ColorTransform(mred.floatValue(),
                mgreen.floatValue(), mblue.floatValue(), malpha.floatValue());
        final SWFEncoder encoder = new SWFEncoder(data.length);

        transform.prepareToEncode(encoder, context);
        transform.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(data, encoder.getData());
    }

    @Test
    public void checkObjectIsEncoded() throws CoderException {

        Assume.assumeNotNull(ared);
        Assume.assumeNotNull(mred);

        final ColorTransform transform = new ColorTransform(
                ared, agreen, ablue, aalpha,
                mred.floatValue(), mgreen.floatValue(), mblue.floatValue(),
                malpha.floatValue());
        final SWFEncoder encoder = new SWFEncoder(data.length);

        transform.prepareToEncode(encoder, context);
        transform.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(data, encoder.getData());
    }

    @Test
    public void checkAddIsDecoded() throws CoderException {

        Assume.assumeNotNull(ared);
        Assume.assumeTrue(mred == null);

        final SWFDecoder decoder = new SWFDecoder(data);
        final ColorTransform transform = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(ared.intValue(), transform.getAddRed());
        assertEquals(agreen.intValue(), transform.getAddGreen());
        assertEquals(ablue.intValue(), transform.getAddBlue());
        assertEquals(aalpha.intValue(), transform.getAddAlpha());
        assertEquals(DEFAULT_MULTIPLY, new Float(transform.getMultiplyRed()));
        assertEquals(DEFAULT_MULTIPLY, new Float(transform.getMultiplyGreen()));
        assertEquals(DEFAULT_MULTIPLY, new Float(transform.getMultiplyBlue()));
        assertEquals(DEFAULT_MULTIPLY, new Float(transform.getMultiplyAlpha()));
    }

    @Test
    public void checkMultiplyIsDecoded() throws CoderException {

        Assume.assumeTrue(ared == null);
        Assume.assumeNotNull(mred);

        final SWFDecoder decoder = new SWFDecoder(data);
        final ColorTransform transform = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(DEFAULT_ADD, transform.getAddRed());
        assertEquals(DEFAULT_ADD, transform.getAddGreen());
        assertEquals(DEFAULT_ADD, transform.getAddBlue());
        assertEquals(DEFAULT_ADD, transform.getAddAlpha());
        assertEquals(mred, new Double(transform.getMultiplyRed()));
        assertEquals(mgreen, new Double(transform.getMultiplyGreen()));
        assertEquals(mblue, new Double(transform.getMultiplyBlue()));
        assertEquals(malpha, new Double(transform.getMultiplyAlpha()));
    }

    @Test
    public void checkObjectIsDecoded() throws CoderException {

        Assume.assumeNotNull(ared);
        Assume.assumeNotNull(mred);

        final SWFDecoder decoder = new SWFDecoder(data);
        final ColorTransform transform = new ColorTransform(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(ared.intValue(), transform.getAddRed());
        assertEquals(agreen.intValue(), transform.getAddGreen());
        assertEquals(ablue.intValue(), transform.getAddBlue());
        assertEquals(aalpha.intValue(), transform.getAddAlpha());
        assertEquals(mred, new Double(transform.getMultiplyRed()));
        assertEquals(mgreen, new Double(transform.getMultiplyGreen()));
        assertEquals(mblue, new Double(transform.getMultiplyBlue()));
        assertEquals(malpha, new Double(transform.getMultiplyAlpha()));
    }
}
