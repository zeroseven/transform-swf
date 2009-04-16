/*
 * Kerning.java
 * Transform
 * 
 * Copyright (c) 2001-2008 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.movie.font;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Encodeable;
import com.flagstone.transform.movie.Strings;

/**
 * Kerning is used to fine-tune the spacing between specific pairs 
 * of characters to make them visually more appealing.</p>
 * 
 * <p>The glyphs are identified by an index into the glyph table for the font. 
 * The adjustment, in twips, is specified relative to the advance define for the 
 * left hand glyph.</p>
 * 
 * <p>Kerning objects are only used within DefineFont2 objects and provide more
 * precise control over the layout of a font's glyph than was possible using the
 * DefineFont and FontInfo objects.</p>
 *
 * @see DefineFont2
 */
public final class Kerning implements Encodeable
{
	private static final String FORMAT = "Kerning: { leftIndex=%d; rightIndex=%d; adjustment=%d } ";
	
	private final transient int leftGlyph;
	private final transient int rightGlyph;
	private final transient int adjustment;
	
	private transient int size;

	public Kerning(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		size = (context.isWideCodes()) ? 2 : 1;
		leftGlyph = coder.readWord(size, false);
		rightGlyph = coder.readWord(size, false);
		adjustment = coder.readWord(2, true);
	}

	/**
	 * Creates a Kerning object specifying the glyph indexes and
	 * adjustment. The value for the adjustment must be specified in twips.
	 * 
	 * @param leftIndex
	 *            the index in a code table for the glyph on the left side of
	 *            the pair. Must be in the range 0..65535.
	 * @param rightIndex
	 *            the index in a code table for the glyph on the right side of
	 *            the pair. Must be in the range 0..65535.
	 * @param adjust
	 *            the adjustment that will be added to the advance defined for
	 *            the left glyph. Must be in the range -32768..32767.
	 */
	public Kerning(int leftIndex, int rightIndex, int adjust)
	{
		if (leftIndex < 0 || leftIndex > 65535) {
			throw new IllegalArgumentException(Strings.GLYPH_INDEX_OUT_OF_RANGE);
		}
		leftGlyph = leftIndex; 

		if (rightIndex < 0 || rightIndex > 65535) {
			throw new IllegalArgumentException(Strings.GLYPH_INDEX_OUT_OF_RANGE);
		}
		rightGlyph = rightIndex; 

		if (adjust < -32768 || adjust > 32767) {
			throw new IllegalArgumentException(Strings.SIGNED_VALUE_OUT_OF_RANGE);
		}
		adjustment = adjust;
	}
	
	/**
	 * Returns the index of the left glyph in the kerning pair.
	 */
	public int getLeftGlyph()
	{
		return leftGlyph;
	}

	/**
	 * Returns the index of the right glyph in the kerning pair.
	 */
	public int getRightGlyph()
	{
		return rightGlyph;
	}

	/**
	 * Returns the adjustment, in twips, to the advance of the left glyph.
	 */
	public int getAdjustment()
	{
		return adjustment;
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, leftGlyph, rightGlyph, adjustment);
	}
	
	@Override
	public boolean equals(Object object) {
		boolean result;
		Kerning kerning;
		
		if (object == null) {
			result = false;
		} else if (object == this) {
			result = true;
		} else if (object instanceof Kerning) {
			kerning = (Kerning)object;
			result = leftGlyph == kerning.leftGlyph &&
				rightGlyph == kerning.rightGlyph &&
				adjustment == kerning.adjustment;
		} else {
			result = false;
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		return ((leftGlyph*31) + rightGlyph)*31 + adjustment;
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		size = context.isWideCodes() ? 2 : 1;
		return (size << 2) + 2;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		coder.writeWord(leftGlyph, size);
		coder.writeWord(rightGlyph, size);
		coder.writeWord(adjustment, 2);
	}
}
