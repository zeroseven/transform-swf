package com.flagstone.transform.coder;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.flagstone.transform.Strings;

/**
 * Contexts are used to pass information between objects when they are being
 * encoded or decoded.
 */
public final class Context {

	public final static int VERSION = 1;
	public final static int TYPE = 2;
	public final static int TRANSPARENT = 3;
	public final static int WIDE_CODES = 4;
	public final static int DECODE_ACTIONS = 5;
	public final static int DECODE_SHAPES = 6;
	public final static int DECODE_GLYPHS = 7;
	public final static int ARRAY_EXTENDED = 8;
	public final static int POSTSCRIPT = 9;
	public final static int SCALING_STROKE = 10;
	public final static int FILL_SIZE = 11;
	public final static int LINE_SIZE = 12;
	public final static int ADVANCE_SIZE = 13;
	public final static int GLYPH_SIZE = 14;
	public final static int SHAPE_SIZE = 15;

	private String encoding;
	private DecoderRegistry registry;
	private Map<Integer,Integer>variables;
	
	public Context() {
		variables = new LinkedHashMap<Integer,Integer>();
	}
	
	/**
	 * Returns character encoding scheme used when encoding or decoding strings.
	 */
	public String getEncoding() {
		return encoding;
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
	
	public DecoderRegistry getRegistry() {
		return registry;
	}
	
	public void setRegistry(DecoderRegistry registry) {
		this.registry = registry;
	}
	
	public Map<Integer,Integer> getVariables() {
		return variables;
	}
	
	public void setVariables(Map<Integer,Integer> map) {
		variables = map;
	}
}
