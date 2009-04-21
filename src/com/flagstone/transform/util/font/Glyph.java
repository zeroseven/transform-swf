package com.flagstone.transform.util.font;

import com.flagstone.transform.Bounds;
import com.flagstone.transform.shape.Shape;

/**
 * Glyph is a simple container class used by Font to record the shape, bounding
 * box and advance for a glyph.
 */
public final class Glyph
{
	private static final int EM_SQUARE = 1024;

	private Shape shape;
	private Bounds bounds;
	private int advance;

	public Glyph(Shape shape, Bounds bounds, int advance)
	{
		this.shape = shape;
		this.bounds = bounds;
		this.advance = advance;
	}

	public Glyph(Shape shape)
	{
		this.shape = shape;
	}

	public Shape getShape()
	{
		return shape;
	}

	public void setShape(Shape shape)
	{
		this.shape = shape;
	}

	public Bounds getBounds()
	{
		return bounds;
	}

	public void setBounds(Bounds bounds)
	{
		this.bounds = bounds;
	}

	public int getAdvance()
	{
		return advance;
	}

	public void setAdvance(int advance)
	{
		this.advance = advance;
	}
}

