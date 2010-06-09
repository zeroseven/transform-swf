package com.flagstone.transform.text;

import java.util.List;

import com.flagstone.transform.DefineTag;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.CoordTransform;

/**
 * The StaticTextTag interface provides a common set of methods for accessing
 * the bounding box, coordinate transform and TextSpans for the different
 * static text definition: DefineText, DefineText2.
 */
public interface StaticTextTag extends DefineTag {
    /**
     * Get the bounding rectangle that completely enclosed the text.
     *
     * @return the Bounds that encloses the text.
     */
    Bounds getBounds();
    /**
     * Sets the bounding rectangle that encloses the text.
     *
     * @param aBounds
     *            set the bounding rectangle for the text. Must not be null.
     */
    void setBounds(final Bounds aBounds);

    /**
     * Get the coordinate transform that controls the size, location and
     * orientation of the text when it is displayed.
     *
     * @return the coordinate transform used to position the text.
     */
    CoordTransform getTransform();
    /**
     * Sets the coordinate transform that changes the orientation and size of
     * the text displayed.
     *
     * @param aTransform
     *            an CoordTransform to change the size and orientation of the
     *            text. Must not be null.
     */
    void setTransform(final CoordTransform aTransform);

    /**
     * Get the array of text spans that define the text to be displayed.
     *
     * @return the list of text blocks.
     */
    List<TextSpan> getSpans();
    /**
     * Sets the array of text spans that define the text to be displayed.
     *
     * @param array
     *            an array of TextSpan objects that define the text to be
     *            displayed. Must not be null.
     */
    void setSpans(final List<TextSpan> array);
}
