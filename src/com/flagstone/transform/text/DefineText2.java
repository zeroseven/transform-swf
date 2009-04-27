/*
 * DefineText2.java
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

package com.flagstone.transform.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.CoordTransform;

//TODO(doc) Review
/**
 * DefineText2 defines one or more lines of transparent text.
 * 
 * <p>
 * It extends the functionality provided by the DefineText class by encoding the
 * alpha channel of the colour objects used to set the text color.
 * </p>
 * 
 * <p>
 * DefineText2 class acts as a container for the text. The bounding rectangle
 * and transform controls how the text is laid out. Each TextSpan object
 * specifies an offset from the left and bottom edges of the bounding rectangle,
 * allowing successive lines of text to be arranged as a block or paragraph. The
 * coordinate transform can be used to control the size and orientation of the
 * text when it is displayed.
 * </p>
 * 
 * @see TextSpan
 * @see DefineText
 */
public final class DefineText2 implements DefineTag {
	private static final String FORMAT = "DefineText2: { identifier=%d; bounds=%s; transform=%s; objects=%s }";

	private Bounds bounds;
	private CoordTransform transform;
	private List<TextSpan> objects;

	private transient int end;
	private transient int length;
	private transient int glyphBits;
	private transient int advanceBits;
	private int identifier;

	// TODO(doc)
	// TODO(optimise)
	public DefineText2(final SWFDecoder coder, final Context context)
			throws CoderException {
		final int start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;

		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		identifier = coder.readWord(2, true);
		bounds = new Bounds(coder);

		// This code is used to get round a bug in Flash - sometimes 16,
		// 8-bit zeroes are written out before the transform. The root
		// cause in Flash is unknown but seems to be related to the
		// bounds not being set - all values are zero.

		final int mark = coder.getPointer();
		int count = 0;

		for (int i = 0; i < 16; i++) {
			if (coder.readWord(1, false) == 0) {
				count += 1;
			}
		}

		coder.setPointer(mark);

		if (count == 16) {
			coder.adjustPointer(128);
		}

		// Back to reading the rest of the tag

		transform = new CoordTransform(coder);

		glyphBits = coder.readByte();
		advanceBits = coder.readByte();

		final Map<Integer, Integer> vars = context.getVariables();
		vars.put(Context.TRANSPARENT, 1);
		vars.put(Context.GLYPH_SIZE, glyphBits);
		vars.put(Context.ADVANCE_SIZE, advanceBits);

		objects = new ArrayList<TextSpan>();

		while (coder.readBits(8, false) != 0) {
			coder.adjustPointer(-8);
			objects.add(new TextSpan(coder, context));
		}

		vars.remove(Context.TRANSPARENT);
		vars.put(Context.GLYPH_SIZE, 0);
		vars.put(Context.ADVANCE_SIZE, 0);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	/**
	 * Creates a DefineText2 object with the specified bounding rectangle,
	 * coordinate transform and text records.
	 * 
	 * @param uid
	 *            the unique identifier for this object. Must be in the range
	 *            1..65535
	 * @param bounds
	 *            the bounding rectangle enclosing the text. Must not be null.
	 * @param transform
	 *            an CoordTransform to change the size and orientation of the
	 *            text. Must not be null.
	 * @param spans
	 *            an array of TextSpan objects that define the text to be
	 *            displayed. Must not be null.
	 */
	public DefineText2(final int uid, final Bounds bounds,
			final CoordTransform transform, final List<TextSpan> spans) {
		setIdentifier(uid);
		setBounds(bounds);
		setTransform(transform);
		setObjects(spans);
	}

	// TODO(doc)
	public DefineText2(final DefineText2 object) {
		identifier = object.identifier;
		bounds = object.bounds;
		transform = object.transform;
		objects = new ArrayList<TextSpan>(object.objects.size());
		for (TextSpan span : object.objects) {
			objects.add(span.copy());
		}
	}

	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final int uid) {
		if (uid < 0 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_RANGE);
		}
		identifier = uid;
	}

	/**
	 * Returns the width of the text block in twips.
	 */
	public int getWidth() {
		return bounds.getWidth();
	}

	/**
	 * Returns the height of the text block in twips.
	 */
	public int getHeight() {
		return bounds.getHeight();
	}

	/**
	 * Add a TextSpan object to the array of text spans.
	 * 
	 * @param obj
	 *            an TextSpan object. Must not be null.
	 */
	public DefineText2 add(final TextSpan obj) {
		if (obj == null) {
			throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
		}
		objects.add(obj);
		return this;
	}

	/**
	 * Returns the bounding rectangle that completely encloses the text to be
	 * displayed.
	 * 
	 * @return the bounding rectangle of the text.
	 */
	public Bounds getBounds() {
		return bounds;
	}

	/**
	 * Returns the coordinate transform that controls how the text is displayed.
	 */
	public CoordTransform getTransform() {
		return transform;
	}

	/**
	 * Returns the array of text records that define the text to be displayed as
	 * its attributes.
	 */
	public List<TextSpan> getObjects() {
		return objects;
	}

	/**
	 * Sets the bounding rectangle that encloses the text being displayed.
	 * 
	 * @param aBounds
	 *            the bounding rectangle enclosing the text. Must not be null.
	 */
	public void setBounds(final Bounds aBounds) {
		if (aBounds == null) {
			throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
		}
		bounds = aBounds;
	}

	/**
	 * Sets the coordinate transform that changes the orientation and size of
	 * the text displayed.
	 * 
	 * @param aTransform
	 *            an CoordTransform to change the size and orientation of the
	 *            text. Must not be null.
	 */
	public void setTransform(final CoordTransform aTransform) {
		if (aTransform == null) {
			throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
		}
		transform = aTransform;
	}

	/**
	 * Sets the array of text spans that define the text to be displayed.
	 * 
	 * @param array
	 *            an array of TextSpan objects that define the text to be
	 *            displayed. Must not be null.
	 */
	public void setObjects(final List<TextSpan> array) {
		if (array == null) {
			throw new IllegalArgumentException(Strings.ARRAY_IS_NULL);
		}
		objects = array;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public DefineText2 copy() {
		return new DefineText2(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, identifier, bounds, transform, objects);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		glyphBits = calculateSizeForGlyphs();
		advanceBits = calculateSizeForAdvances();

		final Map<Integer, Integer> vars = context.getVariables();
		vars.put(Context.TRANSPARENT, 1);
		vars.put(Context.GLYPH_SIZE, glyphBits);
		vars.put(Context.ADVANCE_SIZE, advanceBits);

		length = 2 + bounds.prepareToEncode(coder, context);
		length += transform.prepareToEncode(coder, context);
		length += 2;

		for (TextSpan span : objects) {
			length += span.prepareToEncode(coder, context);
		}

		length += 1;

		vars.remove(Context.TRANSPARENT);
		vars.put(Context.GLYPH_SIZE, 0);
		vars.put(Context.ADVANCE_SIZE, 0);

		return (length > 62 ? 6 : 2) + length;
	}

	// TODO(optimise)
	public void encode(final SWFEncoder coder, final Context context)
			throws CoderException {
		final int start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((MovieTypes.DEFINE_TEXT_2 << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((MovieTypes.DEFINE_TEXT_2 << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);

		coder.writeWord(identifier, 2);
		final Map<Integer, Integer> vars = context.getVariables();
		vars.put(Context.TRANSPARENT, 1);
		vars.put(Context.GLYPH_SIZE, glyphBits);
		vars.put(Context.ADVANCE_SIZE, advanceBits);

		bounds.encode(coder, context);
		transform.encode(coder, context);

		coder.writeByte(glyphBits);
		coder.writeByte(advanceBits);

		for (TextSpan span : objects) {
			span.encode(coder, context);
		}

		coder.writeWord(0, 1);

		vars.remove(Context.TRANSPARENT);
		vars.put(Context.GLYPH_SIZE, 0);
		vars.put(Context.ADVANCE_SIZE, 0);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	private int calculateSizeForGlyphs() {
		int total = 0;
		int size;

		for (TextSpan span : objects) {
			size = span.glyphBits();

			if (size > total) {
				total = size;
			}
		}

		return total;
	}

	private int calculateSizeForAdvances() {
		int total = 0;
		int size;

		for (TextSpan span : objects) {
			size = span.advanceBits();

			if (size > total) {
				total = size;
			}
		}

		return total;
	}
}