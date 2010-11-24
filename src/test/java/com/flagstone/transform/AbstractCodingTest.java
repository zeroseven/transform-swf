/*
 * AbstractCodingTest.java
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DecoderRegistry;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;

@SuppressWarnings({"PMD.AbstractClassWithoutAbstractMethod" })
public abstract class AbstractCodingTest {

    protected static final String CALCULATED_LENGTH =
        "Incorrect calculated length";
    protected static final String NOT_ENCODED =
        "Object was not encoded properly";
    protected static final String NOT_DECODED =
        "Object was not decoded properly";

    private final transient DecoderRegistry registry =
        DecoderRegistry.getDefault();

    protected final int prepare(final MovieTag object) throws IOException {
        final Context context = new Context();
        return object.prepareToEncode(context);
    }

    protected final int prepare(final MovieTag object, final Context context)
            throws IOException {
         return object.prepareToEncode(context);
    }

    protected final byte[] encode(final MovieTag object) throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        final Context context = new Context();
        object.prepareToEncode(context);
        object.encode(encoder, context);
        encoder.flush();
        return stream.toByteArray();
    }

    protected final byte[] encode(final MovieTag object, final Context context)
            throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final SWFEncoder encoder = new SWFEncoder(stream);
        context.setRegistry(registry);
        object.prepareToEncode(context);
        object.encode(encoder, context);
        encoder.flush();
        return stream.toByteArray();
    }

    protected final MovieTag decodeMovieTag(final byte[] bytes)
            throws IOException {
        final SWFFactory<MovieTag> factory = registry.getMovieDecoder();
        final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        final SWFDecoder decoder = new SWFDecoder(stream);
        final Context context = new Context();
        context.setRegistry(registry);
        final List<MovieTag> list = new ArrayList<MovieTag>();
        factory.getObject(list, decoder, context);
        return list.get(0);
    }
}
