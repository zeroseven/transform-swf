package com.flagstone.transform.coder;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import com.flagstone.transform.movie.Strings;

/**
 * Contexts are used to pass information between objects when they are being
 * encoded or decoded.
 */
public final class SWFContext {

	private int version;
	private String encoding;

	private int type;

	private boolean transparent;
	private boolean wideCodes;
	private boolean decodeActions;
	private boolean decodeShapes;
	private boolean decodeGlyphs;
	private boolean arrayExtended;
	private boolean postscript;
	private boolean scalingStroke;

	private int fillSize;
	private int lineSize;
	private int advanceSize;
	private int glyphSize;
	private int shapeSize;

	/**
	 * Sets the version of Flash that an object is being encoded or decoded for.
	 */
	public void setVersion(final int version) {
		this.version = version;
	}

	/**
	 * Returns the version of Flash that an object is being encoded or decoded
	 * for.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Sets the character encoding scheme used when encoding or decoding
	 * strings.
	 * 
	 * If the character set encoding is not supported by the Java environment
	 * then an UnsupportedCharsetException will be thrown. If the character 
	 * set cannot be identified then an IllegalCharsetNameException will be 
	 * thrown.
	 * 
	 * @param charSet
	 *            the name of the character set used to encode strings.   
	 */
	public void setEncoding(final String charSet) 
	{
		if (!Charset.isSupported(charSet)) {
			throw new UnsupportedCharsetException(
					String.format(Strings.UNSUPPORTED_ENCODING, charSet));
		}
		encoding = charSet;
	}
	
	/**
	 * Returns character encoding scheme used when encoding or decoding strings.
	 */
	public String getEncoding() {
		return encoding;
	}

	/** 
	 * Sets the type of the object being decoded. 
	 */
	public void setType(final int type) {
		this.type = type;
	}

	/** 
	 * Returns the type of the object being decoded. 
	 */
	public int getType() {
		return type;
	}

	/**
	 * Indicates whether Color objects should also encode their alpha channel.
	 */
	public void setTransparent(final boolean transparent) {
		this.transparent = transparent;
	}

	/**
	 * Returns true if Color objects should also encode their alpha channel,
	 * false if the alpha channel should be ignored.
	 */
	public boolean isTransparent() {
		return transparent;
	}

	/**
	 * Indicates the character codes in a font are 16-bit values rather than
	 * 8-bit.
	 */
	public void setWideCodes(final boolean wideCodes) {
		this.wideCodes = wideCodes;
	}

	/**
	 * Returns true if the character codes in a font are 16-bit values, false 
	 * if the codes used 8-bits.
	 */
	public boolean isWideCodes() {
		return wideCodes;
	}

	/**
	 * Indicates arrays of actions should be decoded instead of leaving them as
	 * binary data.
	 */
	public void setDecodeActions(final boolean decodeActions) {
		this.decodeActions = decodeActions;
	}

	/**
	 * Returns true if arrays of actions should be decoded, false if the arrays 
	 * are treated as binary data.
	 */
	public boolean isDecodeActions() {
		return decodeActions;
	}

	/**
	 * Indicates arrays of shapes should be decoded instead of leaving them as
	 * binary data.
	 */
	public void setDecodeShapes(final boolean decodeShapes) {
		this.decodeShapes = decodeShapes;
	}

	/**
	 * Returns true if arrays of shapes should be decoded, false if the arrays 
	 * are treated as binary data.
	 */
	public boolean isDecodeShapes() {
		return decodeShapes;
	}

	/**
	 * Indicates arrays of glyphs should be decoded instead of leaving them as
	 * binary data.
	 */
	public void setDecodeGlyphs(final boolean decodeGlyphs) {
		this.decodeGlyphs = decodeGlyphs;
	}

	/**
	 * Returns true if arrays of glyphs should be decoded, false if the arrays 
	 * are treated as binary data.
	 */
	public boolean isDecodeGlyphs() {
		return decodeGlyphs;
	}

	/** 
	 * Indicates that an array can contain more than 255 entries. 
	 */
	public void setArrayExtended(final boolean arrayExtended) {
		this.arrayExtended = arrayExtended;
	}

	/** Returns true if an array of styles can contain more than 255 entries,
	 * false if the maximum size is 255.
	 */
	public boolean isArrayExtended() {
		return arrayExtended;
	}

	/**
	 * This flag is set whenever a PathsArePostscript object is used. It
	 * controls the number of bits used when encoding the paths for glyphs and
	 * shapes. However its exact purpose is undocumented by Adobe.
	 */
	public void setPostscript(final boolean postscript) {
		this.postscript = postscript;
	}

	/**
	 * Returns true if the font paths are treated as postscript, false 
	 * otherwise.
	 */
	public boolean isPostscript() {
		return postscript;
	}

	public void setScalingStroke(final boolean scaling) {
		scalingStroke = scaling;
	}

	public boolean isScalingStoke() {
		return scalingStroke;
	}
	/**
	 * Sets the number of bits used to encode the index into the fill style
	 * array.
	 */
	public void setFillSize(final int fillSize) {
		this.fillSize = fillSize;
	}

	/**
	 * Returns the number of bits used to encode the index into the fill style
	 * array.
	 */
	public int getFillSize() {
		return fillSize;
	}

	/**
	 * Sets the number of bits used to encode the index into the line style
	 * array.
	 */
	public void setLineSize(final int lineSize) {
		this.lineSize = lineSize;
	}

	/**
	 * Returns the number of bits used to encode the index into the line style
	 * array.
	 */
	public int getLineSize() {
		return lineSize;
	}

	/**
	 * Sets the number of bits used to encode the advance between two
	 * successive glyphs.
	 */
	public void setAdvanceSize(final int advanceSize) {
		this.advanceSize = advanceSize;
	}

	/**
	 * Returns the number of bits used to encode the advance between two
	 * successive glyphs.
	 */
	public int getAdvanceSize() {
		return advanceSize;
	}

	/** 
	 * Sets the number of bits used to encode shape segments in a glyph.
	 */
	public void setGlyphSize(final int glyphSize) {
		this.glyphSize = glyphSize;
	}

	/** 
	 * Returns the number of bits used to encode shape segments in a glyph. 
	 */
	public int getGlyphSize() {
		return glyphSize;
	}

	/** 
	 * Sets the number of bits used to encode shape segments in a shape. 
	 */
	public void setShapeSize(final int shapeSize) {
		this.shapeSize = shapeSize;
	}

	/** 
	 * Returns the number of bits used to encode shape segments in a shape. 
	 */
	public int getShapeSize() {
		return shapeSize;
	}
}
