/*
 * With.java
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

package com.flagstone.transform.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;


//TODO(doc) Review
/**
 * With is a stack-based action and supports the <em>with</em> statement from 
 * the ActionScript language.
 * 
 * <pre>
 * with(_root.movieClip) {
 *     gotoAndPlay(&quot;frame&quot;);
 * }
 * </pre>
 * 
 * <p>The action temporarily selects the movie clip allowing the following
 * stream of actions to control the movie clip's time-line.</p>
 */
public final class With implements Action
{
	private static final String FORMAT = "With: { actions=%s }";
	
	private List<Action> actions;

	private transient int length;

	//TODO(doc)
	public With(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		coder.readByte();
		coder.readWord(2, false);
		length = coder.readWord(2, false);

		int end = coder.getPointer() + (length << 3);

		actions = new ArrayList<Action>();

		while (coder.getPointer() < end) {
			actions.add(context.actionOfType(coder, context));
		}
	}

	/**
	 * Creates a With object with an array of actions.
	 * 
	 * @param anArray
	 *            the array of action objects. Must not be null.
	 */
	public With(List<Action> anArray)
	{
		setActions(anArray);
	}
	
	//TODO(doc)
	public With(With object)
	{
		actions = new ArrayList<Action>(object.actions.size());
		
		for (Action action : object.actions) {
			actions.add(action.copy());
		}
	}

	/**
	 * Adds the action object to the array of actions.
	 * 
	 * @param anAction
	 *            an object belonging to a class derived from Action.
	 *            Must not be null.
	 */
	public With add(Action anAction)
	{
		if (anAction == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		actions.add(anAction);
		return this;
	}

	/**
	 * Get the array of actions that are executed for the movie clip target.
	 */
	public List<Action> getActions()
	{
		return actions;
	}

	/**
	 * Set the array of actions that will be executed for the movie clip target.
	 * 
	 * @param anArray
	 *            the array of action objects. Must not be null.
	 */
	public void setActions(List<Action> anArray)
	{
		actions = anArray;
	}

	public With copy() 
	{
		return new With(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, actions);
	}

	//TODO(optimise) Replace iterator with foreach loop
	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		length = 2;

		Iterator<Action> iAction = actions.iterator();
		
		while (iAction.hasNext()) {
			length += iAction.next().prepareToEncode(coder, context);
		}

		return 3 + length;
	}

	//TODO(optimise) Replace iterator with foreach loop
	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		coder.writeWord(ActionTypes.WITH, 1);
		coder.writeWord(2, 2);
		coder.writeWord(length-2, 2);

		Iterator<Action> iAction = actions.iterator();
		
		while (iAction.hasNext()) {
			iAction.next().encode(coder, context);
		}
	}
}
