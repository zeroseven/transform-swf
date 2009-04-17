/*
 * ClipEvent.java
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

package com.flagstone.transform.movie.movieclip;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Encodeable;
import com.flagstone.transform.movie.Place2;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.action.Action;
import com.flagstone.transform.movie.action.ActionData;
import com.flagstone.transform.movie.button.ButtonEventHandler;


/**
 * <p>ClipEvent is used to define the actions that a movie clip will execute in
 * response to a particular event. ClipEvent objects are added to an 
 * {@link Place2} object and the actions are registered with the Flash 
 * Player when the movie clip is added to the display list.</p>
 * 
 * <p>The events that a movie clip responds to are:</p>
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
 * <td>the left mouse button is pressed while the cursor is outside
 * of the bounding rectangle of the movie clip.</td>
 * </tr>
 * <tr>
 * <td valign="top">MouseUp</td>
 * <td>the left mouse button is pressed and released while the
 * cursor is outside of the bounding rectangle of the movie clip.</td>
 * </tr>
 * <tr>
 * <td valign="top">KeyDown</td>
 * <td>a key is pressed on the keyboard. From Flash 6 a key code
 * can be specified to identify a specific key rather than testing for the value
 * inside the actions that are executed in response to the event.</td>
 * </tr>
 * <tr>
 * <td valign="top">KeyUp</td>
 * <td>akey being pressed on the keyboard is released.</td>
 * </tr>
 * <tr>
 * <td valign="top">Data</td>
 * <td>a GetUrl2 action is executed with the movie clip specified as a target.</td>
 * </tr>
 * <tr>
 * <td valign="top">Construct</td>
 * <td>This event is not documented by Adobe.</td>
 * </tr>
 * </table>
 * 
 * <p>Starting with Flash 6 movie clips also respond to the same set of events 
 * as buttons, see {@link ButtonEventHandler}</p>
 * 
 * <p>A ClipEvent object can define the actions that will be executed in
 * response to more than one event, simply bitwise OR together the individual 
 * event codes:
 * </p>
 * 
 * <pre>
 * int loadAndMouseMove = ClipEvent.Load | ClipEvent.MouseMove;
 * </pre>
 * 
 * @see Place2
 */
public final class MovieClipEventHandler implements Encodeable
{	
	private static final String FORMAT = "MovieClipEventHandler: { event=%d; keyCode=%s; actions=%s }";
	
	private int event;
	private int keyCode;
	private List<Action> actions;

	private transient int offset;

	public MovieClipEventHandler(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		int eventSize = (context.getVersion() > 5) ? 4 : 2;

		event = coder.readWord(eventSize, false);
		offset = coder.readWord(4, false);

		if ((event & MovieClipEvent.KEY_PRESS.getValue()) != 0)
		{
			keyCode = coder.readByte();
			offset -= 1;
		}

		actions = new ArrayList<Action>();

		if (context.isDecodeActions()) {
			int len = offset;

			while (len > 0) {
				actions.add(context.actionOfType(coder, context));
			}
		} 
		else {
			actions.add(new ActionData(offset, coder, context));
		}
	}

	/**
	 * Creates a ClipEvent object that with an array of actions that will be 
	 * executed when a particular event occurs.
	 * 
	 * @param eventCode
	 *            the code representing one or more events.
	 * @param anArray
	 *            the array of actions that will be executed when the specified
	 *            event occurs.
	 */
	public MovieClipEventHandler(int eventCode, List<Action> anArray)
	{
		setEvent(eventCode);
		setActions(anArray);
	}

	/**
	 * Creates a ClipEvent object that defines the array of actions that
	 * will be executed when a particular event occurs or when the specified key
	 * is pressed.
	 * 
	 * @param eventCode
	 *            the code representing one or more events.
	 * @param keyCode
	 *            the ASCII code for the key pressed on the keyboard.
	 * @param anArray
	 *            the array of actions that will be executed when the specified
	 *            event occurs.
	 *            
	 * @throws IllegalArgumentException if the array is null.
	 */
	public MovieClipEventHandler(int eventCode, int keyCode, List<Action> anArray)
	{
		setEvent(eventCode);
		setKeyCode(keyCode);
		setActions(anArray);
	}
	
	public MovieClipEventHandler(MovieClipEventHandler object) {
		event = object.event;
		keyCode = object.keyCode;
		
		actions = new ArrayList<Action>(object.actions.size());
		
		for (Action action : object.actions) {
			actions.add(action.copy());
		}
	}

	/**
	 * Adds an action to the array of actions.
	 * 
	 * @param anAction
	 *            an action object. Must not be null.
	 */
	public MovieClipEventHandler add(Action anAction) throws CoderException
	{
		if (anAction == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		actions.add(anAction);
		return this;
	}

	/**
	 * Sets the event code that this ClipEvent defines actions for.
	 * 
	 * @param aNumber
	 *            the code representing one or more events.
	 */
	public void setEvent(int aNumber)
	{
		event = aNumber;
	}

	/**
	 * Returns the event code that this ClipEvent defines actions for.
	 */
	public int getEvent()
	{
		return event;
	}

	/**
	 * Returns the code for the key that triggers the event when pressed. The 
	 * code is typically the ASCII code for standard western keyboards.
	 */
	public int getKeyCode()
	{
		return keyCode;
	}

	/**
	 * Sets the code for the key that triggers the event when pressed. The code
	 * is typically the ASCII code for standard western keyboards.
	 * 
	 * @param code
	 *            the ASCII code for the key that triggers the event.
	 */
	public void setKeyCode(int code)
	{
		keyCode = code;
	}

	/**
	 * Sets the array of actions that are executed by the movie clip in response
	 * to specified event(s).
	 * 
	 * @param array
	 *            the array of actions that will be executed when the specified
	 *            event occurs.
	 *            
	 * @throws IllegalArgumentException if the array is null.
	 */
	public void setActions(List<Action> array)
	{
		if (array == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		actions = array;
	}

	/**
	 * Returns the array of actions that are executed by the movie clip.
	 */
	public List<Action> getActions() throws CoderException
	{
		return actions;
	}

	/**
	 * Creates and returns a deep copy of this object. 
	 */
	public MovieClipEventHandler copy()
	{
		return new MovieClipEventHandler(this);	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, event, keyCode, actions);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		int length = 4 + ((context.getVersion() > 5) ? 4 : 2);

		offset = (event & MovieClipEvent.KEY_PRESS.getValue()) == 0 ? 0 : 1;

		for (Action action : actions) {
			offset += action.prepareToEncode(coder, context);
		}
		
		length += offset;
		
		return length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		int eventSize = (context.getVersion() > 5) ? 4 : 2;

		coder.writeWord(event, eventSize);
		coder.writeWord(offset, 4);

		if ((event & MovieClipEvent.KEY_PRESS.getValue()) != 0) {
			coder.writeWord(keyCode, 1);
		}

		for (Action action : actions) {
			action.encode(coder, context);
		}
	}
}
