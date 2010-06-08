/*
 * TTFDecoder.java
 * Transform
 *
 * Copyright (c) 2009-2010 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

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

import com.flagstone.transform.coder.BigDecoder;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.CoordTransform;
import com.flagstone.transform.font.CharacterFormat;
import com.flagstone.transform.shape.Shape;
import com.flagstone.transform.shape.ShapeRecord;
import com.flagstone.transform.util.shape.Canvas;

/**
 * TTFDecoder decodes TrueType or OpenType Fonts so they can be used in a
 * Flash file.
 */
@SuppressWarnings({"PMD.TooManyFields", "PMD.TooManyMethods" })
public final class TTFDecoder implements FontProvider, FontDecoder {

    /**
     * Scaling factor for converting between floating-point and
     * 15.15 fixed-point values.
     */
    private static final float SCALE_14 = 16384.0f;
    /**
     * Scaling factor for converting between floating-point and
     * 16.16 fixed-point values.
     */
    private static final float SCALE_16 = 65536.0f;

    private static final int SIGN_EXTEND = 24;

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
//    private static final int ITLF_LONG = 1;

//    private static final int WEIGHT_THIN = 100;
//    private static final int WEIGHT_EXTRALIGHT = 200;
//    private static final int WEIGHT_LIGHT = 300;
//    private static final int WEIGHT_NORMAL = 400;
//    private static final int WEIGHT_MEDIUM = 500;
//    private static final int WEIGHT_SEMIBOLD = 600;
    private static final int WEIGHT_BOLD = 700;
//    private static final int WEIGHT_EXTRABOLD = 800;
//    private static final int WEIGHT_BLACK = 900;

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
    private static final int HAS_MORE = 0x10;

    private transient String name;
    private transient boolean bold;
    private transient boolean italic;

    private transient CharacterFormat encoding;

    private transient float ascent;
    private transient float descent;
    private transient float leading;

    private transient int[] charToGlyph;
    private transient int[] glyphToChar;

    private transient TrueTypeGlyph[] glyphTable;

    private transient int glyphCount;
    private transient int missingGlyph;
    private transient char maxChar;

    private transient int scale = 1;
    private transient int metrics;
    private transient int glyphOffset;

    private final transient List<Font>fonts = new ArrayList<Font>();

    /** {@inheritDoc} */
    public FontDecoder newDecoder() {
        return new TTFDecoder();
    }

    /** {@inheritDoc} */
    public void read(final File file) throws IOException, DataFormatException {
        FileInputStream stream = new FileInputStream(file);
        try {
            read(stream, (int) file.length());
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /** {@inheritDoc} */
    public void read(final URL url) throws IOException, DataFormatException {
        final URLConnection connection = url.openConnection();
        final int contentLength = connection.getContentLength();

        if (contentLength < 0) {
            throw new FileNotFoundException(url.getFile());
        }

        final InputStream stream = connection.getInputStream();

        try {
            read(stream, contentLength);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /** {@inheritDoc} */
    public List<Font> getFonts() {
        return fonts;
    }

    public void read(final InputStream stream, final int length)
            throws IOException {
        final BigDecoder coder = new BigDecoder(stream, length);

        /* float version = */coder.readUI32();

        final int tableCount = coder.readUI16();
        /* int searchRange = */coder.readUI16();
        /* int entrySelector = */coder.readUI16();
        /* int rangeShift = */coder.readUI16();

        int os2Offset = 0;
        int headOffset = 0;
        int hheaOffset = 0;
        int maxpOffset = 0;
        int locaOffset = 0;
        int cmapOffset = 0;
        int glyfOffset = 0;
        int hmtxOffset = 0;
        int nameOffset = 0;

//        int os2Length = 0;
//        int headLength = 0;
//        int hheaLength = 0;
//        int maxpLength = 0;
//        int locaLength = 0;
//        int cmapLength = 0;
//        int hmtxLength = 0;
//        int nameLength = 0;
//        int glyfLength = 0;

        int chunkType;
//        int checksum;
        int offset;
//        int length;

        for (int i = 0; i < tableCount; i++) {
            chunkType = coder.readUI32();
            /* checksum = */ coder.readUI32();
            offset = coder.readUI32() << 3;
            /* length = */ coder.readUI32();

            /*
             * Chunks are encoded in ascending alphabetical order so the
             * location of the tables is mapped before they are decoded since
             * the glyphs come before the loca or maxp table which identify how
             * many glyphs are encoded.
             */
            switch (chunkType) {
            case OS_2:
                os2Offset = offset;
//                os2Length = length;
                break;
            case CMAP:
                cmapOffset = offset;
//                cmapLength = length;
                break;
            case GLYF:
                glyfOffset = offset;
//                glyfLength = length;
                break;
            case HEAD:
                headOffset = offset;
//                headLength = length;
                break;
            case HHEA:
                hheaOffset = offset;
//                hheaLength = length;
                break;
            case HMTX:
                hmtxOffset = offset;
//                hmtxLength = length;
                break;
            case LOCA:
                locaOffset = offset;
//                locaLength = length;
                break;
            case MAXP:
                maxpOffset = offset;
//                maxpLength = length;
                break;
            case NAME:
                nameOffset = offset;
//                nameLength = length;
                break;
            default:
                break;
            }
        }

//        int bytesRead;

        if (maxpOffset != 0) {
            coder.setPointer(maxpOffset);
            decodeMAXP(coder);
//            bytesRead = (coder.getPointer() - maxpOffset) >> 3;
        }
        if (os2Offset != 0) {
            coder.setPointer(os2Offset);
            decodeOS2(coder);
//            bytesRead = (coder.getPointer() - os2Offset) >> 3;
        }
        if (headOffset != 0) {
            coder.setPointer(headOffset);
            decodeHEAD(coder);
//            bytesRead = (coder.getPointer() - headOffset) >> 3;
        }
        if (hheaOffset != 0) {
            coder.setPointer(hheaOffset);
            decodeHHEA(coder);
//            bytesRead = (coder.getPointer() - hheaOffset) >> 3;
        }
        if (nameOffset != 0) {
            coder.setPointer(nameOffset);
            decodeNAME(coder);
//            bytesRead = (coder.getPointer() - nameOffset) >> 3;
        }

        glyphTable = new TrueTypeGlyph[glyphCount];
        charToGlyph = new int[Coder.UNSIGNED_SHORT_MAX + 1];
        glyphToChar = new int[glyphCount];

        // Decode glyphs first so objects will be created.
        if (locaOffset != 0) {
            coder.setPointer(locaOffset);
            decodeGlyphs(coder, glyfOffset);
//            bytesRead = (coder.getPointer() - locaOffset) >> 3;
        }
        if (hmtxOffset != 0) {
            coder.setPointer(hmtxOffset);
            decodeHMTX(coder);
//            bytesRead = (coder.getPointer() - hmtxOffset) >> 3;
        }
        if (cmapOffset != 0) {
            coder.setPointer(cmapOffset);
            decodeCMAP(coder);
//            bytesRead = (coder.getPointer() - cmapOffset) >> 3;
        }

        final Font font = new Font();

        font.setFace(new FontFace(name, bold, italic));
        font.setEncoding(encoding);
        font.setAscent((int) ascent);
        font.setDescent((int) descent);
        font.setLeading((int) leading);
        font.setNumberOfGlyphs(glyphCount);
        font.setMissingGlyph(missingGlyph);
        font.setHighestChar(maxChar);

        for (int i = 0; i < glyphCount; i++) {
            font.addGlyph((char) glyphToChar[i], glyphTable[i]);
        }

        fonts.add(font);
    }

    private void decodeHEAD(final BigDecoder coder) throws IOException {
        final byte[] date = new byte[8];

        coder.readUI32(); // table version fixed 16
        coder.readUI32(); // font version fixed 16
        coder.readUI32(); // checksum adjustment
        coder.readUI32(); // magic number
        coder.readUI16(); // See following comments
        // bit15: baseline at y=0
        // bit14: side bearing at x=0;
        // bit13: instructions depend on point size
        // bit12: force ppem to integer values
        // bit11: instructions may alter advance
        // bits 10-0: unused.
        scale = coder.readUI16() / 1024; // units per em

        if (scale == 0) {
            scale = 1;
        }

        coder.readBytes(date); // number of seconds since midnight, Jan 01 1904
        coder.readBytes(date); // number of seconds since midnight, Jan 01 1904

        coder.readSI16(); // xMin for all glyph bounding boxes
        coder.readSI16(); // yMin for all glyph bounding boxes
        coder.readSI16(); // xMax for all glyph bounding boxes
        coder.readSI16(); // yMax for all glyph bounding boxes

        /*
         * Next two byte define font appearance on Macs, values are specified in
         * the OS/2 table
         */
        int flags = coder.readUI16();
        bold = (flags & Coder.BIT15) != 0;
        italic = (flags & Coder.BIT10) != 0;

        coder.readUI16(); // smallest readable size in pixels
        coder.readSI16(); // font direction hint
        glyphOffset = coder.readSI16();
        coder.readSI16(); // glyph data format
    }

    private void decodeHHEA(final BigDecoder coder) throws IOException {
        coder.readUI32(); // table version, fixed 16

        ascent = coder.readSI16() / scale;
        descent = -(coder.readSI16() / scale);
        leading = coder.readSI16() / scale;

        coder.readUI16(); // maximum advance in the htmx table
        coder.readSI16(); // minimum left side bearing in the htmx table
        coder.readSI16(); // minimum right side bearing in the htmx table
        coder.readSI16(); // maximum extent
        coder.readSI16(); // caret slope rise
        coder.readSI16(); // caret slope run
        coder.readSI16(); // caret offset

        coder.readUI16(); // reserved
        coder.readUI16(); // reserved
        coder.readUI16(); // reserved
        coder.readUI16(); // reserved

        coder.readSI16(); // metric data format

        metrics = coder.readUI16();
    }

    private void decodeOS2(final BigDecoder coder) throws IOException {
        final byte[] panose = new byte[10];
        final int[] unicodeRange = new int[4];
        final byte[] vendor = new byte[4];

        final int version = coder.readUI16(); // version
        coder.readSI16(); // average character width

        final int weight = coder.readUI16();

        if (weight == WEIGHT_BOLD) {
            bold = true;
        }

        coder.readUI16(); // width class
        coder.readUI16(); // embedding licence

        coder.readSI16(); // subscript x size
        coder.readSI16(); // subscript y size
        coder.readSI16(); // subscript x offset
        coder.readSI16(); // subscript y offset
        coder.readSI16(); // superscript x size
        coder.readSI16(); // superscript y size
        coder.readSI16(); // superscript x offset
        coder.readSI16(); // superscript y offset
        coder.readSI16(); // width of strikeout stroke
        coder.readSI16(); // strikeout stroke position
        coder.readSI16(); // font family class

        coder.readBytes(panose);

        for (int i = 0; i < 4; i++) {
            unicodeRange[i] = coder.readUI32();
        }

        coder.readBytes(vendor); // font vendor identification
        int flags = coder.readUI16();
        italic = (flags & Coder.BIT15) != 0;
        bold = (flags & Coder.BIT10) != 0;

        coder.readUI16(); // first unicode character code
        coder.readUI16(); // last unicode character code

        ascent = coder.readUI16() / scale;
        descent = -(coder.readUI16() / scale);
        leading = coder.readUI16() / scale;

        coder.readUI16(); // ascent in Windows
        coder.readUI16(); // descent in Windows

        if (version > 0) {
            coder.readUI32(); // code page range
            coder.readUI32(); // code page range

            if (version > 1) {
                coder.readSI16(); // height
                coder.readSI16(); // Capitals height
                missingGlyph = coder.readUI16();
                coder.readUI16(); // break character
                coder.readUI16(); // maximum context
            }
        }
    }

    private void decodeNAME(final BigDecoder coder) throws IOException {
        final int stringTableBase = coder.getPointer() >>> 3;

        /* final int format = */ coder.readUI16();
        final int names = coder.readUI16();
        final int stringTable = coder.readUI16() + stringTableBase;

        for (int i = 0; i < names; i++) {
            final int platformId = coder.readUI16();
            final int encodingId = coder.readUI16();
            final int languageId = coder.readUI16();
            final int nameId = coder.readUI16();

            final int stringLength = coder.readUI16();
            final int stringOffset = coder.readUI16();

            final int current = coder.getPointer();

            coder.setPointer((stringTable + stringOffset) << 3);
            final byte[] bytes = new byte[stringLength];
            coder.readBytes(bytes);

            String nameEncoding = "UTF-8";

            if (platformId == 0) {
                // Unicode
                nameEncoding = "UTF-16";
            } else if (platformId == 1) {
                // Macintosh
                if ((encodingId == 0) && (languageId == 0)) {
                    nameEncoding = "ISO8859-1";
                }
            } else if (platformId == 3) {
                // Microsoft
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
            } catch (final UnsupportedEncodingException e) {
                name = new String(bytes);
            }
            coder.setPointer(current);
        }
    }

    private void decodeMAXP(final BigDecoder coder) throws IOException {
        final float version = coder.readUI32() / SCALE_16;
        glyphCount = coder.readUI16();

        if (version == 1.0f) {
            coder.readUI16(); // maximum number of points in a simple glyph
            coder.readUI16(); // maximum number of contours in a simple glyph
            coder.readUI16(); // maximum number of points in a composite glyph
            coder.readUI16(); // maximum number of contours in a composite glyph
            coder.readUI16(); // maximum number of zones
            coder.readUI16(); // maximum number of point in Z0
            coder.readUI16(); // number of storage area locations
            coder.readUI16(); // maximum number of FDEFs
            coder.readUI16(); // maximum number of IDEFs
            coder.readUI16(); // maximum stack depth
            coder.readUI16(); // maximum byte count for glyph instructions
            coder.readUI16(); // maximum no. of components for composite glyphs
            coder.readUI16(); // maximum level of recursion
        }
    }

    private void decodeHMTX(final BigDecoder coder) throws IOException {
        int index = 0;

        for (index = 0; index < metrics; index++) {
            glyphTable[index].setAdvance((coder.readUI16() / scale));
            coder.readSI16(); // left side bearing
        }

        final int advance = glyphTable[index - 1].getAdvance();

        while (index < glyphCount) {
            glyphTable[index++].setAdvance(advance);
        }

        while (index < glyphCount) {
            coder.readSI16();
            index++;
        }
    }

    private void decodeCMAP(final BigDecoder coder) throws IOException {
        final int tableStart = coder.getPointer();

        /* final int version = */ coder.readUI16();
        final int numberOfTables = coder.readUI16();

        int platformId = 0;
        int encodingId = 0;
        int offset = 0;
        int current = 0;

        int format = 0;
//        int length = 0;
//        int language = 0;

        int segmentCount = 0;
        int[] startCount = null;
        int[] endCount = null;
        int[] delta = null;
        int[] range = null;
        int[] rangeAdr = null;

        int tableCount = 0;
        int index = 0;

        for (tableCount = 0; tableCount < numberOfTables; tableCount++) {
            platformId = coder.readUI16();
            encodingId = coder.readUI16();
            offset = coder.readUI32() << 3;
            current = coder.getPointer();

            if (platformId == 0) {
                // Unicode
                encoding = CharacterFormat.UCS2;
            } else if (platformId == 1) {
                // Macintosh
                if (encodingId == 1) {
                    encoding = CharacterFormat.SJIS;
                } else {
                    encoding = CharacterFormat.ANSI;
                }
            } else if (platformId == 3) {
                // Microsoft
                if (encodingId == 1) {
                    encoding = CharacterFormat.UCS2;
                } else if (encodingId == 2) {
                    encoding = CharacterFormat.SJIS;
                } else {
                    encoding = CharacterFormat.ANSI;
                }
            }

            coder.setPointer(tableStart + offset);

            format = coder.readUI16();
            /* length = */ coder.readUI16();
            /* language = */ coder.readUI16();

            switch (format) {
            case 0:
                for (index = 0; index < 256; index++) {
                    charToGlyph[index] = coder.readByte();
                    glyphToChar[charToGlyph[index]] = index;
                }
                break;
            case 4:
                segmentCount = coder.readUI16() / 2;

                coder.readUI16(); // search range
                coder.readUI16(); // entry selector
                coder.readUI16(); // range shift

                startCount = new int[segmentCount];
                endCount = new int[segmentCount];
                delta = new int[segmentCount];
                range = new int[segmentCount];
                rangeAdr = new int[segmentCount];

                for (index = 0; index < segmentCount; index++) {
                    endCount[index] = coder.readUI16();
                }

                coder.readUI16(); // reserved padding

                for (index = 0; index < segmentCount; index++) {
                    startCount[index] = coder.readUI16();
                }

                for (index = 0; index < segmentCount; index++) {
                    delta[index] = coder.readSI16();
                }

                for (index = 0; index < segmentCount; index++) {
                    rangeAdr[index] = coder.getPointer() >> 3;
                    range[index] = coder.readSI16();
                }

                int glyphIndex = 0;
                int location = 0;

                for (index = 0; index < segmentCount; index++) {
                    for (int code = startCount[index];
                    code <= endCount[index]; code++) {
                        if (range[index] == 0) {
                            glyphIndex = (delta[index] + code)
                                    % Coder.UNSIGNED_SHORT_MAX;
                        } else {
                            location = rangeAdr[index] + range[index]
                                    + ((code - startCount[index]) << 1);
                            coder.setPointer(location << 3);
                            glyphIndex = coder.readUI16();

                            if (glyphIndex != 0) {
                                glyphIndex = (glyphIndex + delta[index])
                                        % Coder.UNSIGNED_SHORT_MAX;
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
        encoding = CharacterFormat.SJIS;
    }

    private void decodeGlyphs(final BigDecoder coder, final int glyfOffset)
            throws IOException {
        int numberOfContours = 0;
//        final int glyphStart = 0;
        final int start = coder.getPointer();
        int end = 0;
        final int[] offsets = new int[glyphCount];

        if (glyphOffset == ITLF_SHORT) {
            offsets[0] = glyfOffset + (coder.readUI16() * 2 << 3);
        } else {
            offsets[0] = glyfOffset + (coder.readUI32() << 3);
        }

        for (int i = 1; i < glyphCount; i++) {
            if (glyphOffset == ITLF_SHORT) {
                offsets[i] = glyfOffset + (coder.readUI16() * 2 << 3);
            } else {
                offsets[i] = glyfOffset + (coder.readUI32() << 3);
            }

            if (offsets[i] == offsets[i - 1]) {
                offsets[i - 1] = 0;
            }
        }

        end = coder.getPointer();

        for (int i = 0; i < glyphCount; i++) {
            if (offsets[i] == 0) {
                glyphTable[i] = new TrueTypeGlyph(new Shape(
                        new ArrayList<ShapeRecord>()), new Bounds(0, 0, 0, 0),
                        0);
            } else {
                coder.setPointer(offsets[i]);

                numberOfContours = coder.readSI16();

                if (numberOfContours >= 0) {
                    decodeSimpleGlyph(coder, i, numberOfContours);
                }
            }
        }

        coder.setPointer(start);

        for (int i = 0; i < glyphCount; i++) {
            if (offsets[i] != 0) {
                coder.setPointer(offsets[i]);

                if (coder.readSI16() == -1) {
                    decodeCompositeGlyph(coder, i);
                }
            }
        }
        coder.setPointer(end);
    }

    private void decodeSimpleGlyph(final BigDecoder coder,
            final int glyphIndex, final int numberOfContours)
            throws IOException {
        final int xMin = coder.readSI16() / scale;
        final int yMin = coder.readSI16() / scale;
        final int xMax = coder.readSI16() / scale;
        final int yMax = coder.readSI16() / scale;

        final int[] endPtsOfContours = new int[numberOfContours];

        for (int i = 0; i < numberOfContours; i++) {
            endPtsOfContours[i] = coder.readUI16();
        }

        final int instructionCount = coder.readUI16();
        final int[] instructions = new int[instructionCount];

        for (int i = 0; i < instructionCount; i++) {
            instructions[i] = coder.readByte();
        }

        final int numberOfPoints = (numberOfContours == 0) ? 0
                : endPtsOfContours[endPtsOfContours.length - 1] + 1;

        final int[] flags = new int[numberOfPoints];
        final int[] xCoordinates = new int[numberOfPoints];
        final int[] yCoordinates = new int[numberOfPoints];
        final boolean[] onCurve = new boolean[numberOfPoints];

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
                    xCoordinates[i] = last + coder.readSI16();
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
                    yCoordinates[i] = last + coder.readSI16();
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

        glyphTable[glyphIndex] = new TrueTypeGlyph(path.getShape(),
                new Bounds(xMin, -yMax, xMax, -yMin), 0);
        glyphTable[glyphIndex].setCoordinates(xCoordinates, yCoordinates);
        glyphTable[glyphIndex].setOnCurve(onCurve);
        glyphTable[glyphIndex].setEnds(endPtsOfContours);
    }

    private void decodeCompositeGlyph(final BigDecoder coder,
            final int glyphIndex) throws IOException {
        final Shape shape = new Shape(new ArrayList<ShapeRecord>());
        CoordTransform transform = null;

        final int xMin = coder.readSI16();
        final int yMin = coder.readSI16();
        final int xMax = coder.readSI16();
        final int yMax = coder.readSI16();

        TrueTypeGlyph points = null;

        int numberOfPoints = 0;

        int[] endPtsOfContours = null;
        int[] xCoordinates = null;
        int[] yCoordinates = null;
        boolean[] onCurve = null;

        int flags = 0;
        int sourceGlyph = 0;

        int xOffset = 0;
        int yOffset = 0;

//        int sourceIndex = 0;
//        int destIndex = 0;

        do {
            flags = coder.readUI16();
            sourceGlyph = coder.readUI16();

            if ((sourceGlyph >= glyphTable.length)
                    || (glyphTable[sourceGlyph] == null)) {
                glyphTable[glyphIndex] = new TrueTypeGlyph(null,
                        new Bounds(xMin, yMin, xMax, yMax), 0);
                return;
            }

            points = glyphTable[sourceGlyph];
            numberOfPoints = points.numberOfPoints();

            endPtsOfContours = new int[points.numberOfContours()];
            points.getEnd(endPtsOfContours);

            xCoordinates = new int[numberOfPoints];
            points.getXCoordinates(xCoordinates);

            yCoordinates = new int[numberOfPoints];
            points.getYCoordinates(yCoordinates);

            onCurve = new boolean[numberOfPoints];
            points.getCurve(onCurve);

            if (((flags & ARGS_ARE_WORDS) == 0)
                    && ((flags & ARGS_ARE_XY) == 0)) {
                /* destIndex = */ coder.readByte();
                /* sourceIndex = */ coder.readByte();

                //xCoordinates[destIndex] =
                //glyphTable[sourceGlyph].xCoordinates[sourceIndex];
                //yCoordinates[destIndex] =
                //glyphTable[sourceGlyph].yCoordinates[sourceIndex];
                transform = CoordTransform.translate(0, 0);
            } else if (((flags & ARGS_ARE_WORDS) == 0)
                    && ((flags & ARGS_ARE_XY) > 0)) {
                xOffset = (coder.readByte() << SIGN_EXTEND) >> SIGN_EXTEND;
                yOffset = (coder.readByte() << SIGN_EXTEND) >> SIGN_EXTEND;
                transform = CoordTransform.translate(xOffset, yOffset);
            } else if (((flags & ARGS_ARE_WORDS) > 0)
                    && ((flags & ARGS_ARE_XY) == 0)) {
                /* destIndex = */ coder.readUI16();
                /* sourceIndex = */ coder.readUI16();

                //xCoordinates[destIndex] =
                //glyphTable[sourceGlyph].xCoordinates[sourceIndex];
                //yCoordinates[destIndex] =
                //glyphTable[sourceGlyph].yCoordinates[sourceIndex];
                transform = CoordTransform.translate(0, 0);
            } else {
                xOffset = coder.readSI16();
                yOffset = coder.readSI16();
                transform = CoordTransform.translate(xOffset, yOffset);
           }

            if ((flags & HAVE_SCALE) > 0) {
                final float scaleXY = coder.readSI16() / SCALE_14;
                transform = new CoordTransform(scaleXY, scaleXY, 0, 0, xOffset,
                        yOffset);
            } else if ((flags & HAVE_XYSCALE) > 0) {
                final float scaleX = coder.readSI16() / SCALE_14;
                final float scaleY = coder.readSI16() / SCALE_14;
                transform = new CoordTransform(scaleX, scaleY, 0, 0, xOffset,
                        yOffset);
            } else if ((flags & HAVE_2X2) > 0) {
                final float scaleX = coder.readSI16() / SCALE_14;
                final float scale01 = coder.readSI16() / SCALE_14;
                final float scale10 = coder.readSI16() / SCALE_14;
                final float scaleY = coder.readSI16() / SCALE_14;

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

        } while ((flags & HAS_MORE) > 0);

        glyphTable[glyphIndex] = new TrueTypeGlyph(shape,
                new Bounds(xMin, yMin, xMax, yMax), 0);

        glyphTable[glyphIndex].setCoordinates(xCoordinates, yCoordinates);
        glyphTable[glyphIndex].setOnCurve(onCurve);
        glyphTable[glyphIndex].setEnds(endPtsOfContours);
    }
}
