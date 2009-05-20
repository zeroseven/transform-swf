/*
 * ScenesAndLabels.java
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

import java.util.LinkedHashMap;
import java.util.Map;

import com.flagstone.transform.exception.StringSizeException;
import com.flagstone.transform.exception.IllegalArgumentRangeException;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/** TODO(class). */
public final class ScenesAndLabels implements MovieTag {

    private static final String FORMAT = "ScenesAndLabels: { scenes=%s;"
            + " labels=%s }";

    private Map<Integer, String> scenes;
    private Map<Integer, String> labels;

    private transient int length;

    /**
     * Creates and initialises a ScenesAndLabels object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public ScenesAndLabels(final SWFDecoder coder) throws CoderException {

        final int start = coder.getPointer();

        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        int count = coder.readVariableU32();

        scenes = new LinkedHashMap<Integer, String>();
        labels = new LinkedHashMap<Integer, String>();
        for (int i = 0; i < count; i++) {
            scenes.put(coder.readVariableU32(), coder.readString());
        }

        count = coder.readVariableU32();

        for (int i = 0; i < count; i++) {
            labels.put(coder.readVariableU32(), coder.readString());
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates a ScenesAndLabels object with empty tables for the scenes and 
     * labels.
     */
    public ScenesAndLabels() {
        scenes = new LinkedHashMap<Integer, String>();
        labels = new LinkedHashMap<Integer, String>();
    }

    /** TODO(method). */
    public ScenesAndLabels(final Map<Integer, String> scenes,
            final Map<Integer, String> labels) {
        this.scenes = scenes;
        this.labels = labels;
    }

    /**
     * Creates and initialises a ScenesAndLabels object using the values copied
     * from another ScenesAndLabels object.
     *
     * @param object
     *            a ScenesAndLabels object from which the values will be
     *            copied.
     */
    public ScenesAndLabels(final ScenesAndLabels object) {
        scenes = new LinkedHashMap<Integer, String>(object.scenes);
        labels = new LinkedHashMap<Integer, String>(object.labels);
    }

    /** TODO(method). */
    public ScenesAndLabels addScene(final int offset, final String name) {
        if ((offset < 0) || (offset > 65535)) {
            throw new IllegalArgumentRangeException(0, 65535, offset);
        }
        if (name == null) {
            throw new NullPointerException();
        }
        if (name.length() == 0) {
            throw new StringSizeException(0, Integer.MAX_VALUE, 0);
        }
        scenes.put(offset, name);
        return this;
    }

    /** TODO(method). */
    public Map<Integer, String> getScenes() {
        return scenes;
    }

    /** TODO(method). */
    public void setScenes(final Map<Integer, String> map) {
        if (map == null) {
            throw new NullPointerException();
        }
        scenes = map;
    }

    /** TODO(method). */
    public ScenesAndLabels addLabel(final int offset, final String name) {
        if ((offset < 0) || (offset > 65535)) {
            throw new IllegalArgumentRangeException(0, 65535, offset);
        }
        if (name == null) {
            throw new NullPointerException();
        }
        if (name.length() == 0) {
            throw new StringSizeException(0, Integer.MAX_VALUE, 0);
        }
        labels.put(offset, name);
        return this;
    }

    /** TODO(method). */
    public Map<Integer, String> getLabels() {
        return labels;
    }

    /** TODO(method). */
    public void setLabels(final Map<Integer, String> map) {
        if (map == null) {
            throw new NullPointerException();
        }
        labels = map;
    }

    /** {@inheritDoc} */
    public ScenesAndLabels copy() {
        return new ScenesAndLabels(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, scenes, labels);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {

        length = SWFEncoder.sizeVariableU32(scenes.size());

        for (final Integer offset : scenes.keySet()) {
            length += SWFEncoder.sizeVariableU32(offset)
                    + coder.strlen(scenes.get(offset));
        }

        length += SWFEncoder.sizeVariableU32(labels.size());

        for (final Integer offset : labels.keySet()) {
            length += SWFEncoder.sizeVariableU32(offset)
                    + coder.strlen(labels.get(offset));
        }

        return (length > 62 ? 6 : 2) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {

        final int start = coder.getPointer();

        if (length > 62) {
            coder.writeWord((MovieTypes.SCENES_AND_LABELS << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.SCENES_AND_LABELS << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);

        coder.writeVariableU32(scenes.size());

        for (final Integer identifier : scenes.keySet()) {
            coder.writeVariableU32(identifier.intValue());
            coder.writeString(scenes.get(identifier));
        }

        coder.writeVariableU32(labels.size());

        for (final Integer identifier : labels.keySet()) {
            coder.writeVariableU32(identifier.intValue());
            coder.writeString(labels.get(identifier));
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
