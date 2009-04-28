package com.flagstone.transform.util.font;

import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.shape.Shape;

/**
 * Glyph is a simple container class used by Font to record the shape, bounding
 * box and advance for a glyph.
 */
public final class Glyph {

    private Shape shape;
    private Bounds bounds;
    private int advance;

    public Glyph(final Shape shape, final Bounds bounds, final int advance) {
        this.shape = shape;
        this.bounds = bounds;
        this.advance = advance;
    }

    public Glyph(final Shape shape) {
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(final Shape shape) {
        this.shape = shape;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(final Bounds bounds) {
        this.bounds = bounds;
    }

    public int getAdvance() {
        return advance;
    }

    public void setAdvance(final int advance) {
        this.advance = advance;
    }
}
