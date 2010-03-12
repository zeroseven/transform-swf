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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/** TODO(class). */
public final class FocalGradientFill implements FillStyle {

    private static final String FORMAT = "FocalGradientFill: { spread=%s;"
            + " interpolation=%s; focalPoint=%d, gradients=%s}";

    private final transient int type;
    private int spread;
    private int interpolation;
    private int focalPoint;
    private List<Gradient> gradients;

    private transient int count;

    /**
     * Creates and initialises a FocalGradientFill fill style using values encoded
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
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public FocalGradientFill(final SWFDecoder coder, final Context context)
            throws CoderException {
        type = coder.readByte();
        count = coder.readByte();
        gradients = new ArrayList<Gradient>(count);

        for (int i = 0; i < count; i++) {
            gradients.add(new Gradient(coder, context));
        }
    }

    /** TODO(method). */
    public FocalGradientFill(final Spread spread,
            final Interpolation interpolation, final float point,
            final List<Gradient> anArray) {
        type = 0x13;
        setSpread(spread);
        setInterpolation(interpolation);
        setGradients(anArray);
        setFocalPoint(point);
    }

    /**
     * Creates and initialises a FocalGradientFill fill style using the values copied
     * from another FocalGradientFill object.
     *
     * @param object
     *            a FocalGradientFill fill style from which the values will be
     *            copied.
     */
    public FocalGradientFill(final FocalGradientFill object) {
        type = object.type;
        spread = object.spread;
        interpolation = object.interpolation;
        focalPoint = object.focalPoint;
        gradients = new ArrayList<Gradient>(object.gradients);
    }

    /** TODO(method). */
    public Spread getSpread() {
        Spread value;
        switch (spread) {
        case 0:
            value = Spread.PAD;
            break;
        case 0x40:
            value = Spread.REFLECT;
            break;
        case 0xC0:
            value = Spread.REPEAT;
            break;
        default:
            throw new IllegalStateException();
        }
        return value;
    }

    /** TODO(method). */
    public void setSpread(final Spread type) {
        switch (type) {
        case PAD:
            spread = 0;
            break;
        case REFLECT:
            spread = 0x40;
            break;
        case REPEAT:
            spread = 0xC0;
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    /** TODO(method). */
    public Interpolation getInterpolation() {
        Interpolation value;
        switch (interpolation) {
        case 0:
            value = Interpolation.NORMAL;
            break;
        case 16:
            value = Interpolation.LINEAR;
            break;
        default:
            throw new IllegalStateException();
        }
        return value;
    }

    /** TODO(method). */
    public void setInterpolation(final Interpolation type) {
        switch (type) {
        case NORMAL:
            interpolation = 0;
            break;
        case LINEAR:
            interpolation = 16;
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    /** TODO(method). */
    public float getFocalPoint() {
        return focalPoint / 256.0f;
    }

    /** TODO(method). */
    public void setFocalPoint(final float point) {
        this.focalPoint = (int) (point * 256);
    }

    /**
     * Add a Gradient object to the array of gradient objects. For Flash 7 and
     * earlier versions there can be up to 8 Gradients. For Flash 8 onwards this
     * number was increased to 15.
     *
     * @param aGradient
     *            an Gradient object. Must not be null.
     */
    public FocalGradientFill add(final Gradient aGradient) {
        if (aGradient == null) {
            throw new NullPointerException();
        }
        if (gradients.size() == 15) {
            throw new IllegalStateException("Maximum number of gradients exceeded.");
        }
        gradients.add(aGradient);
        return this;
    }

    /**
     * Returns the array of Gradient objects defining the points for the
     * gradient fill.
     */
    public List<Gradient> getGradients() {
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
    public void setGradients(final List<Gradient> anArray) {
        if (anArray == null) {
            throw new NullPointerException();
        }
        if (gradients.size() == 15) {
            throw new IllegalStateException("Maximum number of gradients exceeded.");
        }
        gradients = anArray;
    }

    /** {@inheritDoc} */
    public FocalGradientFill copy() {
        return new FocalGradientFill(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, getSpread(), getInterpolation(),
                focalPoint, gradients);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        // TODO(optimise) Calculate size of gradient array directly.
        int length = 2;
        count = gradients.size();

        for (final Gradient gradient : gradients) {
            length += gradient.prepareToEncode(coder, context);
        }

        return length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeByte(type);
        coder.writeWord(count | spread | interpolation, 1);

        for (final Gradient gradient : gradients) {
            gradient.encode(coder, context);
        }
    }
}
