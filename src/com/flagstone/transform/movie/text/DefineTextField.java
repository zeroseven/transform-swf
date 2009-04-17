/*
 * DefineTextField.java
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

package com.flagstone.transform.movie.text;

import java.util.LinkedHashMap;
import java.util.Map;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.DefineTag;
import com.flagstone.transform.movie.Movie;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;
import com.flagstone.transform.movie.datatype.Bounds;
import com.flagstone.transform.movie.datatype.Color;

/**
 * DefineTextField defines an editable text field.
 * 
 * <p>The value entered into the text field is assigned to a specified variable
 * allowing the creation of forms to accept values entered by a person viewing
 * the Flash file.</p>
 * 
 * <p>The class contains a complex set of attributes which allows a high degree of
 * control over how a text field is displayed:</p>
 * 
 * <table class="datasheet">
 * 
 * <tr>
 * <td valign="top">wordWrap</td>
 * <td>Indicates whether the text should be wrapped.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top">multiline</td>
 * <td>Indicates whether the text field contains multiple lines.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top">password</td>
 * <td>Indicates whether the text field will be used to display a password.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top">readOnly</td>
 * <td>Indicates whether the text field is read only.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top">selectable</td>
 * <td>Indicates whether the text field is selectable.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top">bordered</td>
 * <td>Indicates whether the text field is bordered.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top">HTML</td>
 * <td>Indicates whether the text field contains HTML.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top">useFontGlyphs</td>
 * <td>Use either the glyphs defined in the movie to display the text or load
 * the specified from the platform on which the Flash Player is hosted.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top">autosize</td>
 * <td>Indicates whether the text field will resize automatically to fit the
 * text entered.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top">maxLength</td>
 * <td>The maximum length of the text field. May be set to zero is not maximum
 * length is defined.</td>
 * </tr>

 * </table>
 * 
 * <p>Additional layout information for the spacing of the text relative to the
 * text field borders can also be specified through the following set of
 * attributes:</p>
 * 
 * <table class="datasheet">
 * 
 * <tr>
 * <td valign="top">alignment</td>
 * <td>Whether the text in the field is left-aligned, right-aligned, centred.</td>
 * </tr>
 * <tr>
 * <td valign="top">leftMargin</td>
 * <td>Left margin in twips.</td>
 * </tr>
 * <tr>
 * <td valign="top">rightMargin</td>
 * <td>Right margin in twips.</td>
 * </tr>
 * <tr>
 * <td valign="top">indent</td>
 * <td>Text indentation in twips.</td>
 * </tr>
 * <tr>
 * <td valign="top">leading</td>
 * <td>Leading in twips.</td>
 * </tr>
 * </table>
 * 
 * <p>The default values for the alignment is AlignLeft while the leftMargin,
 * rightMargin indent and leading attributes are set to the constant
 * Transform.VALUE_NOT_SET. If the attributes remain unchanged then the layout
 * information will not be encoded. If any of the values in this group are set
 * then they must all have values assigned for the field to be displayed
 * correctly otherwise default values of 0 will be used.
 * </p>
 * 
 * <p>
 * <b>HTML Support</b><br/> Setting the HTML flag to true allows text marked
 * up with a limited set of HTML tags to be displayed in the text field. The
 * following tags are supported:
 * </p>
 * 
 * <table>
 * <tr>
 * <td>&lt;p&gt;&lt;/p&gt;</td>
 * <td>Delimits a paragraph. Only the align attribute is supported:<br>
 * <p [align = left | right | center ]>
 * </p>
 * </td>
 * </tr>
 * 
 * <tr>
 * <td valign="top" nowrap>&lt;br&gt;</td>
 * <td>Inserts a line break.>
 * </p>
 * </td>
 * </tr>
 * 
 * <tr>
 * <td valign="top" nowrap>&lt;a&gt;&lt;/a&gt;</td>
 * <td>Define a hyperlink. Two attributes are supported:
 * <ul>
 * <li>href - the URL of the link.</li>
 * <li>target - name of a window or frame. (optional)</li>
 * </ul>
 * </td>
 * </tr>
 * 
 * <tr>
 * <td valign="top" nowrap>&lt;font&gt;&lt;/font&gt;</td>
 * <td>Format enclosed text using the font. Three attributes are supported:
 * <ul>
 * <li>name - must match the name of a font defined using the DefineFont2
 * class.</li>
 * <li>size - the height of the font in twips.</li>
 * <li>color - the colour of the text in the hexadecimal format #RRGGBB.</li>
 * </ul>
 * </td>
 * </tr>
 * 
 * <tr>
 * <td valign="top" nowrap>&lt;b&gt;&lt;/b&gt;</td>
 * <td>Delimits text that should be displayed in bold.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top" nowrap>&lt;b&gt;&lt;/b&gt;</td>
 * <td>Delimits text that should be displayed in italics.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top" nowrap>&lt;b&gt;&lt;/b&gt;</td>
 * <td>Delimits text that should be displayed underlined.</td>
 * </tr>
 * 
 * <tr>
 * <td valign="top" nowrap>&lt;li&gt;&lt;/li&gt;</td>
 * <td>Display bulleted paragraph. Strictly speaking this is not an HTML list.
 * The &lt;ul&gt; tag is not required and no other list format is supported.</td>
 * </tr>
 * 
 * </table>
 * 
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class DefineTextField implements DefineTag
{
	public enum Align {
		/** Defines that the text displayed in a text field is left aligned. */
		LEFT(0),
		/** Defines that the text displayed in a text field is right aligned. */
		RIGHT(1),
		/** Defines that the text displayed in a text field is centre aligned. */
		CENTER(2),
		/** Defines that the text displayed in a text field is justified. */
		JUSTIFY(3);

		private static final Map<Integer,Align>table 
			= new LinkedHashMap<Integer,Align>();

		static {
			for (Align align : values()) {
				table.put(align.value, align);
			}
		}
		
		public static Align fromInt(int type) {
			return table.get(type);
		}
		
		private final int value;
		
		private Align(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	public static class Builder {
		
		private int identifier;
		private Bounds bounds;
		private boolean wordWrapped;
		private boolean multiline;
		private boolean password;
		private boolean readOnly;
		private int reserved1;
		private boolean selectable;
		private boolean bordered;
		private boolean reserved2;
		private boolean html;
		private boolean useGlyphs;
		private boolean autoSize;
		private int fontIdentifier;
		private String fontClass;
		private int fontHeight;
		private Color color;
		private int maxLength;
		private Align alignment;
		private Integer leftMargin;
		private Integer rightMargin;
		private Integer indent;
		private Integer leading;
		private String variableName;
		private String initialText;

		Builder identifier(int identifier) {
			this.identifier = identifier;
			return this;
		}
		
		Builder bounds(Bounds bounds) {
			this.bounds = bounds;
			return this;
		}
		
		Builder wordWrapped(boolean wrapped) {
			this.wordWrapped = wrapped;
			return this;
		}
		
		Builder multiline(boolean multiline) {
			this.multiline = multiline;
			return this;
		}
		
		Builder password(boolean password) {
			this.password = password;
			return this;
		}
		
		Builder readOnly(boolean readOnly) {
			this.readOnly = readOnly;
			return this;
		}
		
		Builder selectable(boolean selectable) {
			this.selectable = selectable;
			return this;
		}
		
		Builder bordered(boolean bordered) {
			this.bordered = bordered;
			return this;
		}
		
		Builder html(boolean html) {
			this.html = html;
			return this;
		}
		
		Builder useGlyphs(boolean useGlyphs) {
			this.useGlyphs = useGlyphs;
			return this;
		}
		
		Builder autoSize(boolean autoSize) {
			this.autoSize = autoSize;
			return this;
		}
		
		Builder fontIdentifier(int identifier) {
			this.fontIdentifier = identifier;
			return this;
		}
		
		Builder fontClass(String className) {
			this.fontClass = className;
			return this;
		}
		
		Builder fontHeight(int height) {
			this.fontHeight = height;
			return this;
		}
		
		Builder color(Color color) {
			this.color = color;
			return this;
		}
		
		Builder maxLength(int length) {
			this.maxLength = length;
			return this;
		}
		
		Builder alignment(Align alignment) {
			this.alignment = alignment;
			return this;
		}
		
		Builder leftMargin(Integer margin) {
			this.leftMargin = margin;
			return this;
		}
		
		Builder rightMargin(Integer margin) {
			this.rightMargin = margin;
			return this;
		}
		
		Builder indent(Integer indent) {
			this.indent = indent;
			return this;
		}
		
		Builder leading(Integer leading) {
			this.leading = leading;
			return this;
		}
		
		Builder variable(String name) {
			this.variableName = name;
			return this;
		}
		
		Builder text(String text) {
			this.initialText = text;
			return this;
		}
		
		DefineTextField build() {
			return new DefineTextField(this);
		}
	}

	private int identifier;
	private Bounds bounds;
	private boolean wordWrapped;
	private boolean multiline;
	private boolean password;
	private boolean readOnly;
	private int reserved1;
	private boolean selectable;
	private boolean bordered;
	private boolean reserved2;
	private boolean html;
	private boolean useGlyphs;
	private boolean autoSize;
	private int fontIdentifier;
	private String fontClass;
	private int fontHeight;
	private Color color;
	private int maxLength;
	private Align alignment;
	private Integer leftMargin;
	private Integer rightMargin;
	private Integer indent;
	private Integer leading;
	private String variableName;
	private String initialText;

	
	private transient int start;
	private transient int end;
	private transient int length;

	private DefineTextField(Builder object) {
		identifier = object.identifier;
		bounds = object.bounds;
		wordWrapped = object.wordWrapped;
		multiline = object.multiline;
		password = object.password;
		readOnly = object.readOnly;
		reserved1 = object.reserved1;
		selectable = object.selectable;
		bordered = object.bordered;
		reserved2 = object.reserved2;
		html = object.html;
		useGlyphs = object.useGlyphs;
		autoSize = object.autoSize;
		fontIdentifier = object.fontIdentifier;
		fontClass = object.fontClass;
		fontHeight = object.fontHeight;
		color = object.color;
		maxLength = object.maxLength;
		alignment = object.alignment;
		leftMargin = object.leftMargin;
		rightMargin = object.rightMargin;
		indent = object.indent;
		leading = object.leading;
		variableName = object.variableName;
		initialText = object.initialText;
	}

	public DefineTextField(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		identifier = coder.readWord(2, true);
		context.setTransparent(true);

		bounds = new Bounds(coder);

		boolean containsText = coder.readBits(1, false) != 0;
		wordWrapped = coder.readBits(1, false) != 0;
		multiline = coder.readBits(1, false) != 0;
		password = coder.readBits(1, false) != 0;
		readOnly = coder.readBits(1, false) != 0;
		boolean containsColor = coder.readBits(1, false) != 0;
		boolean containsMaxLength = coder.readBits(1, false) != 0;
		boolean containsFont = coder.readBits(1, false) != 0;
		boolean containsClass = coder.readBits(1, false) != 0;
		autoSize = coder.readBits(1, false) != 0;
		boolean containsLayout = coder.readBits(1, false) != 0;
		selectable = coder.readBits(1, false) != 0;
		bordered = coder.readBits(1, false) != 0;
		reserved2 = coder.readBits(1, false) != 0;
		html = coder.readBits(1, false) != 0;
		useGlyphs = coder.readBits(1, false) != 0;

		if (containsFont)
		{
			fontIdentifier = coder.readWord(2, false);
			
			if (containsClass) {
				fontClass = coder.readString();
			}
			fontHeight = coder.readWord(2, false);
		}

		if (containsColor) {
			color= new Color(coder, context);
		}

		if (containsMaxLength) {
			maxLength = coder.readWord(2, false);
		}

		if (containsLayout)
		{
			alignment = Align.fromInt(coder.readByte());
			leftMargin = coder.readWord(2, false);
			rightMargin = coder.readWord(2, false);
			indent = coder.readWord(2, false);
			leading = coder.readWord(2, true);
		}

		variableName = coder.readString();

		if (containsText) {
			initialText = coder.readString();
		}

		context.setTransparent(false);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}


	/**
	 * Creates a DefineTextField object with the specified identifier and
	 * with the size defined by the bounding rectangle. All other attributes are
	 * set to their default values which will result in a blank, single-line,
	 * editable text field. Any values entered will not be assigned to a
	 * variable. Since no font is specified the text will be displayed in a
	 * non-spaced font 20 twips in height.
	 * 
	 * @param uid
	 *            the unique identifier for this object. Must be in the range 
	 *            1..65535.
	 * @param aBounds
	 *            the bounding rectangle for the field which defines its size.
	 */
	public DefineTextField(int uid, Bounds aBounds, String name, String value)
	{
		setIdentifier(uid);
		setBounds(aBounds);
		color = new Color(0,0,0,0);
		variableName = name;
		initialText = value;
	}
	
	public DefineTextField(DefineTextField object) {
		identifier = object.identifier;
		bounds = object.bounds;
		wordWrapped = object.wordWrapped;
		multiline = object.multiline;
		password = object.password;
		readOnly = object.readOnly;
		reserved1 = object.reserved1;
		selectable = object.selectable;
		bordered = object.bordered;
		reserved2 = object.reserved2;
		html = object.html;
		useGlyphs = object.useGlyphs;
		autoSize = object.autoSize;
		fontIdentifier = object.fontIdentifier;
		fontClass = object.fontClass;
		fontHeight = object.fontHeight;
		color = object.color;
		maxLength = object.maxLength;
		alignment = object.alignment;
		leftMargin = object.leftMargin;
		rightMargin = object.rightMargin;
		indent = object.indent;
		leading = object.leading;
		variableName = object.variableName;
		initialText = object.initialText;
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
	 * Returns the width of the text field in twips.
	 */
	public int getWidth() 
	{
		return bounds.getWidth();
	}

	/**
	 * Returns the width of the text field in twips.
	 */
	public int getHeight()
	{
		return bounds.getHeight();
	}
	
	/**
	 * Returns the bounding rectangle that completely encloses the text field.
	 */
	public Bounds getBounds()
	{
		return bounds;
	}

	/**
	 * Does the text field support word wrapping.
	 */
	public boolean isWordWrapped()
	{
		return wordWrapped;
	}

	/**
	 * Does the text field support multiple lines of text.
	 */
	public boolean isMultiline()
	{
		return multiline;
	}

	/**
	 * Does the text field protect passwords being entered.
	 */
	public boolean isPassword()
	{
		return password;
	}

	/**
	 * Is the text field read-only.
	 */
	public boolean isReadOnly()
	{
		return readOnly;
	}

	/**
	 * Is the text field selectable.
	 */
	public boolean isSelectable()
	{
		return selectable;
	}

	/**
	 * Is the text field bordered.
	 */
	public boolean isBordered()
	{
		return bordered;
	}

	/**
	 * Does the text field contain HTML.
	 */
	public boolean isHTML()
	{
		return html;
	}

	/**
	 * Does the text field resize to fit the contents.
	 */
	public boolean isAutoSize()
	{
		return autoSize;
	}

	// End Flash 6
	/**
	 * Sets whether the text field will resize to fit the contents.
	 * 
	 * @param aFlag
	 *            indicate whether the text field will resize automatically.
	 */
	public void setAutoSize(boolean aFlag)
	{
		autoSize = aFlag;
	}

	/**
	 * Indicates whether the text will be displayed using the font defined in
	 * the movie or whether a font defined on the host platform will be used.
	 * 
	 * @return true if the text will be displayed using the glyphs from the font
	 *         defined in the movie, false if the glyphs will be loaded from the
	 *         platform on which the Flash Player is hosted.
	 */
	public boolean useFontGlyphs()
	{
		return useGlyphs;
	}

	/**
	 * Returns the identifier of the font used to display the characters.
	 */
	public int getFontIdentifier()
	{
		return fontIdentifier;
	}

	public String getFontClass()
	{
		return fontClass;
	}

	/**
	 * Returns the height of the characters.
	 */
	public int getFontHeight()
	{
		return fontHeight;
	}

	/**
	 * Returns the text color.
	 */
	public Color getColor()
	{
		return color;
	}

	/**
	 * Returns the maximum number of characters displayed in the field.
	 */
	public int getMaxLength()
	{
		return maxLength;
	}

	/**
	 * Returns the alignment of the text, either AlignLeft, AlignRight, AlignCenter
	 * or AlignJustify.
	 */
	public Align getAlignment()
	{
		return alignment;
	}

	/**
	 * Returns the left margin in twips.
	 */
	public int getLeftMargin()
	{
		return leftMargin;
	}

	/**
	 * Returns the right margin in twips.
	 */
	public int getRightMargin()
	{
		return rightMargin;
	}

	/**
	 * Returns the indentation of the first line of text in twips.
	 */
	public int getIndent()
	{
		return indent;
	}

	/**
	 * Returns the leading in twips.
	 */
	public int getLeading()
	{
		return leading;
	}

	/**
	 * Returns the name of the variable the value in the text field will be
	 * assigned to.
	 */
	public String getVariableName()
	{
		return variableName;
	}

	/**
	 * Returns the default text displayed in the field.
	 */
	public String getInitialText()
	{
		return initialText;
	}

	/**
	 * Sets the bounding rectangle of the text field.
	 * 
	 * @param aBounds
	 *            the bounding rectangle enclosing the text field. Must not be
	 *            null.
	 */
	public void setBounds(Bounds aBounds)
	{
		if (aBounds == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		bounds = aBounds;
	}

	/**
	 * Set whether the text field supports word wrapping.
	 * 
	 * @param aFlag
	 *            set whether the text field is word wrapped.
	 */
	public void setWordWrapped(boolean aFlag)
	{
		wordWrapped = aFlag;
	}

	/**
	 * Set whether the text field contains multiple lines of text.
	 * 
	 * @param aFlag
	 *            set whether the text field is multiline.
	 */
	public void setMultiline(boolean aFlag)
	{
		multiline = aFlag;
	}

	/**
	 * Set whether the text field should protect passwords entered.
	 * 
	 * @param aFlag
	 *            set whether the text field is password protected.
	 */
	public void setPassword(boolean aFlag)
	{
		password = aFlag;
	}

	/**
	 * Set whether the text field is read-only.
	 * 
	 * @param aFlag
	 *            set whether the text field is read-only.
	 */
	public void setReadOnly(boolean aFlag)
	{
		readOnly = aFlag;
	}

	/**
	 * Set whether the text field is selectable.
	 * 
	 * @param aFlag
	 *            set whether the text field is selectable.
	 */
	public void setSelectable(boolean aFlag)
	{
		selectable = aFlag;
	}

	/**
	 * Set whether the text field is bordered.
	 * 
	 * @param aFlag
	 *            set whether the text field is bordered.
	 */
	public void setBordered(boolean aFlag)
	{
		bordered = aFlag;
	}

	/**
	 * Set whether the text field contains HTML.
	 * 
	 * @param aFlag
	 *            set whether the text field contains HTML.
	 */
	public void setHTML(boolean aFlag)
	{
		html = aFlag;
	}

	/**
	 * Set whether the text field characters are displayed using the font
	 * defined in the movie or whether the Flash Player uses a font definition
	 * loaded from the platform on which it is hosted.
	 * 
	 * @param aFlag
	 *            set whether the text field characters will be drawn using the
	 *            font in the movie (true) or use a font loaded by the Flash
	 *            Player (false).
	 */
	public void setUseFontGlyphs(boolean aFlag)
	{
		useGlyphs = aFlag;
	}

	/**
	 * Sets the identifier of the font used to display the characters.
	 * 
	 * @param anIdentifier
	 *            the identifier for the font that the text will be rendered in.
	 *            
	 * @throws IllegalArgumentException if the identifier for the font is not in the range 1..65535.
	 */
	public void setFontIdentifier(int anIdentifier)
	{
		if (anIdentifier < 1 || anIdentifier > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		fontIdentifier = anIdentifier;
		fontClass = null;
	}
	
	public void setFontClass(String fontClass) {
		this.fontClass = fontClass;
		fontIdentifier = 0;
	}

	/**
	 * Sets the height of the characters.
	 * 
	 * @param aNumber
	 *            the height of the font.
	 *            
	 * @throws IllegalArgumentException if the height font is not in the range 0..65535.
	 */
	public void setFontHeight(int aNumber)
	{
		if (aNumber < 0 || aNumber > 65535) {
			throw new IllegalArgumentException(Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
		}
		fontHeight = aNumber;
	}

	/**
	 * Sets the text color. If set to null then the text color defaults to
	 * black.
	 * 
	 * @param aColor
	 *            the colour object that defines the text colour.
	 */
	public void setColor(Color aColor)
	{
		if (aColor == null) {
			color = new Color(0,0,0);
		}
		else {
			color = aColor;
		}
	}

	/**
	 * Sets the maximum length of the text displayed. May be set to zero if no
	 * maximum length is defined.
	 * 
	 * @param aNumber
	 *            the maximum number of characters displayed in the field. Must be
	 *            in the range 0..65535.
	 */
	public void setMaxLength(int aNumber)
	{
		if (aNumber < 0 || aNumber > 65535) {
			throw new IllegalArgumentException(Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
		}
		maxLength = aNumber;
	}

	/**
	 * Sets the alignment of the text, either AlignLeft, AlignRight, AlignCenter
	 * or AlignJustify.
	 * 
	 * @param aType
	 *            the type of alignment. Must be either ALIGN_LEFT, ALIGN_RIGHT
	 *            or ALIGN_JUSTIFY.
	 */
	public void setAlignment(Align align)
	{
		alignment = align;
	}

	/**
	 * Sets the left margin in twips.
	 * 
	 * @param aNumber
	 *            the width of the left margin. Must be in the range 0..65535.
	 */
	public void setLeftMargin(Integer aNumber)
	{
		if (aNumber != null && (aNumber < 0 || aNumber > 65535)) {
			throw new IllegalArgumentException(Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
		}
		leftMargin = aNumber;
	}

	/**
	 * Sets the right margin in twips.
	 * 
	 * @param aNumber
	 *            the width of the right margin. Must be in the range 0..65535.
	 */
	public void setRightMargin(Integer aNumber)
	{
		if (aNumber != null && (aNumber < 0 || aNumber > 65535)) {
			throw new IllegalArgumentException(Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
		}
		rightMargin = aNumber;
	}

	/**
	 * Returns the indentation of the first line of text in twips.
	 * 
	 * @param aNumber
	 *            the indentation for the first line. Must be in the range 0..65535.
	 */
	public void setIndent(Integer aNumber)
	{
		if (aNumber != null && (aNumber < 0 || aNumber > 65535)) {
			throw new IllegalArgumentException(Strings.UNSIGNED_VALUE_OUT_OF_RANGE);
		}
		indent = aNumber;
	}

	/**
	 * Sets the spacing between lines, measured in twips.
	 * 
	 * @param aNumber
	 *            the value for the leading. Must be in the range -32768..32767.
	 */
	public void setLeading(Integer aNumber)
	{
		if (aNumber != null && (aNumber < -32768 || aNumber > 32767)) {
			throw new IllegalArgumentException(Strings.SIGNED_VALUE_OUT_OF_RANGE);
		}
		leading = aNumber;
	}

	/**
	 * Sets the name of the variable the value in the text field will be
	 * assigned to.
	 * 
	 * @param aString
	 *            the name of the variable.
	 */
	public void setVariableName(String aString)
	{
		variableName = aString;
	}

	/**
	 * Sets the value that will initially be displayed in the text field.
	 * 
	 * @param aString
	 *            the initial text displayed.
	 */
	public void setInitialText(String aString)
	{
		initialText = aString;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public DefineTextField copy() 
	{
		return new DefineTextField(this);
	}

	/**
	 * Returns a short description of this action.
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("DefineTextField: { identifier = ").append(identifier);
		builder.append("; bounds = ").append(bounds.toString());
		builder.append("; wordWrapped = ").append(wordWrapped);
		builder.append("; multiline = ").append(multiline);
		builder.append("; password = ").append(password);
		builder.append("; readOnly = ").append(readOnly);
		builder.append("; autoSize = ").append(autoSize);
		builder.append("; selectable = ").append(selectable);
		builder.append("; bordered = ").append(bordered);
		builder.append("; HTML = ").append(html);
		builder.append("; useFontGlyphs = ").append(useGlyphs);
		builder.append("; fontIdentifier = ").append(fontIdentifier).append(";");
		builder.append("; fontHeight = ").append(fontHeight);
		builder.append("; color = ").append(color.toString());
		builder.append("; maxLength = ").append(maxLength);
		builder.append("; alignment = ").append(alignment);
		builder.append("; leftMargin = ").append(leftMargin);
		builder.append("; rightMargin = ").append(rightMargin);
		builder.append("; indent = ").append(indent);
		builder.append("; leading = ").append(leading);
		builder.append("; variableName = ").append(variableName);
		builder.append("; initalText = ").append(initialText);
		builder.append(" }");

		return String.format("", identifier, bounds, wordWrapped, multiline);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		context.setTransparent(true);

		length = 2 + bounds.prepareToEncode(coder, context);
		length += 2;
		length += (fontIdentifier == 0) ? 0 : 4;
		length += fontClass == null ? 0 : coder.strlen(fontClass) + 2;
		length += 4;
		length += (maxLength > 0) ? 2 : 0;
		length += (containsLayout()) ? 9 : 0;
		length += coder.strlen(variableName);
		length += (initialText.length() > 0) ? coder.strlen(initialText) : 0;

		context.setTransparent(false);

		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((Types.DEFINE_TEXT_FIELD << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.DEFINE_TEXT_FIELD << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);

		coder.writeWord(identifier, 2);
		context.setTransparent(true);

		bounds.encode(coder, context);
		coder.writeBits(initialText.length() > 0 ? 1 : 0, 1);
		coder.writeBits(wordWrapped ? 1 : 0, 1);
		coder.writeBits(multiline ? 1 : 0, 1);
		coder.writeBits(password ? 1 : 0, 1);
		coder.writeBits(readOnly ? 1 : 0, 1);
		coder.writeBits(1, 1);
		coder.writeBits(maxLength > 0 ? 1 : 0, 1);
		coder.writeBits(fontIdentifier == 0 ? 0 : 1, 1);
		coder.writeBits(fontClass == null ? 0 : 1, 1);
		coder.writeBits(autoSize ? 1 : 0, 1);
		coder.writeBits(containsLayout() ? 1 : 0, 1);
		coder.writeBits(selectable ? 1 : 0, 1);
		coder.writeBits(bordered ? 1 : 0, 1);
		coder.writeBits(0, 1);
		coder.writeBits(html ? 1 : 0, 1);
		coder.writeBits(useGlyphs ? 1 : 0, 1);

		if (fontIdentifier != 0)
		{
			coder.writeWord(fontIdentifier, 2);
			coder.writeWord(fontHeight, 2);
		} else if (fontClass != null) {
			coder.writeString(fontClass);
			coder.writeWord(fontHeight, 2);
		}

		color.encode(coder, context);

		if (maxLength > 0) {
			coder.writeWord(maxLength, 2);
		}

		if (containsLayout())
		{
			coder.writeWord(alignment == null ? 0 : alignment.getValue(), 1);
			coder.writeWord(leftMargin == null ? 0 : leftMargin, 2);
			coder.writeWord(rightMargin == null ? 0 : rightMargin, 2);
			coder.writeWord(indent == null ? 0 : indent, 2);
			coder.writeWord(leading == null ? 0 : leading, 2);
		}

		coder.writeString(variableName);

		if (initialText.length() > 0)
		{
			coder.writeString(initialText);
		}
		context.setTransparent(false);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	private boolean containsLayout()
	{
		return alignment != null || 
			leftMargin != null || 
			rightMargin != null || 
			indent != null || 
			leading != null;
	}
}
