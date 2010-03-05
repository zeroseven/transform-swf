/*
 * ActionObject.java
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

package com.flagstone.transform.action;

import java.util.Arrays;


import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * ActionObject is a general-purpose class that can be used to represent any
 * action. It allow actions not supported in the current version of Transform to
 * be decoded and encoded from movies until direct support is provided in the 
 * framework.
 */
public final class ActionObject implements Action {

    private static final int HIGHEST_BYTE_CODE = 127;

    private static final String FORMAT = "ActionObject: { type=%d; data=byte[%s] }";

    private final transient int type;
    private final transient byte[] data;

    /**
     * Creates and initialises an ActionObject using values encoded in the Flash
     * binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public ActionObject(final SWFDecoder coder) throws CoderException {
        type = coder.readByte();

        if (type > HIGHEST_BYTE_CODE) {
            data = coder.readBytes(new byte[coder.readWord(2, false)]);
        } else {
            data = null;
        }
    }

    /**
     * Creates an ActionObject specifying only the type.
     *
     * @param actionType
     *            the value identifying the action when it is encoded.
     */
    public ActionObject(final int actionType) {
        type = actionType;
        data = null;
    }

    /**
     * Creates an ActionObject specifying the type and the data that represents
     * the body of the action encoded in the Flash binary format.
     *
     * @param actionType
     *            the value identifying the action when it is encoded.
     * @param bytes
     *            the body of the action when it is encoded in the Flash format.
     */
    public ActionObject(final int actionType, final byte[] bytes) {
        type = actionType;

        if (bytes == null) {
            throw new NullPointerException();
        }
        data = Arrays.copyOf(bytes, bytes.length);
    }

    /**
     * Creates an ActionObject by copying an existing one.
     *
     * @param object
     *            an ActionObject.
     */
    public ActionObject(final ActionObject object) {
        type = object.type;
        data = object.data;
    }

    /**
     * Returns the type that identifies the type of action when it is encoded in
     * the Flash binary format.
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the encoded data for the action.
     */
    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    /** {@inheritDoc} */
    public ActionObject copy() {
        return new ActionObject(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, type, (data == null ? data : String
                .valueOf(data.length)));
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        final int length;

        if (type > HIGHEST_BYTE_CODE) {
            length = 3 + data.length;
        } else {
            length = 1;
        }
        return length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeByte(type);

        if (type > HIGHEST_BYTE_CODE) {
            coder.writeWord(data.length, 2);
            coder.writeBytes(data);
        }
    }
}
