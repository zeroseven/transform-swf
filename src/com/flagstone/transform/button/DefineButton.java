/*
 * DefineButton.java
 * Transform
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.button;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.Strings;
import com.flagstone.transform.action.ActionData;
import com.flagstone.transform.coder.Action;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;

/**
 * DefineButton defines the appearance of a button and the actions performed
 * when the button is clicked.
 *
 * <p>
 * DefineButton must contain at least one ButtonShape object. If more than one
 * button shape is defined for a given button state then each shape will be
 * displayed by the button. The order in which the shapes are displayed is
 * determined by the layer assigned to each ButtonShape object.
 * </P>
 *
 * @see ButtonShape
 */
//TODO(class)
public final class DefineButton implements DefineTag {
    private static final String FORMAT = "DefineButton: { identifier=%d; buttonRecords=%s; actions=%s }";

    private int identifier;

    private List<ButtonShape> shapes;
    private List<Action> actions;

    private transient int length;

    /**
     * Creates and initialises a DefineButton object using values encoded
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
    public DefineButton(final SWFDecoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        final int mark = coder.getPointer() - 16;

        shapes = new ArrayList<ButtonShape>();

        while (coder.readByte() != 0) {
            coder.adjustPointer(-8);
            shapes.add(new ButtonShape(coder, context));
        }

        final int actionsLength = length - ((coder.getPointer() - mark) >>> 3);

        actions = new ArrayList<Action>();

        final SWFFactory<Action> decoder = context.getRegistry()
                .getActionDecoder();

        if (decoder == null) {
            actions
                    .add(new ActionData(coder
                            .readBytes(new byte[actionsLength])));
        } else {
            while (coder.getPointer() < end) {
                actions.add(decoder.getObject(coder, context));
            }
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates a DefineButton object with the identifier, button shapes and
     * actions.
     *
     * @param uid
     *            the unique identifier for this button.
     * @param buttons
     *            an array of ButtonShapes that are used to draw the button.
     * @param actions
     *            and array of actions that are executed when the button is
     *            clicked.
     */
    public DefineButton(final int uid, final List<ButtonShape> buttons,
            final List<Action> actions) {
        setIdentifier(uid);
        setShapes(buttons);
        setActions(actions);
    }

    /**
     * Creates and initialises a DefineButton object using the values copied
     * from another DefineButton object.
     *
     * @param object
     *            a DefineButton object from which the values will be
     *            copied.
     */
    public DefineButton(final DefineButton object) {
        identifier = object.identifier;
        shapes = new ArrayList<ButtonShape>(object.shapes.size());
        for (final ButtonShape shape : object.shapes) {
            shapes.add(shape.copy());
        }
        actions = new ArrayList<Action>(object.actions.size());
        for (final Action action : object.actions) {
            actions.add(action.copy());
        }
    }

    /** TODO(method). */
    public int getIdentifier() {
        return identifier;
    }

    /** TODO(method). */
    public void setIdentifier(final int uid) {
        if ((uid < 0) || (uid > 65535)) {
            throw new IllegalArgumentException(Strings.IDENTIFIER_RANGE);
        }
        identifier = uid;
    }

    /**
     * Adds the button shape to the array of button shapes.
     *
     * @param obj
     *            an ButtonShape object. Must not be null.
     */
    public DefineButton add(final ButtonShape obj) {
        if (obj == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        shapes.add(obj);
        return this;
    }

    /**
     * Adds the action to the array of actions.
     *
     * @param obj
     *            an action object. Must not be null.
     */
    public DefineButton add(final Action obj) throws CoderException {
        if (obj == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        actions.add(obj);
        return this;
    }

    /**
     * Returns the array of button shapes.
     */
    public List<ButtonShape> getShapes() {
        return shapes;
    }

    /**
     * Returns the array of actions that will be executed when the button is
     * clicked and released.
     */
    public List<Action> getActions() throws CoderException {
        return actions;
    }

    /**
     * Sets the array of button shapes defined for this button.
     *
     * @param anArray
     *            an array of Button objects. Must not be null.
     */
    public void setShapes(final List<ButtonShape> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException(Strings.ARRAY_IS_NULL);
        }
        shapes = anArray;
    }

    /**
     * Sets the array of actions that will be executed when the button is
     * clicked and released.
     *
     * @param anArray
     *            and array of action objects. Must not be null.
     */
    public void setActions(final List<Action> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException(Strings.ARRAY_IS_NULL);
        }
        actions = anArray;
    }

    /** TODO(method). */
    public DefineButton copy() {
        return new DefineButton(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, identifier, shapes, actions);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        length = 2;

        for (final ButtonShape shape : shapes) {
            length += shape.prepareToEncode(coder, context);
        }

        length += 1;

        for (final Action action : actions) {
            length += action.prepareToEncode(coder, context);
        }

        return (length > 62 ? 6 : 2) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();

        if (length >= 63) {
            coder.writeWord((MovieTypes.DEFINE_BUTTON << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.DEFINE_BUTTON << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);
        coder.writeWord(identifier, 2);

        for (final ButtonShape shape : shapes) {
            shape.encode(coder, context);
        }

        coder.writeWord(0, 1);

        for (final Action action : actions) {
            action.encode(coder, context);
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
