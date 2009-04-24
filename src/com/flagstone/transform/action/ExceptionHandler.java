/*
 * ExceptionHandler.java
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

package com.flagstone.transform.action;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.Action;
import com.flagstone.transform.coder.ActionTypes;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;

/**
 * The ExceptionHandler class is used to represent try..catch blocks in
 * Actionscript so exceptions can be thrown and handled when executing a series
 * of actions.
 * 
 * <p>
 * The thrown object can be assigned to either one of the Flash Player's 256
 * internal registers or to a variable in memory. If a register number is set
 * the variable name is set to the empty string. Similarly if a variable name is
 * set then the register number is set to zero.
 * </p>
 * 
 * <p>
 * The ExceptionHandler class contains three arrays of actions supporting the
 * standard syntax for an exception with try, catch and finally blocks. Both the
 * catch and finally blocks are optional when defining an exception, the
 * corresponding arguments in constructors and methods may be set to empty
 * arrays.
 * </p>
 */
public final class ExceptionHandler implements Action {
	private static final String FORMAT = "ExceptionHandler: { variable=%s; register=%d try=%s; catch=%s; final=%s }";

	private int register;
	private String variable;
	private List<Action> tryActions;
	private List<Action> catchActions;
	private List<Action> finalActions;

	private transient int length;
	private transient int tryLength;
	private transient int catchLength;
	private transient int finalLength;

	// TODO(doc)
	// TODO(optimise)
	public ExceptionHandler(final SWFDecoder coder, final Context context)
			throws CoderException {
		coder.readByte();
		length = coder.readWord(2, false);

		coder.readBits(5, false);
		boolean containsVariable = coder.readBits(1, false) == 1;
		boolean containsFinal = coder.readBits(1, false) == 1;
		boolean containsCatch = coder.readBits(1, false) == 1;

		tryLength = coder.readWord(2, false);
		catchLength = coder.readWord(2, false);
		finalLength = coder.readWord(2, false);

		if (length == 8) {
			length += tryLength;
			length += catchLength;
			length += finalLength;
		}

		if (containsVariable) {
			variable = coder.readString();
		} else {
			register = coder.readByte();
		}

		tryActions = new ArrayList<Action>();
		catchActions = new ArrayList<Action>();
		finalActions = new ArrayList<Action>();

		int end = coder.getPointer() + (tryLength << 3);
		SWFFactory<Action> decoder = context.getRegistry().getActionDecoder();

		while (coder.getPointer() < end) {
			tryActions.add(decoder.getObject(coder, context));
		}

		if (containsCatch) {
			end = coder.getPointer() + (catchLength << 3);
			while (coder.getPointer() < end) {
				catchActions.add(decoder.getObject(coder, context));
			}
		}

		if (containsFinal) {
			end = coder.getPointer() + (finalLength << 3);
			while (coder.getPointer() < end) {
				finalActions.add(decoder.getObject(coder, context));
			}
		}
	}

	/**
	 * Creates a new exception handler with the thrown object assigned to a
	 * local variable.
	 * 
	 * @param name
	 *            the name of the variable that the thrown object will be
	 *            assigned to. Must not be null.
	 * @param tryArray
	 *            actions that will be executed in the try block of the
	 *            exception. Must not be null.
	 * @param catchArray
	 *            actions that will be executed in the catch block of the
	 *            exception, if one is defined. This may be an empty array if no
	 *            catch block is required - the exception will be handled by
	 *            another catch block higher in the exception tree.
	 * @param finallyArray
	 *            actions that will be executed in the finally block of the
	 *            exception, if one is defined. This may be an empty array if no
	 *            finally block is required.
	 */

	public ExceptionHandler(String name, List<Action> tryArray,
			List<Action> catchArray, List<Action> finallyArray) {
		setVariable(name);
		setTryActions(tryArray);
		setCatchActions(catchArray);
		setFinalActions(finallyArray);
	}

	/**
	 * Constructs a new exception handler with the thrown object assigned to one
	 * of the Flash Player's internal registers.
	 * 
	 * @param index
	 *            the number of the register that the thrown object will be
	 *            assigned to. Must be in the range 0..255.
	 * @param tryArray
	 *            actions that will be executed in the try block of the
	 *            exception. Must not be null.
	 * @param catchArray
	 *            actions that will be executed in the catch block of the
	 *            exception, if one is defined. This may be an empty array if no
	 *            catch block is required - the exception will be handled by
	 *            another catch block higher in the exception tree.
	 * @param finallyArray
	 *            actions that will be executed in the finally block of the
	 *            exception, if one is defined. This may be an empty array is no
	 *            finally block is required.
	 */
	public ExceptionHandler(int index, List<Action> tryArray,
			List<Action> catchArray, List<Action> finallyArray) {
		setRegister(index);
		setTryActions(tryArray);
		setCatchActions(catchArray);
		setFinalActions(finallyArray);
	}

	public ExceptionHandler(ExceptionHandler object) {
		variable = object.variable;
		register = object.register;

		tryActions = new ArrayList<Action>(object.tryActions.size());

		for (Action action : object.tryActions) {
			tryActions.add(action.copy());
		}

		catchActions = new ArrayList<Action>(object.catchActions.size());

		for (Action action : object.catchActions) {
			catchActions.add(action.copy());
		}

		finalActions = new ArrayList<Action>(object.finalActions.size());

		for (Action action : object.finalActions) {
			finalActions.add(action.copy());
		}
	}

	/**
	 * Adds the action object to the array of actions for the try block.
	 * 
	 * @param anAction
	 *            an action. Must not be null.
	 */
	public ExceptionHandler addToTry(Action anAction) {
		if (anAction == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}

		if (tryActions == null) {
			tryActions = new ArrayList<Action>();
		}

		tryActions.add(anAction);
		return this;
	}

	/**
	 * Adds the action object to the array of actions for the catch block.
	 * 
	 * @param anAction
	 *            an action. Must not be null.
	 */
	public ExceptionHandler addToCatch(Action anAction) {
		if (anAction == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}

		if (catchActions == null) {
			catchActions = new ArrayList<Action>();
		}

		catchActions.add(anAction);
		return this;
	}

	/**
	 * Adds the action object to the array of actions for the finally block.
	 * 
	 * @param anAction
	 *            an action. Must not be null.
	 */
	public ExceptionHandler addToFinally(Action anAction) {
		if (anAction == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}

		if (catchActions == null) {
			finalActions = new ArrayList<Action>();
		}

		finalActions.add(anAction);
		return this;
	}

	/**
	 * Returns the name of the variable which the exception object is assigned
	 * to.
	 * 
	 * @return the name of the function. Returns null if the exception object
	 *         will be assigned to a register.
	 */
	public String getVariable() {
		return variable;
	}

	/**
	 * Sets the name of the variable that the exception object is assigned to.
	 * 
	 * @param name
	 *            the name of the variable. May be null if the exception object
	 *            will be signed to a register, but not null.
	 */
	public void setVariable(String name) {

		if (name.length() == 0) {
			throw new IllegalArgumentException(Strings.STRING_CANNOT_BE_EMPTY);
		}
		variable = name;
		register = 0;
	}

	/**
	 * Returns the index of the register that the exception object is assigned
	 * to.
	 * 
	 * @return the number of register. Returns 0 if the exception object will be
	 *         assigned to a local variable.
	 */
	public int getRegister() {
		return register;
	}

	/**
	 * Sets the index of the register that the exception object is assigned to.
	 * 
	 * @param index
	 *            the number of the register in the range 0..255. If the index
	 *            is 0 then the exception object will be assigned to a local
	 *            variable.
	 */
	public void setRegister(int index) {
		if (index < 0 || index > 255) {
			throw new IllegalArgumentException(Strings.REGISTER_OUT_OF_RANGE);
		}
		register = index;
		variable = null;
	}

	/**
	 * Returns the array of actions executed in the try block.
	 * 
	 * @return the array of actions for the try block.
	 */
	public List<Action> getTryActions() {
		return tryActions;
	}

	/**
	 * Sets the array of actions executed in the try block.
	 * 
	 * @param array
	 *            the array of actions for the try block. Must not be null.
	 */
	public void setTryActions(List<Action> array) {
		if (array == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		tryActions = array;
	}

	/**
	 * Returns the array of actions executed in the catch block.
	 * 
	 * @return the array of actions for the catch block.
	 */
	public List<Action> getCatchActions() {
		return catchActions;
	}

	/**
	 * Sets the array of actions executed in the catch block.
	 * 
	 * @param array
	 *            the array of actions for the catch block. May be empty if no
	 *            catch block is defined but must not be null.
	 */
	public void setCatchActions(List<Action> array) {
		if (array == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		catchActions = array;
	}

	/**
	 * Returns the array of actions executed in the finally block.
	 * 
	 * @return the array of actions for the finally block.
	 */
	public List<Action> getFinalActions() {
		return finalActions;
	}

	/**
	 * Sets the array of actions executed in the final block.
	 * 
	 * @param array
	 *            the array of actions for the final block. May be empty if no
	 *            finally block is defined but must not be null.
	 */
	public void setFinalActions(List<Action> array) {
		if (array == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		finalActions = array;
	}

	public ExceptionHandler copy() {
		return new ExceptionHandler(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, variable, register, tryActions,
				catchActions, finalActions);
	}

	// TODO(optimise)
	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		length = 7;
		length += (variable != null) ? coder.strlen(variable) : 1;

		tryLength = tryActions.isEmpty() ? 1 : 0;
		catchLength = 0;
		finalLength = 0;

		for (Action action : tryActions) {
			tryLength += action.prepareToEncode(coder, context);
		}

		for (Action action : catchActions) {
			catchLength += action.prepareToEncode(coder, context);
		}

		for (Action action : finalActions) {
			finalLength += action.prepareToEncode(coder, context);
		}

		length += tryLength;
		length += catchLength;
		length += finalLength;

		return 3 + length;
	}

	// TODO(optimise)
	public void encode(final SWFEncoder coder, final Context context)
			throws CoderException {
		coder.writeByte(ActionTypes.EXCEPTION_HANDLER);
		coder.writeWord(length, 2);
		coder.writeBits(0, 5);
		coder.writeBits(variable != null ? 1 : 0, 1);
		coder.writeBits(finalLength > 0 ? 1 : 0, 1);
		coder.writeBits(catchLength > 0 ? 1 : 0, 1);

		coder.writeWord(tryLength, 2);
		coder.writeWord(catchLength, 2);
		coder.writeWord(finalLength, 2);

		if (variable.length() > 0) {
			coder.writeString(variable);
		} else {
			coder.writeByte(register);
		}

		for (Action action : tryActions) {
			action.encode(coder, context);
		}

		if (tryActions.isEmpty()) {
			coder.writeByte(0);
		}

		for (Action action : catchActions) {
			action.encode(coder, context);
		}

		for (Action action : finalActions) {
			action.encode(coder, context);
		}
	}
}
