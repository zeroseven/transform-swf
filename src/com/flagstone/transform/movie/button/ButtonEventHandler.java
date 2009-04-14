/*
 * ButtonEvent.java
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
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Codeable;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.action.Action;
import com.flagstone.transform.movie.action.ActionData;


/**
 * ButtonEvent is used to define the actions that a (menu or push) button will 
 * execute in response to a particular event. A button responds to:</p>
 * 
 * <table class="datasheet">
 * <tr>
 * <td valign="top">RollOver</td>
 * <td>the mouse cursor moves over the active area of a button.</td>
 * </tr>
 * <tr>
 * <td valign="top">RollOut</td>
 * <td>the mouse cursor moves out of the active area of a button.</td>
 * </tr>
 * <tr>
 * <td valign="top">Press</td>
 * <td>the mouse button is clicked while the mouse cursor is over
 * the active area of the button.</td>
 * </tr>
 * <tr>
 * <td valign="top">Release</td>
 * <td>the mouse button is clicked and released while the mouse
 * cursor is over the active area of the button.</td>
 * </tr>
 * <tr>
 * <td valign="top">DragOut</td>
 * <td>the mouse button is clicked and the mouse cursor is dragged
 * out of the active area of the button.</td>
 * </tr>
 * <tr>
 * <td valign="top">DragOver</td>
 * <td>the mouse button is clicked, the mouse cursor is dragged
 * into the active area of the button and the mouse button is released.</td>
 * </tr>
 * </td>
 * </tr>
 * </table>
 * 
 * <p>When a button is configured as a menu item then three additional events 
 * can occur:</p>
 * 
 * <table class="datasheet">
 * <tr>
 * <td valign="top">MenuDragOver</td>
 * <td>occurs when the mouse button is clicked and the mouse cursor is dragged
 * into the active area of the menu item.</td>
 * </tr>
 * <tr>
 * <td valign="top">MenuDragOut</td>
 * <td>occurs when the mouse button is clicked and the mouse cursor is dragged
 * out of the active area of the menu item.</td>
 * </tr>
 * <tr>
 * <td valign="top">ReleaseOut</td>
 * <td>occurs when the mouse button is clicked and the mouse cursor is dragged
 * into the active area of the menu item.</td>
 * </tr>
 * </table>
 * 
 * <p>In addition to responding to mouse events, buttons also respond to keys 
 * being pressed on the keyboard. Keyboard events are defined by the character 
 * key being pressed, e.g. "t", "T", "$", etc. The event code for a key is 
 * generated using the <b>codeForKey</b> method:</p>
 * 
 * <pre>
 * int eventCode = ButtonEvent.codeForKey('J');
 * </pre>
 * 
 * <p>For control keys the codes are defined using the following set of constants:</p>
 * 
 * <table>
 * <tr>
 * <td>&lt;Left&gt;</td>
 * <td>Left arrow key.</td>
 * </tr>
 * <tr>
 * <td>&lt;Right&gt;</td>
 * <td>Right arrow key.</td>
 * </tr>
 * <tr>
 * <td>&lt;Home&gt;</td>
 * <td>Home key.</td>
 * </tr>
 * <tr>
 * <td>&lt;End&gt;</td>
 * <td>End key</td>
 * </tr>
 * <tr>
 * <td>&lt;Insert&gt;</td>
 * <td>Insert key.</td>
 * </tr>
 * <tr>
 * <td>&lt;Delete&gt;</td>
 * <td>Delete key.</td>
 * </tr>
 * <tr>
 * <td>&lt;Backspace&gt;</td>
 * <td>Backspace key.</td>
 * </tr>
 * <tr>
 * <td>&lt;Enter&gt;</td>
 * <td>Enter (return) key.</td>
 * </tr>
 * <tr>
 * <td>&lt;Up&gt;</td>
 * <td>Up arrow key.</td>
 * </tr>
 * <tr>
 * <td>&lt;Down&gt;</td>
 * <td>Down arrow key.</td>
 * </tr>
 * <tr>
 * <td>&lt;Pageup&gt;</td>
 * <td>Page up key.</td>
 * </tr>
 * <tr>
 * <td>&lt;Pagedown&gt;</td>
 * <td>Page down key.</td>
 * </tr>
 * <tr>
 * <td>&lt;Tab&gt;</td>
 * <td>Tab key.</td>
 * </tr>
 * <tr>
 * <td>&lt;Escape&gt;</td>
 * <td>Escape key.</td>
 * </tr>
 * <tr>
 * <td>&lt;Space&gt;</td>
 * <td>Space bar.</td>
 * </tr>
 * </table>
 * 
 * <p>A button can respond in the same way for multiple events by creating 
 * a compound event code created by performing a bit-wise Or of the individual 
 * codes:</p>
 * 
 * <pre>
 * int eventCode = ButtonEvent.RollOver | ButtonEvent.Press;
 * int eventCode = ButtonEvent.Enter | ButtonEvent.PageUp;
 * </pre>
 * 
 * <p>While multiple mouse events can be defined for a button only one
 * keyboard event can be defined.</p>
 * 
 * @see DefineButton2
 */
public final class ButtonEventHandler implements Codeable
{
	private static final String FORMAT = "ButtonEventHandler: { event=%s; actions=%s }";
	
	/**
	 * Returns the code used to identify that a character has been typed on the 
	 * keyboard. This method should be used for characters that are not already
	 * defined as a constant in this class.
	 * 
	 * @param character
	 *            a keyboard character.
	 */
	public static int codeForKey(char character)
	{
		return character << 9;
	}

	protected int event;
	protected List<Action> actions;

	protected transient int length = 0;

	protected ButtonEventHandler(int size, final SWFDecoder coder) throws CoderException
	{
		event = coder.readWord(2, false);
		length -= 2;

		actions = new ArrayList<Action>();

		if (coder.getContext().isDecodeActions()) {
			
			int end = coder.getPointer() + (length << 3);

			while (coder.getPointer() < end) {
				actions.add(coder.actionOfType(coder));
			}
		} 
		else 
		{
			if (length != 0) {
				actions.add(new ActionData(length, coder));
			}
		}
	}


	protected ButtonEventHandler()
	{
		actions = new ArrayList<Action>();
	}

	/**
	 * Creates an ButtonEvent object that defines the array of actions that
	 * will be executed when a particular event occurs.
	 * 
	 * @param aNumber
	 *            the event code. Must be in the range 1..65535.
	 * @param anArray
	 *            the array of action objects that will be executed when the
	 *            specified event(s) occur.
	 */
	public ButtonEventHandler(Set<ButtonEvent> aNumber, List<Action> anArray)
	{
		setEvent(aNumber);
		setActions(anArray);
	}
	
	public ButtonEventHandler(ButtonEventHandler object) {
		event = object.event;
		actions = new ArrayList<Action>();
		
		for (Action action : actions) {
			actions.add(action.copy());
		}
	}

	/**
	 * Add an action to the end of the actions array.
	 * 
	 * @param anAction
	 *            an object derived from the base class Action. Must not be null.
	 */
	public void add(Action anAction) throws CoderException
	{
		if (anAction == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		actions.add(anAction);
	}

	/**
	 * Returns the compound event code that this ButtonEvent defines actions for.
	 */
	public Set<ButtonEvent> getEvent()
	{
		Set<ButtonEvent>set = EnumSet.allOf(ButtonEvent.class);
		
		for (Iterator<ButtonEvent>iter = set.iterator(); iter.hasNext();) {
			if ((event & iter.next().getValue()) == 0) {
				iter.remove();
			}
		}
		return set;
	}

	/**
	 * Returns the array of actions that are executed by the button in response to
	 * specified event(s).
	 */
	public List<Action> getActions() throws CoderException
	{
		return actions;
	}

	/**
	 * Sets the event code that this ButtonEvent defines actions for.
	 * 
	 * @param aNumber
	 *            the event code. Must be in the range 1..65535.
	 */
	public void setEvent(Set<ButtonEvent>set)
	{
		for (ButtonEvent event : set) {
			this.event |= event.getValue();
		}
	}

	/**
	 * Sets the array of actions that are executed by the button in response to
	 * specified event(s).
	 * 
	 * @param anArray
	 *            the array of action objects that will be executed when the
	 *            specified event(s) occur. The array may be empty but must not 
	 *            be null.
	 */
	public void setActions(List<Action> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		actions = anArray;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	public ButtonEventHandler copy()
	{
		return new ButtonEventHandler(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, event, actions);
	}

	public int prepareToEncode(final SWFEncoder coder)
	{
		length = 2;

		for (Action action : actions) {
			length += action.prepareToEncode(coder);
		}
		
		return length;
	}

	public void encode(final SWFEncoder coder) throws CoderException
	{
		coder.writeWord(event, 2);

		for (Action action : actions) {
			action.encode(coder);
		}
	}
}
