package com.flagstone.transform.shape;

import java.util.List;

import com.flagstone.transform.DefineTag;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.fillstyle.FillStyle;
import com.flagstone.transform.linestyle.LineStyle;

/**
 * The ShapeTag interface provides a common set of methods for accessing the
 * bounding box, line styles, fill styles and shape of all the different
 * shape definitions available in Flash.
 */
public interface ShapeTag extends DefineTag {
    /**
     * Get the bounding rectangle that completely enclosed the shape.
     *
     * @return the Bounds that encloses the shape
     */
    Bounds getBounds();
    /**
     * Sets the bounding rectangle that encloses the shape.
     *
     * @param rect
     *            set the bounding rectangle for the shape. Must not be null.
     */
    void setBounds(final Bounds rect);

    /**
     * Get the list line styles.
     *
     * @return the list of line styles used in the shape.
     */
    List<LineStyle> getLineStyles();
    /**
     * Sets the list of styles that will be used to draw the outline of the
     * shape.
     *
     * @param styles
     *            the line styles for the shape. Must not be null.
     */
    void setLineStyles(final List<LineStyle> styles);
    /**
     * Add a line style.
     *
     * @param style
     *            an instance of LineStyle. Must not be null.
     */
    ShapeTag add(final LineStyle style);
    /**
     * Get the list fill styles.
     *
     * @return the list of fill styles used in the shape.
     */
    List<FillStyle> getFillStyles();
    /**
     * Sets the list fill styles that will be used to draw the shape.
     *
     * @param styles
     *            the fill styles for the shape. Must not be null.
     */
    void setFillStyles(final List<FillStyle> styles);
    /**
     * Add a fill style.
     *
     * @param style
     *            an instance of FillStyle. Must not be null.
     */
    ShapeTag add(final FillStyle style);
    /**
     * Get the shape.
     *
     * @return the shape.
     */
    Shape getShape();
    /**
     * Sets the shape.
     *
     * @param aShape
     *            set the shape to be drawn. Must not be null.
     */
    void setShape(final Shape aShape);
}
