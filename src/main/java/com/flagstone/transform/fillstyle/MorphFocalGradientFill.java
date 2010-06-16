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
public final class MorphFocalGradientFill implements FillStyle {

    /** Scaling factor for saving floats as 8.8 fixed point numbers. */
    private static final float SCALE_8 = 256.0f;

    /** Format string used in toString() method. */
    private static final String FORMAT = "MorphFocalGradientFill: { spread=%s;"
            + " interpolation=%s; startFocalPoint=%f; endFocalPoint=%f;"
            + " startTransform=%s; endTransform=%s; gradients=%s}";

    private CoordTransform startTransform;
    private CoordTransform endTransform;
    private int spread;
    private int interpolation;
    private int startFocalPoint;
    private int endFocalPoint;
    private List<MorphGradient> gradients;

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
    public MorphFocalGradientFill(final SWFDecoder coder, final Context context)
            throws IOException {
        startTransform = new CoordTransform(coder);
        endTransform = new CoordTransform(coder);
        count = coder.readByte() & Gradient.MAX_GRADIENTS;
        spread = count & FillStyleDecoder.SPREAD_MASK;
        interpolation = count & FillStyleDecoder.INTERPOLATION_MASK;
        count = count & FillStyleDecoder.GRADIENT_MASK;
        gradients = new ArrayList<MorphGradient>(count);

        for (int i = 0; i < count; i++) {
            gradients.add(new MorphGradient(coder, context));
        }

        startFocalPoint = coder.readSignedShort();
        endFocalPoint = coder.readSignedShort();
    }

    /**
     * Creates a MorphFocalGradientFill.
     *
     * @param startMatrix
     *            the coordinate transform mapping the gradient square onto
     *            physical coordinates at the start of the morphing process.
     *            Must not be null.
     * @param endMatrix
     *            the coordinate transform mapping the gradient square onto
     *            physical coordinates at the end of the morphing process.
     *            Must not be null.
     * @param spreadType
     *            TODO
     * @param interpolationType
     *            how the changes in colours across the gradient are calculated.
     * @param startPoint
     *            the position of the focal point relative to the centre of the
     *            radial circle at the start of the morphing process. Values
     *            range from -1.0 (close to the left edge), to 1.0 (close to
     *            the right edge).
     * @param endPoint
     *            the position of the focal point relative to the centre of the
     *            radial circle at the start of the morphing process. Values
     *            range from -1.0 (close to the left edge), to 1.0 (close to
     *            the right edge).
     * @param list
     *            a list of Gradient objects defining the control points for
     *            the gradient. For Flash 7 and earlier versions there can be up
     *            to 8 Gradients. For Flash 8 onwards this number was increased
     *            to 15. Must not be null.
     */
    public MorphFocalGradientFill(final CoordTransform startMatrix,
            final CoordTransform endMatrix,
            final Spread spreadType,
            final Interpolation interpolationType,
            final float startPoint,
            final float endPoint,
            final List<MorphGradient> list) {
        setStartTransform(startMatrix);
        setEndTransform(endMatrix);
        setSpread(spreadType);
        setInterpolation(interpolationType);
        setStartFocalPoint(startPoint);
        setEndFocalPoint(endPoint);
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
    public MorphFocalGradientFill(final MorphFocalGradientFill object) {
        spread = object.spread;
        interpolation = object.interpolation;
        startTransform = object.startTransform;
        endTransform = object.endTransform;
        startFocalPoint = object.startFocalPoint;
        endFocalPoint = object.endFocalPoint;
        gradients = new ArrayList<MorphGradient>(object.gradients);
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
     * @param aSpread the Spread.
     */
    public void setSpread(final Spread aSpread) {
        spread = aSpread.getValue();
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
     * @param interp the Interpolation that describes how colours change.
     */
    public void setInterpolation(final Interpolation interp) {
        interpolation = interp.getValue();
    }

    /**
     * Get the focal point at the start of the morphing process.
     * @return the focal point in the range -1.0 to 1.0.
     */
    public float getStartFocalPoint() {
        return startFocalPoint / SCALE_8;
    }

    /**
     * Set the focal point at the start of the morphing process.
     * @param point the focal point in the range -1.0 to 1.0.
     */
    public void setStartFocalPoint(final float point) {
        startFocalPoint = (int) (point * SCALE_8);
    }

    /**
     * Get the focal point at the end of the morphing process.
     * @return the focal point in the range -1.0 to 1.0.
     */
    public float getEndFocalPoint() {
        return endFocalPoint / SCALE_8;
    }

    /**
     * Set the focal point at the end of the morphing process.
     * @param point the focal point in the range -1.0 to 1.0.
     */
    public void setEndFocalPoint(final float point) {
        endFocalPoint = (int) (point * SCALE_8);
    }

    /**
     * Add a Gradient object to the list of gradient objects. For Flash 7 and
     * earlier versions there can be up to 8 Gradients. For Flash 8 onwards this
     * number was increased to 15.
     *
     * @param aGradient
     *            an Gradient object. Must not be null.
     * @return this object.
     */
    public MorphFocalGradientFill add(final MorphGradient aGradient) {
        if (aGradient == null) {
            throw new IllegalArgumentException();
        }
        if (gradients.size() == Gradient.MAX_GRADIENTS) {
            throw new IllegalStateException(
                    "Maximum number of gradients exceeded.");
        }
        gradients.add(aGradient);
        return this;
    }

    /**
     * Get the list of Gradient objects defining the points for the
     * gradient fill.
     *
     * @return the set of points that define the gradient.
     */
    public List<MorphGradient> getGradients() {
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
    public void setGradients(final List<MorphGradient> list) {
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
     * physical coordinates at the start of the morphing process.
     *
     * @return the starting transform for the gradient.
     */
    public CoordTransform getStartTransform() {
        return startTransform;
    }

    /**
     * Get the coordinate transform mapping the gradient square onto
     * physical coordinates at the end of the morphing process.
     *
     * @return the final transform for the gradient.
     */
    public CoordTransform getEndTransform() {
        return endTransform;
    }

    /**
     * Sets the coordinate transform mapping the gradient square onto physical
     * coordinates at the start of the morphing process.
     *
     * @param matrix
     *            the starting coordinate transform. Must not be null.
     */
    public void setStartTransform(final CoordTransform matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException();
        }
        startTransform = matrix;
    }

    /**
     * Sets the coordinate transform mapping the gradient square onto physical
     * coordinates at the end of the morphing process.
     *
     * @param matrix
     *            the ending coordinate transform. Must not be null.
     */
    public void setEndTransform(final CoordTransform matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException();
        }
        endTransform = matrix;
    }

    /** {@inheritDoc} */
    public MorphFocalGradientFill copy() {
        return new MorphFocalGradientFill(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, getSpread(), getInterpolation(),
                getStartFocalPoint(), getEndFocalPoint(),
                startTransform.toString(), endTransform.toString(),
                gradients.toString());
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        // CHECKSTYLE:OFF
        // TODO(optimise) Calculate size of gradient list directly.
        int length = 6 + startTransform.prepareToEncode(context)
            + endTransform.prepareToEncode(context);
        count = gradients.size();

        for (final MorphGradient gradient : gradients) {
            length += gradient.prepareToEncode(context);
        }
        //CHECKSTYLE:ON
        return length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        coder.writeByte(FillStyleTypes.FOCAL_GRADIENT);
        startTransform.encode(coder, context);
        endTransform.encode(coder, context);
        coder.writeByte(count | spread | interpolation);

        for (final MorphGradient gradient : gradients) {
            gradient.encode(coder, context);
        }

        coder.writeShort(startFocalPoint);
        coder.writeShort(endFocalPoint);
    }
}
