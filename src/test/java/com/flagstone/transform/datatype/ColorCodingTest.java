/*
 * ColorCodingTest.java
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
public final class ColorCodingTest {

    private static final String RESOURCE =
        "com/flagstone/transform/datatype/Color.yaml";

    private static final String RED = "red";
    private static final String GREEN = "green";
    private static final String BLUE = "blue";
    private static final String ALPHA = "alpha";
    private static final String DATA = "data";

    @Parameters
    public static Collection<Object[]>  patterns() {

        ClassLoader loader = ColorCodingTest.class.getClassLoader();
        InputStream other = loader.getResourceAsStream(RESOURCE);
        Yaml yaml = new Yaml();

        Collection<Object[]> list = new ArrayList<Object[]>();

        for (Object data : yaml.loadAll(other)) {
            list.add(new Object[] {data });
        }

        return list;
    }

    private final Integer red;
    private final Integer green;
    private final Integer blue;
    private final Integer alpha;
    private final byte[] data;

    private final Context context;

    public ColorCodingTest(final Map<String, Object>values) {
        red = (Integer) values.get(RED);
        green = (Integer) values.get(GREEN);
        blue = (Integer) values.get(BLUE);
        alpha = (Integer) values.get(ALPHA);
        data = (byte[]) values.get(DATA);

        context = new Context();

        if (data.length == 4) {
            context.put(Context.TRANSPARENT, 1);
        }
    }

    @Test
    public void checkSizeMatchesEncodedSize() throws CoderException {
        final Color color = new Color(red, green, blue, alpha);
        final SWFEncoder encoder = new SWFEncoder(data.length);

        assertEquals(data.length, color.prepareToEncode(encoder, context));
    }

    @Test
    public void checkColorIsEncoded() throws CoderException {
        final Color color = new Color(red, green, blue, alpha);
        final SWFEncoder encoder = new SWFEncoder(data.length);

        color.prepareToEncode(encoder, context);
        color.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(data, encoder.getData());
    }

    @Test
    public void checkColorIsDecoded() throws CoderException {
        final SWFDecoder decoder = new SWFDecoder(data);
        final Color color = new Color(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(red.intValue(), color.getRed());
        assertEquals(green.intValue(), color.getGreen());
        assertEquals(blue.intValue(), color.getBlue());
        assertEquals(alpha.intValue(), color.getAlpha());
    }
}
