/*
 * LineStyle2.java
 * Transform
 * 
 * Copyright (c) 2009 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.movie.linestyle;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Encodeable;
import com.flagstone.transform.movie.Copyable;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.datatype.Color;
import com.flagstone.transform.movie.fillstyle.FillStyle;

@SuppressWarnings( { "PMD.CyclomaticComplexity", "PMD.LongVariable",
		"PMD.TooManyMethods" })
public final class MorphLineStyle2 implements Encodeable,
		Copyable<MorphLineStyle2> {

	public static final String FORMAT = "LineStyle2: { width=%d; color=%s;"
			+ " fillStyle=%s; startCap=%s; endCap=%s; joinStyle=%s;"
			+ " scaledHorizontally=%d; scaledVertically=%d;"
			+ " pixelAligned=%s; lineClosed=%d; miterLimit=%d }";

	private int startWidth;
	private int endWidth;
	private Color startColor;
	private Color endColor;

	private int startCap;
	private int endCap;
	private int joinStyle;
	private FillStyle fillStyle;

	private boolean scaledHorizontally;
	private boolean scaledVertically;
	private boolean pixelAligned;
	private boolean lineClosed;

	private int miterLimit;

	private transient boolean hasFillStyle;
	private transient boolean hasMiter;

	public MorphLineStyle2(final SWFDecoder coder, final SWFContext context) throws CoderException {
		startWidth = coder.readWord(2, false);
		endWidth = coder.readWord(2, false);
		unpack(coder.readB16());

		if (hasMiter) {
			coder.readWord(2, false);
		}

		if (hasFillStyle) {
			fillStyle = context.morphFillStyleOfType(coder, context);
		} else {
			startColor= new Color(coder, context);
			endColor= new Color(coder, context);
		}
	}


	public MorphLineStyle2(int startWidth, int endWidth, Color startColor,
			Color endColor) {
		super();

		setStartWidth(startWidth);
		setEndWidth(endWidth);
		setStartColor(startColor);
		setEndColor(endColor);

		scaledVertically = true;
		scaledVertically = true;
		lineClosed = true;
	}

	public MorphLineStyle2(MorphLineStyle2 object) {
		startWidth = object.startWidth;
		endWidth = object.endWidth;

		startColor = object.startColor;
		endColor = object.endColor;

		if (fillStyle != null) {
			object.fillStyle = fillStyle.copy();
		}

		startCap = object.startCap;
		endCap = object.endCap;
		joinStyle = object.joinStyle;

		scaledHorizontally = object.scaledHorizontally;
		scaledVertically = object.scaledVertically;
		pixelAligned = object.pixelAligned;
		lineClosed = object.lineClosed;
		miterLimit = object.miterLimit;
	}

	/**
	 * Returns the width of the line at the start of the morphing process.
	 */
	public int getStartWidth() {
		return startWidth;
	}

	/**
	 * Returns the width of the line at the end of the morphing process.
	 */
	public int getEndWidth() {
		return endWidth;
	}

	/**
	 * Returns the colour of the line at the start of the morphing process.
	 */
	public Color getStartColor() {
		return startColor;
	}

	/**
	 * Returns the colour of the line at the end of the morphing process.
	 */
	public Color getEndColor() {
		return endColor;
	}

	/**
	 * Sets the width of the line at the start of the morphing process.
	 * 
	 * @param aNumber
	 *            the starting width of the line. Must be in the range 0..65535.
	 */
	public void setStartWidth(int aNumber) {
		if (aNumber < 0 || aNumber > 65535) {
			throw new IllegalArgumentException(
					Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
		}
		startWidth = aNumber;
	}

	/**
	 * Sets the width of the line at the end of the morphing process.
	 * 
	 * @param aNumber
	 *            the ending width of the line. Must be in the range 0..65535.
	 */
	public void setEndWidth(int aNumber) {
		if (aNumber < 0 || aNumber > 65535) {
			throw new IllegalArgumentException(
					Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
		}
		endWidth = aNumber;
	}

	/**
	 * Returns the colour of the line at the start of the morphing process.
	 * 
	 * @param aColor
	 *            the starting colour of the line. Must not be null.
	 */
	public void setStartColor(Color aColor) {
		if (aColor == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		startColor = aColor;
	}

	/**
	 * Sets the colour of the line at the end of the morphing process.
	 * 
	 * @param aColor
	 *            the ending colour of the line. Must not be null.
	 */
	public void setEndColor(Color aColor) {
		if (aColor == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		endColor = aColor;
	}

	public int getStartCap() {
		return startCap;
	}

	public void setStartCap(final int capStyle) {
		startCap = capStyle;
	}

	public int getEndCap() {
		return endCap;
	}

	public void setEndCap(final int capStyle) {
		endCap = capStyle;
	}

	public int getJoinStyle() {
		return joinStyle;
	}

	public void setJoinStyle(final int joinStyle) {
		this.joinStyle = joinStyle;
	}

	public boolean isScaledHorizontally() {
		return scaledHorizontally;
	}

	public void setScaledHorizontally(final boolean scaled) {
		scaledHorizontally = scaled;
	}

	public boolean isScaledVertically() {
		return scaledVertically;
	}

	public void setScaledVertically(final boolean scaled) {
		scaledVertically = scaled;
	}

	public boolean isPixelAligned() {
		return pixelAligned;
	}

	public void setPixelAligned(final boolean aligned) {
		pixelAligned = aligned;
	}

	public boolean isLineClosed() {
		return lineClosed;
	}

	public void setLineClosed(final boolean closed) {
		lineClosed = closed;
	}

	public int getMiterLimit() {
		return miterLimit;
	}

	public void setMiterLimit(final int limit) {
		if (limit < 0 || limit > 65535) {
			throw new IllegalArgumentException(
					Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
		}
		miterLimit = limit;
	}

	public FillStyle getFillStyle() {
		return fillStyle;
	}

	public void setFillStyle(final FillStyle style) {
		fillStyle = style;
	}

	public MorphLineStyle2 copy() {
		return new MorphLineStyle2(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, startWidth, endWidth, startColor,
				endColor, fillStyle, startCap, endCap, joinStyle,
				scaledHorizontally, scaledVertically, pixelAligned, lineClosed,
				miterLimit);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context) {

		hasFillStyle = fillStyle != null;
		hasMiter = joinStyle == LineJoinStyle.MITER;

		int length = 6;

		if (hasMiter) {
			length += 2;
		}

		if (hasFillStyle) {
			length += fillStyle.prepareToEncode(coder, context);
		} else {
			length += 4;
			length += 4;
		}

		if (scaledHorizontally || scaledVertically) {
			context.setScalingStroke(true);
		}

		return length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException {
		coder.writeWord(startWidth, 2);
		coder.writeWord(endWidth, 2);
		coder.writeB16(pack());

		if (hasMiter) {
			coder.writeWord(miterLimit, 2);
		}

		if (hasFillStyle) {
			fillStyle.encode(coder, context);
		} else {
			startColor.encode(coder, context);
			endColor.encode(coder, context);
		}
	}

	@SuppressWarnings( { "PMD.CyclomaticComplexity", "PMD.NPathComplexity" })
	private int pack() {

		int value = 0;

		switch (startCap) {
		case LineCapStyle.NONE:
			value |= 0x00004000;
			break;
		case LineCapStyle.SQUARE:
			value |= 0x00008000;
			break;
		default:
			break;
		}

		switch (joinStyle) {
		case LineJoinStyle.BEVEL:
			value |= 0x00001000;
			break;
		case LineJoinStyle.MITER:
			value |= 0x00002000;
			break;
		default:
			break;
		}

		value |= fillStyle == null ? 0 : 0x00000800;
		value |= scaledHorizontally ? 0 : 0x00000400;
		value |= scaledVertically ? 0 : 0x00000200;
		value |= pixelAligned ? 0x00000100 : 0;
		value |= lineClosed ? 0 : 0x00000004;

		switch (endCap) {
		case LineCapStyle.NONE:
			value |= 0x00000001;
			break;
		case LineCapStyle.SQUARE:
			value |= 0x00000002;
			break;
		default:
			break;
		}

		return value;
	}

	@SuppressWarnings("PMD.CyclomaticComplexity")
	private void unpack(final int value) {

		if ((value & 0x00004000) > 0) {
			startCap = LineCapStyle.NONE;
		} else if ((value & 0x00008000) > 0) {
			startCap = LineCapStyle.SQUARE;
		} else {
			startCap = LineCapStyle.ROUND;
		}

		if ((value & 0x00001000) > 0) {
			joinStyle = LineJoinStyle.BEVEL;
			hasMiter = false;
		} else if ((value & 0x00002000) > 0) {
			joinStyle = LineJoinStyle.MITER;
			hasMiter = true;
		} else {
			joinStyle = LineJoinStyle.ROUND;
			hasMiter = false;
		}

		hasFillStyle = (value & 0x00000800) != 0;
		scaledHorizontally = (value & 0x00000400) == 0;
		scaledVertically = (value & 0x00000200) == 0;
		pixelAligned = (value & 0x00000100) != 0;
		lineClosed = (value & 0x00000004) == 0;

		if ((value & 0x00000001) > 0) {
			endCap = LineCapStyle.NONE;
		} else if ((value & 0x00000002) > 0) {
			endCap = LineCapStyle.SQUARE;
		} else {
			endCap = LineCapStyle.ROUND;
		}
	}
}
