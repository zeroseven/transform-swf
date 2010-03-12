/*
 * Context.java
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

package com.flagstone.transform.coder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contexts are used to pass information between objects when they are being
 * encoded or decoded.
 */
public final class Context {

    /** TODO(doc). */
    public static final int VERSION = 1;
    /** TODO(doc). */
    public static final int TYPE = 2;
    /** TODO(doc). */
   public static final int TRANSPARENT = 3;
    /** TODO(doc). */
    public static final int WIDE_CODES = 4;
    /** TODO(doc). */
    public static final int ARRAY_EXTENDED = 8;
    /** TODO(doc). */
    public static final int POSTSCRIPT = 9;
    /** TODO(doc). */
    public static final int SCALING_STROKE = 10;
    /** TODO(doc). */
    public static final int FILL_SIZE = 11;
    /** TODO(doc). */
    public static final int LINE_SIZE = 12;
    /** TODO(doc). */
    public static final int ADVANCE_SIZE = 13;
    /** TODO(doc). */
    public static final int GLYPH_SIZE = 14;
    /** TODO(doc). */
    public static final int SHAPE_SIZE = 15;

    private DecoderRegistry registry;
    private Map<Integer, Integer> variables;

    /** TODO(method). */
    public Context() {
        variables = new LinkedHashMap<Integer, Integer>();
    }

    /** TODO(method). */
    public DecoderRegistry getRegistry() {
        return registry;
    }

    /** TODO(method). */
    public void setRegistry(final DecoderRegistry registry) {
        this.registry = registry;
    }

    /** TODO(method). */
    public Map<Integer, Integer> getVariables() {
        return variables;
    }

    /** TODO(method). */
    public void setVariables(final Map<Integer, Integer> map) {
        variables = map;
    }

    /** TODO(method). */
    public Context put(final Integer key, final Integer value) {
        variables.put(key, value);
        return this;
    }
}
