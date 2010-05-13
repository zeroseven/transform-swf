/*
 * ExportCodingTest.java
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
public final class SymbolClassCodingTest {

    private static final String RESOURCE =
        "com/flagstone/transform/Export.yaml";

    private static final String TABLE = "table";
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

    private final transient Map<Integer, String> table;
    private final transient byte[] din;
    private final transient byte[] dout;
    private final transient Context context;

    @SuppressWarnings("unchecked")
    public SymbolClassCodingTest(final Map<String, Object>values) {
        table = (Map<Integer, String>) values.get(TABLE);
        din = (byte[]) values.get(DIN);
        dout = (byte[]) values.get(DOUT);
        context = new Context();
    }

    @Test
    public void checkSizeMatchesEncodedSize() throws CoderException {
        final Export object = new Export(table);
        final SWFEncoder encoder = new SWFEncoder(dout.length);

        assertEquals(dout.length, object.prepareToEncode(encoder, context));
    }

    @Test
    public void checkObjectIsEncoded() throws CoderException {
        final Export object = new Export(table);
        final SWFEncoder encoder = new SWFEncoder(dout.length);

        object.prepareToEncode(encoder, context);
        object.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(dout, encoder.getData());
    }

    @Test
    public void checkObjectIsDecoded() throws CoderException {
        final SWFDecoder decoder = new SWFDecoder(din);
        final Export object = new Export(decoder);

        assertTrue(decoder.eof());
        assertEquals(table, object.getObjects());
    }
}
