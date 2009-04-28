/*
 * Shape.java
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

package com.flagstone.transform.shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncodeable;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.font.DefineFont;

/**
 * Shape is a container class for the shape objects (Line, Curve and ShapeStyle
 * objects) that describe how a particular shape is drawn.
 * 
 * <p>
 * Shapes are used in shape and font definitions. The Shape class is used to
 * simplify the design of these classes and provides no added functionality
 * other than acting as a container class.
 * </p>
 * 
 * @see DefineShape
 * @see DefineFont
 */
public final class Shape implements SWFEncodeable {
    private static final String FORMAT = "Shape: { records=%s }";

    private List<ShapeRecord> objects;

    // TODO(doc)
    public Shape(final SWFDecoder coder, final Context context)
            throws CoderException {
        objects = new ArrayList<ShapeRecord>();

        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.FILL_SIZE, coder.readBits(4, false));
        vars.put(Context.LINE_SIZE, coder.readBits(4, false));

        int type;
        ShapeRecord shape;

        do {
            type = coder.readBits(6, false);

            if (type != 0) {

                coder.adjustPointer(-6);

                if ((type & 0x20) > 0) {
                    if ((type & 0x10) > 0) {
                        shape = new Line(coder);
                    } else {
                        shape = new Curve(coder);
                    }
                } else {
                    shape = new ShapeStyle(coder, context);
                }
                objects.add(shape);
            }
        } while (type != 0);

        coder.alignToByte();
    }

    // TODO(doc)
    public Shape() {
        objects = new ArrayList<ShapeRecord>();
    }

    /**
     * Creates a Shape object, specifying the Objects that describe how the
     * shape is drawn.
     * 
     * @param anArray
     *            the array of shape records. Must not be null.
     */
    public Shape(final List<ShapeRecord> anArray) {
        setObjects(anArray);
    }

    // TODO(doc)
    public Shape(final Shape object) {
        objects = new ArrayList<ShapeRecord>(object.objects.size());

        for (final ShapeRecord record : object.objects) {
            objects.add(record.copy());
        }
    }

    /**
     * Adds the object to the array of shape records.
     * 
     * @param anObject
     *            an instance of ShapeStyle, Line or Curve. Must not be null.
     */
    public Shape add(final ShapeRecord anObject) {
        if (anObject == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        objects.add(anObject);
        return this;
    }

    /**
     * Returns the array of shape records that define the shape.
     */
    public List<ShapeRecord> getObjects() {
        return objects;
    }

    /**
     * Sets the array of shape records.
     * 
     * @param anArray
     *            the array of shape records. Must not be null.
     */
    public void setObjects(final List<ShapeRecord> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException(Strings.ARRAY_IS_NULL);
        }
        objects = anArray;
    }

    public Shape copy() {
        return new Shape(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, objects);
    }

    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        int numberOfBits = 0;

        context.getVariables().put(Context.SHAPE_SIZE, numberOfBits);

        numberOfBits += 8;

        for (final ShapeRecord record : objects) {
            numberOfBits += record.prepareToEncode(coder, context);
        }

        numberOfBits += 6; // Add size of end of shape

        numberOfBits += (numberOfBits % 8 > 0) ? 8 - (numberOfBits % 8) : 0;

        return numberOfBits >> 3;
    }

    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final Map<Integer, Integer> vars = context.getVariables();
        coder.writeBits(vars.get(Context.FILL_SIZE), 4);
        coder.writeBits(vars.get(Context.LINE_SIZE), 4);

        for (final ShapeRecord record : objects) {
            record.encode(coder, context);
        }

        coder.writeBits(0, 6); // End of shape
        coder.alignToByte();
    }
}
