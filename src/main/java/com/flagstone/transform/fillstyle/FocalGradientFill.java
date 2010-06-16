/*
 * FocalGradientFill.java
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

package com.flagstone.transform.fillstyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.CoordTransform;

/** TODO(class). */
public final class FocalGradientFill implements FillStyle {
    /** Scaling factor for saving floats as 8.8 fixed point numbers. */
    private static final float SCALE_8 = 256.0f;

    /** Format string used in toString() method. */
    private static final String FORMAT = "FocalGradientFill: { spread=%s;"
            + " interpolation=%s; focalPoint=%f; transform=%s; gradients=%s}";

    private int spread;
    private int interpolation;
    private int focalPoint;
    private CoordTransform transform;
    private List<Gradient> gradients;

    private transient int count;

    /**
     * Creates and initialises a FocalGradientFill fill style using values
     * encoded in the Flash binary format.
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
    public FocalGradientFill(final SWFDecoder coder, final Context context)
            throws IOException {
        transform = new CoordTransform(coder);
        count = coder.readByte();
        spread = count & FillStyleDecoder.SPREAD_MASK;
        interpolation = count & FillStyleDecoder.INTERPOLATION_MASK;
        count = count & FillStyleDecoder.GRADIENT_MASK;
        gradients = new ArrayList<Gradient>(count);

        for (int i = 0; i < count; i++) {
            gradients.add(new Gradient(coder, context));
        }

        focalPoint = coder.readSignedShort();
    }

    /**
     * Creates a GradientFill object specifying the type, coordinate transform
     * and list of gradient points.
     *
     * @param matrix
     *            the coordinate transform mapping the gradient square onto
     *            physical coordinates. Must not be null.
     * @param spreadType
     *            TODO
     * @param interpolationType
     *            how the changes in colours across the gradient are calculated.
     * @param point
     *            the position of the focal point relative to the centre of the
     *            radial circle. Values range from -1.0 (close to the left
     *            edge), to 1.0 (close to the right edge).
     * @param list
     *            a list of Gradient objects defining the control points for
     *            the gradient. For Flash 7 and earlier versions there can be up
     *            to 8 Gradients. For Flash 8 onwards this number was increased
     *            to 15. Must not be null.
     */
    public FocalGradientFill(final CoordTransform matrix,
            final Spread spreadType,
            final Interpolation interpolationType,
            final float point,
            final List<Gradient> list) {
        setTransform(matrix);
        setSpread(spreadType);
        setInterpolation(interpolationType);
        setFocalPoint(point);
        setGradients(list);
    }

    /**
     * Creates and initialises a FocalGradientFill fill style using the values
     * copied from another FocalGradientFill object.
     *
     * @param object
     *            a FocalGradientFill fill style from which the values will be
     *            copied.
     */
    public FocalGradientFill(final FocalGradientFill object) {
        spread = object.spread;
        interpolation = object.interpolation;
        focalPoint = object.focalPoint;
        transform = object.transform;
        gradients = new ArrayList<Gradient>(object.gradients);
    }

    /**
     * TODO
     * Get the Spread.
     *
     * @return the Spread.
     */
    public Spread getSpread() {
        return Spread.fromInt(spread);
    }

    /**
     * TODO
     * Set the Spread.
     *
     * @param spreadType the Spread.
     */
    public void setSpread(final Spread spreadType) {
        spread = spreadType.getValue();
    }

    /**
     * Get the method used to calculate the colour changes across the gradient.
     *
     * @return the Interpolation that describes how colours change.
     */
    public Interpolation getInterpolation() {
        return Interpolation.fromInt(interpolation);
    }

    /**
     * Set the method used to calculate the colour changes across the gradient.
     *
     * @param interpolationType
     *            the Interpolation that describes how colours change.
     */
    public void setInterpolation(final Interpolation interpolationType) {
        interpolation = interpolationType.getValue();
    }

    /**
     * Get the focal point for the radial gradient.
     * @return the focal point value in the range from -1.0 to 1.0.
     */
    public float getFocalPoint() {
        return focalPoint / SCALE_8;
    }

    /**
     * Set the focal point for the radial gradient.
     * @param point the focal point value in the range from -1.0 to 1.0.
     */
    public void setFocalPoint(final float point) {
        focalPoint = (int) (point * SCALE_8);
    }

    /**
     * Add a Gradient object to the list of gradient objects. For Flash 7 and
     * earlier versions there can be up to 8 Gradients. For Flash 8 onwards this
     * number was increased to 15.
     *
     * @param gradient
     *            an Gradient object. Must not be null.
     * @return this object.
     */
    public FocalGradientFill add(final Gradient gradient) {
        if (gradient == null) {
            throw new IllegalArgumentException();
        }
        if (gradients.size() == Gradient.MAX_GRADIENTS) {
            throw new IllegalStateException(
                    "Maximum number of gradients exceeded.");
        }
        gradients.add(gradient);
        return this;
    }

    /**
     * Get the list of Gradient objects defining the points for the
     * gradient fill.
     *
     * @return the list of points that define the gradient.
     */
    public List<Gradient> getGradients() {
        return gradients;
    }

    /**
     * Sets the list of control points that define the gradient. For Flash 7
     * and earlier this list can contain up to 8 Gradient objects. For Flash 8
     * onwards this limit was increased to 15.
     *
     * @param list
     *            a list of Gradient objects. Must not be null.
     */
    public void setGradients(final List<Gradient> list) {
        if (list == null) {
            throw new IllegalArgumentException();
        }
        if (gradients.size() > Gradient.MAX_GRADIENTS) {
            throw new IllegalStateException(
                    "Maximum number of gradients exceeded.");
        }
        gradients = list;
    }

    /**
     * Get the coordinate transform mapping the gradient square onto
     * physical coordinates.
     *
     * @return the coordinate transform for defining the gradient.
     */
    public CoordTransform getTransform() {
        return transform;
    }

    /**
     * Sets the coordinate transform mapping the gradient square onto physical
     * coordinates.
     *
     * @param matrix
     *            the coordinate transform. Must not be null.
     */
    public void setTransform(final CoordTransform matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException();
        }
        transform = matrix;
    }

    /** {@inheritDoc} */
    public FocalGradientFill copy() {
        return new FocalGradientFill(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, getSpread(), getInterpolation(),
                getFocalPoint(), transform.toString(), gradients.toString());
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        // CHECKSTYLE:OFF
        // TODO(optimise) Calculate size of gradient list directly.
        int length = 4 + transform.prepareToEncode(context);
        count = gradients.size();

        for (final Gradient gradient : gradients) {
            length += gradient.prepareToEncode(context);
        }

        return length;
        // CHECKSTYLE:ON
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        coder.writeByte(FillStyleTypes.FOCAL_GRADIENT);
        transform.encode(coder, context);
        coder.writeByte(count | spread | interpolation);

        for (final Gradient gradient : gradients) {
            gradient.encode(coder, context);
        }

        coder.writeShort(focalPoint);
    }
}
