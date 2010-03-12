/*
 * CoordTransformCodingTest.java
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
public final class CoordTransformCodingTest {
    
    private static final String RESOURCE = "com/flagstone/transform/datatype/CoordTransform.yaml";
    
    private static final String XSCALE = "xscale";
    private static final String YSCALE = "yscale";
    private static final String XSHEAR = "xshear";
    private static final String YSHEAR = "yshear";
    private static final String XCOORD = "xcoord";
    private static final String YCOORD = "ycoord";
    private static final String DATA = "data";

    @Parameters
    public static Collection<Object[]>  patterns() {

        ClassLoader loader = CoordTransformCodingTest.class.getClassLoader();
        InputStream other = loader.getResourceAsStream(RESOURCE);
        Yaml yaml = new Yaml();
        
        Collection<Object[]> list = new ArrayList<Object[]>();
         
        for (Object data : yaml.loadAll(other)) {
            list.add(new Object[] { data });
        }

        return list;
    }

    private Double xscale;
    private Double yscale;
    private Double xshear;
    private Double yshear;
    private Integer xcoord;
    private Integer ycoord;
    private byte[] data;
    
    private Context context;
    
    public CoordTransformCodingTest(Map<String,Object>values) {
        xscale = values.get(XSCALE) == null ? CoordTransform.DEFAULT_SCALE : (Double)values.get(XSCALE);
        yscale = values.get(YSCALE) == null ? CoordTransform.DEFAULT_SCALE : (Double)values.get(YSCALE);
        xshear = values.get(XSHEAR) == null ? CoordTransform.DEFAULT_SHEAR : (Double)values.get(XSHEAR);
        yshear = values.get(YSHEAR) == null ? CoordTransform.DEFAULT_SHEAR : (Double)values.get(YSHEAR);
        xcoord = values.get(XCOORD) == null ? CoordTransform.DEFAULT_COORD : (Integer)values.get(XCOORD);
        ycoord = values.get(YCOORD) == null ? CoordTransform.DEFAULT_COORD : (Integer)values.get(YCOORD);
        data = (byte[])values.get(DATA);
        
        context = new Context();
    }

    @Test
    public void checkSizeMatchesEncodedSize() throws CoderException {
        
        final CoordTransform transform = new CoordTransform(
                xscale.floatValue(), yscale.floatValue(),
                xshear.floatValue(), yshear.floatValue(),
                xcoord.intValue(), ycoord.intValue());       
        final SWFEncoder encoder = new SWFEncoder(data.length);        
         
        assertEquals(data.length, transform.prepareToEncode(encoder, context));
    }

    @Test
    public void checkTransformIsEncoded() throws CoderException {
        
        final CoordTransform transform = new CoordTransform(
                xscale.floatValue(), yscale.floatValue(),
                xshear.floatValue(), yshear.floatValue(),
                xcoord.intValue(), ycoord.intValue());       
        final SWFEncoder encoder = new SWFEncoder(data.length);        
        
        transform.prepareToEncode(encoder, context);
        transform.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(data, encoder.getData());
    }

    @Test
    public void checkTranformIsDecoded() throws CoderException {
        
        final SWFDecoder decoder = new SWFDecoder(data);
        final CoordTransform transform = new CoordTransform(decoder);

        assertTrue(decoder.eof());
        assertEquals(xscale.floatValue(), transform.getScaleX(), 0.0);
        assertEquals(yscale.floatValue(), transform.getScaleY(), 0.0);
        assertEquals(xshear.floatValue(), transform.getShearX(), 0.0);
        assertEquals(yshear.floatValue(), transform.getShearY(), 0.0);
        assertEquals(xcoord.intValue(), transform.getTranslateX());
        assertEquals(ycoord.intValue(), transform.getTranslateY());
     }
}
