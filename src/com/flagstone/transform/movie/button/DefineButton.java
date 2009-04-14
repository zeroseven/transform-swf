/*
 * DefineButton.java
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
import java.util.Iterator;
import java.util.List;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.DefineTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;
import com.flagstone.transform.movie.action.Action;
import com.flagstone.transform.movie.action.ActionData;


/**
 * DefineButton defines the appearance of a button and the actions performed
 * when the button is clicked.
 * 
 * <p>DefineButton must contain at least one ButtonShape object. If more
 * than one button shape is defined for a given button state then each shape
 * will be displayed by the button. The order in which the shapes are displayed
 * is determined by the layer assigned to each ButtonShape object.</P>
 * 
 * @see ButtonShape
 */
public final class DefineButton implements DefineTag
{
	private static final String FORMAT = "DefineButton: { identifier=%d; buttonRecords=%s; actions=%s }";
	
	private int identifier;
	
	protected List<ButtonShape> shapes;
	protected List<Action> actions;
	
	private transient int start;
	private transient int end;
	private transient int length;

	public DefineButton(final SWFDecoder coder) throws CoderException
	{
		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		int start = coder.getPointer() - 16;

		shapes = new ArrayList<ButtonShape>();
		ButtonShape shape;
		
		while (coder.readByte() != 0) 
		{
			coder.adjustPointer(-8);
			shapes.add(new ButtonShape(coder));
		}

		int actionsLength = length - ((coder.getPointer() - start) >>> 3);

		actions = new ArrayList<Action>();

		if (coder.getContext().isDecodeActions()) {
			int len = length;
			while (len > 0) {
				actions.add(coder.actionOfType(coder));
			}
		} 
		else {
			actions.add(new ActionData(actionsLength, coder));
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}


	/**
	 * Creates a DefineButton object with the identifier, button shapes
	 * and actions.
	 * 
	 * @param uid
	 *            the unique identifier for this button.
	 * @param buttons
	 *            an array of ButtonShapes that are used to draw the button.
	 * @param actions
	 *            and array of actions that are executed when the button is clicked.
	 */
	public DefineButton(int uid, List<ButtonShape> buttons, List<Action> actions)
	{
		setButtonShapes(buttons);
		setActions(actions);
	}
	
	public DefineButton(DefineButton object) {
		identifier = object.identifier;
		shapes = new ArrayList<ButtonShape>(object.shapes.size());
		for (ButtonShape shape : object.shapes) {
			shapes.add(shape.copy());
		}
		actions = new ArrayList<Action>(object.actions.size());
		for (Action action : object.actions) {
			actions.add(action.copy());
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
	 * Adds the button shape to the array of button shapes.
	 * 
	 * @param obj
	 *            an ButtonShape object. Must not be null.
	 */
	public void add(ButtonShape obj)
	{
		if (obj == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		shapes.add(obj);
	}

	/**
	 * Adds the action to the array of actions.
	 * 
	 * @param obj
	 *            an action object. Must not be null.
	 */
	public void add(Action obj) throws CoderException
	{
		if (obj == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		actions.add(obj);
	}

	/**
	 * Returns the array of button shapes.
	 */
	public List<ButtonShape> getButtonShapes()
	{
		return shapes;
	}

	/**
	 * Returns the array of actions that will be executed when the button is
	 * clicked and released.
	 */
	public List<Action> getActions() throws CoderException
	{
		return actions;
	}

	/**
	 * Sets the array of button shapes defined for this button.
	 * 
	 * @param anArray
	 *            an array of Button objects. Must not be null.
	 */
	public void setButtonShapes(List<ButtonShape> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
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
	public DefineButton copy()
	{
		return new DefineButton(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, identifier, shapes, actions);
	}

	public int prepareToEncode(final SWFEncoder coder)
	{
		length = 2;
		
		for (ButtonShape shape : shapes) {
			length += shape.prepareToEncode(coder);
		}

		length += 1;
		
		for (Action action : actions) {
			length += action.prepareToEncode(coder);
		}

		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder) throws CoderException
	{
		start = coder.getPointer();

		if (length >= 63) {
			coder.writeWord((Types.DEFINE_BUTTON << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.DEFINE_BUTTON << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);
		coder.writeWord(identifier, 2);
		
		for (ButtonShape shape : shapes) {
			shape.encode(coder);
		}

		coder.writeWord(0, 1);

		Iterator<Action> iAction = actions.iterator();
		
		while (iAction.hasNext()) {
			iAction.next().encode(coder);
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
