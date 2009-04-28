/*
 * PlaceObject2.java
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

package com.flagstone.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.Filter;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.datatype.Blend;
import com.flagstone.transform.datatype.ColorTransform;
import com.flagstone.transform.datatype.CoordTransform;
import com.flagstone.transform.datatype.Placement;
import com.flagstone.transform.movieclip.MovieClipEvent;
import com.flagstone.transform.movieclip.MovieClipEventHandler;

//TODO(doc) Review description and methods
/**
 * PlaceObject2 is used to add and manipulate objects (shape, button, etc.) on
 * the Flash Player's display list.
 * 
 * <p>
 * PlaceObject2 supersedes the PlaceObject class providing more functionality
 * and easier manipulation of objects in the display list through the following
 * operations:
 * </p>
 * 
 * <ul>
 * <li>Place a new shape on the display list.</li>
 * <li>Change an existing shape by moving it to new location or changing its
 * appearance.</li>
 * <li>Replace an existing shape with a another.</li>
 * <li>Define clipping layers to mask objects displayed in front of a shape.</li>
 * <li>Control the morphing process that changes one shape into another.</li>
 * <li>Assign names to objects rather than using their identifiers.</li>
 * <li>Define the sequence of actions that are executed when an event occurs in
 * movie clip.</li>
 * </ul>
 * 
 * <p>
 * <b>Clipping Depth</b><br/>
 * With the introduction of Flash 3 the display list supported a clipping layer.
 * This allowed the outline of an object to define a clipping path that is used
 * to mask other objects placed in front of it. The clipping depth can be set to
 * mask objects between the layer containing the clipping path and a specified
 * layer.
 * </p>
 * 
 * <p>
 * <b>Shape Morphing</b><br/>
 * Shapes that will be morphed are defined using the DefineMorphShape class
 * which defines a start and end shape. The Flash Player performs the
 * interpolation that transforms one shape into another. The progress of the
 * morphing process is controlled by a ratio which ranges from 0.0 to 1.0, where
 * 0 generates a shape identical to the starting shape in the DefineMorphShape
 * object and 1.0 generates the shape at the end of the morphing process.
 * </p>
 * 
 * <p>
 * <b>Movie Clip Event Handlers</b><br/>
 * With the introduction of Flash 5, movie clips (defined using the
 * DefineMovieClip class) could specify sequences of actions that would be
 * performed in response to mouse or keyboard events. The actions are specified
 * using ClipEvent objects and the PlaceObject2 class is used to register the
 * actions in response to a particular event with the Flash player. Multiple
 * events can be handled by defining an ClipEvent for each type of event. For
 * more information see the ClipEvent class.
 * </p>
 * 
 * <p>
 * Since only one object can be placed on a given layer an existing object on
 * the display list can be identified by the layer it is displayed on rather
 * than its identifier. Therefore Layer is the only required attribute. The
 * remaining attributes are optional according to the different operation being
 * performed:
 * </p>
 * 
 * <ul>
 * <li>If an existing object on the display list is being modified then only the
 * layer number is required. Previously in the PlaceObject class both the
 * identifier and the layer number were required.</li>
 * <li>If no coordinate transform is applied to the shape (the default is a
 * unity transform that does not change the shape) then it is not encoded.</li>
 * <li>Similarly if no colour transform is applied to the shape (the default is
 * a unity transform that does not change the shape's colour) then it is not
 * encoded.</li>
 * <li>If a shape is not being morphed then the ratio attribute may be left at
 * its default value (-1.0).</li>
 * <li>If a shape is not used to define a clipping area then the depth attribute
 * may be left at its default value (0).</li>
 * <li>If a name is net assigned to an object the name attribute may be left its
 * default value (an empty string).</li>
 * <li>If no events are being defined for a movie clip then the array of
 * ClipEvent object may be left empty.</li>
 * </ul>
 * 
 * <p>
 * The Layer class provides a simple API for manipulating objects on the display
 * list. While it is relatively simple to create instances of PlaceObject2
 * object that perform the same steps the API provided by Player is easier to
 * use and much more readable.
 * </p>
 * 
 * @see com.flagstone.transform.util.movie.Layer
 */
public final class Place3 implements MovieTag {
    private static final String FORMAT = "PlaceObject3: { place=%s; layer=%d; "
            + "identifier=%d; transform=%d; colorTransform=%d; ratio=%d; "
            + "clippingDepth=%d; name=%d; clipEvents=%s}";

    public static Place3 show(final int identifier, final int layer,
            final int xCoord, final int yCoord) {
        final Place3 object = new Place3();
        object.placeType = Placement.NEW;
        object.setIdentifier(identifier);
        object.setLayer(layer);
        object.transform = CoordTransform.translate(xCoord, yCoord);
        return object;
    }

    public static Place3 modify(final int layer) {
        final Place3 object = new Place3();
        object.placeType = Placement.MODIFY;
        object.setLayer(layer);
        return object;
    }

    public static Place3 move(final int layer, final int xCoord,
            final int yCoord) {
        final Place3 object = new Place3();
        object.placeType = Placement.MODIFY;
        object.setLayer(layer);
        object.transform = CoordTransform.translate(xCoord, yCoord);
        return object;
    }

    public static Place3 replace(final int identifier, final int layer) {
        final Place3 object = new Place3();
        object.placeType = Placement.REPLACE;
        object.setIdentifier(identifier);
        object.setLayer(layer);
        return object;
    }

    public static Place3 replace(final int identifier, final int layer,
            final int xCoord, final int yCoord) {
        final Place3 object = new Place3();
        object.placeType = Placement.REPLACE;
        object.setIdentifier(identifier);
        object.setLayer(layer);
        object.transform = CoordTransform.translate(xCoord, yCoord);
        return object;
    }

    private Placement placeType;
    private int layer;
    private String className;
    private int identifier;
    private CoordTransform transform;
    private ColorTransform colorTransform;
    private Integer ratio;
    private String name;
    private Integer depth;
    private List<Filter> filters;
    private int blend;
    private List<MovieClipEventHandler> events;

    private transient int length;

    // TODO(doc)
    // TODO(optimise)
    public Place3(final SWFDecoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();

        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.TRANSPARENT, 1);
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        final boolean hasEvents = coder.readBits(1, false) != 0;
        final boolean hasDepth = coder.readBits(1, false) != 0;
        final boolean hasName = coder.readBits(1, false) != 0;
        final boolean hasRatio = coder.readBits(1, false) != 0;
        final boolean hasColorTransform = coder.readBits(1, false) != 0;
        final boolean hasTransform = coder.readBits(1, false) != 0;
        final boolean hasFilters = coder.readBits(1, false) != 0;

        switch (coder.readBits(2, false)) {
        case 1:
            placeType = Placement.MODIFY;
            break;
        case 2:
            placeType = Placement.NEW;
            break;
        case 3:
            placeType = Placement.REPLACE;
            break;
        }

        layer = coder.readWord(2, false);

        if ((placeType == Placement.NEW) || (placeType == Placement.REPLACE)) {
            identifier = coder.readWord(2, false);
        }

        if (hasTransform) {
            transform = new CoordTransform(coder);
        }

        if (hasColorTransform) {
            colorTransform = new ColorTransform(coder, context);
        }

        if (hasRatio) {
            ratio = coder.readWord(2, false);
        }

        if (hasName) {
            name = coder.readString();
        }

        if (hasDepth) {
            depth = coder.readWord(2, false);
        }

        filters = new ArrayList<Filter>();

        if (hasFilters) {
            final SWFFactory<Filter> decoder = context.getRegistry()
                    .getFilterDecoder();

            final int count = coder.readByte();

            for (int i = 0; i < count; i++) {
                filters.add(decoder.getObject(coder, context));
            }
        }

        if (hasEvents) {
            final int eventSize = Context.VERSION > 5 ? 4 : 2;
            events = new ArrayList<MovieClipEventHandler>(eventSize);

            coder.readWord(2, false);
            coder.readWord(eventSize, false);

            while (coder.readWord(eventSize, false) != 0) {
                coder.adjustPointer(-(eventSize << 3));
                events.add(new MovieClipEventHandler(coder, context));
            }

        }
        vars.remove(Context.TRANSPARENT);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates an uninitialised Place3 object.
     */
    public Place3() {
        // Create uninitialized object
    }

    // TODO(doc)
    public Place3(final Place3 object) {
        placeType = object.placeType;
        layer = object.layer;
        className = object.className;
        identifier = object.identifier;
        if (object.transform != null) {
            transform = object.transform;
        }
        if (object.colorTransform != null) {
            colorTransform = object.colorTransform;
        }
        ratio = object.ratio;
        depth = object.depth;
        name = object.name;

        filters = new ArrayList<Filter>(object.filters.size());

        for (final Filter filter : object.filters) {
            filters.add(filter.copy());
        }

        events = new ArrayList<MovieClipEventHandler>(object.events.size());

        for (final MovieClipEventHandler event : object.events) {
            events.add(event.copy());
        }

        blend = object.blend;
    }

    /**
     * Returns the type of place operation being performed.
     */
    public Placement getPlacement() {
        return placeType;
    }

    /**
     * Sets the type of placement.
     * 
     * @param aType
     *            the type of operation to be performed, either New, Modify or
     *            Replace.
     */
    public Place3 setPlacement(final Placement aType) {
        placeType = aType;
        return this;
    }

    /**
     * Returns the Layer on which the object will be displayed in the display
     * list.
     */
    public int getLayer() {
        return layer;
    }

    /**
     * Sets the layer at which the object will be placed.
     * 
     * @param aLayer
     *            the layer number on which the object is being displayed. Must
     *            be in the range 1..65535.
     */
    public Place3 setLayer(final int aLayer) {
        if ((aLayer < 1) || (aLayer > 65535)) {
            throw new IllegalArgumentException(Strings.LAYER_RANGE);
        }
        layer = aLayer;
        return this;
    }

    /**
     * Returns the identifier of the object to be placed. This is only required
     * when placing an object for the first time. Subsequent references to the
     * object on this layer can simply use the layer number.
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier of the object.
     * 
     * @param uid
     *            the identifier of a new object to be displayed. Must be in the
     *            range 1..65535.
     */
    public Place3 setIdentifier(final int uid) {
        if ((uid < 1) || (uid > 65535)) {
            throw new IllegalArgumentException(Strings.IDENTIFIER_RANGE);
        }
        identifier = uid;
        return this;
    }

    /**
     * Returns the coordinate transform. May be null if no coordinate transform
     * was defined.
     */
    public CoordTransform getTransform() {
        return transform;
    }

    /**
     * Sets the coordinate transform that defines the position where the object
     * will be displayed. The argument may be null if the location of the object
     * is not being changed.
     * 
     * @param aTransform
     *            an CoordTransform object that will be applied to the object
     *            displayed.
     */
    public Place3 setTransform(final CoordTransform aTransform) {
        transform = aTransform;
        return this;
    }

    /**
     * Sets the location where the object will be displayed.
     * 
     * @param xCoord
     *            the x-coordinate of the object's origin.
     * @param yCoord
     *            the x-coordinate of the object's origin.
     * @return this object.
     */
    public Place3 setLocation(final int xCoord, final int yCoord) {
        transform = CoordTransform.translate(xCoord, yCoord);
        return this;
    }

    /**
     * Returns the colour transform. May be null if no colour transform was
     * defined.
     */
    public ColorTransform getColorTransform() {
        return colorTransform;
    }

    /**
     * Sets the colour transform that defines the colour effects applied to the
     * object. The argument may be null if the color of the object is not being
     * changed.
     * 
     * @param aTransform
     *            an ColorTransform object that will be applied to the object
     *            displayed.
     */
    public Place3 setColorTransform(final ColorTransform aTransform) {
        colorTransform = aTransform;
        return this;
    }

    /**
     * Returns the morph ratio, in the range 0..65535 that defines the progress
     * in the morphing process performed by the Flash Player from the defined
     * start and end shapes. A value of 0 indicates the start of the process and
     * 65535 the end. Returns null if no ratio was specified.
     */
    public Integer getRatio() {
        return ratio;
    }

    /**
     * Sets point of the morphing process for a morph shape in the range
     * 0..65535. May be set to null if the shape being placed is not being
     * morphed.
     * 
     * @param aNumber
     *            the progress in the morphing process.
     */
    public Place3 setRatio(final Integer aNumber) {
        if ((aNumber != null) && ((aNumber < 0) || (aNumber > 65535))) {
            throw new IllegalArgumentException(
                    "Morphing ratio must be in the range 0..65535.");
        }
        ratio = aNumber;
        return this;
    }

    /**
     * Returns the number of layers that will be clipped by the object placed on
     * the layer specified in this object.
     */
    public Integer getDepth() {
        return depth;
    }

    /**
     * Sets the number of layers that this object will mask. May be set to zero
     * if the shape being placed does not define a clipping area.
     * 
     * @param aNumber
     *            the number of layers clipped.
     */
    public Place3 setDepth(final Integer aNumber) {
        if ((aNumber != null) && ((aNumber < 1) || (aNumber > 65535))) {
            throw new IllegalArgumentException(Strings.IDENTIFIER_RANGE);
        }
        depth = aNumber;
        return this;
    }

    /**
     * Returns the name of the object. May be null if a name was not assigned to
     * the object.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of an object to be displayed. If a shape is not being
     * assigned a name then setting the argument to null will omit the attribute
     * when the object is encoded.
     * 
     * @param aString
     *            the name assigned to the object.
     */
    public Place3 setName(final String aString) {
        name = aString;
        return this;
    }

    /**
     * Returns the name of the object. May be an empty string if a name was not
     * assigned to the object.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Set the name of an object to be displayed. If a shape is not being
     * assigned a name then setting the argument to an empty string will omit
     * the attribute when the object is encoded.
     * 
     * @param aString
     *            the name assigned to the object.
     */
    public Place3 setClassName(final String aString) {
        className = aString;
        return this;
    }

    // TODO(doc)
    public Blend getBlend() {
        return Blend.fromInt(blend);
    }

    // TODO(doc)
    public void setBlend(final Blend mode) {
        blend = mode.getValue();
    }

    /**
     * Adds a clip event to the array of clip events. If the object already
     * contains a set of encoded clip event objects they will be deleted.
     * 
     * @param aClipEvent
     *            a clip event object.
     * 
     *            throws NullPointerException of the clip event object is null
     */
    public Place3 add(final MovieClipEventHandler aClipEvent)
            throws CoderException {
        if (aClipEvent == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        events.add(aClipEvent);
        return this;
    }

    /**
     * Returns the array of ClipEvent object that define the actions that will
     * be executed in response to events that occur in the DefineMovieClip being
     * placed.
     */
    public List<MovieClipEventHandler> getEvents() throws CoderException {
        return events;
    }

    /**
     * Set the array of Clip events. Clip Events are only valid for movie clips
     * and the argument should be set to null when placing other types of
     * object.
     * 
     * If the object already contains a set of encoded clip event objects they
     * will be deleted.
     * 
     * @param anArray
     *            an array of ClipEvent objects.
     */
    public void setEvents(final List<MovieClipEventHandler> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException(Strings.ARRAY_IS_NULL);
        }
        events = anArray;
    }

    // TODO(doc)
    public Place3 add(final Filter filter) {
        if (filter == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        filters.add(filter);
        return this;
    }

    // TODO(doc)
    public List<Filter> getFilters() {
        return filters;
    }

    // TODO(doc)
    public void setFilters(final List<Filter> array) {
        if (array == null) {
            throw new IllegalArgumentException(Strings.ARRAY_IS_NULL);
        }
        filters = array;
    }

    public Place3 copy() {
        return new Place3(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, placeType, layer, identifier, transform,
                colorTransform, ratio, depth, name, events);
    }

    // TODO(optimise)
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.TRANSPARENT, 1);

        length = 3;
        length += ((placeType == Placement.NEW) || (placeType == Placement.REPLACE)) ? 2
                : 0;
        length += transform == null ? 0 : transform.prepareToEncode(coder,
                context);
        length += colorTransform == null ? 0 : colorTransform.prepareToEncode(
                coder, context);
        length += ratio == null ? 0 : 2;
        length += depth == null ? 0 : 2;
        length += name == null ? 0 : coder.strlen(name);

        if (!events.isEmpty()) {
            final int eventSize = Context.VERSION > 5 ? 4 : 2;

            length += 2 + eventSize;

            for (final MovieClipEventHandler handler : events) {
                length += handler.prepareToEncode(coder, context);
            }

            length += eventSize;
        }

        vars.remove(Context.TRANSPARENT);

        return (length > 62 ? 6 : 2) + length;
    }

    // TODO(optimise)
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();

        if (length >= 63) {
            coder.writeWord((MovieTypes.PLACE_3 << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.PLACE_3 << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);

        final Map<Integer, Integer> vars = context.getVariables();
        vars.put(Context.TRANSPARENT, 1);
        coder.writeBits(events.isEmpty() ? 0 : 1, 1);
        coder.writeBits(depth == null ? 0 : 1, 1);
        coder.writeBits(name == null ? 0 : 1, 1);
        coder.writeBits(ratio == null ? 0 : 1, 1);
        coder.writeBits(colorTransform == null ? 0 : 1, 1);
        coder.writeBits(transform == null ? 0 : 1, 1);

        if (placeType == Placement.MODIFY) {
            coder.writeBits(1, 2);
        } else if (placeType == Placement.NEW) {
            coder.writeBits(2, 2);
        } else {
            coder.writeBits(3, 2);
        }

        coder.writeWord(layer, 2);

        if ((placeType == Placement.NEW) || (placeType == Placement.REPLACE)) {
            coder.writeWord(identifier, 2);
        }
        if (transform != null) {
            transform.encode(coder, context);
        }
        if (colorTransform != null) {
            colorTransform.encode(coder, context);
        }
        if (ratio != null) {
            coder.writeWord(ratio, 2);
        }
        if (name != null) {
            coder.writeString(name);
        }

        if (depth != null) {
            coder.writeWord(depth, 2);
        }

        if (!events.isEmpty()) {
            final int eventSize = Context.VERSION > 5 ? 4 : 2;
            int eventMask = 0;

            coder.writeWord(0, 2);

            for (final MovieClipEventHandler handler : events) {
                for (final MovieClipEvent event : handler.getEvent()) {
                    eventMask |= event.getValue();
                }
            }

            coder.writeWord(eventMask, eventSize);

            for (final MovieClipEventHandler handler : events) {
                handler.encode(coder, context);
            }

            coder.writeWord(0, eventSize);
        }
        vars.remove(Context.TRANSPARENT);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
