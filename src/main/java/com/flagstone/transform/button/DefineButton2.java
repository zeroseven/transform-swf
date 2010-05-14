/*
 * DefineButton2.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.flagstone.transform.SWF;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/**
 * DefineButton2 defines the appearance and actions of push and menu buttons.
 *
 * <p>
 * It provides a more sophisticated model for creating buttons than
 * {@link DefineButton}:
 * </p>
 *
 * <ul>
 * <li>Two types of button are supported, <B>Push</B> and <B>Menu</B>.</li>
 * <li>The number of events that a button can respond to is increased.</li>
 * <li>Actions can be executed for any button event.</li>
 * </ul>
 *
 * <p>
 * Push and Menu buttons behave slightly differently in tracking mouse movements
 * when the button is clicked. A Push button 'captures' the mouse so if the
 * cursor is dragged outside of the active area of the button and the mouse
 * click is released then the Release Outside event is still sent to the button.
 * A Menu button does not 'capture' the mouse so if the cursor is dragged out of
 * the active area the button returns to its 'inactive' state.
 * </p>
 *
 * <p>
 * A DefineButton2 object must contain at least one ButtonShape. If more than
 * one button shape is defined for a given button state then each shape will be
 * displayed by the button. The order in which the shapes are displayed is
 * determined by the layer assigned to each button record.
 * </p>
 *
 * @see ButtonShape
 * @see ButtonEventHandler
 */
//TODO(class)
public final class DefineButton2 implements DefineTag {

    private static final String FORMAT = "DefineButton2: { identifier=%d;"
            + " buttonRecords=%s; handlers=%s }";

    private int identifier;
    private int type;
    private List<ButtonShape> shapes;
    private List<ButtonEventHandler> events;

    private transient int length;

    /**
     * Creates and initialises a DefineButton2 object using values encoded
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
    // TODO(optimise)
    public DefineButton2(final SWFDecoder coder, final Context context)
            throws CoderException {
        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.TYPE, MovieTypes.DEFINE_BUTTON_2);
        vars.put(Context.TRANSPARENT, 1);

        final int start = coder.getPointer();
        length = coder.readHeader();
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        identifier = coder.readUI16();
        type = coder.readByte();
        shapes = new ArrayList<ButtonShape>();

        int offsetToNext = coder.readUI16();

        while (coder.scanByte() != 0) {
            shapes.add(new ButtonShape(coder, context));
        }

        events = new ArrayList<ButtonEventHandler>();

        if (offsetToNext != 0) {
            ButtonEventHandler event;

            do {
                offsetToNext = coder.readUI16();

                if (offsetToNext == 0) {
                    event = new ButtonEventHandler(
                            (end - coder.getPointer() - 16)
                            >> Coder.BITS_TO_BYTES, coder, context);
                } else {
                    event = new ButtonEventHandler(offsetToNext - 4,
                            coder, context);
                }
                events.add(event);

            } while (offsetToNext != 0);
        }

        vars.remove(Context.TYPE);
        vars.remove(Context.TRANSPARENT);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }

    /**
     * Creates a DefineButton2 object, specifying the unique identifier, the
     * type of button to be created, the button shapes that describe the
     * button's appearance and the actions that are performed in response to
     * each button event.
     *
     * @param uid
     *            a unique identifier for this button. Must be in the range
     *            1..65535.
     * @param buttonType
     *            the button is a menu button (true) or push button (false).
     * @param buttonShapes
     *            an array of Button objects. Must not be null.
     * @param handlers
     *            an array of ButtonEvent objects. Must not be null.
     */
    public DefineButton2(final int uid, final ButtonType buttonType,
            final List<ButtonShape> buttonShapes,
            final List<ButtonEventHandler> handlers) {
        setIdentifier(uid);
        setType(buttonType);
        setShapes(buttonShapes);
        setEvents(handlers);
    }

    /**
     * Creates and initialises a DefineButton2 object using the values copied
     * from another DefineButton2 object.
     *
     * @param object
     *            a DefineButton2 object from which the values will be
     *            copied.
     */
    public DefineButton2(final DefineButton2 object) {
        identifier = object.identifier;
        type = object.type;
        shapes = new ArrayList<ButtonShape>(object.shapes.size());
        for (final ButtonShape shape : object.shapes) {
            shapes.add(shape.copy());
        }
        events = new ArrayList<ButtonEventHandler>(object.events.size());
        for (final ButtonEventHandler event : object.events) {
            events.add(event.copy());
        }
    }

    /** {@inheritDoc} */
    public int getIdentifier() {
        return identifier;
    }

    /** {@inheritDoc} */
    public void setIdentifier(final int uid) {
        if ((uid < SWF.MIN_IDENTIFIER) || (uid > SWF.MAX_IDENTIFIER)) {
            throw new IllegalArgumentRangeException(
                    SWF.MIN_IDENTIFIER, SWF.MAX_IDENTIFIER, uid);
        }
        identifier = uid;
    }

    /**
     * Adds an ButtonShape to the array of button records.
     *
     * @param obj
     *            a button shape object. Must not be null.
     */
    public DefineButton2 add(final ButtonShape obj) {
        if (obj == null) {
            throw new IllegalArgumentException();
        }
        shapes.add(obj);
        return this;
    }

    /**
     * Adds a button event object to the array of button events.
     *
     * @param obj
     *            a button event. Must not be null.
     */
    public DefineButton2 add(final ButtonEventHandler obj)
            throws CoderException {
        if (obj == null) {
            throw new IllegalArgumentException();
        }
        events.add(obj);
        return this;
    }

    /**
     * Returns the button type - either PUSH or MENU.
     */
    public ButtonType getType() {
        ButtonType value;
        if (type == 0) {
            value = ButtonType.PUSH;
        } else {
            value = ButtonType.MENU;
        }
        return value;
    }

    /**
     * Returns the array of button records defined for this button.
     */
    public List<ButtonShape> getShapes() {
        return shapes;
    }

    /**
     * Returns the array of button records defined for this button.
     */
    public List<ButtonEventHandler> getEvents() throws CoderException {
        return events;
    }

    /**
     * Sets whether the button is a menu button or a push button.
     *
     * @param buttonType
     *            the type of button, either ButtonType.MENU or ButtonType.PUSH.
     */
    public void setType(final ButtonType buttonType) {
        if (buttonType == ButtonType.PUSH) {
            type = 0;
        } else {
            type = 1;
        }
    }

    /**
     * Sets the array of button shapes defined for this button.
     *
     * @param anArray
     *            an array of ButtonShape objects. Must not be null.
     */
    public void setShapes(final List<ButtonShape> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        shapes = anArray;
    }

    /**
     * Sets the array of button events defined for this button. If the object
     * already contains encodedEvents then they will be deleted.
     *
     * @param anArray
     *            and array of ButtonEvent objects. Must not be null.
     */
    public void setEvents(final List<ButtonEventHandler> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        events = anArray;
    }

    /** {@inheritDoc} */
    public DefineButton2 copy() {
        return new DefineButton2(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, identifier, shapes, events);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        // CHECKSTYLE:OFF - Fixed length when encoded.
        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.TYPE, MovieTypes.DEFINE_BUTTON_2);
        vars.put(Context.TRANSPARENT, 1);

        length = 6;

        for (final ButtonShape shape : shapes) {
            length += shape.prepareToEncode(coder, context);
        }

        for (final ButtonEventHandler handler : events) {
            length += 2 + handler.prepareToEncode(coder, context);
        }

        vars.remove(Context.TYPE);
        vars.remove(Context.TRANSPARENT);

        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
        // CHECKSTYLE:ON
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {

        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.TYPE, MovieTypes.DEFINE_BUTTON_2);
        vars.put(Context.TRANSPARENT, 1);

        final int start = coder.getPointer();
        coder.writeHeader(MovieTypes.DEFINE_BUTTON_2, length);
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        coder.writeI16(identifier);
        coder.writeByte(type);

        int offsetStart = coder.getPointer();
        coder.writeI16(0);

        for (final ButtonShape shape : shapes) {
            shape.encode(coder, context);
        }

        coder.writeByte(0);

        // Write actions offset

        int currentCursor = coder.getPointer();
        final int offsetEnd = (currentCursor - offsetStart)
                >> Coder.BITS_TO_BYTES;
        coder.setPointer(offsetStart);
        coder.writeI16(offsetEnd);
        coder.setPointer(currentCursor);

        for (final ButtonEventHandler handler : events) {
            offsetStart = coder.getPointer();
            handler.encode(coder, context);
        }

        // Write offset of zero for last Action Condition
        currentCursor = coder.getPointer();
        coder.setPointer(offsetStart);
        coder.writeI16(0);
        coder.setPointer(currentCursor);

        vars.remove(Context.TYPE);
        vars.remove(Context.TRANSPARENT);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }
}
