/*
 * Canvas.java
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

package com.flagstone.transform.util.shape;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.fillstyle.FillStyle;
import com.flagstone.transform.linestyle.LineStyle;
import com.flagstone.transform.linestyle.LineStyle1;
import com.flagstone.transform.shape.Curve;
import com.flagstone.transform.shape.DefineShape2;
import com.flagstone.transform.shape.DefineShape3;
import com.flagstone.transform.shape.Line;
import com.flagstone.transform.shape.Shape;
import com.flagstone.transform.shape.ShapeRecord;
import com.flagstone.transform.shape.ShapeStyle;

/**
 * <p>
 * The Canvas class is used to create shape definitions. Arbitrary paths can be
 * created using a series of move, line or curve segments. Drawing operations
 * using both absolute coordinates and coordinates relative to the current point
 * (updated after every operation) are supported.
 * </p>
 *
 * <p>
 * For curves both cubic and quadratic curves are supported. Flash only supports
 * quadratic curves so cubic curves are approximated by a series of line
 * segments using (converting cubic to quadratic curves is mathematically
 * difficult). The smoothness of cubic curves is controlled by the flatness
 * attribute which can be used to limit the number of line segments that are
 * drawn.
 * </p>
 *
 * <p>
 * As a path is drawn the maximum and minimum x and y coordinates are recorded
 * so that the bounding rectangle that completely encloses the shape can be
 * defined. This is used when creating shape definitions using the DefineShape,
 * DefineShape2 or DefineShape3 classes.
 * <p>
 *
 * <p>
 * The Canvas class also supports a number of method to create closed paths that
 * represent different geometric shapes. Basic rectangles, ellipses and circles
 * are supported. More complex shapes can be drawn using the polygon() method
 * which uses pairs of points to specified the vertices of an arbitrary shapes.
 * </p>
 *
 * <p>
 * When drawing paths whether coordinates are specified in twips or pixels is
 * set when the Canvas object is created. When specifying coordinates in pixels
 * all coordinates are converted internally to twips to perform the actual
 * drawing.
 * </p>
 *
 * <p>
 * The following code samples illustrate how to use the Canvas class create
 * shapes.
 * </p>
 *
 *<pre>
 * Canvas path = new Canvas(true); // coordinates are in pixels.
 *
 * int width = 200;
 * int height = 100;
 *
 * newPath();
 * setLineStyle(new LineStyle(1, ColorTable.black()));
 * setFillStyle(new SolidFill(ColorTable.red()));
 * move(-width / 2, -height / 2);
 * rline(width, 0);
 * rline(0, height);
 * rline(-width, 0);
 * rline(0, -height);
 * closePath();
 *
 * DefineShape3 rect = path.defineShape(movie.newIdentifier());
 *
 *</pre>
 */
//TODO(class)
@SuppressWarnings({"PMD.TooManyMethods", "PMD.TooManyFields" })
public final class Canvas {
    /**
     * Value used in the algorithm to convert quadratio Bezier curves in to
     * a set of straight lines that approximate the curve.
     */
    private static final double FLATTEN_LIMIT = 0.25;
    /** Number of twips in a pixel. */
    private static final int TWIPS_PER_PIXEL = 20;

    private static final int START = 0;
    private static final int CTRLA = 1;
    private static final int CTRLB = 2;
    private static final int ANCHOR = 3;
    private static final int MID = 2;
    private static final int CUBIC_POINTS = 4;
    private static final double CTRL_AVG = 2.0;
    private static final double ANCHOR_AVG = 3.0;

    private final transient boolean arePixels;
    private transient boolean pathInProgress = false;

    private final transient double[] cubicX = new double[CUBIC_POINTS];
    private final transient double[] cubicY = new double[CUBIC_POINTS];

    private transient int initialX;
    private transient int initialY;

    private transient int currentX;
    private transient int currentY;
    /** The x-coordinate for the last control point when drawing a curve. */
    private transient int controlX;
    /** The y-coordinate for the last control point when drawing a curve. */
    private transient int controlY;

    private transient int minX;
    private transient int minY;
    private transient int maxX;
    private transient int maxY;

    private transient int lineWidth;

    private final transient List<ShapeRecord> objects;
    private final transient List<LineStyle> lineStyles;
    private final transient List<FillStyle> fillStyles;

    /**
     * Creates an ShapeConstructor object with no path defined.
     *
     * The pixels flag controls whether the coordinates passed to methods when
     * creating a path of predefined shape are expressed in pixels (true) or
     * twips (false).
     *
     * Flash coordinates are specified in twips (1 twip equals 1/1440th of an
     * inch or 1/20th of a point). Allowing coordinates to be specified in
     * pixels simplifies the drawing process avoiding the conversion to twips by
     * multiplying each value by 20.
     *
     * @param pixels
     *            coordinates are specified in pixels when true and twips when
     *            false.
     */
    public Canvas(final boolean pixels) {
        arePixels = pixels;
        objects = new ArrayList<ShapeRecord>();
        lineStyles = new ArrayList<LineStyle>();
        fillStyles = new ArrayList<FillStyle>();
    }

    /**
     * Generates the bounding box that encloses the current path.
     *
     * @return the bounding box that encloses the current shape.
     */
    public Bounds getBounds() {
        return new Bounds(minX, minY, maxX, maxY);
    }

    /**
     * Get a copy of the array line styles.
     *
     * @return the list of line styles.
     */
    public List<LineStyle> getLineStyles() {
        final List<LineStyle> array = new ArrayList<LineStyle>(lineStyles
                .size());

        for (final LineStyle style : lineStyles) {
            array.add(style.copy());
        }

        return array;
    }

    /**
     * Get a copy of the array fill styles.
     *
     * @return the list of fill styles.
     */
    public List<FillStyle> getFillStyles() {
        final List<FillStyle> array = new ArrayList<FillStyle>(fillStyles
                .size());

        for (final FillStyle style : fillStyles) {
            array.add(style.copy());
        }

        return array;
    }

    /**
     * Returns the Shape object containing the objects used to draw the current
     * path.
     *
     * @return an Shape object contain the Line, Curve and ShapeStyle objects
     *         used to construct the current path.
     */
    public Shape getShape() {
        final ArrayList<ShapeRecord> array = new ArrayList<ShapeRecord>(objects
                .size());

        for (final ShapeRecord record : objects) {
            array.add(record.copy());
        }
        return new Shape(array);
    }

    /**
     * Set the style used to draw lines.
     *
     * @param style
     *            a line style.
     */
    public void setLineStyle(final LineStyle1 style) {
        int index;

        if (lineStyles.contains(style)) {
            index = lineStyles.indexOf(style);
        } else {
            index = lineStyles.size();
            lineStyles.add(style.copy());
        }
        lineWidth = style.getWidth();
        objects.add(new ShapeStyle().setLineStyle(index + 1));
    }

    /**
     * Set the style used to fill enclosed areas.
     *
     * @param style
     *            a fill style.
     */
    public void setFillStyle(final FillStyle style) {
        int index;

        if (fillStyles.contains(style)) {
            index = fillStyles.indexOf(style);
        } else {
            index = fillStyles.size();
            fillStyles.add(style.copy());
        }
        objects.add(new ShapeStyle().setFillStyle(index + 1));
    }

    /**
     * Set the style used to fill overlapping enclosed areas.
     *
     * @param style
     *            a fill style.
     */
    public void setAltStyle(final FillStyle style) {
        int index;

        if (fillStyles.contains(style)) {
            index = fillStyles.indexOf(style);
        } else {
            index = fillStyles.size();
            fillStyles.add(style.copy());
        }
        objects.add(new ShapeStyle().setAltFillStyle(index + 1));
    }

    /**
     * Generates a shape containing the current path and styles.
     *
     * The shape is constructed with copies of the style arrays and the shape
     * representing the path drawn. This allows the number of styles to be
     * changed without affecting previously created shapes.
     *
     * @param identifier
     *            an unique identifier for the shape.
     * @return this object.
     */
    public DefineShape2 defineShape(final int identifier) {
        return new DefineShape2(identifier, getBounds(), getFillStyles(),
                getLineStyles(), getShape());
    }

    /**
     * Generates a transparent shape containing the current path and styles.
     *
     * The shape is constructed with copies of the style arrays and the shape
     * representing the path drawn. This allows the number of styles to be
     * changed without affecting previously created shapes.
     *
     * @param identifier
     *            an unique identifier for the shape.
     * @return this object.
     */
    public DefineShape3 defineTransparentShape(final int identifier) {
        return new DefineShape3(identifier, getBounds(), getFillStyles(),
                getLineStyles(), getShape());
    }

    /**
     * Creates a new path, discarding any path elements drawn.
     */
    public void clear() {
        pathInProgress = false;

        setInitial(0, 0);
        setCurrent(0, 0);
        setControl(0, 0);
        setBounds(0, 0, 0, 0);

        fillStyles.clear();
        lineStyles.clear();
        objects.clear();

        lineWidth = 0;
    }

    /**
     * Closes the current path by drawing a line from the current point to the
     * starting point of the path.
     */
    public void close() {
        final int deltaX = initialX - currentX;
        final int deltaY = initialY - currentY;

        if ((deltaX != 0) || (deltaY != 0)) {
            objects.add(new Line(deltaX, deltaY));
        }

        setCurrent(initialX, initialY);
        pathInProgress = false;
    }

    /**
     * Move to the point (x,y).
     *
     * @param xCoord
     *            the x-coordinate of the point to move to.
     * @param yCoord
     *            the y-coordinate of the point to move to.
     */
    public void move(final int xCoord, final int yCoord) {
        final int pointX = arePixels ? xCoord * TWIPS_PER_PIXEL : xCoord;
        final int pointY = arePixels ? yCoord * TWIPS_PER_PIXEL : yCoord;

        objects.add(new ShapeStyle().setMove(pointX, pointY));

        setControl((currentX + pointX) / 2, (currentY + pointY) / 2);
        setCurrent(pointX, pointY);
        setInitial(pointX, pointY);
    }

    public void moveForFont(final int xCoord, final int yCoord) {
        final int pointX = arePixels ? xCoord * TWIPS_PER_PIXEL : xCoord;
        final int pointY = arePixels ? yCoord * TWIPS_PER_PIXEL : yCoord;
        final ShapeStyle style = new ShapeStyle().setMove(pointX, pointY);

        if (objects.isEmpty()) {
            style.setFillStyle(1);
        }

        objects.add(style);

        setControl((currentX + pointX) / 2, (currentY + pointY) / 2);
        setCurrent(pointX, pointY);
        setInitial(pointX, pointY);
    }

    /**
     * Move relative to the current point.
     *
     * @param xCoord
     *            the distance along the x-axis.
     * @param yCoord
     *            the distance along the y-axis.
     */
    public void rmove(final int xCoord, final int yCoord) {
        final int pointX = arePixels ? xCoord * TWIPS_PER_PIXEL : xCoord;
        final int pointY = arePixels ? yCoord * TWIPS_PER_PIXEL : yCoord;

        objects.add(new ShapeStyle().setMove(pointX + currentX, pointY
                + currentY));

        setControl(currentX + pointX / 2, currentY + pointY / 2);
        setCurrent(currentX + pointX, currentY + pointY);
    }

    /**
     * draw a line from the current point to the point (x,y).
     *
     * @param xCoord
     *            the x-coordinate of the end of the line.
     * @param yCoord
     *            the y-coordinate of the end of the line.
     */
    public void line(final int xCoord, final int yCoord) {
        final int pointX = (arePixels ? xCoord * TWIPS_PER_PIXEL
                : xCoord) - currentX;
        final int pointY = (arePixels ? yCoord * TWIPS_PER_PIXEL
                : yCoord) - currentY;

        objects.add(new Line(pointX, pointY));

        if (!pathInProgress) {
            setInitial(currentX, currentY);
            pathInProgress = true;
        }
        setControl(currentX + pointX / 2, currentY + pointY / 2);
        setCurrent(currentX + pointX, currentY + pointY);
    }

    /**
     * Draw a line relative to the current point.
     *
     * @param xCoord
     *            the distance along the x-axis to the end of the line.
     * @param yCoord
     *            the distance along the y-axis to the end of the line.
     */
    public void rline(final int xCoord, final int yCoord) {
        final int pointX = arePixels ? xCoord * TWIPS_PER_PIXEL : xCoord;
        final int pointY = arePixels ? yCoord * TWIPS_PER_PIXEL : yCoord;

        objects.add(new Line(pointX, pointY));

        if (!pathInProgress) {
            setInitial(currentX, currentY);
            pathInProgress = true;
        }
        setControl(currentX + pointX / 2, currentY + pointY / 2);
        setCurrent(currentX + pointX, currentY + pointY);
    }

    /**
     * Draw a quadratic bezier curve from the current point to the point (x,y)
     * with the control point (x1, y1).
     *
     * @param acontrolX
     *            the x-coordinate of the control point.
     * @param acontrolY
     *            the y-coordinate of the control point.
     * @param aanchorX
     *            the x-coordinate of the end of the curve.
     * @param aanchorY
     *            the y-coordinate of the end of the curve.
     */
    public void curve(final int acontrolX, final int acontrolY,
            final int aanchorX, final int aanchorY) {
        final int rcontrolX;
        final int rcontrolY;
        final int ranchorX;
        final int ranchorY;

        if (arePixels) {
            rcontrolX = acontrolX * TWIPS_PER_PIXEL - currentX;
            rcontrolY = acontrolY * TWIPS_PER_PIXEL - currentY;
            ranchorX = aanchorX * TWIPS_PER_PIXEL - currentX - rcontrolX;
            ranchorY = aanchorY * TWIPS_PER_PIXEL - currentY - rcontrolY;
        } else {
            rcontrolX = acontrolX - currentX;
            rcontrolY = acontrolY - currentY;
            ranchorX = aanchorX - currentX - rcontrolX;
            ranchorY = aanchorY - currentY - rcontrolY;
        }

        objects.add(new Curve(rcontrolX, rcontrolY, ranchorX, ranchorY));

        if (!pathInProgress) {
            setInitial(currentX, currentY);
            pathInProgress = true;
        }
        setControl(currentX + rcontrolX, currentY + rcontrolY);
        setCurrent(currentX + rcontrolX + ranchorX, currentY + rcontrolY
                + ranchorY);
    }

    /**
     * Draw a quadratic bezier curve relative to the current point to the point.
     *
     * @param rcontrolX
     *            the distance along the x-axis from the current point to the
     *            control point.
     * @param rcontrolY
     *            the distance along the y-axis from the current point to the
     *            control point.
     * @param ranchorX
     *            the distance along the x-axis from the current point to the
     *            end of the curve.
     * @param ranchorY
     *            the distance along the y-axis from the current point to the
     *            end of the curve.
     */
    public void rcurve(final int rcontrolX, final int rcontrolY,
            final int ranchorX, final int ranchorY) {
        final int px1;
        final int py1;
        final int px2;
        final int py2;

        if (arePixels) {
            px1 = rcontrolX * TWIPS_PER_PIXEL;
            py1 = rcontrolY * TWIPS_PER_PIXEL;
            px2 = ranchorX * TWIPS_PER_PIXEL;
            py2 = ranchorY * TWIPS_PER_PIXEL;
        } else {
            px1 = rcontrolX;
            py1 = rcontrolY;
            px2 = ranchorX;
            py2 = ranchorY;
        }

        objects.add(new Curve(px1, py1, px2, py2));

        if (!pathInProgress) {
            setInitial(currentX, currentY);
            pathInProgress = true;
        }

        setControl(currentX + px1, currentY + py1);
        setCurrent(currentX + px1 + px2, currentY + py1 + py2);
    }

    /**
     * Draw a cubic bezier curve from the current point to the point (x,y) with
     * the off-curve control points (x1, y1) and (x2, y2).
     *
     * IMPORTANT: Converting cubic bezier curves to the quadratic bezier curves
     * supported by Flash is mathematically difficult. The cubic curve is
     * approximated by a series of straight line segments.
     *
     * @param cax
     *            the x-coordinate of the first control point.
     * @param cay
     *            the y-coordinate of the first control point.
     * @param cbx
     *            the x-coordinate of the second control point.
     * @param cby
     *            the y-coordinate of the second control point.
     * @param anx
     *            the x-coordinate of the end of the curve.
     * @param any
     *            the y-coordinate of the end of the curve.
     */
    public void curve(final int cax, final int cay, final int cbx,
            final int cby, final int anx, final int any) {
        cubicX[0] = currentX;
        cubicY[0] = currentY;

        if (arePixels) {
            cubicX[CTRLA] = cax * TWIPS_PER_PIXEL;
            cubicY[CTRLA] = cay * TWIPS_PER_PIXEL;
            cubicX[CTRLB] = cbx * TWIPS_PER_PIXEL;
            cubicY[CTRLB] = cby * TWIPS_PER_PIXEL;
            cubicX[ANCHOR] = anx * TWIPS_PER_PIXEL;
            cubicY[ANCHOR] = any * TWIPS_PER_PIXEL;
        } else {
            cubicX[CTRLA] = cax;
            cubicY[CTRLA] = cay;
            cubicX[CTRLB] = cbx;
            cubicY[CTRLB] = cby;
            cubicX[ANCHOR] = anx;
            cubicY[ANCHOR] = any;
        }
        flatten();
    }

    /**
     * Draw a cubic bezier curve relative to the current point.
     *
     * IMPORTANT: Converting cubic bezier curves to the quadratic bezier curves
     * supported by Flash is mathematically difficult. The cubic curve is
     * approximated by a series of straight line segments.
     *
     * @param controlAX
     *            the distance along the x-axis from the current point to the
     *            first control point.
     * @param controlAY
     *            the distance along the y-axis from the current point to the
     *            first control point.
     * @param controlBX
     *            the distance along the x-axis from the current point to the
     *            second control point.
     * @param controlBY
     *            the distance along the y-axis from the current point to the
     *            second control point.
     * @param anchorX
     *            the distance along the x-axis from the current point to the
     *            end of the curve.
     * @param anchorY
     *            the distance along the y-axis from the current point to the
     *            end of the curve.
     */
    public void rcurve(final int controlAX, final int controlAY,
            final int controlBX, final int controlBY, final int anchorX,
            final int anchorY) {
        cubicX[0] = currentX;
        cubicY[0] = currentY;

        if (arePixels) {
            cubicX[CTRLA] = currentX + controlAX * TWIPS_PER_PIXEL;
            cubicY[CTRLA] = currentY + controlAY * TWIPS_PER_PIXEL;
            cubicX[CTRLB] = currentX + controlBX * TWIPS_PER_PIXEL;
            cubicY[CTRLB] = currentY + controlBY * TWIPS_PER_PIXEL;
            cubicX[ANCHOR] = currentX + anchorX * TWIPS_PER_PIXEL;
            cubicY[ANCHOR] = currentY + anchorY * TWIPS_PER_PIXEL;
        } else {
            cubicX[CTRLA] = currentX + controlAX;
            cubicY[CTRLA] = currentY + controlAY;
            cubicX[CTRLB] = currentX + controlBX;
            cubicY[CTRLB] = currentY + controlBY;
            cubicX[ANCHOR] = currentX + anchorX;
            cubicY[ANCHOR] = currentY + anchorY;
        }

        flatten();
    }

    /**
     * Draw a quadratic bezier curve from the current point to the point (x,y)
     * using the control point for the previously drawn curve.
     *
     * If no curve has been drawn previously then a control point midway along
     * the previous line or move is used.
     *
     * @param xCoord
     *            the x-coordinate of the end of the curve.
     * @param yCoord
     *            the y-coordinate of the end of the curve.
     */
    public void reflect(final int xCoord, final int yCoord) {
        final int rcontrolX = currentX - controlX;
        final int rcontrolY = currentY - controlY;

        final int pointX = (arePixels ? xCoord * TWIPS_PER_PIXEL
                : xCoord) - currentX;
        final int pointY = (arePixels ? yCoord * TWIPS_PER_PIXEL
                : yCoord) - currentY;

        objects.add(new Curve(rcontrolX, rcontrolY, pointX, pointY));

        if (!pathInProgress) {
            setInitial(currentX, currentY);
            pathInProgress = true;
        }

        setControl(rcontrolX + currentX, rcontrolY + currentY);
        setCurrent(pointX + currentX, pointY + currentY);
    }

    /**
     * Draw a quadratic bezier curve relative to the current point to the point
     * using the control point for the previously drawn curve.
     *
     * If no curve has been drawn previously then a control point midway along
     * the previous line or move is used.
     *
     * @param xCoord
     *            the distance along the x-axis from the current point to the
     *            end of the curve.
     * @param yCoord
     *            the distance along the y-axis from the current point to the
     *            end of the curve.
     */
    public void rreflect(final int xCoord, final int yCoord) {
        final int rcontrolX = currentX - controlX;
        final int rcontrolY = currentY - controlY;

        final int pointX = arePixels ? xCoord * TWIPS_PER_PIXEL : xCoord;
        final int pointY = arePixels ? yCoord * TWIPS_PER_PIXEL : yCoord;

        objects.add(new Curve(rcontrolX, rcontrolY, pointX, pointY));

        if (!pathInProgress) {
            setInitial(currentX, currentY);
            pathInProgress = true;
        }

        setControl(rcontrolX + currentX, rcontrolY + currentY);
        setCurrent(pointX + currentX, pointY + currentY);
    }

    /**
     * Draw a cubic bezier curve from the current point to the point (x,y). The
     * first control point is the one defined for the previously drawn curve.
     * The second control point is the coordinates (x2, y2).
     *
     * If no curve has been drawn previously then a control point midway along
     * the previous line or move is used.
     *
     * @param ctrlX
     *            the x-coordinate of the control point.
     * @param ctrlY
     *            the y-coordinate of the control point.
     * @param anchorX
     *            the x-coordinate of the end of the curve.
     * @param anchorY
     *            the y-coordinate of the end of the curve.
     */
    public void reflect(final int ctrlX, final int ctrlY, final int anchorX,
            final int anchorY) {
        final int acontrolX = currentX - controlX;
        final int acontrolY = currentY - controlY;

        final int bcontrolX;
        final int bcontrolY;

        final int pointX;
        final int pointY;

        if (arePixels) {
            bcontrolX = ctrlX * TWIPS_PER_PIXEL - currentX;
            bcontrolY = ctrlY * TWIPS_PER_PIXEL - currentY;
            pointX = anchorX * TWIPS_PER_PIXEL - currentX;
            pointY = anchorY * TWIPS_PER_PIXEL - currentY;
        } else {
            bcontrolX = ctrlX - currentX;
            bcontrolY = ctrlY - currentY;
            pointX = anchorX - currentX;
            pointY = anchorY - currentY;
        }

        rcurve(acontrolX, acontrolY, bcontrolX, bcontrolY, pointX, pointY);
    }

    /**
     * Draw a cubic bezier curve relative to the current point. The first
     * control point is the one defined for the previously drawn curve. The
     * second control point is the relative point (x2, y2).
     *
     * If no curve has been drawn previously then a control point midway along
     * the previous line or move is used.
     *
     * @param ctrlX
     *            the distance along the x-axis from the current point to the
     *            second control point.
     * @param ctrlY
     *            the distance along the y-axis from the current point to the
     *            second control point.
     * @param anchorX
     *            the distance along the x-axis from the current point to the
     *            end of the curve.
     * @param anchorY
     *            the distance along the y-axis from the current point to the
     *            end of the curve.
     */
    public void rreflect(final int ctrlX, final int ctrlY, final int anchorX,
            final int anchorY) {
        final int acontrolX = currentX - controlX;
        final int acontrolY = currentY - controlY;

        final int bcontrolX;
        final int bcontrolY;
        final int pointX;
        final int pointY;

        if (arePixels) {
            bcontrolX = ctrlX * TWIPS_PER_PIXEL;
            bcontrolY = ctrlY * TWIPS_PER_PIXEL;
            pointX = anchorX * TWIPS_PER_PIXEL;
            pointY = anchorY * TWIPS_PER_PIXEL;
        } else {
            bcontrolX = ctrlX;
            bcontrolY = ctrlY;
            pointX = anchorX;
            pointY = anchorY;
        }

        rcurve(acontrolX, acontrolY, bcontrolX, bcontrolY, pointX, pointY);
    }

    /**
     * Draws a closed shape with vertices defines by pairs of coordinates from
     * the array argument. The first pair of points in the array specifies a
     * move. Line segments a drawn relative to the current point which is
     * updated after each segment is drawn.
     *
     * If the number of points is an odd number then the last point will be
     * ignored.
     *
     * @param points
     *            and array of coordinate pairs. The first pair of points
     *            defines the coordinates of a move operation, successive pairs
     *            define the coordinates for relative lines.
     */
    public void rpolygon(final int[] points) {
        int length = points.length;

        if (length % 2 == 1) {
            length -= 1;
        }

        rmove(points[0], points[1]);

        for (int i = 2; i < length; i += 2) {
            rline(points[i], points[i + 1]);
        }

        close();
    }

    /**
     * Draws a closed shape with vertices defines by pairs of coordinates from
     * the array argument. The first pair of points in the array specifies a
     * move. Line segments a drawn using absolute coordinates. The current point
     * which is updated after each segment is drawn.
     *
     * If the number of points is an odd number then the last point will be
     * ignored.
     *
     * @param points
     *            and array of coordinate pairs. The first pair of points
     *            defines the coordinates of a move operation, successive pairs
     *            define the coordinates of the lines.
     */
    public void polygon(final int[] points) {
        int length = points.length;

        if (length % 2 == 1) {
            length -= 1;
        }

        move(points[0], points[1]);

        for (int i = 2; i < length; i += 2) {
            line(points[i], points[i + 1]);
        }

        close();
    }

    private void setInitial(final int xCoord, final int yCoord) {
        initialX = xCoord;
        initialY = yCoord;
    }

    private void setCurrent(final int xCoord, final int yCoord) {
        currentX = xCoord;
        currentY = yCoord;

        if ((xCoord - lineWidth / 2) < minX) {
            minX = xCoord - lineWidth / 2;
        }
        if ((yCoord - lineWidth / 2) < minY) {
            minY = yCoord - lineWidth / 2;
        }
        if ((xCoord + lineWidth / 2) > maxX) {
            maxX = xCoord + lineWidth / 2;
        }
        if ((yCoord + lineWidth / 2) > maxY) {
            maxY = yCoord + lineWidth / 2;
        }
    }

    private void setControl(final int xCoord, final int yCoord) {
        controlX = xCoord;
        controlY = yCoord;

        if ((xCoord - lineWidth / 2) < minX) {
            minX = xCoord - lineWidth / 2;
        }
        if ((yCoord - lineWidth / 2) < minY) {
            minY = yCoord - lineWidth / 2;
        }
        if ((xCoord + lineWidth / 2) > maxX) {
            maxX = xCoord + lineWidth / 2;
        }
        if ((yCoord + lineWidth / 2) > maxY) {
            maxY = yCoord + lineWidth / 2;
        }
    }

    /**
     * Set the bounds for the shape being drawn.
     *
     * @param xmin
     *            x-coordinate of the top left corner.
     * @param ymin
     *            y-coordinate of the top left corner.
     * @param xmax
     *            x-coordinate of bottom right corner.
     * @param ymax
     *            y-coordinate of bottom right corner.
     */
    private void setBounds(final int xmin, final int ymin, final int xmax,
            final int ymax) {
        minX = xmin;
        minY = ymin;
        maxX = xmax;
        maxY = ymax;
    }

    /**
     * Flatten a cubic Bezier curve into a series of straight line segments.
     */
    private void flatten() {
        final double[] quadX = {0.0, 0.0, 0.0, 0.0};
        final double[] quadY = {0.0, 0.0, 0.0, 0.0};

        double delta;
        double pointAX;
        double pointAY;
        double pointBX;
        double pointBY;

        while (true) {
            pointAX = CTRL_AVG * cubicX[START]
                                        + cubicX[ANCHOR] - ANCHOR_AVG
                                        * cubicX[CTRLA];
            pointAX *= pointAX;
            pointBX = CTRL_AVG * cubicX[ANCHOR]
                                   + cubicX[START] - ANCHOR_AVG
                                   * cubicX[CTRLB];
            pointBX *= pointBX;

            if (pointAX < pointBX) {
                pointAX = pointBX;
            }

            pointAY = CTRL_AVG * cubicY[START]
                                        + cubicY[ANCHOR] - ANCHOR_AVG
                                        * cubicY[CTRLA];
            pointAY *= pointAY;
            pointBY = CTRL_AVG * cubicY[ANCHOR]
                                        + cubicY[START] - ANCHOR_AVG
                                        * cubicY[CTRLB];
            pointBY *= pointBY;

            if (pointAY < pointBY) {
                pointAY = pointBY;
            }

            if ((pointAX + pointAY) < FLATTEN_LIMIT) {
                objects.add(new Line((int) (cubicX[ANCHOR]) - currentX,
                        (int) (cubicY[ANCHOR]) - currentY));
                setControl((int) cubicX[CTRLA], (int) cubicY[CTRLA]);
                setControl((int) cubicX[CTRLB], (int) cubicY[CTRLB]);
                setCurrent((int) cubicX[ANCHOR], (int) cubicY[ANCHOR]);
                break;
            } else {
                quadX[ANCHOR] = cubicX[ANCHOR];
                delta = (cubicX[CTRLA] + cubicX[CTRLB]) / MID;
                cubicX[1] = (cubicX[START] + cubicX[CTRLA]) / MID;
                quadX[2] = (cubicX[CTRLB] + cubicX[ANCHOR]) / MID;
                cubicX[2] = (cubicX[CTRLA] + delta) / MID;
                quadX[1] = (delta + quadX[CTRLB]) / MID;
                cubicX[ANCHOR] = (cubicX[CTRLB] + quadX[CTRLA]) / MID;
                quadX[0] = (cubicX[CTRLB] + quadX[CTRLA]) / MID;

                quadY[ANCHOR] = cubicY[ANCHOR];
                delta = (cubicY[CTRLA] + cubicY[CTRLB]) / MID;
                cubicY[1] = (cubicY[START] + cubicY[CTRLA]) / MID;
                quadY[2] = (cubicY[CTRLB] + cubicY[ANCHOR]) / MID;
                cubicY[2] = (cubicY[CTRLA] + delta) / MID;
                quadY[1] = (delta + quadY[CTRLB]) / MID;
                cubicY[ANCHOR] = (cubicY[CTRLB] + quadY[CTRLA]) / MID;
                quadY[0] = (cubicY[CTRLB] + quadY[CTRLA]) / MID;

                flatten();

                cubicX[START] = quadX[START];
                cubicY[START] = quadY[START];
                cubicX[CTRLA] = quadX[CTRLA];
                cubicY[CTRLA] = quadY[CTRLA];
                cubicX[CTRLB] = quadX[CTRLB];
                cubicY[CTRLB] = quadY[CTRLB];
                cubicX[ANCHOR] = quadX[ANCHOR];
                cubicY[ANCHOR] = quadY[ANCHOR];
            }
        }
    }
}
