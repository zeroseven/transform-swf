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
public class Glyph {
    /** The outline of the glyph. */
    private Shape shape;
    /** The bounding box that encloses the glyph. */
    private Bounds bounds;
    /** The distance in twips to the next glyph. */
    private int advance;

    /**
     * Create a Glyph with the specified outline, bounding box and advance.
     * @param aShape the outline of the glyph.
     * @param box the bounding box that encloses the glyph.
     * @param dist the advance to the next glyph.
     */
    public Glyph(final Shape aShape, final Bounds box, final int dist) {
        shape = aShape;
        bounds = box;
        advance = dist;
    }

    /**
     * Create a Glyph with the specified outline - the bounding box and advance
     * default to zero.
     * @param aShape the outline of the glyph.
     */
    public Glyph(final Shape aShape) {
        shape = aShape;
    }

   /**
    * Get the outline of the glyph.
    *
    * @return the Shape used to trace the outline of the glyph.
    */
    public final Shape getShape() {
        return shape;
    }

    /**
     * Set the outline of the glyph.
     * @param aShape the Shape used to trace the outline of the glyph.
     */
    public final void setShape(final Shape aShape) {
        shape = aShape;
    }

    /**
     * Get the bounding box enclosing the glyph.
     * @return the Bounds that encloses the outline of the glyph.
     */
    public final Bounds getBounds() {
        return bounds;
    }

    /**
     * Set the bounding box enclosing the glyph.
     * @param box the Bounds that encloses the outline of the glyph.
     */
    public final void setBounds(final Bounds box) {
        bounds = box;
    }

    /**
     * Get the distance to the next glyph.
     * @return the distance in twips from this glyph to the next.
     */
    public final int getAdvance() {
        return advance;
    }

    /**
     * Set the distance to the next glyph.
     * @param dist the distance in twips from this glyph to the next.
     */
    public final void setAdvance(final int dist) {
        advance = dist;
    }
}
