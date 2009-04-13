/*
 * DefineButton2.java
 * Transform
 * 
 * Copyright (c) 2001-2008 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.movie.button;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.DefineTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;

/**
 * DefineButton2 defines the appearance and actions of push and menu buttons.
 * 
 * <p>It provides a more sophisticated model for creating buttons than 
 * {@link DefineButton}:
 * </p>
 * 
 * <ul>
 * <li>Two types of button are supported, <B>Push</B> and <B>Menu</B>.</li>
 * <li>The number of events that a button can respond to is increased.</li>
 * <li>Actions can be executed for any button event.</li>
 * </ul>
 * 
 * <p>Push and Menu buttons behave slightly differently in tracking mouse movements
 * when the button is clicked. A Push button 'captures' the mouse so if the
 * cursor is dragged outside of the active area of the button and the mouse
 * click is released then the Release Outside event is still sent to the button.
 * A Menu button does not 'capture' the mouse so if the cursor is dragged out of
 * the active area the button returns to its 'inactive' state.</p>
 * 
 * <p>A DefineButton2 object must contain at least one ButtonShape. If more
 * than one button shape is defined for a given button state then each shape
 * will be displayed by the button. The order in which the shapes are displayed
 * is determined by the layer assigned to each button record.
 * </p>
 * 
 * @see ButtonShape
 * @see ButtonEventHandler
 */
public final class DefineButton2 implements DefineTag
{
	private static final String FORMAT = "DefineButton2: { identifier=%d; buttonRecords=%s; handlers=%s }";

	private int identifier;
	protected boolean menu;
	protected List<ButtonShape> shapes;
	protected List<ButtonEventHandler> events;

	private transient int start;
	private transient int end;
	private transient int length;
	
	public DefineButton2()
	{
		shapes = new ArrayList<ButtonShape>();
		events = new ArrayList<ButtonEventHandler>();
	}

	/**
	 * Creates a DefineButton2 object, specifying the unique identifier,
	 * the type of button to be created, the button shapes that describe the
	 * button's appearance and the actions that are performed in response to
	 * each button event.
	 * 
	 * @param uid
	 *            a unique identifier for this button. Must be in the range 1..65535.
	 * @param menu
	 *            the button is a menu button (true) or push button (false).
	 * @param shapes
	 *            an array of Button objects. Must not be null.
	 * @param events
	 *            an array of ButtonEvent objects. Must not be null.
	 */
	public DefineButton2(int uid, boolean menu, List<ButtonShape> shapes, List<ButtonEventHandler> events)
	{
		setMenu(menu);
		setShapes(shapes);
		setEvents(events);
	}
	
	public DefineButton2(DefineButton2 object) {
		identifier = object.identifier;
		menu = object.menu;
		shapes = new ArrayList<ButtonShape>(object.shapes.size());
		for (ButtonShape shape : object.shapes) {
			shapes.add(shape.copy());
		}
		events = new ArrayList<ButtonEventHandler>(object.events.size());
		for (ButtonEventHandler event : object.events) {
			events.add(event.copy());
		}
	}
		
	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final int uid) {
		if (uid < 0 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = uid;
	}

	/**
	 * Adds an ButtonShape to the array of button records.
	 * 
	 * @param obj
	 *            a button shape object. Must not be null.
	 */
	public void add(ButtonShape obj)
	{
		if (obj == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		shapes.add(obj);
	}

	/**
	 * Adds a button event object to the array of button events.
	 * 
	 * @param obj
	 *            a button event. Must not be null.
	 */
	public void add(ButtonEventHandler obj) throws CoderException
	{
		if (obj == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}		
		events.add(obj);
	}

	/**
	 * Returns the button type - either PUSH or MENU.
	 */
	public boolean isMenu()
	{
		return menu;
	}

	/**
	 * Returns the array of button records defined for this button.
	 */
	public List<ButtonShape> getShapes()
	{
		return shapes;
	}

	/**
	 * Returns the array of button records defined for this button.
	 */
	public List<ButtonEventHandler> getEvents() throws CoderException
	{
		return events;
	}

	/**
	 * Sets the button type.
	 * 
	 * @param aType
	 *            the type of button. Must be either PUSH or MENU.
	 */
	public void setMenu(boolean menu)
	{
		this.menu = menu;
	}

	/**
	 * Sets the array of button shapes defined for this button.
	 * 
	 * @param anArray
	 *            an array of ButtonShape objects. Must not be null.
	 */
	public void setShapes(List<ButtonShape> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
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
	public void setEvents(List<ButtonEventHandler> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		events = anArray;
	}
	
	/**
	 * Creates and returns a deep copy of this object.
	 */
	public DefineButton2 copy()
	{
		return new DefineButton2(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, identifier, shapes, events);
	}

	public int prepareToEncode(final SWFEncoder coder)
	{
		coder.getContext().setType(Types.DEFINE_BUTTON_2);
		coder.getContext().setTransparent(true);

		length = 6;

		for (ButtonShape shape : shapes) {
			length += shape.prepareToEncode(coder);
		}
		
		for (ButtonEventHandler handler : events) {
			length += 2 + handler.prepareToEncode(coder);
		}

		coder.getContext().setType(0);
		coder.getContext().setTransparent(false);

		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder) throws CoderException
	{
		coder.getContext().setType(Types.DEFINE_BUTTON_2);
		coder.getContext().setTransparent(true);
		
		start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((Types.DEFINE_BUTTON_2 << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.DEFINE_BUTTON_2 << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);
		
		coder.writeWord(identifier, 2);
		coder.writeWord(menu ? 1:0, 1);

		int offsetStart = coder.getPointer();
		coder.writeWord(0, 2);

		for (ButtonShape shape : shapes) {
			shape.encode(coder);
		}

		coder.writeWord(0, 1);

		// Write actions offset

		int currentCursor = coder.getPointer();
		int offsetEnd = (currentCursor - offsetStart) >> 3;
		coder.setPointer(offsetStart);
		coder.writeWord(offsetEnd, 2);
		coder.setPointer(currentCursor);

		for (ButtonEventHandler handler : events)
		{
			offsetStart = coder.getPointer();
			coder.writeWord(handler.length + 2, 2);
			handler.encode(coder);
		}		
		
		// Write offset of zero for last Action Condition
		currentCursor = coder.getPointer();
		coder.setPointer(offsetStart);
		coder.writeWord(0, 2);
		coder.setPointer(currentCursor);

		coder.getContext().setType(0);
		coder.getContext().setTransparent(false);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	public void decode(final SWFDecoder coder) throws CoderException
	{
		coder.getContext().setType(Types.DEFINE_BUTTON_2);
		coder.getContext().setTransparent(true);

		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		identifier = coder.readWord(2, false);
		menu = coder.readByte() != 0;
		
		int offsetToNext = coder.readWord(2, false);
		
		ButtonShape shape;

		while (coder.readByte() != 0) {
			coder.adjustPointer(-8);
			shape = new ButtonShape();
			shape.decode(coder);
			shapes.add(shape);
		}

		if (offsetToNext != 0)
		{
			ButtonEventHandler event;
			
			do
			{
				offsetToNext = coder.readWord(2, false);

				if (offsetToNext == 0) {
					event = new ButtonEventHandler((end - coder.getPointer()) >>> 3);
				} else {
					event = new ButtonEventHandler(offsetToNext);
				}
				event.decode(coder);
				events.add(event);

			} while (offsetToNext != 0);
		}
		
		coder.getContext().setType(0);
		coder.getContext().setTransparent(false);

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
