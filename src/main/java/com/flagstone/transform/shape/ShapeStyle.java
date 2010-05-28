/*
 * ShapeStyle.java
 * Transform
 *
 * Copyright (c) 2001-2010 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.shape;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.flagstone.transform.SWF;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.Encoder;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.exception.IllegalArgumentRangeException;
import com.flagstone.transform.fillstyle.FillStyle;
import com.flagstone.transform.linestyle.LineStyle;

/**
 * ShapeStyle is used to change the drawing environment when a shape is drawn.
 * Three operations can be performed:
 *
 * <ul>
 * <li>Select a line style or fill style.</li>
 * <li>Move the current drawing point.</li>
 * <li>Define a new set of line and fill styles.</li>
 * </ul>
 *
 * <p>
 * An ShapeStyle object can specify one or more of the operations rather than
 * specifying them in separate ShapeStyle objects - compacting the size of the
 * binary data when the object is encoded. Conversely if an operation is not
 * defined then the values may be omitted.
 * </p>
 *
 * <p>
 * Line and Fill styles are selected by the index position, starting at 1, of
 * the style in an array of styles. An index of zero means that no style is
 * used. Using the constant VALUE_NOT_SET means that the current style is
 * unchanged. Two types of fill style are supported: fillStyle is used where a
 * shape does not contain overlapping areas and altFillStyle is used where areas
 * overlap. This differs from graphics environments that only support one fill
 * style as the overlapping area would form a hole in the shape and not be
 * filled.
 * </p>
 *
 * <p>
 * A new drawing point is specified using the absolute x and y coordinates. If
 * an ShapeStyle object is the first in a shape then the current drawing point
 * is the origin of the shape (0,0). As with the line and fill styles if no
 * drawing point is set then the x and y coordinates may be set to
 * VALUE_NOT_SET.
 * </p>
 *
 * <p>
 * Finally the line or fill style arrays may left empty if no new styles are
 * being specified.
 * </p>
 *
 * <p>
 * Note that the values for the moveX and moveY attributes and the line and fill
 * styles arrays are defined in pairs and are optional only if both are set to
 * VALUE_NOT_SET.
 * </p>
 *
 */
//TODO(class)
public final class ShapeStyle implements ShapeRecord {

    /**
     * Reserved length for style counts indicated that the number of line
     * or fill styles is encoded in the next 16-bit word.
     */
    private static final int EXTENDED = 255;

    /** Format string used in toString() method. */
    private static final String FORMAT = "ShapeStyle: { move=(%d, %d);"
            + " fill=%d; alt=%d; line=%d; fillStyles=%s; lineStyles=%s }";

    private Integer moveX;
    private Integer moveY;
    private Integer fillStyle;
    private Integer altFillStyle;
    private Integer lineStyle;
    private List<FillStyle> fillStyles;
    private List<LineStyle> lineStyles;

    private transient boolean hasStyles;
    private transient boolean hasLine;
    private transient boolean hasAlt;
    private transient boolean hasFill;
    private transient boolean hasMove;

    /**
     * Creates and initialises a ShapeStyle object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @param context
     *            a Context object used to manage the decoders for different
     *            type of object and to pass information on how objects are
     *            decoded.
     *
     * @throws IOException
     *             if an error occurs while decoding the data.
     */
    // TODO(optimise)
    public ShapeStyle(final int flags, final SWFDecoder coder,
            final Context context) throws IOException {
        final Map<Integer, Integer> vars = context.getVariables();
        int numberOfFillBits = vars.get(Context.FILL_SIZE);
        int numberOfLineBits = vars.get(Context.LINE_SIZE);

        hasStyles = (flags & Coder.BIT4) != 0;
        hasLine = (flags & Coder.BIT3) != 0;
        hasAlt = (flags & Coder.BIT2) != 0;
        hasFill = (flags & Coder.BIT1) != 0;
        hasMove = (flags & Coder.BIT0) != 0;

        if (hasMove) {
            final int moveFieldSize = coder.readBits(5, false);
            moveX = coder.readBits(moveFieldSize, true);
            moveY = coder.readBits(moveFieldSize, true);
        }
        fillStyles = new ArrayList<FillStyle>();
        lineStyles = new ArrayList<LineStyle>();

        if (hasFill) {
            fillStyle = coder.readBits(numberOfFillBits, false);
        }
        if (hasAlt) {
            altFillStyle = coder.readBits(numberOfFillBits, false);
        }
        if (hasLine) {
            lineStyle = coder.readBits(numberOfLineBits, false);
        }

        if (hasStyles) {
            coder.alignToByte();

            int fillStyleCount = coder.readByte();

            if (vars.containsKey(Context.ARRAY_EXTENDED)
                    && (fillStyleCount == EXTENDED)) {
                fillStyleCount = coder.readUnsignedShort();
            }

            final SWFFactory<FillStyle> decoder = context.getRegistry()
                    .getFillStyleDecoder();

            for (int i = 0; i < fillStyleCount; i++) {
                fillStyles.add(decoder.getObject(coder, context));
            }

            int lineStyleCount = coder.readByte();

            if (vars.containsKey(Context.ARRAY_EXTENDED)
                    && (lineStyleCount == EXTENDED)) {
                lineStyleCount = coder.readUnsignedShort();
            }

            for (int i = 0; i < lineStyleCount; i++) {
                lineStyles.add(new LineStyle(coder, context));
            }

            final int sizes = coder.readByte();
            numberOfFillBits = (sizes & 0x00F0) >> 4;
            numberOfLineBits = sizes & 0x000F;

            vars.put(Context.FILL_SIZE, numberOfFillBits);
            vars.put(Context.LINE_SIZE, numberOfLineBits);
        }
    }

    /**
     * Creates an uninitialised ShapeStyle object.
     */
    public ShapeStyle() {
        fillStyles = new ArrayList<FillStyle>();
        lineStyles = new ArrayList<LineStyle>();
    }

    /**
     * Creates and initialises a ShapeStyle object using the values copied
     * from another ShapeStyle object.
     *
     * @param object
     *            a ShapeStyle object from which the values will be
     *            copied.
     */
    public ShapeStyle(final ShapeStyle object) {
        moveX = object.moveX;
        moveY = object.moveY;
        lineStyle = object.lineStyle;
        fillStyle = object.fillStyle;
        altFillStyle = object.altFillStyle;

        lineStyles = new ArrayList<LineStyle>(object.lineStyles.size());

        for (final LineStyle style : object.lineStyles) {
            lineStyles.add(style.copy());
        }

        fillStyles = new ArrayList<FillStyle>(object.fillStyles.size());

        for (final FillStyle style : object.fillStyles) {
            fillStyles.add(style.copy());
        }
    }

    /**
     * Add a LineStyle object to the array of line styles.
     *
     * @param style
     *            and LineStyle object. Must not be null.
     * @return this object.
     */
    public ShapeStyle add(final LineStyle style) {
        if (style == null) {
            throw new IllegalArgumentException();
        }
        lineStyles.add(style);
        return this;
    }

    /**
     * Add the fill style object to the array of fill styles.
     *
     * @param style
     *            and FillStyle object. Must not be null.
     * @return this object.
     */
    public ShapeStyle add(final FillStyle style) {
        if (style == null) {
            throw new IllegalArgumentException();
        }
        fillStyles.add(style);
        return this;
    }

    /**
     * Get the x-coordinate of any relative move or null if no move is
     * specified.
     *
     * @return the relative move in the x direction.
     */
    public Integer getMoveX() {
        return moveX;
    }

    /**
     * Get the y-coordinate of any relative move or null if no move is
     * specified.
     *
     * @return the relative move in the y direction.
     */
    public Integer getMoveY() {
        return moveY;
    }

    /**
     * Get the index of the line style that will be applied to any line
     * drawn. Returns null if no line style is defined.
     *
     * @return the selected line style.
     */
    public Integer getLineStyle() {
        return lineStyle;
    }

    /**
     * Get the index of the fill style that will be applied to any area
     * filled. Returns null if no fill style is defined.
     *
     * @return the selected fill style.
     */
    public Integer getFillStyle() {
        return fillStyle;
    }

    /**
     * Get the index of the fill style that will be applied to any
     * overlapping area filled. Returns null if no alternate fill style is
     * defined.
     *
     * @return the selected alternate fill style.
     */
    public Integer getAltFillStyle() {
        return altFillStyle;
    }

    /**
     * Get the array of new line styles.
     *
     * @return the array of line styles.
     */
    public List<LineStyle> getLineStyles() {
        return lineStyles;
    }

    /**
     * Returns the array of new fill styles.
     *
     * @return the array of fill styles.
     */
    public List<FillStyle> getFillStyles() {
        return fillStyles;
    }

    /**
     * Sets the x-coordinate of any relative move.
     *
     * @param coord
     *            move the current point by aNumber in the x direction. Must be
     *            in the range -65535..65535.
     * @return this object.
     */
    public ShapeStyle setMoveX(final Integer coord) {
        if ((coord != null)
                && ((coord < SWF.MIN_COORD) || (coord > SWF.MAX_COORD))) {
            throw new IllegalArgumentRangeException(
                    SWF.MIN_COORD, SWF.MAX_COORD, coord);
        }
        moveX = coord;
        return this;
    }

    /**
     * Sets the x-coordinate of any relative move.
     *
     * @param coord
     *            move the current point by aNumber in the x direction. Must be
     *            in the range -65535..65535.
     * @return this object.
     */
    public ShapeStyle setMoveY(final Integer coord) {
        if ((coord != null)
                && ((coord < SWF.MIN_COORD) || (coord > SWF.MAX_COORD))) {
            throw new IllegalArgumentRangeException(
                    SWF.MIN_COORD, SWF.MAX_COORD, coord);
        }
        moveY = coord;
        return this;
    }

    /**
     * Sets the coordinates of any relative move.
     *
     * @param xCoord
     *            move the current point by aNumber in the x direction. Must be
     *            in the range -65535..65535.
     *
     * @param yCoord
     *            move the current point by aNumber in the y direction. Must be
     *            in the range -65535..65535.
     * @return this object.
     */
    public ShapeStyle setMove(final Integer xCoord, final Integer yCoord) {
        if (((xCoord == null) && (yCoord != null))
                || ((xCoord != null) && (yCoord == null))) {
            throw new IllegalArgumentException();
        }
        if ((xCoord != null)
                && ((xCoord < SWF.MIN_COORD) || (xCoord > SWF.MAX_COORD))) {
            throw new IllegalArgumentRangeException(
                    SWF.MIN_COORD, SWF.MAX_COORD, xCoord);
        }
        if ((yCoord != null)
                && ((yCoord < SWF.MIN_COORD) || (yCoord > SWF.MAX_COORD))) {
            throw new IllegalArgumentRangeException(
                    SWF.MIN_COORD, SWF.MAX_COORD, yCoord);
        }
        moveX = xCoord;
        moveY = yCoord;
        return this;
    }

    /**
     * Sets the index of the fill style that will be applied to any area filled.
     * May be set to zero if no style is selected or null if the line style
     * remains unchanged.
     *
     * @param anIndex
     *            selects the fill style at anIndex in the fill styles array of
     *            the parent Shape object.
     * @return this object.
     */
    public ShapeStyle setFillStyle(final Integer anIndex) {
        fillStyle = anIndex;
        return this;
    }

    /**
     * Sets the index of the fill style that will be applied to any overlapping
     * area filled. May be set to zero if no style is selected or null if the ~
     * line style remains unchanged.
     *
     * @param anIndex
     *            selects the alternate fill style at anIndex in the fill styles
     *            array of the parent Shape object.
     * @return this object.
     */
    public ShapeStyle setAltFillStyle(final Integer anIndex) {
        altFillStyle = anIndex;
        return this;
    }

    /**
     * Sets the index of the line style that will be applied to any line drawn.
     * May be set to zero if no style is selected or null if the line style
     * remains unchanged.
     *
     * @param anIndex
     *            selects the line style at anIndex in the line styles array of
     *            the parent Shape object.
     * @return this object.
     */
    public ShapeStyle setLineStyle(final Integer anIndex) {
        lineStyle = anIndex;
        return this;
    }

    /**
     * Sets the array of new line styles. May be set to null if no styles are
     * being defined.
     *
     * @param anArray
     *            an array of LineStyle objects. Must not be null.
     * @return this object.
     */
    public ShapeStyle setLineStyles(final List<LineStyle> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        lineStyles = anArray;
        return this;
    }

    /**
     * Sets the array of new fill styles. May be set to null if no styles are
     * being defined.
     *
     * @param anArray
     *            an array of fill style objects. Must not be null.
     * @return this object.
     */
    public ShapeStyle setFillStyles(final List<FillStyle> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        fillStyles = anArray;
        return this;
    }

    /** {@inheritDoc} */
    public ShapeStyle copy() {
        return new ShapeStyle(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, moveX, moveY, fillStyle, altFillStyle,
                lineStyle, fillStyles, lineStyles);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        hasLine = lineStyle != null;
        hasFill = fillStyle != null;
        hasAlt = altFillStyle != null;
        hasMove = (moveX != null) && (moveY != null);
        hasStyles = !lineStyles.isEmpty() || !fillStyles.isEmpty();

        int numberOfBits = 6;

        if (hasMove) {
            final int fieldSize = Math.max(Encoder.size(moveX), Encoder
                    .size(moveY));
            numberOfBits += 5 + fieldSize * 2;
        }

        final Map<Integer, Integer> vars = context.getVariables();
        numberOfBits += hasFill ? vars.get(Context.FILL_SIZE) : 0;
        numberOfBits += hasAlt ? vars.get(Context.FILL_SIZE) : 0;
        numberOfBits += (hasLine) ? vars.get(Context.LINE_SIZE) : 0;

        vars.put(Context.SHAPE_SIZE, vars.get(Context.SHAPE_SIZE)
                + numberOfBits);

        if (hasStyles) {
            int numberOfFillBits = Encoder.unsignedSize(fillStyles.size());
            int numberOfLineBits = Encoder.unsignedSize(lineStyles.size());

            if ((numberOfFillBits == 0)
                    && vars.containsKey(Context.POSTSCRIPT)) {
                numberOfFillBits = 1;
            }

            if ((numberOfLineBits == 0)
                    && vars.containsKey(Context.POSTSCRIPT)) {
                numberOfLineBits = 1;
            }

            final boolean countExtended = vars
                    .containsKey(Context.ARRAY_EXTENDED);

            int numberOfStyleBits = 0;
            final int flushBits = vars.get(Context.SHAPE_SIZE);

            numberOfStyleBits += (flushBits % 8 > 0)
                    ? 8 - (flushBits % 8) : 0;
            numberOfStyleBits += (countExtended
                    && (fillStyles.size() >= EXTENDED)) ? 24
                    : 8;

            for (final FillStyle style : fillStyles) {
                numberOfStyleBits += style.prepareToEncode(context) * 8;
            }

            numberOfStyleBits += (countExtended
                    && (lineStyles.size() >= EXTENDED)) ? 24
                    : 8;

            for (final LineStyle style : lineStyles) {
                numberOfStyleBits += style.prepareToEncode(context) * 8;
            }

            numberOfStyleBits += 8;

            vars.put(Context.FILL_SIZE, numberOfFillBits);
            vars.put(Context.LINE_SIZE, numberOfLineBits);
            vars.put(Context.SHAPE_SIZE, vars.get(Context.SHAPE_SIZE)
                    + numberOfStyleBits);

            numberOfBits += numberOfStyleBits;
        }
        return numberOfBits;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        coder.writeBits(0, 1);
        coder.writeBits(hasStyles ? 1 : 0, 1);
        coder.writeBits(hasLine ? 1 : 0, 1);
        coder.writeBits(hasAlt ? 1 : 0, 1);
        coder.writeBits(hasFill ? 1 : 0, 1);
        coder.writeBits(hasMove ? 1 : 0, 1);

        final Map<Integer, Integer> vars = context.getVariables();

        if (hasMove) {
            final int fieldSize = Math.max(Encoder.size(moveX), Encoder
                    .size(moveY));

            coder.writeBits(fieldSize, 5);
            coder.writeBits(moveX, fieldSize);
            coder.writeBits(moveY, fieldSize);
        }

        if (hasFill) {
            coder.writeBits(fillStyle, vars.get(Context.FILL_SIZE));
        }

        if (hasAlt) {
            coder.writeBits(altFillStyle, vars.get(Context.FILL_SIZE));
        }

        if (hasLine) {
            coder.writeBits(lineStyle, vars.get(Context.LINE_SIZE));
        }

        if (hasStyles) {
            final boolean countExtended = vars
                    .containsKey(Context.ARRAY_EXTENDED);

            coder.alignToByte();

            if (countExtended && (fillStyles.size() >= EXTENDED)) {
                coder.writeByte(EXTENDED);
                coder.writeI16(fillStyles.size());
            } else {
                coder.writeByte(fillStyles.size());
            }

            for (final FillStyle style : fillStyles) {
                style.encode(coder, context);
            }

            if (countExtended && (lineStyles.size() >= EXTENDED)) {
                coder.writeByte(EXTENDED);
                coder.writeI16(lineStyles.size());
            } else {
                coder.writeByte(lineStyles.size());
            }

            for (final LineStyle style : lineStyles) {
                style.encode(coder, context);
            }

            int numberOfFillBits = Encoder.unsignedSize(fillStyles.size());
            int numberOfLineBits = Encoder.unsignedSize(lineStyles.size());

            if (vars.containsKey(Context.POSTSCRIPT)) {
                if (numberOfFillBits == 0) {
                    numberOfFillBits = 1;
                }

                if (numberOfLineBits == 0) {
                    numberOfLineBits = 1;
                }
            }

            coder.writeBits(numberOfFillBits, 4);
            coder.writeBits(numberOfLineBits, 4);

            // Update the stream with the new numbers of line and fill bits
            vars.put(Context.FILL_SIZE, numberOfFillBits);
            vars.put(Context.LINE_SIZE, numberOfLineBits);
        }
    }
}
