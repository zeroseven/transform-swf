package com.flagstone.transform.factory.font;

import com.flagstone.transform.movie.datatype.Bounds;
import com.flagstone.transform.movie.shape.Shape;

/**
 * Glyph is a simple container class used by Font to record the shape, bounding
 * box and advance for a glyph.
 */
public final class Glyph
{
	protected static final int EM_SQUARE = 1024;

	protected Shape shape;
	protected Bounds bounds;
	protected int advance;

	protected Glyph(Shape shape, Bounds bounds, int advance)
	{
		this.shape = shape;
		this.bounds = bounds;
		this.advance = advance;
	}

	protected Glyph(Shape shape)
	{
		this.shape = shape;
	}

	protected Shape getShape()
	{
		return shape;
	}

	protected void setShape(Shape shape)
	{
		this.shape = shape;
	}

	protected Bounds getBounds()
	{
		return bounds;
	}

	protected void setBounds(Bounds bounds)
	{
		this.bounds = bounds;
	}

	protected int getAdvance()
	{
		return advance;
	}

	protected void setAdvance(int advance)
	{
		this.advance = advance;
	}
}

