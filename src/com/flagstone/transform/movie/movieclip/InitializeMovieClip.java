/*
 * Initialize.java
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
import java.util.Iterator;
import java.util.List;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.DoAction;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.Types;
import com.flagstone.transform.movie.action.Action;
import com.flagstone.transform.movie.action.ActionData;


/**
 * Initialize is used to specify a sequence of actions that are executed to
 * initialise a movie clip before it is displayed.
 * 
 * <p>Initialize implements the #initclip pragma defined in the ActionScript 
 * language.</p>
 * 
 * <p>Unlike the DoAction class which specifies the actions that are executed
 * when a particular frame is displayed the actions contained in an Initialize
 * object are executed only once, regardless of where the object is included in
 * a movie. If a frame containing the Initialize object is played again the
 * actions are skipped. Also there can only be one Initialize object for each
 * movie clip defined in the movie.
 * </p>

 * @see DoAction
 */
public final class InitializeMovieClip implements MovieTag
{
	private static final String FORMAT = "Initialize: { identifier=%d; actions=%s }";
	
	protected int identifier;
	protected List<Action> actions;
	
	private transient int start;
	private transient int end;
	private transient int length;

	public InitializeMovieClip(final SWFDecoder coder) throws CoderException
	{
		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		end = coder.getPointer() + (length << 3);

		identifier = coder.readWord(2, false);
		actions = new ArrayList<Action>();

		if (coder.getContext().isDecodeActions()) {
			
			while (coder.getPointer() < end) {
				actions.add(coder.actionOfType(coder));
			}
		} else {
			actions.add(new ActionData(length-2, coder));
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	/**
	 * Creates a Initialize object that will initialise the movie clip
	 * with the specified identifier with the actions in the array.
	 * 
	 * @param uid
	 *            the identifier of the movie clip to initialise. Must be in the 
	 *            range 1..65535.
	 * @param anArray
	 *            the array of action objects. Must not be null.
	 */
	public InitializeMovieClip(int uid, List<Action> anArray)
	{
		setIdentifier(uid);
		setActions(anArray);
	}
	
	public InitializeMovieClip(InitializeMovieClip object) {
		identifier = object.identifier;
		
		actions = new ArrayList<Action>(object.actions.size());
		
		for (Action action : object.actions) {
			actions.add(action.copy());
		}
	}

	/**
	 * Returns the identifier of the movie clip that will be initialised.
	 */
	public int getIdentifier()
	{
		return identifier;
	}

	/**
	 * Sets the identifier of the movie clip that will be initialised.
	 * 
	 * @param aNumber
	 *            the identifier of the movie clip. The value must be in the
	 *            range 1..65535.
 	 */
	public void setIdentifier(int aNumber)
	{
		if (aNumber < 1 || aNumber > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = aNumber;
	}

	/**
	 * Adds the action object to the array of actions.
	 * 
	 * @param anAction
	 *            an object belonging to a class derived from Action.
	 *            Must not be null.
	 */
	public void add(Action anAction)
	{
		if (anAction == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		actions.add(anAction);
	}

	/**
	 * Get the array of actions that are used to initialise the movie clip.
	 */
	public List<Action> getActions()
	{
		return actions;
	}

	/**
	 * Set the array of actions of the movie clip that will be initialised
	 * 
	 * @param anArray
	 *            the array of action objects. Must not be null.
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
	public InitializeMovieClip copy() 
	{
		return new InitializeMovieClip(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, identifier, actions);
	}

	public int prepareToEncode(final SWFEncoder coder)
	{
		length = 2;

		Iterator<Action> iAction = actions.iterator();
		
		while (iAction.hasNext()) {
			length += iAction.next().prepareToEncode(coder);
		}

		return (length > 62 ? 6:2) + length;
	}

	public void encode(final SWFEncoder coder) throws CoderException
	{
		start = coder.getPointer();

		if (length > 62) {
			coder.writeWord((Types.INITIALIZE << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.INITIALIZE << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);

		coder.writeWord(identifier, 2);

		for (Action action : actions) {
			action.encode(coder);
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}