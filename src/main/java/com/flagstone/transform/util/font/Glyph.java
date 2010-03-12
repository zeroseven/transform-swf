/*
 * Glyph.java
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

package com.flagstone.transform.util.font;

import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.shape.Shape;

/**
 * Glyph is a simple container class used by Font to record the shape, bounding
 * box and advance for a glyph.
 */
//TODO(class)
public final class Glyph {

    private Shape shape;
    private Bounds bounds;
    private int advance;

    /** TODO(method). */
    public Glyph(final Shape shape, final Bounds bounds, final int advance) {
        this.shape = shape;
        this.bounds = bounds;
        this.advance = advance;
    }

    /** TODO(method). */
    public Glyph(final Shape shape) {
        this.shape = shape;
    }

    /** TODO(method). */
    public Shape getShape() {
        return shape;
    }

    /** TODO(method). */
    public void setShape(final Shape shape) {
        this.shape = shape;
    }

    /** TODO(method). */
    public Bounds getBounds() {
        return bounds;
    }

    /** TODO(method). */
    public void setBounds(final Bounds bounds) {
        this.bounds = bounds;
    }

    /** TODO(method). */
    public int getAdvance() {
        return advance;
    }

    /** TODO(method). */
    public void setAdvance(final int advance) {
        this.advance = advance;
    }
}
