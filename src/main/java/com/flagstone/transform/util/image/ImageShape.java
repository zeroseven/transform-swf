/*
 * ImageUtils.java
 * Transform
 *
 * Copyright (c) 2010 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.util.image;

import java.util.ArrayList;

import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.CoordTransform;
import com.flagstone.transform.datatype.HorizontalAlign;
import com.flagstone.transform.datatype.VerticalAlign;
import com.flagstone.transform.fillstyle.BitmapFill;
import com.flagstone.transform.fillstyle.FillStyle;
import com.flagstone.transform.image.ImageTag;
import com.flagstone.transform.linestyle.LineStyle;
import com.flagstone.transform.linestyle.LineStyle1;
import com.flagstone.transform.linestyle.LineStyle2;
import com.flagstone.transform.shape.DefineShape3;
import com.flagstone.transform.shape.DefineShape4;
import com.flagstone.transform.shape.Line;
import com.flagstone.transform.shape.Shape;
import com.flagstone.transform.shape.ShapeRecord;
import com.flagstone.transform.shape.ShapeStyle;
import com.flagstone.transform.shape.ShapeStyle2;
import com.flagstone.transform.shape.ShapeTag;

/**
 * ImageShape is used to generate the shape definition that is required to
 * display images in a Flash file.
 */
public final class ImageShape {
	/** The number of twips in a pixel. */
    private static final int TWIPS_PER_PIXEL = 20;
    /** The horizontal alignment of the image. */
    private transient HorizontalAlign xAlign;
    /** The vertical alignment of the image. */
    private transient VerticalAlign yAlign;
    /** The style used to draw the outline, if any of the shape. */
    private transient LineStyle style;

    /**
     * Set the line style used to draw the border around the image.
     *
     * @param lineStyle a LineStyle. May be null if no border will be drawn.
     */
    public void setStyle(final LineStyle lineStyle) {
        style = lineStyle.copy();
    }

    /**
     * Set the registration point, definition the position of the image
     * relative to the origin of the shape.
     *
     * @param halign the alignment along the x-axis.
     * @param valign the alignment along the y-axis.
     */
    public void setRegistration(final HorizontalAlign halign,
            final VerticalAlign valign) {
        xAlign = halign;
        yAlign = valign;
    }

    /**
     * Generates the shape definition used to display an image using the
     * predefined registration point and border style.
     *
     * @param uid
     *            an unique identifier that is used to reference the shape
     *            definition in a Flash movie.
     *
     * @param image
     *            the image definition.
     *
     * @return the shape that is used to display the image in a Flash movie.
     */
    public ShapeTag defineShape(final int uid, final ImageTag image) {
        int xOffset;
        int yOffset;

        if (xAlign == HorizontalAlign.LEFT) {
            xOffset = -(image.getWidth() >> 2);
        } else if (xAlign == HorizontalAlign.RIGHT) {
            xOffset = image.getWidth() >> 2;
        } else {
            xOffset = 0;
        }

        if (yAlign == VerticalAlign.TOP) {
            yOffset = -(image.getHeight() >> 2);
        } else if (yAlign == VerticalAlign.BOTTOM) {
            yOffset = image.getHeight() >> 2;
        } else {
            yOffset = 0;
        }

        return defineShape(uid, image, xOffset, yOffset, style);
    }

    /**
     * Generates the shape definition used to display an image.
     *
     * @param uid
     *            an unique identifier that is used to reference the shape
     *            definition in a Flash movie.
     *
     * @param image
     *            the image definition.
     *
     * @param xOrigin
     *            the offset in pixels along the x-axis, relative to the top
     *            left corner of the image, where the origin (0,0) of the shape
     *            will be located.
     *
     * @param yOrigin
     *            the offset in pixels along the y-axis, relative to the top
     *            left corner of the image, where the origin (0,0) of the shape
     *            will be located.
     *
     * @param border
     *            the style drawn around the border of the image. May be null if
     *            no border is drawn.
     *
     * @return the shape that is used to display the image in a Flash movie.
     */
    public ShapeTag defineShape(final int uid, final ImageTag image,
            final int xOrigin, final int yOrigin, final LineStyle border) {

        final Bounds bounds = getBounds(xOrigin, yOrigin,
                image.getWidth(), image.getHeight(), border);

        final Shape shape = getShape(xOrigin, yOrigin,
                image.getWidth(), image.getHeight(), border);

        ShapeTag definition;

        if (border == null || border instanceof LineStyle1) {
            definition = new DefineShape3(uid, bounds,
                    new ArrayList<FillStyle>(), new ArrayList<LineStyle>(),
                    shape);
        } else {
            final Bounds edges = getEdges(xOrigin, yOrigin,
                    image.getWidth(), image.getHeight());

            definition = new DefineShape4(uid, bounds, edges,
                    new ArrayList<FillStyle>(), new ArrayList<LineStyle>(),
                    shape);
        }

        if (border != null) {
            definition.add(border);
        }

        definition.add(getFillStyle(image.getIdentifier(), xOrigin, yOrigin));

        return definition;
    }

    /**
     * Get the bound box that encloses the shape taking into account the
     * thickness of the outline.
     * @param xOrigin the x-coordinate of the origin.
     * @param yOrigin the y-coordinate of the origin.
     * @param width the width of the image.
     * @param height the height of the image.
     * @param border the style used to draw the outline around the image.
     * @return the bounding box that completely encloses the shape.
     */
    private Bounds getBounds(final int xOrigin, final int yOrigin,
            final int width, final int height, final LineStyle border) {

        int lineWidth;

        if (border instanceof LineStyle1) {
            lineWidth = ((LineStyle1) border).getWidth() / 2;
        } else if (border instanceof LineStyle2) {
            lineWidth = ((LineStyle2) border).getWidth() / 2;
        } else {
            lineWidth = 0;
        }

        final Bounds bounds = new Bounds(
                -xOrigin * TWIPS_PER_PIXEL - lineWidth,
                -yOrigin * TWIPS_PER_PIXEL - lineWidth,
                (width - xOrigin) * TWIPS_PER_PIXEL + lineWidth,
                (height - yOrigin) * TWIPS_PER_PIXEL + lineWidth);

        return bounds;
    }

    /**
     * Get the bound box that encloses the shape.
     * @param xOrigin the x-coordinate of the origin.
     * @param yOrigin the y-coordinate of the origin.
     * @param width the width of the image.
     * @param height the height of the image.
     * @return the bounding box that encloses the shape.
     */
    private Bounds getEdges(final int xOrigin, final int yOrigin,
            final int width, final int height) {

        return new Bounds(
                -xOrigin * TWIPS_PER_PIXEL,
                -yOrigin * TWIPS_PER_PIXEL,
                (width - xOrigin) * TWIPS_PER_PIXEL,
                (height - yOrigin) * TWIPS_PER_PIXEL);

    }

    /**
     * Get the shape used to display the image.
     * @param xOrigin the x-coordinate of the origin.
     * @param yOrigin the y-coordinate of the origin.
     * @param width the width of the image.
     * @param height the height of the image.
     * @param border the style used to draw the outline around the image.
     * @return the shape definition size correctly to display the image.
     */
    private Shape getShape(final int xOrigin, final int yOrigin,
            final int width, final int height, final LineStyle border) {
        final Shape shape = new Shape(new ArrayList<ShapeRecord>());

        if (style instanceof LineStyle2) {
            final ShapeStyle2 shapeStyle = new ShapeStyle2();
            shapeStyle.setLineStyle(1);
            shapeStyle.setFillStyle(1);
            shapeStyle.setMove(
                    -xOrigin * TWIPS_PER_PIXEL,
                    -yOrigin * TWIPS_PER_PIXEL);
            shape.add(shapeStyle);
        } else {
            final ShapeStyle shapeStyle = new ShapeStyle();
            shapeStyle.setLineStyle(border == null ? 0 : 1);
            shapeStyle.setFillStyle(1);
            shapeStyle.setMove(
                    -xOrigin * TWIPS_PER_PIXEL,
                    -yOrigin * TWIPS_PER_PIXEL);
            shape.add(shapeStyle);
        }

        shape.add(new Line(width * TWIPS_PER_PIXEL, 0));
        shape.add(new Line(0, height * TWIPS_PER_PIXEL));
        shape.add(new Line(-width * TWIPS_PER_PIXEL, 0));
        shape.add(new Line(0, -height * TWIPS_PER_PIXEL));
        return shape;
    }

    /**
     * Return the fill style that references the image and scales it to the
     * correct size.
     * @param uid the unique identifier of the image.
     * @param xOrigin the x-coordinate of the image origin.
     * @param yOrigin the y-coordinate of the image origin.
     * @return the FillStyle used to display the image.
     */
    private FillStyle getFillStyle(final int uid,
            final int xOrigin, final int yOrigin) {

        final CoordTransform transform = new CoordTransform(
                TWIPS_PER_PIXEL, TWIPS_PER_PIXEL, 0, 0,
                -xOrigin * TWIPS_PER_PIXEL, -yOrigin * TWIPS_PER_PIXEL);

        return new BitmapFill(false, false, uid, transform);
    }

}
