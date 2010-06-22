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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    private static final class TableEntry implements Comparable<TableEntry> {
        private transient int type;
        private transient int offset;
        private transient int length;
        private transient byte[] data;

        public int compareTo(final TableEntry obj) {
            return Integer.valueOf(offset).compareTo(obj.offset);
        }

        public void setData(final byte[] bytes) {
            data = Arrays.copyOf(bytes, bytes.length);
        }

        public byte[] getData() {
            return Arrays.copyOf(data, data.length);
        }
    }

    private static final class NameEntry {
        private int platform;
        private int encoding;
        private int language;
        private int name;
        private int length;
        private int offset;
    }

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

    private static final int BYTES_TO_BITS = 3;

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

    private transient int[] offsets;

    private transient final Map<Integer, TableEntry> table
            = new LinkedHashMap<Integer, TableEntry>();

    private final transient List<Font>fonts = new ArrayList<Font>();

    /** {@inheritDoc} */
    public FontDecoder newDecoder() {
        return new TTFDecoder();
    }

    /** {@inheritDoc} */
    public void read(final File file) throws IOException, DataFormatException {
        final FileInputStream stream = new FileInputStream(file);
        try {
            read(stream);
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
            read(stream);
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

    /** {@inheritDoc} */
    public void read(final InputStream stream) throws IOException {
        loadTables(stream);
        decodeTables();

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

    private void loadTables(final InputStream stream)  throws IOException {
        final BigDecoder coder = new BigDecoder(stream);
        coder.mark();

        /* float version = */coder.readInt();
        final int tableCount = coder.readUnsignedShort();
        /* int searchRange = */coder.readUnsignedShort();
        /* int entrySelector = */coder.readUnsignedShort();
        /* int rangeShift = */coder.readUnsignedShort();

        TableEntry[] directory = new TableEntry[tableCount];

        for (int i = 0; i < tableCount; i++) {
            directory[i] = new TableEntry();
            directory[i].type = coder.readInt();
            /* checksum */ coder.readInt();
            directory[i].offset = coder.readInt();
            directory[i].length = coder.readInt();
        }
        Arrays.sort(directory);

        for (TableEntry entry : directory) {
            coder.skip(entry.offset - coder.bytesRead());
            entry.setData(coder.readBytes(new byte[entry.length]));
            table.put(entry.type, entry);
        }
    }

    private void decodeTables() throws IOException {
        decodeMAXP(table.get(MAXP));
        decodeOS2(table.get(OS_2));
        decodeHEAD(table.get(HEAD));
        decodeHHEA(table.get(HHEA));
        decodeNAME(table.get(NAME));
        decodeLOCA(table.get(LOCA));
        decodeGlyphs(table.get(GLYF));
        decodeHMTX(table.get(HMTX));
        decodeCMAP(table.get(CMAP));
    }

    private void decodeHEAD(final TableEntry entry) throws IOException {
        final byte[] data = entry.getData();
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final BigDecoder coder = new BigDecoder(stream, data.length);

        final byte[] date = new byte[8];

        coder.readInt(); // table version fixed 16
        coder.readInt(); // font version fixed 16
        coder.readInt(); // checksum adjustment
        coder.readInt(); // magic number
        coder.readUnsignedShort(); // See following comments
        // bit15: baseline at y=0
        // bit14: side bearing at x=0;
        // bit13: instructions depend on point size
        // bit12: force ppem to integer values
        // bit11: instructions may alter advance
        // bits 10-0: unused.
        scale = coder.readUnsignedShort() / 1024; // units per em

        if (scale == 0) {
            scale = 1;
        }

        coder.readBytes(date); // number of seconds since midnight, Jan 01 1904
        coder.readBytes(date); // number of seconds since midnight, Jan 01 1904

        coder.readShort(); // xMin for all glyph bounding boxes
        coder.readShort(); // yMin for all glyph bounding boxes
        coder.readShort(); // xMax for all glyph bounding boxes
        coder.readShort(); // yMax for all glyph bounding boxes

        /*
         * Next two byte define font appearance on Macs, values are specified in
         * the OS/2 table
         */
        final int flags = coder.readUnsignedShort();
        bold = (flags & Coder.BIT15) != 0;
        italic = (flags & Coder.BIT10) != 0;

        coder.readUnsignedShort(); // smallest readable size in pixels
        coder.readShort(); // font direction hint
        glyphOffset = coder.readShort();
        coder.readShort(); // glyph data format
    }

    private void decodeHHEA(final TableEntry entry) throws IOException {
        final byte[] data = entry.getData();
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final BigDecoder coder = new BigDecoder(stream, data.length);

        coder.readInt(); // table version, fixed 16
        ascent = coder.readShort() / scale;
        descent = -(coder.readShort() / scale);
        leading = coder.readShort() / scale;

        coder.readUnsignedShort(); // maximum advance in the htmx table
        coder.readShort(); // minimum left side bearing in the htmx table
        coder.readShort(); // minimum right side bearing in the htmx table
        coder.readShort(); // maximum extent
        coder.readShort(); // caret slope rise
        coder.readShort(); // caret slope run
        coder.readShort(); // caret offset

        coder.readUnsignedShort(); // reserved
        coder.readUnsignedShort(); // reserved
        coder.readUnsignedShort(); // reserved
        coder.readUnsignedShort(); // reserved

        coder.readShort(); // metric data format

        metrics = coder.readUnsignedShort();
    }

    private void decodeOS2(final TableEntry entry) throws IOException {
        final byte[] data = entry.getData();
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final BigDecoder coder = new BigDecoder(stream, data.length);

        final byte[] panose = new byte[10];
        final int[] unicodeRange = new int[4];
        final byte[] vendor = new byte[4];

        final int version = coder.readUnsignedShort(); // version
        coder.readShort(); // average character width

        final int weight = coder.readUnsignedShort();

        if (weight == WEIGHT_BOLD) {
            bold = true;
        }

        coder.readUnsignedShort(); // width class
        coder.readUnsignedShort(); // embedding licence

        coder.readShort(); // subscript x size
        coder.readShort(); // subscript y size
        coder.readShort(); // subscript x offset
        coder.readShort(); // subscript y offset
        coder.readShort(); // superscript x size
        coder.readShort(); // superscript y size
        coder.readShort(); // superscript x offset
        coder.readShort(); // superscript y offset
        coder.readShort(); // width of strikeout stroke
        coder.readShort(); // strikeout stroke position
        coder.readShort(); // font family class

        coder.readBytes(panose);

        for (int i = 0; i < 4; i++) {
            unicodeRange[i] = coder.readInt();
        }

        coder.readBytes(vendor); // font vendor identification
        final int flags = coder.readUnsignedShort();
        italic = (flags & Coder.BIT15) != 0;
        bold = (flags & Coder.BIT10) != 0;

        coder.readUnsignedShort(); // first unicode character code
        coder.readUnsignedShort(); // last unicode character code

        ascent = coder.readUnsignedShort() / scale;
        descent = -(coder.readUnsignedShort() / scale);
        leading = coder.readUnsignedShort() / scale;

        coder.readUnsignedShort(); // ascent in Windows
        coder.readUnsignedShort(); // descent in Windows

        if (version > 0) {
            coder.readInt(); // code page range
            coder.readInt(); // code page range

            if (version > 1) {
                coder.readShort(); // height
                coder.readShort(); // Capitals height
                missingGlyph = coder.readUnsignedShort();
                coder.readUnsignedShort(); // break character
                coder.readUnsignedShort(); // maximum context
            }
        }
    }

    private void decodeNAME(final TableEntry entry) throws IOException {
        final byte[] data = entry.getData();
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final BigDecoder coder = new BigDecoder(stream, data.length);
        /* final int format = */ coder.readUnsignedShort();
        final int names = coder.readUnsignedShort();
        final int tableOffset = coder.readUnsignedShort();

        NameEntry[] table = new NameEntry[names];

        for (int i = 0; i < names; i++) {
            table[i] = new NameEntry();
            table[i].platform = coder.readUnsignedShort();
            table[i].encoding = coder.readUnsignedShort();
            table[i].language = coder.readUnsignedShort();
            table[i].name = coder.readUnsignedShort();
            table[i].length = coder.readUnsignedShort();
            table[i].offset = coder.readUnsignedShort();
        }

        for (int i = 0; i < names; i++) {
            coder.reset();
            coder.skip(tableOffset + table[i].offset);

            final byte[] bytes = new byte[table[i].length];
            coder.readBytes(bytes);

            String nameEncoding = "UTF-8";

            if (table[i].platform == 0) {
                // Unicode
                nameEncoding = "UTF-16";
            } else if (table[i].platform == 1) {
                // Macintosh
                if ((table[i].encoding == 0) && (table[i].language == 0)) {
                    nameEncoding = "ISO8859-1";
                }
            } else if (table[i].platform == 3) {
                // Microsoft
                switch (table[i].encoding) {
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
                if (table[i].name == 1) {
                    name = new String(bytes, nameEncoding);
                }
            } catch (final UnsupportedEncodingException e) {
                name = new String(bytes);
            }
        }
    }

    private void decodeMAXP(final TableEntry entry) throws IOException {
        final byte[] data = entry.getData();
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final BigDecoder coder = new BigDecoder(stream, data.length);
        final float version = coder.readInt() / SCALE_16;

        glyphCount = coder.readUnsignedShort();
        glyphTable = new TrueTypeGlyph[glyphCount];
        charToGlyph = new int[Coder.USHORT_MAX + 1];
        glyphToChar = new int[glyphCount];

        if (version == 1.0f) {
            coder.readUnsignedShort(); // max no. of points in a simple glyph
            coder.readUnsignedShort(); // max no. of contours in a simple glyph
            coder.readUnsignedShort(); // max no. of points in a composite glyph
            coder.readUnsignedShort(); // max no. of composite glyph contours
            coder.readUnsignedShort(); // max no. of zones
            coder.readUnsignedShort(); // max no. of point in Z0
            coder.readUnsignedShort(); // number of storage area locations
            coder.readUnsignedShort(); // max no. of FDEFs
            coder.readUnsignedShort(); // max no. of IDEFs
            coder.readUnsignedShort(); // maximum stack depth
            coder.readUnsignedShort(); // max byte count for glyph instructions
            coder.readUnsignedShort(); // max no. of composite glyphs components
            coder.readUnsignedShort(); // max levels of recursion
        }
    }

    private void decodeHMTX(final TableEntry entry) throws IOException {
        final byte[] data = entry.getData();
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final BigDecoder coder = new BigDecoder(stream, data.length);
        int index = 0;

        for (index = 0; index < metrics; index++) {
            glyphTable[index].setAdvance((coder.readUnsignedShort() / scale));
            coder.readShort(); // left side bearing
        }

        final int advance = glyphTable[index - 1].getAdvance();

        while (index < glyphCount) {
            glyphTable[index++].setAdvance(advance);
        }

        while (index < glyphCount) {
            coder.readShort();
            index++;
        }
    }

    private void decodeCMAP(final TableEntry entry) throws IOException {
        final byte[] data = entry.getData();
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final BigDecoder coder = new BigDecoder(stream, data.length);
        /* final int version = */ coder.readUnsignedShort();
        final int numberOfTables = coder.readUnsignedShort();

        int platformId;
        int encodingId;
        int offset = 0;
        int format = 0;

        for (int tableCount = 0; tableCount < numberOfTables; tableCount++) {
            platformId = coder.readUnsignedShort();
            encodingId = coder.readUnsignedShort();
            offset = coder.readInt();
            coder.mark();

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

            coder.move(offset);

            format = coder.readUnsignedShort();
            /* length = */ coder.readUnsignedShort();
            /* language = */ coder.readUnsignedShort();

            if (format == 0) {
                decodeSimpleCMAP(coder);
            } else if (format == 4) {
                decodeRangeCMAP(coder);
            } else {
                throw new IOException();
            }
            coder.reset();
        }
        encoding = CharacterFormat.SJIS;
    }

    private void decodeSimpleCMAP(final BigDecoder coder) throws IOException {
        for (int index = 0; index < 256; index++) {
            charToGlyph[index] = coder.readByte();
            glyphToChar[charToGlyph[index]] = index;
        }
    }

    private void decodeRangeCMAP(final BigDecoder coder) throws IOException {
        final int segmentCount = coder.readUnsignedShort() / 2;
        coder.readUnsignedShort(); // search range
        coder.readUnsignedShort(); // entry selector
        coder.readUnsignedShort(); // range shift

        final int[] startCount = new int[segmentCount];
        final int[] endCount = new int[segmentCount];
        final int[] delta = new int[segmentCount];
        final int[] range = new int[segmentCount];
        final int[] rangeAdr = new int[segmentCount];

        for (int index = 0; index < segmentCount; index++) {
            endCount[index] = coder.readUnsignedShort();
        }

        coder.readUnsignedShort(); // reserved padding

        for (int index = 0; index < segmentCount; index++) {
            startCount[index] = coder.readUnsignedShort();
        }

        for (int index = 0; index < segmentCount; index++) {
            delta[index] = coder.readShort();
        }

        for (int index = 0; index < segmentCount; index++) {
            rangeAdr[index] = coder.mark();
            range[index] = coder.readShort();
            coder.unmark();
        }

        int glyphIndex = 0;
        int location = 0;

        for (int index = 0; index < segmentCount; index++) {
            for (int code = startCount[index];
            code <= endCount[index]; code++) {
                if (range[index] == 0) {
                    glyphIndex = (delta[index] + code)
                            % Coder.USHORT_MAX;
                } else {
                    location = rangeAdr[index] + range[index]
                            + ((code - startCount[index]) << 1);
                    coder.move(location);
                    glyphIndex = coder.readUnsignedShort();

                    if (glyphIndex != 0) {
                        glyphIndex = (glyphIndex + delta[index])
                                % Coder.USHORT_MAX;
                    }
                }

                charToGlyph[code] = glyphIndex;
                glyphToChar[glyphIndex] = code;
            }
        }
    }

    private void decodeLOCA(final TableEntry entry) throws IOException {
        final byte[] data = entry.getData();
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final BigDecoder coder = new BigDecoder(stream, data.length);
        offsets = new int[glyphCount];

        if (glyphOffset == ITLF_SHORT) {
            offsets[0] = (coder.readUnsignedShort() * 2
                    << BYTES_TO_BITS);
        } else {
            offsets[0] = (coder.readInt()
                    << BYTES_TO_BITS);
        }

        for (int i = 1; i < glyphCount; i++) {
            if (glyphOffset == ITLF_SHORT) {
                offsets[i] = (coder.readUnsignedShort() * 2
                        << BYTES_TO_BITS);
            } else {
                offsets[i] = (coder.readInt()
                        << BYTES_TO_BITS);
            }

            if (offsets[i] == offsets[i - 1]) {
                offsets[i - 1] = 0;
            }
        }
    }

    private void decodeGlyphs(final TableEntry entry) throws IOException {
        final byte[] data = entry.getData();
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final BigDecoder coder = new BigDecoder(stream, data.length);
        int numberOfContours = 0;

        for (int i = 0; i < glyphCount; i++) {
            coder.skip(offsets[i] >> 3);

            numberOfContours = coder.readShort();

            if (numberOfContours >= 0) {
                decodeSimpleGlyph(coder, i, numberOfContours);
            }
            coder.reset();
        }

        for (int i = 0; i < glyphCount; i++) {
            if (offsets[i] != 0) {
                coder.skip(offsets[i] >> 3);

                if (coder.readShort() == -1) {
                    decodeCompositeGlyph(coder, i);
                }
                coder.reset();
            }
        }
    }

    private void decodeSimpleGlyph(final BigDecoder coder,
            final int glyphIndex, final int numberOfContours)
            throws IOException {

        final int xMin = coder.readShort() / scale;
        final int yMin = coder.readShort() / scale;
        final int xMax = coder.readShort() / scale;
        final int yMax = coder.readShort() / scale;

        final int[] endPtsOfContours = new int[numberOfContours];

        for (int i = 0; i < numberOfContours; i++) {
            endPtsOfContours[i] = coder.readUnsignedShort();
        }

        final int instructionCount = coder.readUnsignedShort();
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
                    xCoordinates[i] = last + coder.readShort();
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
                    yCoordinates[i] = last + coder.readShort();
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

        final int xMin = coder.readShort();
        final int yMin = coder.readShort();
        final int xMax = coder.readShort();
        final int yMax = coder.readShort();

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
            flags = coder.readUnsignedShort();
            sourceGlyph = coder.readUnsignedShort();

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
                /* destIndex = */ coder.readUnsignedShort();
                /* sourceIndex = */ coder.readUnsignedShort();

                //xCoordinates[destIndex] =
                //glyphTable[sourceGlyph].xCoordinates[sourceIndex];
                //yCoordinates[destIndex] =
                //glyphTable[sourceGlyph].yCoordinates[sourceIndex];
                transform = CoordTransform.translate(0, 0);
            } else {
                xOffset = coder.readShort();
                yOffset = coder.readShort();
                transform = CoordTransform.translate(xOffset, yOffset);
           }

            if ((flags & HAVE_SCALE) > 0) {
                final float scaleXY = coder.readShort() / SCALE_14;
                transform = new CoordTransform(scaleXY, scaleXY, 0, 0, xOffset,
                        yOffset);
            } else if ((flags & HAVE_XYSCALE) > 0) {
                final float scaleX = coder.readShort() / SCALE_14;
                final float scaleY = coder.readShort() / SCALE_14;
                transform = new CoordTransform(scaleX, scaleY, 0, 0, xOffset,
                        yOffset);
            } else if ((flags & HAVE_2X2) > 0) {
                final float scaleX = coder.readShort() / SCALE_14;
                final float scale01 = coder.readShort() / SCALE_14;
                final float scale10 = coder.readShort() / SCALE_14;
                final float scaleY = coder.readShort() / SCALE_14;

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
