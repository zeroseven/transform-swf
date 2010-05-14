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

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.SWF;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.CoordTransform;

/** TODO(class). */
public final class MorphFocalGradientFill implements FillStyle {

    /** Scaling factor for saving floats as 8.8 fixed point numbers. */
    private static final float SCALE_8 = 256.0f;

    private static final String FORMAT = "MorphFocalGradientFill: { spread=%s;"
            + " interpolation=%s; startFocalPoint=%f; endFocalPoint=%f; "
            + " startTransform=%s; endTransform=%s; gradients=%s }";

    private final transient int type;
    private int spread;
    private int interpolation;
    private int startFocalPoint;
    private int endFocalPoint;
    private CoordTransform startTransform;
    private CoordTransform endTransform;
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
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public MorphFocalGradientFill(final SWFDecoder coder, final Context context)
            throws CoderException {
        type = coder.readByte();
        startTransform = new CoordTransform(coder);
        endTransform = new CoordTransform(coder);
        count = coder.readByte() & SWF.MAX_GRADIENTS;
        spread = count & FillStyleDecoder.SPREAD_MASK;
        interpolation = count & FillStyleDecoder.INTERPOLATION_MASK;
        count = count & FillStyleDecoder.GRADIENT_MASK;
        gradients = new ArrayList<MorphGradient>(count);

        for (int i = 0; i < count; i++) {
            gradients.add(new MorphGradient(coder, context));
        }

        startFocalPoint = coder.readSI16();
        endFocalPoint = coder.readSI16();
    }


    public MorphFocalGradientFill(final Spread spreadType,
            final Interpolation anInterpolation,
            final CoordTransform start, final CoordTransform end,
            final List<MorphGradient> anArray,
            final float startPoint, final float endPoint) {
        type = FillStyleTypes.FOCAL_GRADIENT;
        setSpread(spreadType);
        setInterpolation(anInterpolation);
        setStartTransform(start);
        setEndTransform(end);
        setGradients(anArray);
        setStartFocalPoint(startPoint);
        setEndFocalPoint(endPoint);
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
        type = object.type;
        spread = object.spread;
        interpolation = object.interpolation;
        startTransform = object.startTransform;
        endTransform = object.endTransform;
        startFocalPoint = object.startFocalPoint;
        endFocalPoint = object.endFocalPoint;
        gradients = new ArrayList<MorphGradient>(object.gradients);
    }


    public Spread getSpread() {
        return Spread.fromInt(spread);
    }


    public void setSpread(final Spread aSpread) {
        spread = aSpread.getValue();
    }


    public Interpolation getInterpolation() {
        return Interpolation.fromInt(interpolation);
    }


    public void setInterpolation(final Interpolation anInterpolation) {
        interpolation = anInterpolation.getValue();
    }

    public float getStartFocalPoint() {
        return startFocalPoint / SCALE_8;
    }


    public void setStartFocalPoint(final float point) {
        startFocalPoint = (int) (point * SCALE_8);
    }


    public float getEndFocalPoint() {
        return endFocalPoint / SCALE_8;
    }


    public void setEndFocalPoint(final float point) {
        endFocalPoint = (int) (point * SCALE_8);
    }

    /**
     * Add a Gradient object to the array of gradient objects. For Flash 7 and
     * earlier versions there can be up to 8 Gradients. For Flash 8 onwards this
     * number was increased to 15.
     *
     * @param aGradient
     *            an Gradient object. Must not be null.
     */
    public MorphFocalGradientFill add(final MorphGradient aGradient) {
        if (aGradient == null) {
            throw new IllegalArgumentException();
        }
        if (gradients.size() == SWF.MAX_GRADIENTS) {
            throw new IllegalStateException(
                    "Maximum number of gradients exceeded.");
        }
        gradients.add(aGradient);
        return this;
    }

    /**
     * Returns the array of Gradient objects defining the points for the
     * gradient fill.
     */
    public List<MorphGradient> getGradients() {
        return gradients;
    }

    /**
     * Sets the array of control points that define the gradient. For Flash 7
     * and earlier this array can contain up to 8 Gradient objects. For Flash 8
     * onwards this limit was increased to 15.
     *
     * @param anArray
     *            an array of Gradient objects. Must not be null.
     */
    public void setGradients(final List<MorphGradient> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        if (gradients.size() > SWF.MAX_GRADIENTS) {
            throw new IllegalStateException(
                    "Maximum number of gradients exceeded.");
        }
        gradients = anArray;
    }


    /**
     * Returns the coordinate transform mapping the gradient square onto
     * physical coordinates at the start of the morphing process.
     */
    public CoordTransform getStartTransform() {
        return startTransform;
    }

    /**
     * Returns the coordinate transform mapping the gradient square onto
     * physical coordinates at the end of the morphing process.
     */
    public CoordTransform getEndTransform() {
        return endTransform;
    }

    /**
     * Sets the coordinate transform mapping the gradient square onto physical
     * coordinates at the start of the morphing process.
     *
     * @param aTransform
     *            the starting coordinate transform. Must not be null.
     */
    public void setStartTransform(final CoordTransform aTransform) {
        if (aTransform == null) {
            throw new IllegalArgumentException();
        }
        startTransform = aTransform;
    }

    /**
     * Sets the coordinate transform mapping the gradient square onto physical
     * coordinates at the end of the morphing process.
     *
     * @param aTransform
     *            the ending coordinate transform. Must not be null.
     */
    public void setEndTransform(final CoordTransform aTransform) {
        if (aTransform == null) {
            throw new IllegalArgumentException();
        }
        endTransform = aTransform;
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
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        // CHECKSTYLE:OFF
        // TODO(optimise) Calculate size of gradient array directly.
        int length = 6 + startTransform.prepareToEncode(coder, context)
            + endTransform.prepareToEncode(coder, context);
        count = gradients.size();

        for (final MorphGradient gradient : gradients) {
            length += gradient.prepareToEncode(coder, context);
        }
        //CHECKSTYLE:ON
        return length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeByte(type);
        startTransform.encode(coder, context);
        endTransform.encode(coder, context);
        coder.writeWord(count | spread | interpolation, 1);

        for (final MorphGradient gradient : gradients) {
            gradient.encode(coder, context);
        }

        coder.writeI16(startFocalPoint);
        coder.writeI16(endFocalPoint);
    }
}
