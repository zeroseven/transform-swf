/*
 * Button.java
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

package com.flagstone.transform.button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.flagstone.transform.SWF;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncodeable;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.datatype.Blend;
import com.flagstone.transform.datatype.ColorTransform;
import com.flagstone.transform.datatype.CoordTransform;
import com.flagstone.transform.exception.IllegalArgumentRangeException;
import com.flagstone.transform.filter.Filter;

/**
 * <p>
 * ButtonShape identifies the shape that is drawn when a button is in a
 * particular state. Shapes can be drawn for each of three button states, Over,
 * Up and Down allowing simple animations to be created when a button is
 * clicked.
 * </p>
 *
 * <p>
 * A shape is also used to define active area of the button. When defining the
 * active area the outline of the shape defines the boundary of the area, the
 * shape itself is not displayed. The button will only respond to mouse events
 * when the cursor is placed inside the active area.
 * </p>
 *
 * <p>
 * An shape can be used more than one state. Multiple states can be defined by
 * bitwise Or-ing individual state codes together:
 * </p>
 *
 * <pre>
 * int buttonState = Button.Up | Button.Over;
 * </pre>
 *
 * <p>
 * The order in which shapes are displayed is controlled by the layer number. As
 * with the Flash Player's display list shapes on a layer with a higher number
 * are displayed in front of ones on a layer with a lower number. A coordinate
 * and color transform can also be applied to each shape to change its
 * appearance when it is displayed when the button enters the specified state.
 * </p>
 *
 * @see DefineButton
 * @see DefineButton2
 */
//TODO(class)
public final class ButtonShape implements SWFEncodeable {

    /** Format string used in toString() method. */
    private static final String FORMAT = "ButtonShape: { state=%d;"
            + " identifier=%d; layer=%d; transform=%s; colorTransform=%s"
            + " blend=%s, filters=%s }";

    /** The button state that the shape represents. */
    private int state;
    /** The unique identifier of the shape that will be displayed. */
    private int identifier;
    /** The layer on which the shape is displayed. */
    private int layer;
    /** The coordinate transform used to position the shape. */
    private CoordTransform transform;
    /** The colour transform applied to the shape. */
    private ColorTransform colorTransform;
    /** The set of filters applied to the shape. */
    private List<Filter> filters;
    /** The mode used to blend the shape with its background. */
    private Integer blend;

    /** Flag used when encoded to identify whether the blend is set. */
    private transient boolean hasBlend;
    /** Flag used when encoded to identify whether filters are defined. */
    private transient boolean hasFilters;

    /**
     * Creates and initialises a ButtonShape object using values encoded
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
    public ButtonShape(final SWFDecoder coder,
            final Context context) throws IOException {

        final int bits = coder.readByte();
        hasBlend = (bits & Coder.BIT5) != 0;
        hasFilters = (bits & Coder.BIT4) != 0;
        state = bits & 0x0F;

        identifier = coder.readUnsignedShort();
        layer = coder.readUnsignedShort();
        transform = new CoordTransform(coder);

        if (context.get(Context.TYPE)
                == MovieTypes.DEFINE_BUTTON_2) {
            colorTransform = new ColorTransform(coder, context);
        }

        if (hasFilters) {
            final SWFFactory<Filter> decoder =
                context.getRegistry().getFilterDecoder();
            final int count = coder.readByte();
            filters = new ArrayList<Filter>(count);
            for (int i = 0; i < count; i++) {
               filters.add(decoder.getObject(coder, context));
            }
        } else {
            filters = new ArrayList<Filter>();
        }

        if (hasBlend) {
            blend = coder.readByte();
            if (blend == 0) {
                blend = 1;
            }
        } else {
            blend = 0;
        }
    }

    /**
     * Creates am uninitialised ButtonShape object.
     */
    public ButtonShape() {
        // Empty constructor
    }

    /**
     * Creates and initialises a ButtonShape object using the values copied
     * from another ButtonShape object.
     *
     * @param object
     *            a ButtonShape object from which the values will be
     *            copied.
     */
    public ButtonShape(final ButtonShape object) {
        state = object.state;
        identifier = object.identifier;
        layer = object.layer;
        transform = object.transform;
        colorTransform = object.colorTransform;
        filters = new ArrayList<Filter>(object.filters);
        blend = object.blend;
    }

    public Set<ButtonState> getState() {
        final Set<ButtonState> set = EnumSet.noneOf(ButtonState.class);

        if ((state & 0x01) != 0) {
            set.add(ButtonState.UP);
        }
        if ((state & 0x02) != 0) {
            set.add(ButtonState.OVER);
        }
        if ((state & 0x04) != 0) {
            set.add(ButtonState.DOWN);
        }
        if ((state & 0x08) != 0) {
            set.add(ButtonState.ACTIVE);
        }
        return set;
    }


    public ButtonShape setState(final Set<ButtonState> states) {
        for (final ButtonState buttonState : states) {
            switch (buttonState) {
            case UP:
                state |= 1;
                break;
            case OVER:
                state |= 2;
                break;
            case DOWN:
                state |= 4;
                break;
            case ACTIVE:
                state |= 8;
                break;
            default:
                throw new IllegalArgumentException();
            }
        }
        return this;
    }

    /**
     * Get the unique identifier of the shape that this Button applies to.
     *
     * @return the unique identifier of the shape.
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Sets the unique identifier of the DefineShape, DefineShape2 or
     * DefineShape3 object that defines the appearance of the button when it is
     * in the specified state(s).
     *
     * @param uid
     *            the unique identifier of the shape object that defines the
     *            shape's appearance. Must be in the range 1..65535.
     * @return this object.
     */
    public ButtonShape setIdentifier(final int uid) {
        if ((uid < SWF.MIN_IDENTIFIER) || (uid > SWF.MAX_IDENTIFIER)) {
            throw new IllegalArgumentRangeException(
                    SWF.MIN_IDENTIFIER, SWF.MAX_IDENTIFIER, uid);
        }
        identifier = uid;
        return this;
    }

    /**
     * Get the layer that the button will be displayed on.
     *
     * @return the layer that the shape is displayed on.
     */
    public int getLayer() {
        return layer;
    }

    /**
     * Sets the layer in the display list that the shape will be displayed on.
     *
     * @param aNumber
     *            the number of the layer in the display list where the shape is
     *            drawn. Must be in the range 1..65535.
     * @return this object.
     */
    public ButtonShape setLayer(final int aNumber) {
        if ((aNumber < 1) || (aNumber > SWF.MAX_LAYER)) {
            throw new IllegalArgumentRangeException(1, SWF.MAX_LAYER, aNumber);
        }
        layer = aNumber;
        return this;
    }

    /**
     * Get the coordinate transform that will be applied to the button.
     *
     * @return the coordinate transform that is applied to the shape.
     */
    public CoordTransform getTransform() {
        return transform;
    }

    /**
     * Sets the coordinate transform that will be applied to the shape to change
     * it's appearance.
     *
     * @param aTransform
     *            an CoordTransform object that will be applied to the shape.
     *            Must not be null.
     * @return this object.
     */
    public ButtonShape setTransform(final CoordTransform aTransform) {
        if (aTransform == null) {
            throw new IllegalArgumentException();
        }
        transform = aTransform;
        return this;
    }

    /**
     * Get the colour transform that will be applied to the button.
     *
     * Note that the colour transform will only be used if the ButtonShape is
     * added to a DefineButton2 object.
     *
     * @return the colour transform that is applied to the shape.
     */
    public ColorTransform getColorTransform() {
        return colorTransform;
    }

    /**
     * Sets the colour transform that will be applied to the shape to change
     * it's colour.
     *
     * IMPORTANT: The colour transform is only used in DefineButton2 objects.
     *
     * @param aTransform
     *            an ColorTransform object that will be applied to the shape.
     *            Must not be null, even if the ButtonShape will be added to a
     *            DefineButton object.
     * @return this object.
     */
    public ButtonShape setColorTransform(final ColorTransform aTransform) {
        if (aTransform == null) {
            throw new IllegalArgumentException();
        }
        colorTransform = aTransform;
        return this;
    }

    public ButtonShape add(final Filter filter) {
        if (filter == null) {
            throw new IllegalArgumentException();
        }
        filters.add(filter);
        return this;
    }


    public List<Filter> getFilters() {
        return filters;
    }


    public ButtonShape setFilters(final List<Filter> array) {
        if (array == null) {
            throw new IllegalArgumentException();
        }
        filters = array;
        return this;
    }


    public Blend getBlend() {
        return Blend.fromInt(blend);
    }


    public ButtonShape setBlend(final Blend mode) {
        blend = mode.getValue();
        return this;
    }

    /** {@inheritDoc} */
    public ButtonShape copy() {
        return new ButtonShape(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, state, identifier, layer, transform,
                colorTransform, blend, filters);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {

        hasBlend = blend != 0;
        hasFilters ^= filters.isEmpty();

        int length = 5 + transform.prepareToEncode(context);

        if (context.get(Context.TYPE)
                == MovieTypes.DEFINE_BUTTON_2) {
            length += colorTransform.prepareToEncode(context);
        }

        if (hasFilters) {
            length += 1;
            for (Filter filter : filters) {
                length += filter.prepareToEncode(context);
            }
        }

        if (hasBlend) {
            length += 1;
        }

        return length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        int bits = 0;
        bits |= hasBlend ? Coder.BIT5 : 0;
        bits |= hasFilters ? Coder.BIT4 : 0;
        bits |= state;
        coder.writeByte(bits);
        coder.writeShort(identifier);
        coder.writeShort(layer);
        transform.encode(coder, context);

        if (context.get(Context.TYPE)
                == MovieTypes.DEFINE_BUTTON_2) {
            colorTransform.encode(coder, context);
        }

        if (hasFilters) {
            coder.writeByte(filters.size());
            for (Filter filter : filters) {
                filter.encode(coder, context);
            }
        }

        if (hasBlend) {
            coder.writeByte(blend);
        }
    }
}
