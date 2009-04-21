/*
 * Character.java
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

package com.flagstone.transform.text;

import com.flagstone.transform.Bounds;
import com.flagstone.transform.Encodeable;
import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

//TODO(doc) Review
/**
 * <p>Character is used to display a text character in a span of text. Each 
 * Character specifies the glyph to be displayed (rather than the character 
 * code) along with the distance to the next Character to be displayed, if any.</p>
 * 
 * <p>A single lines of text is displayed using an {@link TextSpan} object which contains 
 * an array of Character objects. Blocks of text can be created by combining one
 * or more TextSpan objects which specify the size, colour and relative position of 
 * each line.</p>
 * 
 * @see TextSpan
 * @see DefineText
 * @see DefineText2
 * @see com.flagstone.transform.factory.text.TextFactory
 */
public final class GlyphIndex implements Encodeable
{
	private static final String FORMAT = "GlyphIndex: { glyphIndex=%d; advance=%d }";
		
	private final transient int glyphIndex;
	private final transient int advance;

	//TODO(doc)
	public GlyphIndex(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		glyphIndex = coder.readBits(context.getGlyphSize(), false);
		advance = coder.readBits(context.getAdvanceSize(), true);
	}

	/**
	 * Creates a Character specifying the index of the glyph to be
	 * displayed and the spacing to the next glyph.
	 * 
	 * @param anIndex
	 *            the index into the array of Shapes in a font definition
	 *            object that defines the glyph that represents the character to
	 *            be displayed.
	 * 
	 * @param anAdvance
	 *            the relative position in twips, from the origin of the glyph
	 *            representing this character to the next glyph to be displayed.
	 */
	public GlyphIndex(int anIndex, int anAdvance)
	{
		if (anIndex < 0) {
			throw new IllegalArgumentException(Strings.NUMBER_CANNOT_BE_NEGATIVE);
		}
		glyphIndex = anIndex;

		advance = anAdvance;
	}

	/**
	 * Returns the index of the glyph, in a font definition object, that will
	 * displayed to represent this character.
	 */
	public int getGlyphIndex()
	{
		return glyphIndex;
	}

	/**
	 * Returns the spacing in twips between the glyph representing this character
	 * and the next.
	 */
	public int getAdvance()
	{
		return advance;
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, glyphIndex, advance);
	}
	
	@Override
	public boolean equals(final Object object) {
		boolean result;
		GlyphIndex index;
		
		if (object == null) {
			result = false;
		} else if (object == this) {
			result = true;
		} else if (object instanceof Bounds) {
			index = (GlyphIndex)object;
			result = glyphIndex == index.glyphIndex && advance == index.advance;
		} else {
			result = false;
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		return (glyphIndex*31)+advance;
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		return context.getGlyphSize() + context.getAdvanceSize();
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		coder.writeBits(glyphIndex, context.getGlyphSize());
		coder.writeBits(advance, context.getAdvanceSize());
	}
}
