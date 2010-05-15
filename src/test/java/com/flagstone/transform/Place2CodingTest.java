/*
 * Place2CodingTest.java
 * Transform
 *
 * Copyright (c) 2010 Flagstone Software Ltd. All rights reserved.
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
package com.flagstone.transform;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

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
import com.flagstone.transform.datatype.ColorTransform;
import com.flagstone.transform.datatype.CoordTransform;

@RunWith(Parameterized.class)
public final class Place2CodingTest {

    private static final String RESOURCE =
        "com/flagstone/transform/Place2.yaml";

    private static final String PLACE = "place";
    private static final String IDENTIFIER = "identifier";
    private static final String LAYER = "layer";
    private static final String XCOORD = "xcoord";
    private static final String YCOORD = "ycoord";
    private static final String RED = "red";
    private static final String GREEN = "green";
    private static final String BLUE = "blue";
    private static final String ALPHA = "alpha";
    private static final String DIN = "din";
    private static final String DOUT = "dout";

    @Parameters
    public static Collection<Object[]>  patterns() {

        ClassLoader loader = DoActionCodingTest.class.getClassLoader();
        InputStream other = loader.getResourceAsStream(RESOURCE);
        Yaml yaml = new Yaml();

        Collection<Object[]> list = new ArrayList<Object[]>();

        for (Object data : yaml.loadAll(other)) {
            list.add(new Object[] {data });
        }

        return list;
    }

    private final transient int place;
    private final transient int identifier;
    private final transient int layer;
    private final transient CoordTransform position;
    private final transient ColorTransform color;
    private final transient byte[] din;
    private final transient byte[] dout;
    private final transient Context context;

    public Place2CodingTest(final Map<String, Object>values) {
        place = (Integer) values.get(PLACE);
        identifier = (Integer) values.get(IDENTIFIER);
        layer = (Integer) values.get(LAYER);

        if (values.get(XCOORD) != null) {
            position = CoordTransform.translate(
                    (Integer) values.get(XCOORD), (Integer) values.get(YCOORD));
        } else {
            position = null;
        }

        if (values.get(RED) != null) {
            color = new ColorTransform(
                    (Integer) values.get(RED), (Integer) values.get(GREEN),
                    (Integer) values.get(BLUE), (Integer) values.get(ALPHA)
                    );
        } else {
            color = null;
        }

        din = (byte[]) values.get(DIN);
        dout = (byte[]) values.get(DOUT);
        context = new Context();
    }

    @Test
    public void checkSizeMatchesEncodedSize() throws CoderException {
        final Place2 object = new Place2().setType(PlaceType.NEW)
                .setIdentifier(identifier).setLayer(layer)
                .setTransform(position).setColorTransform(color);
        final SWFEncoder encoder = new SWFEncoder(dout.length);

        assertEquals(dout.length, object.prepareToEncode(context));
    }

    @Test
    public void checkObjectIsEncoded() throws CoderException {
        final Place2 object = new Place2().setType(PlaceType.NEW)
                .setIdentifier(identifier).setLayer(layer)
                .setTransform(position).setColorTransform(color);
        final SWFEncoder encoder = new SWFEncoder(dout.length);

        object.prepareToEncode(context);
        object.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(dout, encoder.getData());
    }

    @Test
    public void decode() throws CoderException {
        final SWFDecoder decoder = new SWFDecoder(din);
        final Place2 object = new Place2(decoder, context);

        assertTrue(decoder.eof());

        assertEquals(place, object.getType().ordinal() + 1);
        assertEquals(identifier, object.getIdentifier());
        assertEquals(layer, object.getLayer());
    }

    @Test
    public void decodePosition() throws CoderException {
        assumeNotNull(position);

        final SWFDecoder decoder = new SWFDecoder(din);
        final Place2 object = new Place2(decoder, context);

        assertTrue(decoder.eof());

        assertEquals(position.getTranslateX(), object.getTransform()
                .getTranslateX());
        assertEquals(position.getTranslateY(), object.getTransform()
                .getTranslateY());
    }

    @Test
    public void decodeColor() throws CoderException {
        assumeNotNull(color);

        final SWFDecoder decoder = new SWFDecoder(din);
        final Place2 object = new Place2(decoder, context);

        assertTrue(decoder.eof());

        assertEquals(color.getAddRed(), object.getColorTransform()
                .getAddRed());
        assertEquals(color.getAddGreen(), object.getColorTransform()
                .getAddGreen());
        assertEquals(color.getAddBlue(), object.getColorTransform()
                .getAddBlue());
    }
}
