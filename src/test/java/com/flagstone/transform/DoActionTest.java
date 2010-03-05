/*
 * DoActionTest.java
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
package com.flagstone.transform;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.flagstone.transform.action.Action;
import com.flagstone.transform.action.ActionData;
import com.flagstone.transform.action.BasicAction;
import com.flagstone.transform.coder.ActionDecoder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DecoderRegistry;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class DoActionTest {

    private static final List<Action> actions = new ArrayList<Action>();

    private static final transient byte[] ENCODED = {(byte) 0x02, 0x03,
        0x04, 0x00};

    private static final transient byte[] EXTENDED = {(byte) 0x3F, 0x03,
        0x02, 0x00, 0x00, 0x00, 0x04, 0x00};

    private transient DoAction fixture;

    @BeforeClass
    public void initialize() {
        actions.add(BasicAction.NEXT_FRAME);
        actions.add(BasicAction.END);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessorForActionsWithNull() {
        fixture = new DoAction();
        fixture.setActions(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAddNullAction() {
        fixture = new DoAction();
        fixture.add(null);
    }

    @Test
    public void checkCopy() {
        fixture = new DoAction(actions);
        assertNotSame(fixture, fixture.copy());
        assertEquals(actions, fixture.copy().getActions());
        assertEquals(fixture.toString(), fixture.copy().toString());
    }

    @Test
    public void encode() throws CoderException {
        final SWFEncoder encoder = new SWFEncoder(ENCODED.length);
        final Context context = new Context();

        fixture = new DoAction(actions);
        assertEquals(4, fixture.prepareToEncode(encoder, context));
        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
        assertArrayEquals(ENCODED, encoder.getData());
    }

    @Test
    public void encodeExtended() throws CoderException {
        final SWFEncoder encoder = new SWFEncoder(106);
        final Context context = new Context();

        fixture = new DoAction();

        for (int i = 0; i < 99; i++) {
            fixture.add(BasicAction.ADD);
        }

        fixture.add(BasicAction.END);

        assertEquals(106, fixture.prepareToEncode(encoder, context));
        fixture.encode(encoder, context);

        assertTrue(encoder.eof());
    }

    @Test
    public void checkDecode() throws CoderException {
        final SWFDecoder decoder = new SWFDecoder(ENCODED);
        final Context context = new Context();
        final DecoderRegistry registry = new DecoderRegistry();
        registry.setActionDecoder(new ActionDecoder());
        context.setRegistry(registry);

        fixture = new DoAction(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(actions, fixture.getActions());
    }

    @Test
    public void checkDecodeExtended() throws CoderException {
        final SWFDecoder decoder = new SWFDecoder(EXTENDED);
        final Context context = new Context();
        final DecoderRegistry registry = new DecoderRegistry();
        registry.setActionDecoder(new ActionDecoder());
        context.setRegistry(registry);

        fixture = new DoAction(decoder, context);

        assertTrue(decoder.eof());
        assertEquals(actions, fixture.getActions());
    }

    @Test
    public void checkDecodeContainsActionData() throws CoderException {
        final SWFDecoder decoder = new SWFDecoder(ENCODED);
        final Context context = new Context();
        final DecoderRegistry registry = new DecoderRegistry();
        context.setRegistry(registry);

        fixture = new DoAction(decoder, context);

        assertEquals(1, fixture.getActions().size());
        assertTrue(fixture.getActions().get(0) instanceof ActionData);
    }
}
