/*
 * Event.java
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

package com.flagstone.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.flagstone.transform.action.Action;
import com.flagstone.transform.action.ActionData;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncodeable;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;

/**
 * <p>
 * ClipEvent is used to define the actions that a movie clip will execute in
 * response to a particular event. ClipEvent objects are added to an
 * Place2 object and the actions are registered with the Flash Player
 * when the movie clip is added to the display list.
 * </p>
 *
 * <p>
 * The events that a movie clip responds to are:
 * </p>
 *
 * <table class="datasheet">
 * <tr>
 * <td valign="top">Load</td>
 * <td>the movie clip is finished loading.</td>
 * </tr>
 * <tr>
 * <td valign="top">Unload</td>
 * <td>the movie clip is unloaded from the parent movie.</td>
 * </tr>
 * <tr>
 * <td valign="top">EnterFrame</td>
 * <td>when the mouse enters the Flash Player window.</td>
 * </tr>
 * <tr>
 * <td valign="top">MouseMove</td>
 * <td>the mouse pointer is moved.</td>
 * </tr>
 * <tr>
 * <td valign="top">MouseDown</td>
 * <td>the left mouse button is pressed while the cursor is outside of the
 * bounding rectangle of the movie clip.</td>
 * </tr>
 * <tr>
 * <td valign="top">MouseUp</td>
 * <td>the left mouse button is pressed and released while the cursor is outside
 * of the bounding rectangle of the movie clip.</td>
 * </tr>
 * <tr>
 * <td valign="top">KeyDown</td>
 * <td>a key is pressed on the keyboard. From Flash 6 a key code can be
 * specified to identify a specific key rather than testing for the value inside
 * the actions that are executed in response to the event.</td>
 * </tr>
 * <tr>
 * <td valign="top">KeyUp</td>
 * <td>akey being pressed on the keyboard is released.</td>
 * </tr>
 * <tr>
 * <td valign="top">Data</td>
 * <td>
 * a GetUrl2 action is executed with the movie clip specified as a target.
 * </td>
 * </tr>
 * <tr>
 * <td valign="top">Construct</td>
 * <td>This event is not documented by Adobe.</td>
 * </tr>
 * </table>
 *
 * <p>
 * Starting with Flash 6 movie clips also respond to the same set of events as
 * buttons, see ButtonEventHandler
 * </p>
 *
 * <p>
 * A ClipEvent object can define the actions that will be executed in response
 * to more than one event, simply bitwise OR together the individual event
 * codes:
 * </p>
 *
 * <pre>
 * int loadAndMouseMove = ClipEvent.Load | ClipEvent.MouseMove;
 * </pre>
 */
//TODO(class)
public final class EventHandler implements SWFEncodeable {

    /** Format string used in toString() method. */
    private static final String FORMAT = "EventHandler: { events=%s;"
            + " key=%s; actions=%s }";

    /** Number of bits to shift key code for encoding with event flags. */
    private static final int KEY_OFFSET = 9;
    /** Bit mask for key field. */
    private static final int KEY_MASK = 0xFE00;
    /** Bit mask for key field. */
    private static final int EVENT_MASK = 0x01FF;

    private static final Map<Event,Integer>clipCodes;
    private static final Map<Event,Integer>buttonCodes;
    private static final Map<Event,Integer>menuCodes;

    private static final Map<Integer, Event>clipEvents;
    private static final Map<Integer, Event>buttonEvents;
    private static final Map<Integer, Event>menuEvents;

    static {
        clipCodes = new LinkedHashMap<Event, Integer>();
        clipCodes.put(Event.LOAD, 1);
        clipCodes.put(Event.ENTER_FRAME, 2);
        clipCodes.put(Event.UNLOAD, 4);
        clipCodes.put(Event.MOUSE_MOVE, 8);
        clipCodes.put(Event.MOUSE_DOWN, 16);
        clipCodes.put(Event.MOUSE_UP, 32);
        clipCodes.put(Event.KEY_DOWN, 64);
        clipCodes.put(Event.KEY_UP, 128);
        clipCodes.put(Event.DATA, 256);
        clipCodes.put(Event.INITIALIZE, 512);
        clipCodes.put(Event.PRESS, 1024);
        clipCodes.put(Event.RELEASE, 2048);
        clipCodes.put(Event.RELEASE_OUT, 4096);
        clipCodes.put(Event.ROLL_OVER, 8192);
        clipCodes.put(Event.ROLL_OUT, 16384);
        clipCodes.put(Event.DRAG_OVER, 32768);
        clipCodes.put(Event.DRAG_OUT, 65536);
        clipCodes.put(Event.KEY_PRESS, 131072);
        clipCodes.put(Event.CONSTRUCT, 262144);

        clipEvents = new LinkedHashMap<Integer, Event>();
        clipEvents.put(1, Event.LOAD);
        clipEvents.put(2, Event.ENTER_FRAME);
        clipEvents.put(4, Event.UNLOAD);
        clipEvents.put(8, Event.MOUSE_MOVE);
        clipEvents.put(16, Event.MOUSE_DOWN);
        clipEvents.put(32, Event.MOUSE_UP);
        clipEvents.put(64, Event.KEY_DOWN);
        clipEvents.put(128, Event.KEY_UP);
        clipEvents.put(256, Event.DATA);
        clipEvents.put(512, Event.INITIALIZE);
        clipEvents.put(1024, Event.PRESS);
        clipEvents.put(2048, Event.RELEASE);
        clipEvents.put(4096, Event.RELEASE_OUT);
        clipEvents.put(8192, Event.ROLL_OVER);
        clipEvents.put(16384, Event.ROLL_OUT);
        clipEvents.put(32768, Event.DRAG_OVER);
        clipEvents.put(65536, Event.DRAG_OUT);
        clipEvents.put(131072, Event.KEY_PRESS);
        clipEvents.put(262144, Event.CONSTRUCT);

        buttonCodes = new LinkedHashMap<Event, Integer>();
        buttonCodes.put(Event.ROLL_OVER, 1);
        buttonCodes.put(Event.ROLL_OUT, 2);
        buttonCodes.put(Event.PRESS, 4);
        buttonCodes.put(Event.RELEASE, 8);
        buttonCodes.put(Event.DRAG_OUT, 16);
        buttonCodes.put(Event.DRAG_OVER, 32);
        buttonCodes.put(Event.RELEASE_OUT, 64);

        buttonEvents = new LinkedHashMap<Integer, Event>();
        buttonEvents.put(1, Event.ROLL_OVER);
        buttonEvents.put(2, Event.ROLL_OUT);
        buttonEvents.put(4, Event.PRESS);
        buttonEvents.put(8, Event.RELEASE);
        buttonEvents.put(16, Event.DRAG_OUT);
        buttonEvents.put(32, Event.DRAG_OVER);
        buttonEvents.put(64, Event.RELEASE_OUT);

        menuCodes = new LinkedHashMap<Event, Integer>();
        menuCodes.put(Event.ROLL_OVER, 1);
        menuCodes.put(Event.ROLL_OUT, 2);
        menuCodes.put(Event.PRESS, 4);
        menuCodes.put(Event.RELEASE, 8);
        menuCodes.put(Event.RELEASE_OUT, 64);
        menuCodes.put(Event.DRAG_OVER, 128);
        menuCodes.put(Event.DRAG_OUT, 256);

        menuEvents = new LinkedHashMap<Integer, Event>();
        menuEvents.put(1, Event.ROLL_OVER);
        menuEvents.put(2, Event.ROLL_OUT);
        menuEvents.put(4, Event.PRESS);
        menuEvents.put(8, Event.RELEASE);
        menuEvents.put(64, Event.RELEASE_OUT);
        menuEvents.put(128, Event.DRAG_OVER);
        menuEvents.put(256, Event.DRAG_OUT);
    }

    /** The events that the handler responds to. */
    private Set<Event> events;
    /** The code representing keyboard shortcut for the handler. */
    private int key;
    /** The actions executed by the handler when the event occurs. */
    private List<Action> actions;

    private int eventCode;
    private int length;
    private int offset;

    /**
     * Creates and initialises a EventHandler object using values
     * encoded in the Flash binary format.
     *
     * @param value
     *            is decoded by and it is dependent on the parent object. If
     *            it is a Place2 or Place3 object then the event handler is for
     *            a movie clip and the value represents the the set of events
     *            that the handler responds to. If the parent object is a
     *            button then the value is the length in bytes of the encoded
     *            actions executed by the handler.
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
    public EventHandler(final int value, final SWFDecoder coder,
            final Context context) throws IOException {

        events = EnumSet.noneOf(Event.class);

        final int mask = 1;
        int field;

        if (context.contains(Context.TYPE)
                && context.get(Context.TYPE) == MovieTypes.DEFINE_BUTTON_2) {
            length = value;
            int eventKey = coder.readUnsignedShort();
            eventCode = eventKey & EVENT_MASK;
            key = (eventKey & KEY_MASK) >> KEY_OFFSET;

            if (context.contains(Context.MENU_BUTTON)) {
                for (int i = 0; i < 9; i++) {
                    field = eventCode & (mask << i);
                    if (field != 0 && buttonEvents.containsKey(field)) {
                        events.add(buttonEvents.get(field));
                    }
                }
            } else {
                for (int i = 0; i < 9; i++) {
                    field = eventCode & (mask << i);
                    if (menuEvents.containsKey(field)) {
                        events.add(menuEvents.get(field));
                    }
                }
            }
         } else {
            eventCode = value;
            length = coder.readInt();
            if ((eventCode & clipCodes.get(Event.KEY_PRESS)) != 0) {
                key = coder.readByte();
                length -= 1;
            }
            for (int i = 0; i < 32; i++) {
                field = eventCode & (mask << i);
                if (field != 0 && clipEvents.containsKey(field)) {
                    events.add(clipEvents.get(field));
                }
            }
        }

        actions = new ArrayList<Action>();

        final SWFFactory<Action> decoder = context.getRegistry()
                .getActionDecoder();

        if (decoder == null) {
            if (length != 0) {
                actions.add(new ActionData(coder.readBytes(new byte[length])));
            }
        } else {
            coder.mark();
            while (coder.bytesRead() < length) {
                actions.add(decoder.getObject(coder, context));
            }
            coder.unmark();
        }
    }

    /**
     * Creates a ClipEvent object that with an array of actions that will be
     * executed when a particular event occurs.
     *
     * @param event
     *            the set of Events that the handler will respond to.
     * @param anArray
     *            the array of actions that will be executed when the specified
     *            event occurs.
     */
    public EventHandler(final Set<Event> event, final List<Action> anArray) {
        setEvents(event);
        setActions(anArray);
    }

    /**
     * Creates an EventHandler object that defines the array of actions that
     * will be executed when a particular event occurs or when the specified
     * key is pressed.
     *
     * @param event
     *            the set of Events that the handler will respond to.
     * @param character
     *            the ASCII code for the key pressed on the keyboard.
     * @param anArray
     *            the array of actions that will be executed when the specified
     *            event occurs. Must not be null.
     */
    public EventHandler(final Set<Event> event,
            final int character, final List<Action> anArray) {
        setEvents(event);
        setKey(character);
        setActions(anArray);
    }

    /**
     * Creates and initialises a EventHandler object using the values
     * copied from another EventHandler object.
     *
     * @param object
     *            a EventHandler object from which the values will be
     *            copied.
     */
    public EventHandler(final EventHandler object) {
        events = object.events;
        key = object.key;
        actions = new ArrayList<Action>(object.actions);
    }

    /**
     * Get the value that is encoded to represent the set of events that the
     * handler responds to.
     *
     * NOTE: This method is only used by Place2 and Place3 objects to encode
     * EventHandlers for movie clips. It should not be used.
     *
     * @return the value representing the set of encoded events.
     */
    public int getEventCode() {
        return eventCode;
    }

    /**
     * Get the set of events that the handler responds to.
     * @return a set of Events.
     */
    public Set<Event> getEvents() {
        return events;
    }

    /**
     * Set the events that the handler responds to.
     * @param set the set of Events for the handler.
     */
    public void setEvents(final Set<Event> set) {
        events = set;
    }

    /**
     * Get the code for the key that triggers the event when pressed. The
     * code is typically the ASCII code for standard western keyboards.
     *
     * @return the ASCII code for the key that triggers the event.
     */
    public int getKey() {
        return key;
    }

    /**
     * Sets the code for the key that triggers the event when pressed. The code
     * is typically the ASCII code for standard western keyboards.
     *
     * @param code
     *            the ASCII code for the key that triggers the event.
     */
    public void setKey(final int code) {
        key = code;
    }

    /**
     * Get the array of actions that are executed by the movie clip.
     *
     * @return the actions executed by the handler.
     */
    public List<Action> getActions() {
        return actions;
    }

    /**
     * Sets the array of actions that are executed by the handler in response
     * to specified event(s).
     *
     * @param array
     *            the array of actions that will be executed when the specified
     *            event occurs. Must not be null.
     */
    public void setActions(final List<Action> array) {
        if (array == null) {
            throw new IllegalArgumentException();
        }
        actions = array;
    }

    /**
     * Adds an action to the array of actions.
     *
     * @param anAction
     *            an action object. Must not be null.
     * @return this object.
     */
    public EventHandler add(final Action anAction) {
        if (anAction == null) {
            throw new IllegalArgumentException();
        }
        actions.add(anAction);
        return this;
    }

    /** {@inheritDoc} */
    public EventHandler copy() {
        return new EventHandler(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, events, key, actions);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {

        eventCode = 0;

        if (context.contains(Context.TYPE)
                && context.get(Context.TYPE) == MovieTypes.DEFINE_BUTTON_2) {
            if (context.contains(Context.MENU_BUTTON)) {
                for (Event event : events) {
                    eventCode |= menuCodes.get(event);
                }
            } else {
                for (Event event : events) {
                    eventCode |= buttonCodes.get(event);
                }
            }

            length = 4;
            for (final Action action : actions) {
                length += action.prepareToEncode(context);
            }
            if (context.contains(Context.LAST)) {
                offset = -2;
            } else {
                offset = length - 2;
            }
        } else {
            for (Event event : events) {
                eventCode |= clipCodes.get(event);
            }

            if (context.get(Context.VERSION) > 5) {
                length = 8;
            } else {
                length = 6;
            }
            offset = (eventCode & clipCodes.get(Event.KEY_PRESS)) == 0 ? 0 : 1;

            for (final Action action : actions) {
                offset += action.prepareToEncode(context);
            }

            length += offset;
        }
        return length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        coder.mark();
        if (context.contains(Context.TYPE)
                && context.get(Context.TYPE) == MovieTypes.DEFINE_BUTTON_2) {
            coder.writeShort(offset + 2);
            coder.writeShort((key << KEY_OFFSET) | eventCode);
        } else {
            if (context.get(Context.VERSION) > 5) {
                coder.writeInt(eventCode);
            } else {
                coder.writeShort(eventCode);
            }

            coder.writeInt(offset);

            if ((eventCode & clipCodes.get(Event.KEY_PRESS)) != 0) {
                coder.writeByte(key);
            }
        }

        for (final Action action : actions) {
            action.encode(coder, context);
        }
        coder.unmark(length);
    }
}
