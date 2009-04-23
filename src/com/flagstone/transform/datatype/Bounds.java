/*
 * Bounds.java
 * Transform
 * 
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.datatype;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFEncodeable;
import com.flagstone.transform.coder.Encoder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * <p>
 * The Bounds class defines the area inside which shapes, text fields and
 * characters are drawn.
 * <p>
 * 
 * <p>
 * In Flash the axes are specified relative to the top left corner of the screen
 * and the bounding area is defined by two pairs of coordinates that identify
 * the top left and bottom right corners of a rectangle.
 * </p>
 * 
 * <img src="doc-files/bounds.gif">
 * 
 * <p>
 * The coordinates for each corner also specify the coordinate range so
 * specifying a bounding rectangle with the points (-100,-100) and (100,100)
 * defines a rectangle 200 twips by 200 twips with the point (0,0) located in
 * the centre. Specifying the points (0,0) and (200,200) defines a rectangle
 * with the same size however the centre is now located at (100,100).
 * </p>
 * 
 * <p>
 * The bounding rectangle does not clip the object when it is drawn. Lines and
 * curves drawn outside of the rectangle will still be displayed. However if the
 * position of the object is changed or another object is displayed in front of
 * it then only the pixels inside of the bounding box will be repainted.
 * </p>
 */
//TODO(doc) Check comments for all methods 
public final class Bounds implements SWFEncodeable {

	private static final String FORMAT = "Bounds: { minX=%d; minY=%d; maxX=%d; maxY=%d }";

	private final transient int minX;
	private final transient int minY;
	private final transient int maxX;
	private final transient int maxY;

	private transient int size;

	public Bounds(final SWFDecoder coder) throws CoderException {
		coder.alignToByte(); //TODO(optimise) See if this can be removed
		size = coder.readBits(5, false);
		minX = coder.readBits(size, true);
		maxX = coder.readBits(size, true);
		minY = coder.readBits(size, true);
		maxY = coder.readBits(size, true);
		coder.alignToByte();
	}

	/**
	 * Creates a Bounds object representing a rectangle with the corners at
	 * (xmin,ymin) and (xmax,ymax).
	 * 
	 * @param xmin
	 *            x-coordinate of the top left corner.
	 * @param ymin
	 *            y-coordinate of the top left corner.
	 * @param xmax
	 *            x-coordinate of bottom right corner.
	 * @param ymax
	 *            y-coordinate of bottom right corner.
	 */
	public Bounds(final int xmin, final int ymin, final int xmax, final int ymax) {
		minX = xmin;
		minY = ymin;
		maxX = xmax;
		maxY = ymax;
	}

	/**
	 * Returns the x-coordinate of the top left corner of the bounding rectangle
	 * as seen on a screen.
	 */
	public int getMinX() {
		return minX;
	}

	/**
	 * Returns the x-coordinate of the bottom right corner of the bounding
	 * rectangle as seen on a screen.
	 */
	public int getMaxX() {
		return maxX;
	}

	/**
	 * Returns the y-coordinate of the top left corner of the bounding rectangle
	 * as seen on a screen.
	 */
	public int getMinY() {
		return minY;
	}

	/**
	 * Returns the y-coordinate of the bottom right corner of the bounding
	 * rectangle as seen on a screen.
	 */
	public int getMaxY() {
		return maxY;
	}

	/**
	 * Returns the width of the rectangle in twips.
	 */
	public int getWidth() {
		return maxX - minX;
	}

	/**
	 * Returns the height of the rectangle in twips.
	 */
	public int getHeight() {
		return maxY - minY;
	}

	@Override
	public String toString() {
		return String.format(FORMAT, minX, minY, maxX, maxY);
	}
	
	@Override
	public boolean equals(final Object object) {
		boolean result;
		Bounds bounds;
		
		if (object == null) {
			result = false;
		} else if (object == this) {
			result = true;
		} else if (object instanceof Bounds) {
			bounds = (Bounds)object;
			result = minX == bounds.minX && minY == bounds.minY &&
				maxX == bounds.maxX && maxY == bounds.maxY;
		} else {
			result = false;
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		return (((minX*31)+minY)*31 + maxX)*31 + maxY;
	}
	
	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		size = Encoder.maxSize(minX, minY, maxX, maxY);
		// add extra 7 bit so result is byte aligned.
		return ((5 + (size << 2)) + 7) >> 3; //TODO(optimise) 5+7 = 12
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException {
		coder.alignToByte(); //TODO(optimise) See if this can be removed
		coder.writeBits(size, 5);
		coder.writeBits(minX, size);
		coder.writeBits(maxX, size);
		coder.writeBits(minY, size);
		coder.writeBits(maxY, size);
		coder.alignToByte();
	}
}
