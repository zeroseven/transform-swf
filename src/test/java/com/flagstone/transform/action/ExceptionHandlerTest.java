/*
 * ExceptionHandlerTest.java
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
package com.flagstone.transform.action;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DecoderRegistry;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class ExceptionHandlerTest {

    private static String variable = "var";
    private static List<Action> tryActions = new ArrayList<Action>();
    private static List<Action> catchActions = new ArrayList<Action>();
    private static List<Action> finalActions = new ArrayList<Action>();

    static {
        tryActions.add(BasicAction.ADD);
        tryActions.add(BasicAction.END);
        catchActions.add(BasicAction.SUBTRACT);
        catchActions.add(BasicAction.END);
        finalActions.add(BasicAction.MULTIPLY);
        finalActions.add(BasicAction.END);
    }

    private static final transient int TYPE = ActionTypes.EXCEPTION_HANDLER;
    private transient ExceptionHandler fixture;

    private final transient byte[] encoded = new byte[] {(byte) TYPE, 0x11,
            0x00, 0x07, 0x02, 0x00, 0x02, 0x00, 0x02, 0x00, 0x76, 0x61, 0x72,
            0x00, ActionTypes.ADD, ActionTypes.END, ActionTypes.SUBTRACT,
            ActionTypes.END, ActionTypes.MULTIPLY, ActionTypes.END, };

    @Test
    public void checkCopy() {
        fixture = new ExceptionHandler(variable, tryActions, catchActions,
                finalActions);
        final ExceptionHandler copy = fixture.copy();

        assertEquals(fixture.getTryActions(), copy.getTryActions());
        assertEquals(fixture.getCatchActions(), copy.getCatchActions());
        assertEquals(fixture.getFinalActions(), copy.getFinalActions());
        assertEquals(fixture.toString(), copy.toString());
    }

    @Test
    public void encode() throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();

        fixture = new ExceptionHandler(variable, tryActions, catchActions,
                finalActions);
        assertEquals(encoded.length, fixture.prepareToEncode(context));
        fixture.encode(encoder, context);
        encoder.flush();

        assertArrayEquals(encoded, stream.toByteArray());
    }

    @Test
    public void decode() throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(encoded);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final Context context = new Context();
        final DecoderRegistry registry = new DecoderRegistry();
        registry.setActionDecoder(new ActionDecoder());
        context.setRegistry(registry);

        decoder.readByte();
        fixture = new ExceptionHandler(decoder, context);

        assertNotNull(fixture);
        assertEquals(variable, fixture.getVariable());
        assertEquals(tryActions, fixture.getTryActions());
        assertEquals(catchActions, fixture.getCatchActions());
        assertEquals(finalActions, fixture.getFinalActions());
    }
}
