package com.flagstone.transform.util.font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import com.flagstone.transform.coder.FLVDecoder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.CoordTransform;
import com.flagstone.transform.font.*;
import com.flagstone.transform.shape.Shape;
import com.flagstone.transform.shape.ShapeRecord;
import com.flagstone.transform.util.shape.Canvas;

/**
 * <p>
 * Font is used to add embedded fonts to a movie.
 * </p>
 * 
 * <p>
 * Flash supports two types of font definition: embedded fonts where the Flash
 * file contains the glyphs that are drawn to represents the text characters and
 * device fonts where the font is provided by the Flash Player showing the
 * movie. Embedded fonts are preferred since the movie will always look the same
 * regardless of where it is played - if a Flash Player does not contain a
 * device font it will substitute it with another.
 * </p>
 * 
 * <p>
 * Device fonts can be added to a movie by simply creating a DefineFont or
 * DefineFont2 object which contain the name of the font. An embedded font must
 * contain all the information to draw and layout the glyphs representing the
 * text to be displayed. The Font class hides all this detail and makes it easy
 * to add embedded fonts to a movie.
 * <p>
 * 
 * <p>
 * The Font class can be used to create embedded fonts in three ways:
 * </p>
 * 
 * <ol>
 * <li>Using TrueType or OpenType font definition stored in a file.</li>
 * <li>Using an existing font definition from a flash file.</li>
 * <li>Using a given Java AWT font as a template.</li>
 * </ol>
 * 
 * <P>
 * For OpenType or TrueType fonts, files with the extensions ".otf" or ".ttf"
 * may be used. Files containing collections of fonts ".otc" are not currently
 * supported.
 * </p>
 * 
 * <p>
 * Using an existing Flash font definition is the most interesting. Fonts can
 * initially be created using AWT Font objects or TrueType files and all the
 * visible characters included. If the generated Flash definition is saved to a
 * file it can easily and quickly be loaded. Indeed the overhead of parsing an
 * AWT or TrueType font is significant (sometimes several seconds) so creating
 * libraries of "pre-parsed" flash fonts is the preferred way of use fonts.
 * </p>
 */
@SuppressWarnings("unused")
public final class TTFDecoder implements FontProvider, FontDecoder {
	private static final int OS_2 = 0x4F532F32;
	private static final int HEAD = 0x68656164;
	private static final int HHEA = 0x68686561;
	private static final int MAXP = 0x6D617870;
	private static final int LOCA = 0x6C6F6361;
	private static final int CMAP = 0x636D6170;
	private static final int HMTX = 0x686D7478;
	private static final int NAME = 0x6E616D65;
	private static final int GLYF = 0x676C7966;

	private static final int ITLF_SHORT = 0;
	private static final int ITLF_LONG = 1;

	private static final int WEIGHT_THIN = 100;
	private static final int WEIGHT_EXTRALIGHT = 200;
	private static final int WEIGHT_LIGHT = 300;
	private static final int WEIGHT_NORMAL = 400;
	private static final int WEIGHT_MEDIUM = 500;
	private static final int WEIGHT_SEMIBOLD = 600;
	private static final int WEIGHT_BOLD = 700;
	private static final int WEIGHT_EXTRABOLD = 800;
	private static final int WEIGHT_BLACK = 900;

	private static final int ON_CURVE = 0x01;
	private static final int X_SHORT = 0x02;
	private static final int Y_SHORT = 0x04;
	private static final int REPEAT_FLAG = 0x08;
	private static final int X_SAME = 0x10;
	private static final int Y_SAME = 0x20;
	private static final int X_POSITIVE = 0x10;
	private static final int Y_POSITIVE = 0x20;

	private static final int ARGS_ARE_WORDS = 0x01;
	private static final int ARGS_ARE_XY = 0x02;
	private static final int HAVE_SCALE = 0x08;
	private static final int HAVE_XYSCALE = 0x40;
	private static final int HAVE_2X2 = 0x80;
	private static final int HAS__MORE = 0x10;

	private String name;
	private boolean bold;
	private boolean italic;

	private CharacterEncoding encoding;

	private float ascent;
	private float descent;
	private float leading;

	private int[] charToGlyph;
	private int[] glyphToChar;

	private Glyph[] glyphTable;

	private int glyphCount;
	private int missingGlyph;
	private char maxChar;

	private List<Kerning> kernings = new ArrayList<Kerning>();

	private int scale;
	private int metrics;
	private int glyphOffset;

	public FontDecoder newDecoder() {
		return new TTFDecoder();
	}

	public void read(final File file) throws FileNotFoundException, IOException,
			DataFormatException {
		decode(loadFile(file));
	}

	public void read(final URL url) throws FileNotFoundException, IOException,
			DataFormatException {
		final URLConnection connection = url.openConnection();
		final int contentLength = connection.getContentLength();

		if (contentLength < 0) {
			throw new FileNotFoundException(url.getFile());
		}

		final InputStream stream = connection.getInputStream();

		try {
			final byte[] bytes = new byte[contentLength];
			stream.read(bytes);
			decode(bytes);
		} finally {
			stream.close();
		}
	}

	public Font[] getFonts() {
		final Font[] fonts = null;

		return fonts;
	}

	private void decode(final byte[] bytes) throws CoderException {
		final FLVDecoder coder = new FLVDecoder(bytes);

		/* float version = */coder.readBits(32, true);

		final int tableCount = coder.readWord(2, false);
		/* int searchRange = */coder.readWord(2, false);
		/* int entrySelector = */coder.readWord(2, false);
		/* int rangeShift = */coder.readWord(2, false);

		int os_2Offset = 0;
		int headOffset = 0;
		int hheaOffset = 0;
		int maxpOffset = 0;
		int locaOffset = 0;
		int cmapOffset = 0;
		int glyfOffset = 0;
		int hmtxOffset = 0;
		int nameOffset = 0;

		int os_2Length = 0;
		int headLength = 0;
		int hheaLength = 0;
		int maxpLength = 0;
		int locaLength = 0;
		int cmapLength = 0;
		int hmtxLength = 0;
		int nameLength = 0;
		int glyfLength = 0;

		int chunkType;
		int checksum;
		int offset;
		int length;

		for (int i = 0; i < tableCount; i++) {
			chunkType = coder.readWord(4, false);
			checksum = coder.readWord(4, false);
			offset = coder.readWord(4, false) << 3;
			length = coder.readWord(4, false);

			/*
			 * Chunks are encoded in ascending alphabetical order so the
			 * location of the tables is mapped before they are decoded since
			 * the glyphs come before the loca or maxp table which identify how
			 * many glyphs are encoded.
			 */
			switch (chunkType) {
			case OS_2:
				os_2Offset = offset;
				os_2Length = length;
				break;
			case CMAP:
				cmapOffset = offset;
				cmapLength = length;
				break;
			case GLYF:
				glyfOffset = offset;
				glyfLength = length;
				break;
			case HEAD:
				headOffset = offset;
				headLength = length;
				break;
			case HHEA:
				hheaOffset = offset;
				hheaLength = length;
				break;
			case HMTX:
				hmtxOffset = offset;
				hmtxLength = length;
				break;
			case LOCA:
				locaOffset = offset;
				locaLength = length;
				break;
			case MAXP:
				maxpOffset = offset;
				maxpLength = length;
				break;
			case NAME:
				nameOffset = offset;
				nameLength = length;
				break;
			default:
				break;
			}
		}

		int bytesRead;

		if (maxpOffset != 0) {
			coder.setPointer(maxpOffset);
			decodeMAXP(coder);
			bytesRead = (coder.getPointer() - maxpOffset) >> 3;
		}
		if (os_2Offset != 0) {
			coder.setPointer(os_2Offset);
			decodeOS2(coder);
			bytesRead = (coder.getPointer() - os_2Offset) >> 3;
		}
		if (headOffset != 0) {
			coder.setPointer(headOffset);
			decodeHEAD(coder);
			bytesRead = (coder.getPointer() - headOffset) >> 3;
		}
		if (hheaOffset != 0) {
			coder.setPointer(hheaOffset);
			decodeHHEA(coder);
			bytesRead = (coder.getPointer() - hheaOffset) >> 3;
		}
		if (nameOffset != 0) {
			coder.setPointer(nameOffset);
			decodeNAME(coder);
			bytesRead = (coder.getPointer() - nameOffset) >> 3;
		}

		glyphTable = new Glyph[glyphCount];
		charToGlyph = new int[65536];
		glyphToChar = new int[glyphCount];

		// Decode glyphs first so objects will be created.
		if (locaOffset != 0) {
			coder.setPointer(locaOffset);
			decodeGlyphs(coder, glyfOffset);
			bytesRead = (coder.getPointer() - locaOffset) >> 3;
		}
		if (hmtxOffset != 0) {
			coder.setPointer(hmtxOffset);
			decodeHMTX(coder);
			bytesRead = (coder.getPointer() - hmtxOffset) >> 3;
		}
		if (cmapOffset != 0) {
			coder.setPointer(cmapOffset);
			decodeCMAP(coder);
			bytesRead = (coder.getPointer() - cmapOffset) >> 3;
		}
	}

	private void decodeHEAD(final FLVDecoder coder) {
		final byte[] date = new byte[8];

		coder.readBits(32, true); // table version fixed 16
		coder.readBits(32, true); // font version fixed 16
		coder.readWord(4, false); // checksum adjustment
		coder.readWord(4, false); // magic number
		coder.readBits(1, false); // baseline at y=0
		coder.readBits(1, false); // side bearing at x=0;
		coder.readBits(1, false); // instructions depend on point size
		coder.readBits(1, false); // force ppem to integer values
		coder.readBits(1, false); // instructions may alter advance
		coder.readBits(11, false);
		scale = coder.readWord(2, false) / 1024; // units per em

		if (scale == 0) {
			scale = 1;
		}

		coder.readBytes(date); // number of seconds since midnight, Jan 01 1904
		coder.readBytes(date); // number of seconds since midnight, Jan 01 1904

		coder.readWord(2, true); // xMin for all glyph bounding boxes
		coder.readWord(2, true); // yMin for all glyph bounding boxes
		coder.readWord(2, true); // xMax for all glyph bounding boxes
		coder.readWord(2, true); // yMax for all glyph bounding boxes

		/*
		 * Next two byte define font appearance on Macs, values are specified in
		 * the OS/2 table
		 */
		bold = coder.readBits(1, false) != 0;
		italic = coder.readBits(1, false) != 0;
		coder.readBits(14, false); // 

		coder.readWord(2, false);// smallest readable size in pixels
		coder.readWord(2, true); // font direction hint
		glyphOffset = coder.readWord(2, true);
		coder.readWord(2, true); // glyph data format
	}

	private void decodeHHEA(final FLVDecoder coder) {
		coder.readBits(32, true); // table version, fixed 16

		ascent = coder.readWord(2, true);
		descent = coder.readWord(2, true);
		leading = coder.readWord(2, true);

		coder.readWord(2, false); // maximum advance in the htmx table
		coder.readWord(2, true); // minimum left side bearing in the htmx table
		coder.readWord(2, true); // minimum right side bearing in the htmx table
		coder.readWord(2, true); // maximum extent
		coder.readWord(2, true); // caret slope rise
		coder.readWord(2, true); // caret slope run
		coder.readWord(2, true); // caret offset

		coder.readWord(2, false); // reserved
		coder.readWord(2, false); // reserved
		coder.readWord(2, false); // reserved
		coder.readWord(2, false); // reserved

		coder.readWord(2, true); // metric data format

		metrics = coder.readWord(2, false);
	}

	private void decodeOS2(final FLVDecoder coder) {
		final byte[] panose = new byte[10];
		int[] unicodeRange = new int[4];
		final byte[] vendor = new byte[4];

		final int version = coder.readWord(2, false); // version
		coder.readWord(2, true); // average character width

		final int weight = coder.readWord(2, false);

		if (weight == WEIGHT_BOLD) {
			bold = true;
		}

		coder.readWord(2, false); // width class
		coder.readWord(2, false); // embedding licence

		coder.readWord(2, true); // subscript x size
		coder.readWord(2, true); // subscript y size
		coder.readWord(2, true); // subscript x offset
		coder.readWord(2, true); // subscript y offset
		coder.readWord(2, true); // superscript x size
		coder.readWord(2, true); // superscript y size
		coder.readWord(2, true); // superscript x offset
		coder.readWord(2, true); // superscript y offset
		coder.readWord(2, true); // width of strikeout stroke
		coder.readWord(2, true); // strikeout stroke position
		coder.readWord(2, true); // font family class

		coder.readBytes(panose);

		for (int i = 0; i < 4; i++) {
			unicodeRange[i] = coder.readWord(4, false);
		}

		coder.readBytes(vendor); // font vendor identification

		italic = coder.readBits(1, false) != 0;
		coder.readBits(4, false);
		bold = coder.readBits(1, false) != 0;
		coder.readBits(10, false);

		coder.readWord(2, false); // first unicode character code
		coder.readWord(2, false); // last unicode character code

		ascent = coder.readWord(2, false);
		descent = coder.readWord(2, false);
		leading = coder.readWord(2, false);

		coder.readWord(2, false); // ascent in Windows
		coder.readWord(2, false); // descent in Windows

		if (version > 0) {
			coder.readWord(4, false); // code page range
			coder.readWord(4, false); // code page range

			if (version > 1) {
				coder.readWord(2, true); // height
				coder.readWord(2, true); // Capitals height
				missingGlyph = coder.readWord(2, false);
				coder.readWord(2, false); // break character
				coder.readWord(2, false); // maximum context
			}
		}
	}

	private void decodeNAME(final FLVDecoder coder) {
		final int stringTableBase = coder.getPointer() >>> 3;

		final int format = coder.readWord(2, false);
		final int names = coder.readWord(2, false);
		final int stringTable = coder.readWord(2, false) + stringTableBase;

		for (int i = 0; i < names; i++) {
			final int platformId = coder.readWord(2, false);
			final int encodingId = coder.readWord(2, false);
			final int languageId = coder.readWord(2, false);
			final int nameId = coder.readWord(2, false);

			final int stringLength = coder.readWord(2, false);
			final int stringOffset = coder.readWord(2, false);

			final int current = coder.getPointer();

			coder.setPointer((stringTable + stringOffset) << 3);
			final byte[] bytes = new byte[stringLength];
			coder.readBytes(bytes);

			String nameEncoding = "UTF-8";

			if (platformId == 0) // Unicode
			{
				nameEncoding = "UTF-16";
			} else if (platformId == 1) // Macintosh
			{
				if (encodingId == 0 && languageId == 0) {
					nameEncoding = "ISO8859-1";
				}
			} else if (platformId == 3) // Microsoft
			{
				switch (encodingId) {
				case 1:
					nameEncoding = "UTF-16";
					break;
				case 2:
					nameEncoding = "SJIS";
					break;
				case 4:
					nameEncoding = "Big5";
					break;
				default:
					nameEncoding = "UTF-8";
					break;
				}
			}

			try {
				if (nameId == 1) {
					name = new String(bytes, nameEncoding);
				}
			} catch (UnsupportedEncodingException e) {
				name = new String(bytes);
			}
			coder.setPointer(current);
		}
	}

	private void decodeMAXP(final FLVDecoder coder) {
		final float version = coder.readBits(32, true) / 65536.0f;
		glyphCount = coder.readWord(2, false);

		if (version == 1.0) {
			coder.readWord(2, false); // maximum number of points in a simple
										// glyph
			coder.readWord(2, false); // maximum number of contours in a simple
										// glyph
			coder.readWord(2, false); // maximum number of points in a composite
										// glyph
			coder.readWord(2, false); // maximum number of contours in a
										// composite glyph
			coder.readWord(2, false); // maximum number of zones
			coder.readWord(2, false); // maximum number of point in Z0
			coder.readWord(2, false); // number of storage area locations
			coder.readWord(2, false); // maximum number of FDEFs
			coder.readWord(2, false); // maximum number of IDEFs
			coder.readWord(2, false); // maximum stack depth
			coder.readWord(2, false); // maximum byte count for glyph
										// instructions
			coder.readWord(2, false); // maximum number of components for
										// composite glyphs
			coder.readWord(2, false); // maximum level of recursion
		}
	}

	private void decodeHMTX(final FLVDecoder coder) {
		int index = 0;

		for (index = 0; index < metrics; index++) {
			glyphTable[index].setAdvance((coder.readWord(2, false) / scale));
			coder.readWord(2, true); // left side bearing
		}

		final int advance = glyphTable[index - 1].getAdvance();

		while (index < glyphCount) {
			glyphTable[index++].setAdvance(advance);
		}

		while (index < glyphCount) {
			coder.readWord(2, true);
			index++;
		}
	}

	private void decodeCMAP(final FLVDecoder coder) {
		final int tableStart = coder.getPointer();

		final int version = coder.readWord(2, false);
		final int numberOfTables = coder.readWord(2, false);

		int platformId = 0;
		int encodingId = 0;
		int offset = 0;
		int current = 0;

		int format = 0;
		int length = 0;
		int language = 0;

		int segmentCount = 0;
		int[] startCount = null;
		int[] endCount = null;
		int[] delta = null;
		int[] range = null;
		int[] rangeAdr = null;

		int tableCount = 0;
		int index = 0;

		for (tableCount = 0; tableCount < numberOfTables; tableCount++) {
			platformId = coder.readWord(2, false);
			encodingId = coder.readWord(2, false);
			offset = coder.readWord(4, false) << 3;
			current = coder.getPointer();

			if (platformId == 0) // Unicode
			{
				encoding = CharacterEncoding.UCS2;
			} else if (platformId == 1) // Macintosh
			{
				if (encodingId == 1) {
					encoding = CharacterEncoding.SJIS;
				} else {
					encoding = CharacterEncoding.ANSI;
				}
			} else if (platformId == 3) // Microsoft
			{
				if (encodingId == 1) {
					encoding = CharacterEncoding.UCS2;
				} else if (encodingId == 2) {
					encoding = CharacterEncoding.SJIS;
				} else {
					encoding = CharacterEncoding.ANSI;
				}
			}

			coder.setPointer(tableStart + offset);

			format = coder.readWord(2, false);
			length = coder.readWord(2, false);
			language = coder.readWord(2, false);

			switch (format) {
			case 0:
				for (index = 0; index < 256; index++) {
					charToGlyph[index] = coder.readByte();
					glyphToChar[charToGlyph[index]] = index;
				}
				break;
			case 4:
				segmentCount = coder.readWord(2, false) / 2;

				coder.readWord(2, false); // search range
				coder.readWord(2, false); // entry selector
				coder.readWord(2, false); // range shift

				startCount = new int[segmentCount];
				endCount = new int[segmentCount];
				delta = new int[segmentCount];
				range = new int[segmentCount];
				rangeAdr = new int[segmentCount];

				for (index = 0; index < segmentCount; index++) {
					endCount[index] = coder.readWord(2, false);
				}

				coder.readWord(2, false); // reserved padding

				for (index = 0; index < segmentCount; index++) {
					startCount[index] = coder.readWord(2, false);
				}

				for (index = 0; index < segmentCount; index++) {
					delta[index] = coder.readWord(2, true);
				}

				for (index = 0; index < segmentCount; index++) {
					rangeAdr[index] = coder.getPointer() >> 3;
					range[index] = coder.readWord(2, true);
				}

				int glyphIndex = 0;
				int location = 0;

				for (index = 0; index < segmentCount; index++) {
					for (int code = startCount[index]; code <= endCount[index]; code++) {
						if (range[index] == 0) {
							glyphIndex = (delta[index] + code) % 65536;
						} else {
							location = rangeAdr[index] + range[index]
									+ ((code - startCount[index]) << 1);
							coder.setPointer(location << 3);
							glyphIndex = coder.readWord(2, false);

							if (glyphIndex != 0) {
								glyphIndex = (glyphIndex + delta[index]) % 65536;
							}
						}

						charToGlyph[code] = glyphIndex;
						glyphToChar[glyphIndex] = code;
					}
				}
				break;
			case 2:
			case 6:
				break;
			default:
				break;
			}
			coder.setPointer(current);
		}
		encoding = CharacterEncoding.SJIS;
	}

	private void decodeGlyphs(final FLVDecoder coder, final int glyfOffset)
			throws CoderException {
		int numberOfContours = 0;
		final int glyphStart = 0;
		final int start = coder.getPointer();
		int end = 0;
		int[] offsets = new int[glyphCount];

		if (glyphOffset == ITLF_SHORT) {
			offsets[0] = glyfOffset + (coder.readWord(2, false) * 2 << 3);
		} else {
			offsets[0] = glyfOffset + (coder.readWord(4, false) << 3);
		}

		for (int i = 1; i < glyphCount; i++) {
			if (glyphOffset == ITLF_SHORT) {
				offsets[i] = glyfOffset + (coder.readWord(2, false) * 2 << 3);
			} else {
				offsets[i] = glyfOffset + (coder.readWord(4, false) << 3);
			}

			if (offsets[i] == offsets[i - 1]) {
				offsets[i - 1] = 0;
			}
		}

		end = coder.getPointer();

		for (int i = 0; i < glyphCount; i++) {
			if (offsets[i] == 0) {
				glyphTable[i] = new Glyph(new Shape(
						new ArrayList<ShapeRecord>()), new Bounds(0, 0, 0, 0),
						0);
			} else {
				coder.setPointer(offsets[i]);

				numberOfContours = coder.readWord(2, true);

				if (numberOfContours >= 0) {
					decodeSimpleGlyph(coder, i, numberOfContours);
				}
			}
		}

		coder.setPointer(start);

		for (int i = 0; i < glyphCount; i++) {
			if (offsets[i] != 0) {
				coder.setPointer(offsets[i]);

				if (coder.readWord(2, true) == -1) {
					decodeCompositeGlyph(coder, i);
				}
			}
		}
		coder.setPointer(end);
	}

	private void decodeSimpleGlyph(final FLVDecoder coder, final int glyphIndex,
			final int numberOfContours) {
		final int xMin = coder.readWord(2, true) / scale;
		final int yMin = coder.readWord(2, true) / scale;
		final int xMax = coder.readWord(2, true) / scale;
		final int yMax = coder.readWord(2, true) / scale;

		final int[] endPtsOfContours = new int[numberOfContours];

		for (int i = 0; i < numberOfContours; i++) {
			endPtsOfContours[i] = coder.readWord(2, false);
		}

		final int instructionCount = coder.readWord(2, false);
		final int[] instructions = new int[instructionCount];

		for (int i = 0; i < instructionCount; i++) {
			instructions[i] = coder.readByte();
		}

		final int numberOfPoints = (numberOfContours == 0) ? 0
				: endPtsOfContours[endPtsOfContours.length - 1] + 1;

		int[] flags = new int[numberOfPoints];
		int[] xCoordinates = new int[numberOfPoints];
		int[] yCoordinates = new int[numberOfPoints];
		boolean[] onCurve = new boolean[numberOfPoints];

		int repeatCount = 0;
		int repeatFlag = 0;

		for (int i = 0; i < numberOfPoints; i++) {
			if (repeatCount > 0) {
				flags[i] = repeatFlag;
				repeatCount--;
			} else {
				flags[i] = coder.readByte();

				if ((flags[i] & REPEAT_FLAG) > 0) {
					repeatCount = coder.readByte();
					repeatFlag = flags[i];
				}
			}
			onCurve[i] = (flags[i] & ON_CURVE) > 0;
		}

		int last = 0;

		for (int i = 0; i < numberOfPoints; i++) {
			if ((flags[i] & X_SHORT) > 0) {
				if ((flags[i] & X_POSITIVE) > 0) {
					xCoordinates[i] = last + coder.readByte();
					last = xCoordinates[i];
				} else {
					xCoordinates[i] = last - coder.readByte();
					last = xCoordinates[i];
				}
			} else {
				if ((flags[i] & X_SAME) > 0) {
					xCoordinates[i] = last;
				} else {
					xCoordinates[i] = last + coder.readWord(2, true);
					last = xCoordinates[i];
				}
			}
		}

		last = 0;

		for (int i = 0; i < numberOfPoints; i++) {
			if ((flags[i] & Y_SHORT) > 0) {
				if ((flags[i] & Y_POSITIVE) > 0) {
					yCoordinates[i] = last + coder.readByte();
					last = yCoordinates[i];
				} else {
					yCoordinates[i] = last - coder.readByte();
					last = yCoordinates[i];
				}
			} else {
				if ((flags[i] & Y_SAME) > 0) {
					yCoordinates[i] = last;
				} else {
					yCoordinates[i] = last + coder.readWord(2, true);
					last = yCoordinates[i];
				}
			}
		}

		/*
		 * Convert the coordinates into a shape
		 */
		final Canvas path = new Canvas(false);

		boolean contourStart = true;
		boolean offPoint = false;

		int contour = 0;

		int xCoord = 0;
		int yCoord = 0;

		int prevX = 0;
		int prevY = 0;

		int initX = 0;
		int initY = 0;

		for (int i = 0; i < numberOfPoints; i++) {
			xCoord = xCoordinates[i] / scale;
			yCoord = yCoordinates[i] / scale;

			if (onCurve[i]) {
				if (contourStart) {
					path.moveForFont(xCoord, -yCoord);
					contourStart = false;
					initX = xCoord;
					initY = yCoord;
				} else if (offPoint) {
					path.curve(prevX, -prevY, xCoord, -yCoord);
					offPoint = false;
				} else {
					path.line(xCoord, -yCoord);
				}
			} else {
				if (offPoint) {
					path.curve(prevX, -prevY, (xCoord + prevX) / 2,
							-(yCoord + prevY) / 2);
				}

				prevX = xCoord;
				prevY = yCoord;
				offPoint = true;
			}

			if (i == endPtsOfContours[contour]) {
				if (offPoint) {
					path.curve(xCoord, -yCoord, initX, -initY);
				} else {
					path.close();
				}
				contourStart = true;
				offPoint = false;
				prevX = 0;
				prevY = 0;
				contour++;
			}
		}

		glyphTable[glyphIndex] = new Glyph(path.getShape(), new Bounds(xMin,
				-yMax, xMax, -yMin), 0);

		// glyphTable[glyphIndex].xCoordinates = xCoordinates;
		// glyphTable[glyphIndex].yCoordinates = yCoordinates;
		// glyphTable[glyphIndex].onCurve = onCurve;
		// glyphTable[glyphIndex].endPoints = endPtsOfContours;
	}

	private void decodeCompositeGlyph(final FLVDecoder coder, final int glyphIndex)
			throws CoderException {
		final Shape shape = new Shape(new ArrayList<ShapeRecord>());
		CoordTransform transform = null;

		final int xMin = coder.readWord(2, true);
		final int yMin = coder.readWord(2, true);
		final int xMax = coder.readWord(2, true);
		final int yMax = coder.readWord(2, true);

		Glyph points = null;

		final int numberOfPoints = 0;

		final int[] endPtsOfContours = null;
		final int[] xCoordinates = null;
		final int[] yCoordinates = null;
		final boolean[] onCurve = null;

		int flags = 0;
		int sourceGlyph = 0;

		int xOffset = 0;
		int yOffset = 0;

		int sourceIndex = 0;
		int destIndex = 0;

		do {
			flags = coder.readWord(2, false);
			sourceGlyph = coder.readWord(2, false);

			if (sourceGlyph >= glyphTable.length
					|| glyphTable[sourceGlyph] == null) {
				glyphTable[glyphIndex] = new Glyph(null, new Bounds(xMin, yMin,
						xMax, yMax), 0);
				return;
			}

			points = glyphTable[sourceGlyph];
			// numberOfPoints = points.xCoordinates.length;

			// endPtsOfContours = new int[points.endPoints.length];

			/*
			 * for (int i=0; i<endPtsOfContours.length; i++) endPtsOfContours[i]
			 * = points.endPoints[i];
			 * 
			 * xCoordinates = new int[numberOfPoints];
			 * 
			 * for (int i=0; i<numberOfPoints; i++) xCoordinates[i] =
			 * points.xCoordinates[i];
			 * 
			 * yCoordinates = new int[numberOfPoints];
			 * 
			 * for (int i=0; i<numberOfPoints; i++) yCoordinates[i] =
			 * points.yCoordinates[i];
			 * 
			 * onCurve = new boolean[numberOfPoints];
			 * 
			 * for (int i=0; i<numberOfPoints; i++) onCurve[i] =
			 * points.onCurve[i];
			 */
			if ((flags & ARGS_ARE_WORDS) == 0 && (flags & ARGS_ARE_XY) == 0) {
				destIndex = coder.readByte();
				sourceIndex = coder.readByte();

				// xCoordinates[destIndex] =
				// glyphTable[sourceGlyph].xCoordinates[sourceIndex];
				// yCoordinates[destIndex] =
				// glyphTable[sourceGlyph].yCoordinates[sourceIndex];
			} else if ((flags & ARGS_ARE_WORDS) == 0
					&& (flags & ARGS_ARE_XY) > 0) {
				xOffset = (coder.readByte() << 24) >> 24;
				yOffset = (coder.readByte() << 24) >> 24;
			} else if ((flags & ARGS_ARE_WORDS) > 0
					&& (flags & ARGS_ARE_XY) == 0) {
				destIndex = coder.readWord(2, false);
				sourceIndex = coder.readWord(2, false);

				// xCoordinates[destIndex] =
				// glyphTable[sourceGlyph].xCoordinates[sourceIndex];
				// yCoordinates[destIndex] =
				// glyphTable[sourceGlyph].yCoordinates[sourceIndex];
			} else {
				xOffset = coder.readWord(2, true);
				yOffset = coder.readWord(2, true);
			}

			if ((flags & HAVE_SCALE) > 0) {
				final float scaleXY = coder.readBits(16, true) / 16384.0f;
				transform = new CoordTransform(scaleXY, scaleXY, 0, 0, xOffset,
						yOffset);
			} else if ((flags & HAVE_XYSCALE) > 0) {
				final float scaleX = coder.readBits(16, true) / 16384.0f;
				final float scaleY = coder.readBits(16, true) / 16384.0f;
				transform = new CoordTransform(scaleX, scaleY, 0, 0, xOffset,
						yOffset);
			} else if ((flags & HAVE_2X2) > 0) {
				final float scaleX = coder.readBits(16, true) / 16384.0f;
				final float scale01 = coder.readBits(16, true) / 16384.0f;
				final float scale10 = coder.readBits(16, true) / 16384.0f;
				final float scaleY = coder.readBits(16, true) / 16384.0f;

				transform = new CoordTransform(scaleX, scaleY, scale01,
						scale10, xOffset, yOffset);
			}

			final float[][] matrix = transform.getMatrix();
			float[][] result;

			for (int i = 0; i < numberOfPoints; i++) {
				result = CoordTransform.product(matrix, CoordTransform
						.translate(xCoordinates[i], yCoordinates[i])
						.getMatrix());

				xCoordinates[i] = (int) result[0][2];
				yCoordinates[i] = (int) result[1][2];
			}

			final Canvas path = new Canvas(false);

			boolean contourStart = true;
			boolean offPoint = false;

			int contour = 0;

			int xCoord = 0;
			int yCoord = 0;

			int prevX = 0;
			int prevY = 0;

			int initX = 0;
			int initY = 0;

			for (int i = 0; i < numberOfPoints; i++) {
				xCoord = xCoordinates[i] / scale;
				yCoord = yCoordinates[i] / scale;

				if (onCurve[i]) {
					if (contourStart) {
						path.moveForFont(xCoord, -yCoord);
						contourStart = false;
						initX = xCoord;
						initY = yCoord;
					} else if (offPoint) {
						path.curve(prevX, -prevY, xCoord, -yCoord);
						offPoint = false;
					} else {
						path.line(xCoord, -yCoord);
					}
				} else {
					if (offPoint) {
						path.curve(prevX, -prevY, (xCoord + prevX) / 2,
								-(yCoord + prevY) / 2);
					}

					prevX = xCoord;
					prevY = yCoord;
					offPoint = true;
				}

				if (i == endPtsOfContours[contour]) {
					if (offPoint) {
						path.curve(xCoord, -yCoord, initX, -initY);
					} else {
						path.close();
					}
					contourStart = true;
					offPoint = false;
					prevX = 0;
					prevY = 0;
					contour++;
				}
			}
			shape.getObjects().addAll(path.getShape().getObjects());

		} while ((flags & HAS__MORE) > 0);

		glyphTable[glyphIndex] = new Glyph(shape, new Bounds(xMin, yMin, xMax,
				yMax), 0);

		// glyphTable[glyphIndex].xCoordinates = xCoordinates;
		// glyphTable[glyphIndex].yCoordinates = yCoordinates;
		// glyphTable[glyphIndex].onCurve = onCurve;
		// glyphTable[glyphIndex].endPoints = endPtsOfContours;
	}

	private byte[] loadFile(final File file) throws FileNotFoundException,
			IOException {
		final byte[] data = new byte[(int) file.length()];

		FileInputStream stream = null;

		try {
			stream = new FileInputStream(file);
			final int bytesRead = stream.read(data);

			if (bytesRead != data.length) {
				throw new IOException(file.getAbsolutePath());
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return data;
	}
}