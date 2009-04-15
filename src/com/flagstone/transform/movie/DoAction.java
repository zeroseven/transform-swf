/*
 * DoAction.java
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

package com.flagstone.transform.movie;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.action.Action;
import com.flagstone.transform.movie.action.ActionData;

/**
 * DoAction is used to add a set of actions to a frame in a movie. The actions
 * will be triggered when the Flash Player executes the ShowFrame command.
 * 
 * <p>
 * Only one DoAction object can be used to specify the actions for a given
 * frame. If more than one DoAction object is added in a single frame only the
 * actions contained in the last DoAction object (before the ShowFrame object)
 * will be executed when the frame is displayed. The other DoAction objects will
 * be ignored.
 * </p>
 * 
 * <p>
 * DoAction can only be used in movies that contain Actionscript 1.x or
 * Actionscript 2.x code. For Actionscript 3.0 use the DoABC class.
 * </p>
 * 
 * @see ShowFrame
 * @see DoABC
 */
public final class DoAction implements MovieTag {

	private static final String FORMAT = "DoAction: { actions=%s }";

	private List<Action> actions;

	private transient int start;
	private transient int end;
	private transient int length;


	public DoAction(final SWFDecoder coder) throws CoderException {

		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;

		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}

		end = coder.getPointer() + (length << 3);

		actions = new ArrayList<Action>();

		if (coder.getContext().isDecodeActions()) {

			while (coder.getPointer() < end) {
				actions.add(coder.actionOfType(coder));
			}
		} else {
			actions.add(new ActionData(length, coder));
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
	/**
	 * Creates a new DoAction class with an empty array.
	 */
	public DoAction() {
		actions = new ArrayList<Action>();
	}

	/**
	 * Creates a DoAction object with an array of actions.
	 * 
	 * @param anArray
	 *            the array of action objects. Cannot be null.
	 */
	public DoAction(final List<Action> anArray) {
		setActions(anArray);
	}

	/**
	 * Creates a DoAction object with a copy of the actions from another
	 * DoAction object.
	 * 
	 * @param object
	 *            a DoAction object to copy.
	 */
	public DoAction(final DoAction object) {
		actions = new ArrayList<Action>(object.actions.size());
		for (Action action : object.actions) {
			actions.add(action.copy());
		}
	}

	/**
	 * Adds the action object to the array of actions. If the object already
	 * contains encoded actions then they will be deleted.
	 * 
	 * @param anAction
	 *            an object belonging to a class derived from Action. The
	 *            argument cannot be null.
	 */
	public DoAction add(final Action anAction) {
		if (anAction == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		actions.add(anAction);
		return this;
	}

	/**
	 * Returns the array of actions that are executed when the frame is
	 * displayed.
	 * 
	 * @return the array of action objects.
	 */
	public List<Action> getActions() {
		return actions;
	}

	/**
	 * Set the array of actions that will be executed when the next ShowFrame
	 * tag is executed by the Flash Player. If the object already contains
	 * encoded actions then they will be deleted.
	 * 
	 * @param anArray
	 *            the array of action objects. May be empty but cannot be null.
	 */
	public void setActions(final List<Action> anArray) {
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		actions = anArray;
	}

	public DoAction copy() {
		return new DoAction(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, actions.toString());
	}

	public int prepareToEncode(final SWFEncoder coder) {
		length = 0;

		for (Action action : actions) {
			length += action.prepareToEncode(coder);
		}
		
		if (actions.isEmpty()) {
			length++;
		}

		return (length > 62 ? 6 : 2) + length;
	}

	public void encode(final SWFEncoder coder) throws CoderException {

		start = coder.getPointer();

		if (length > 62) {
			coder.writeWord((Types.DO_ACTION << 6) | 0x3F, 2);
			coder.writeWord(length, 4);
		} else {
			coder.writeWord((Types.DO_ACTION << 6) | length, 2);
		}
		end = coder.getPointer() + (length << 3);

		for (Action action : actions) {
			action.encode(coder);
		}
		
		if (actions.isEmpty()) {
			coder.writeByte(0);
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}