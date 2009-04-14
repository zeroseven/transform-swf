/*
 * DefineFont2.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.DefineTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;
import com.flagstone.transform.movie.datatype.Bounds;
import com.flagstone.transform.movie.shape.Shape;
import com.flagstone.transform.movie.shape.ShapeRecord;
import com.flagstone.transform.movie.text.TextFormat;

/**
 * <p>DefineFont2 defines the shapes and layout of the glyphs used in a font. It 
 * extends the functionality provided by DefineFont and FontInfo by:</p>
 * 
 * <ul>
 * <li>allowing more than 65535 glyphs in a particular font.</li>
 * <li>including the functionality provided by the FontInfo class.</li>
 * <li>specifying ascent, descent and leading layout information for the font.</li>
 * <li>specifying advances for each glyph.</li>
 * <li>specifying bounding rectangles for each glyph.</li>
 * <li>specifying kerning pairs defining the distance between pairs of glyphs.</li>
 * </ul>
 * 
 * @see FontInfo
 * @see DefineFont
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class DefineFont2 implements DefineTag
{
	private static final String FORMAT = "DefineFont2: { identifier=%d; encoding=%s; "+
		"small=%s; italic=%s; bold=%s; language=%s; name=%s; shapes=%s; " +
		"codes=%s; ascent=%d; descent=%d; leading=%d; advances=%s; bounds=%s; kernings=%s }";
	
	private int identifier;
	protected TextFormat encoding;
	protected boolean small;
	protected boolean italic;
	protected boolean bold;
	protected int language;
	protected String name;
	protected List<Shape> shapes;
	protected List<Integer> codes;
	protected int ascent;
	protected int descent;
	protected int leading;
	protected List<Integer> advances;
	protected List<Bounds> bounds;
	protected List<Kerning> kernings;
	
	private transient int start;
	private transient int end;
	private transient int length;
	private transient boolean wideOffsets;
	private transient boolean wideCodes;

	public DefineFont2(final SWFDecoder coder) throws CoderException
	{
		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);
		
		identifier = coder.readWord(2, false);

		shapes = new ArrayList<Shape>();
		codes = new ArrayList<Integer>();
		advances = new ArrayList<Integer>();
		bounds = new ArrayList<Bounds>();
		kernings = new ArrayList<Kerning>();

		boolean containsLayout = coder.readBits(1, false) != 0;
		int format = coder.readBits(3, false);

		encoding = TextFormat.UNICODE;

		if (format == 1) {
			encoding = TextFormat.ANSI;
		}
		// Flash 7
		else if (format == 2) {
			small = true;
		}
		// End Flash 7
		else if (format == 4) {
			encoding = TextFormat.SJIS;
		}

		wideOffsets = coder.readBits(1, false) != 0;
		wideCodes = coder.readBits(1, false) != 0;

		coder.getContext().setWideCodes(wideCodes);

		italic = coder.readBits(1, false) != 0;
		bold = coder.readBits(1, false) != 0;
		language = coder.readBits(8, false);
		int nameLength = coder.readByte();
		name = coder.readString(nameLength, coder.getEncoding());

		if (name.length() > 0)
		{
			while (name.charAt(name.length() - 1) == 0) {
				name = name.substring(0, name.length() - 1);
			}
		}

		int glyphCount = coder.readWord(2, false);
		int offsetStart = coder.getPointer(); // NOPMD
		int[] offset = new int[glyphCount + 1]; // NOPMD

		for (int i = 0; i < glyphCount; i++) {
			offset[i] = coder.readWord((wideOffsets) ? 4 : 2, false); // NOPMD
		}

		offset[glyphCount] = coder.readWord((wideOffsets) ? 4 : 2, false); // NOPMD

		Shape shape;
		
		for (int i = 0; i < glyphCount; i++)
		{
			coder.setPointer(offsetStart + (offset[i] << 3));

			if (coder.getContext().isDecodeGlyphs()) 
			{
				shapes.add(new Shape(coder));
			}
			else {
				shapes.add(new Shape(offset[i + 1] - offset[i], coder));
			}
		}

		for (int i = 0; i < glyphCount; i++) {
			codes.add(coder.readWord((wideCodes) ? 2 : 1, false));
		}

		if (containsLayout)
		{
			ascent = coder.readWord(2, true);
			descent = coder.readWord(2, true);
			leading = coder.readWord(2, true);

			for (int i = 0; i < glyphCount; i++) {
				advances.add(coder.readWord(2, true));
			}

			Bounds box;
			
			for (int i=0; i<glyphCount; i++) 
			{
				bounds.add(new Bounds(coder));
			}

			int kerningCount = coder.readWord(2, false);

			Kerning kern;
	
			for (int i=0; i<kerningCount; i++) 
			{
				kernings.add(new Kerning(coder));
			}
		}

		coder.getContext().setWideCodes(false);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	/**
	 * Creates a DefineFont2 object specifying only the name of the font.
	 * 
	 * If none of the remaining attributes are set the Flash Player will load
	 * the font from the system on which it is running or substitute a suitable
	 * font if the specified font cannot be found. This is particularly useful
	 * when defining fonts that will be used to display text in
	 * DefineTextField objects.
	 * 
	 * The font will be defined to use Unicode encoding. The flags which define
	 * the font's face will be set to false. The arrays of glyphs which define
	 * the shapes and the code which map the character codes to a particular
	 * glyph will remain empty since the font is loaded from the system on which
	 * it is displayed.
	 * 
	 * @param uid
	 *            the unique identifier for this font object.
	 * @param name
	 *            the name of the font.
	 */
	public DefineFont2(int uid, String name)
	{
		setIdentifier(uid);
		setName(name);
		
		shapes = new ArrayList<Shape>();
		codes = new ArrayList<Integer>();
		advances = new ArrayList<Integer>();
		bounds = new ArrayList<Bounds>();
		kernings = new ArrayList<Kerning>();
	}


	public DefineFont2(DefineFont2 object) {
		identifier = object.identifier;
		encoding = object.encoding;
		small = object.small;
		italic = object.italic;
		bold = object.bold;
		language = object.language;
		name = object.name;
		ascent = object.ascent;
		descent = object.descent;
		leading = object.leading;
		shapes = new ArrayList<Shape>(object.shapes.size());
		for (Shape shape : object.shapes) {
			shapes.add(shape.copy());
		}
		codes = new ArrayList<Integer>(object.codes);
		advances = new ArrayList<Integer>(object.advances);
		bounds = new ArrayList<Bounds>(object.bounds);
		kernings = new ArrayList<Kerning>(object.kernings);
	}
	
	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final int uid) {
		if (uid < 0 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = uid;
	}

	/**
	 * Add a shape to the array of shapes that represent the glyphs for the font.
	 * 
	 * @param obj
	 *            a shape. Must not be null.
	 */
	public void addGlyph(Shape obj)
	{
		if (obj == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		shapes.add(obj);
	}

	/**
	 * Add a code to the array of character codes.
	 * 
	 * There is a one-to-one mapping of the entries in the character codes array 
	 * with the glyphs array so the index position of a character code is used
	 * to identify the corresponding glyph  that will be displayed.
	 * 
	 * The character codes should be added to the font in ascending order.
	 * 
	 * @param aCode
	 *            the code for a glyph. Must be in the range 0..65535.
	 */
	public void addCode(int aCode)
	{
		if (aCode < 0 || aCode > 65535) {
			throw new IllegalArgumentException(Strings.CHARACTER_CODE_OUT_OF_RANGE);
		}
		codes.add(aCode);
	}
	
	/**
	 * Add a character code and the corresponding glyph that will be displayed.
	 * Character codes should be added to the font in ascending order.
	 * 
	 * @param code
	 *            the character code. Must be in the range 0..65535.
	 * @param obj
	 *            the shape that represents the glyph displayed for the
	 *            character code.
	 */
	public void addCodeForGlyph(int code, Shape obj)
	{
		addCode(code);
		addGlyph(obj);
	}

	/**
	 * Add an advance to the array of advances. The index position of the 
	 * entry in the advance array is also used to identify the corresponding
	 * glyph and vice-versa.
	 * 
	 * @param anAdvance
	 *            an advance for a glyph. Must be in the range -32768..32767.
	 */
	public void addAdvance(int anAdvance)
	{
		if (anAdvance < -32768 || anAdvance > 32767) {
			throw new IllegalArgumentException(Strings.SIGNED_VALUE_OUT_OF_RANGE);
		}
		advances.add(anAdvance);
	}

	/**
	 * Add a bounds object to the array of bounds for each glyph. The index 
	 * position of the entry in the bounds array is also used to identify the 
	 * corresponding glyph and vice-versa.
	 * 
	 * @param obj
	 *            an Bounds. Must not be null.
	 *            
	 * @throws IllegalArgumentException if the bounds object is null.
	 */
	public void add(Bounds obj)
	{
		if (obj == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		bounds.add(obj);
	}

	/**
	 * Add a kerning object to the array of kernings for pairs of glyphs.
	 * 
	 * @param anObject
	 *            an Kerning.
	 *            
	 * @throws IllegalArgumentException if the kerning is null.
	 */
	public void add(Kerning anObject)
	{
		if (anObject == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		kernings.add(anObject);
	}

	/**
	 * Returns the encoding used for the font codes, either Text.ASCII,
	 * Text.SJIS or Text.Unicode.
	 * 
	 * @return the encoding used to represent characters rendered in the font.
	 */
	public TextFormat getEncoding()
	{
		return encoding;
	}

	/**
	 * Does the font have a small point size. This is used only with a Unicode
	 * font encoding.
	 * 
	 * @return a boolean indicating whether the font will be aligned on pixel
	 *         boundaries.
	 */
	public boolean isSmall()
	{
		return small;
	}

	/**
	 * Sets the font is small. Used only with Unicode fonts.
	 * 
	 * @param aBool
	 *            a boolean flag indicating the font will be aligned on pixel
	 *            boundaries.
	 */
	public void setSmall(boolean aBool)
	{
		small = aBool;
	}

	// End Flash 7

	/**
	 * Is the font italicised.
	 * 
	 * @return a boolean indicating whether the font is rendered in italics.
	 */
	public boolean isItalic()
	{
		return italic;
	}

	/**
	 * Is the font bold.
	 * 
	 * @return a boolean indicating whether the font is rendered in a bold face.
	 */
	public boolean isBold()
	{
		return bold;
	}

	// Flash 6
	/**
	 * Returns the language code identifying the type of spoken language for the
	 * font either Text.Japanese, Text.Korean, Text.Latin,
	 * Text.SimplifiedChinese or Text.TraditionalChinese.
	 * 
	 * @return the language code used to determine how line-breaks are inserted
	 *         into text rendered using the font. Returns 0 if the object was
	 *         decoded from a movie contains Flash 5 or less.
	 */
	public int getLanguage()
	{
		return language;
	}

	/**
	 * Sets the language code used to determine the position of line-breaks in
	 * text rendered using the font.
	 * 
	 * NOTE: The language attribute is ignored if the object is encoded in a
	 * Flash 5 movie.
	 * 
	 * @param code
	 *            the code identifying the spoken language either
	 *            Text.Japanese, Text.Korean, Text.Latin,
	 *            Text.SimplifiedChinese or Text.TraditionalChinese.
	 */
	public void setLanguage(int code)
	{
		language = code;
	}

	// End Flash 6

	/**
	 * Returns the name of the font family.
	 * 
	 * @return the name of the font.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the array of shapes used to define the outlines of each font glyph.
	 * 
	 * @return an array of Shape objects
	 */
	public List<Shape> getShapes()
	{
		return shapes;
	}

	/**
	 * Returns the array of codes used to identify each glyph in the font. The
	 * ordinal position of each Integer representing a code identifies a
	 * particular glyph in the shapes array.
	 * 
	 * @return an array of Integer objects that contain the character codes for
	 *         each glyph in the font.
	 */
	public List<Integer> getCodes()
	{
		return codes;
	}

	/**
	 * Returns the ascent for the font in twips.
	 * 
	 * @return the ascent for the font.
	 */
	public int getAscent()
	{
		return ascent;
	}

	/**
	 * Returns the descent for the font in twips.
	 * 
	 * @return the descent for the font.
	 */
	public int getDescent()
	{
		return descent;
	}

	/**
	 * Returns the leading for the font in twips.
	 * 
	 * @return the leading for the font.
	 */
	public int getLeading()
	{
		return leading;
	}

	/**
	 * Returns the array of advances defined for each glyph in the font.
	 * 
	 * @return an array of Integer objects that contain the advance for each
	 *         glyph in the font.
	 */
	public List<Integer> getAdvances()
	{
		return advances;
	}

	/**
	 * Returns the array of bounding rectangles defined for each glyph in the font.
	 * 
	 * @return an array of Bounds objects.
	 */
	public List<Bounds> getBounds()
	{
		return bounds;
	}

	/**
	 * Returns the array of kerning records that define the spacing between glyph
	 * pairs.
	 * 
	 * @return an array of Kerning objects that define the spacing adjustment
	 *         between pairs of glyphs.
	 */
	public List<Kerning> getKernings()
	{
		return kernings;
	}

	/**
	 * Sets the encoding for the font character codes.
	 * 
	 * @param aType
	 *            the encoding scheme used to denote characters, either
	 *            Text.ASCII, Text.SJIS or Text.Unicode.
	 */
	public void setEncoding(TextFormat aType)
	{
		encoding = aType;
	}

	/**
	 * Set the font is italicised.
	 * 
	 * @param aBool
	 *            a boolean flag indicating whether the font will be rendered in
	 *            italics
	 */
	public void setItalic(boolean aBool)
	{
		italic = aBool;
	}

	/**
	 * Set the font is bold.
	 * 
	 * @param aBool
	 *            a boolean flag indicating whether the font will be rendered in
	 *            bold face.
	 */
	public void setBold(boolean aBool)
	{
		bold = aBool;
	}

	/**
	 * Set the name of the font.
	 * 
	 * @param aString
	 *            the name assigned to the font, identifying the font family.
	 *            
	 * @throws IllegalArgumentException if the name is null.
	 */
	public void setName(String aString)
	{
		if (aString == null) {
			throw new IllegalArgumentException(Strings.STRING_CANNOT_BE_NULL);
		}
		name = aString;
	}

	/**
	 * Set the array of shape records that define the outlines of the characters
	 * used from the font.
	 * 
	 * @param anArray
	 *            an array of Shape objects that define the glyphs for the
	 *            font.
	 *            
	 * @throws IllegalArgumentException if the array is null.
	 */
	public void setShapes(List<Shape> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		shapes = anArray;
	}

	/**
	 * Sets the codes used to identify each glyph in the font.
	 * 
	 * @param anArray
	 *            sets the code table that maps a particular glyph to a
	 *            character code.
	 *            
	 * @throws IllegalArgumentException if the array is null.
	 */
	public void setCodes(List<Integer> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		codes = anArray;
	}

	/**
	 * Sets the ascent for the font in twips.
	 * 
	 * @param aNumber
	 *            the ascent for the font.
	 *            
	 * @throws IllegalArgumentException if the ascent is not in the range -32768..32767.
	 */
	public void setAscent(int aNumber)
	{
		if (aNumber < -32768 || aNumber > 32767) {
			throw new IllegalArgumentException(Strings.SIGNED_VALUE_OUT_OF_RANGE);
		}	
		ascent = aNumber;
	}

	/**
	 * Sets the descent for the font in twips.
	 * 
	 * @param aNumber
	 *            the descent for the font.
	 *            
	 * @throws IllegalArgumentException if the descent is not in the range -32768..32767.
	 */
	public void setDescent(int aNumber)
	{
		if (aNumber < -32768 || aNumber > 32767) {
			throw new IllegalArgumentException(Strings.SIGNED_VALUE_OUT_OF_RANGE);
		}
		descent = aNumber;
	}

	/**
	 * Sets the leading for the font in twips.
	 * 
	 * @param aNumber
	 *            the descent for the font.
	 *            
	 * @throws IllegalArgumentException if the descent is not in the range -32768..32767.
	 */
	public void setLeading(int aNumber)
	{
		if (aNumber < -32768 || aNumber > 32767) {
			throw new IllegalArgumentException(Strings.SIGNED_VALUE_OUT_OF_RANGE);
		}
		leading = aNumber;
	}

	/**
	 * Sets the array of advances for each glyph in the font.
	 * 
	 * @param anArray
	 *            of Integer objects that define the spacing between glyphs.
	 *            
	 * @throws IllegalArgumentException if the array is null.
	 */
	public void setAdvances(List<Integer> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		advances = anArray;
	}

	/**
	 * Sets the array of bounding rectangles for each glyph in the font.
	 * 
	 * @param anArray
	 *            an array of Bounds objects that define the bounding
	 *            rectangles that enclose each glyph in the font.
	 *            
	 * @throws IllegalArgumentException if the array is null.
	 */
	public void setBounds(List<Bounds> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		bounds = anArray;
	}

	/**
	 * Sets the array of kerning records for pairs of glyphs in the font.
	 * 
	 * @param anArray
	 *            an array of Kerning objects that define an adjustment
	 *            applied to the spacing between pairs of glyphs.
	 *            
	 * @throws IllegalArgumentException if the array is null.
	 */
	public void setKernings(List<Kerning> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		kernings = anArray;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public DefineFont2 copy() 
	{
		return new DefineFont2(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, identifier, encoding, small, italic, bold,
				language, name, shapes, codes, ascent, descent, leading,
				advances, bounds, kernings);
	}

	public int prepareToEncode(final SWFEncoder coder)
	{
		wideCodes = coder.getContext().getVersion() > 5 || encoding != TextFormat.ANSI;

		coder.getContext().setFillSize(1);
		coder.getContext().setLineSize(coder.getContext().isPostscript() ? 1 : 0);
		coder.getContext().setWideCodes(wideCodes);
		
		int glyphLength = 0;
			
		for (Shape shape : shapes) {
			glyphLength += shape.prepareToEncode(coder);
		}

 		wideOffsets =  (shapes.size()*2 + glyphLength) > 65535;
		
		length = 5;
		length += coder.strlen(name)-1;
		length += 2;
		length += shapes.size() * (wideOffsets ? 4 : 2);
		length += wideOffsets ? 4 : 2;
		length += glyphLength;
		length += shapes.size() * (wideCodes ? 2 : 1);

		if (containsLayoutInfo())
		{
			length += 6;
			length += advances.size()*2;

			for (Bounds bound : bounds) {
				length += bound.prepareToEncode(coder);
			}

			length += 2;
			length += kernings.size()*(wideCodes ? 6 : 4);
		}

		coder.getContext().setFillSize(0);
		coder.getContext().setLineSize(0);
		coder.getContext().setWideCodes(false);

		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder) throws CoderException
	{
		int format;

		if (encoding == TextFormat.ANSI) {
			format = 1;
		}
		else if (small) {
			format = 2;
		}
		else if (encoding == TextFormat.SJIS) {
			format = 4;
		}
		else {
			format = 0;
		}
			
		start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((Types.DEFINE_FONT_2 << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.DEFINE_FONT_2 << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);
		
		coder.writeWord(identifier, 2);
		coder.getContext().setFillSize(1);
		coder.getContext().setLineSize(coder.getContext().isPostscript() ? 1 : 0);
		coder.getContext().setWideCodes(wideCodes);

		coder.writeBits(containsLayoutInfo() ? 1 : 0, 1);
		coder.writeBits(format, 3);
		coder.writeBits(wideOffsets ? 1 : 0, 1);
		coder.writeBits(wideCodes ? 1:0, 1);
		coder.writeBits(italic ? 1 : 0, 1);
		coder.writeBits(bold ? 1 : 0, 1);
		coder.writeWord(coder.getContext().getVersion() > 5 ? language : 0, 1);
		coder.writeWord(coder.strlen(name)-1, 1);

		coder.writeString(name);
		coder.adjustPointer(-8);
		coder.writeWord(shapes.size(), 2);

		int currentLocation;
		int offset;

		int tableStart = coder.getPointer();
		int tableEntry = tableStart;
		int entrySize = wideOffsets ? 4 : 2;

		for (int i = 0; i <= shapes.size(); i++) {
			coder.writeWord(0, entrySize);
		}

		for (Iterator<Shape> i = shapes.iterator(); i.hasNext(); tableEntry += entrySize << 3)
		{
			currentLocation = coder.getPointer();
			offset = (coder.getPointer() - tableStart) >> 3;

			coder.setPointer(tableEntry);
			coder.writeWord(offset, entrySize);
			coder.setPointer(currentLocation);

			i.next().encode(coder);
		}

		currentLocation = coder.getPointer();
		offset = (coder.getPointer() - tableStart) >> 3;

		coder.setPointer(tableEntry);
		coder.writeWord(offset, entrySize);
		coder.setPointer(currentLocation);

		for (Integer code : codes) {
			coder.writeWord(code.intValue(), wideCodes ? 2 : 1);
		}

		if (containsLayoutInfo())
		{
			coder.writeWord(ascent, 2);
			coder.writeWord(descent, 2);
			coder.writeWord(leading, 2);

			for (Integer advance : advances) {
				coder.writeWord( advance.intValue(), 2);
			}

			for (Bounds bound : bounds) {
				bound.encode(coder);
			}

			coder.writeWord(kernings.size(), 2);

			for (Kerning kerning : kernings) {
				kerning.encode(coder);
			}
		}

		coder.getContext().setFillSize(0);
		coder.getContext().setLineSize(0);
		coder.getContext().setWideCodes(false);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	private boolean containsLayoutInfo()
	{
		boolean layout = ascent != 0 ||
			descent != 0 || 
			leading != 0 || 
			!advances.isEmpty() || 
			!bounds.isEmpty() || 
			!kernings.isEmpty();

		return layout;
	}
}
