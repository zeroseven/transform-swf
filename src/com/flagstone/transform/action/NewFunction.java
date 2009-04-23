/*
 * NewFunction.java
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



//TODO(doc) Review
/**
 * The NewFunction action is used to create a user-defined function.
 * 
 * <p>User-defined functions are also used to create methods for user-defined
 * objects. The name of the function is omitted and the function definition is
 * assigned to a variable which allows it to be referenced at a later time. </p>
 * 
 * <p>In the actions which form the function body all the arguments passed to the
 * function can be referenced by the name supplied in the arguments array.</p>
 * 
 * <p>All the action objects created are owned by the function. They will be deleted
 * when the function definition is deleted.</p>
 * 
 * @see NewFunction2
 */
public final class NewFunction implements Action
{
	private static final String FORMAT = "NewFunction: { name=%s; arguments=%s; actions=%s }";
		
	private String name;
	private List<String> arguments;
	private List<Action> actions;

	private transient int length;
	private transient int actionsLength;

	//TODO(doc)
	public NewFunction(final SWFDecoder coder, final Context context) throws CoderException
	{
		arguments = new ArrayList<String>();
		actions = new ArrayList<Action>();

		coder.readByte();
		length = coder.readWord(2, false);
		name = coder.readString();

		int argumentCount = coder.readWord(2, false);

		arguments = new ArrayList<String>(argumentCount);
		actions = new ArrayList<Action>();

		if (argumentCount > 0)
		{
			for (int i = argumentCount; i > 0; i--) {
				arguments.add(coder.readString());
			}
		}

		actionsLength = coder.readWord(2, false);
		actions = new ArrayList<Action>();
		
		int end = coder.getPointer() + (actionsLength << 3);
		SWFFactory<Action>decoder = context.getRegistry().getActionDecoder();

		while (coder.getPointer() < end) {			
			actions.add(decoder.getObject(coder, context));
		}
	}

	/**
	 * Creates a NewFunction with the specified name, argument names and
	 * actions to be executed. The order of the Strings in the argument array
	 * indicate the order in which the values will be popped off the stack when
	 * the function is executed. The fist argument is popped from the stack
	 * first.
	 * 
	 * @param aString
	 *            the name of the function. May not be null.
	 * @param argumentArray
	 *            an array of Strings listing the names of the arguments.
	 * @param actionArray
	 *            the array of actions that define the operation performed by
	 *            the function.
	 */
	public NewFunction(String aString, List<String> argumentArray,
							List<Action> actionArray)
	{
		setName(aString);
		setArguments(argumentArray);
		setActions(actionArray);
	}

	/**
	 * Creates a anonymous NewFunction with the specified argument names
	 * and actions to be executed. Use this constructor when defining functions
	 * that will be assigned to object variables and used as methods.
	 * 
	 * @param argumentArray
	 *            an array of Strings listing the names of the arguments.
	 * @param actionArray
	 *            the array of actions that define the operation performed by
	 *            the function.
	 */
	public NewFunction(List<String> argumentArray, List<Action> actionArray)
	{
		name = "";
		setArguments(argumentArray);
		setActions(actionArray);
	}
	
	//TODO(doc)
	public NewFunction(NewFunction object) {
		name = object.name;
		
		arguments = new ArrayList<String>(object.arguments);
		actions = new ArrayList<Action>(object.actions.size());
		
		for (Action action : object.actions) {
			actions.add(action.copy());
		}
	}

	/**
	 * Adds the name of an argument to the array of argument names.
	 * 
	 * @param anArgument
	 *            the name of an argument passed to the NewFunction object.
	 *            Must not be null or an empty string.
	 */
	public NewFunction add(String anArgument)
	{
		if (anArgument == null || anArgument.length() == 0) {
			throw new IllegalArgumentException(Strings.STRING_NOT_SET);
		}
		arguments.add(anArgument);
		return this;
	}

	/**
	 * Adds the action object to the array of actions.
	 * 
	 * @param anAction
	 *            an object belonging to a class derived from Action.
	 *            Must not be null.
	 */
	public NewFunction add(Action anAction)
	{
		if (anAction == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		actions.add(anAction);
		return this;
	}

	/**
	 * Returns the name of the function.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the names of the function arguments.
	 */
	public List<String> getArguments()
	{
		return arguments;
	}

	/**
	 * Returns the actions.
	 */
	public List<Action> getActions()
	{
		return actions;
	}

	/**
	 * Sets the name of the function. The name may be an empty string when defining
	 * methods.
	 * 
	 * @param aString
	 *            the name of the function. Must not be null.
	 */
	public void setName(String aString)
	{
		if (aString == null) {
			throw new IllegalArgumentException(Strings.STRING_CANNOT_BE_NULL);
		}
		name = aString;
	}

	/**
	 * Sets the names of the function arguments.
	 * 
	 * @param anArray
	 *            an array of Strings listing the names of the arguments.
	 *            Must not be null.
 	 */
	public void setArguments(List<String> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		arguments = anArray;
	}

	/**
	 * Sets the actions.
	 * 
	 * @param anArray
	 *            the array of actions that define the operation performed by
	 *            the function. Must not be null.
	 */
	public void setActions(List<Action> anArray)
	{
		if (anArray == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		actions = anArray;
	}

	public NewFunction copy() 
	{
		return new NewFunction(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, name, arguments, actions);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context)
	{
		length = 2 + coder.strlen(name);

		//TODO(code) replace with foreach loop
		for (int i=0; i<arguments.size(); i++) {
			length += coder.strlen(arguments.get(i));
		}

		length += 2;
		actionsLength = 0;
		
		for (Action action : actions) {
			actionsLength += action.prepareToEncode(coder, context);
		}
		
		//TODO(doc) fix
		if (actions.isEmpty()) {
			actionsLength = 1;
		}

		length += actionsLength;

		//TODO(optimise) return 3 + length + actionslength
		return 3 + length;
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException
	{
		coder.writeWord(ActionTypes.NEW_FUNCTION, 1);
		//TODO(optimise) just write length, see above
		coder.writeWord(length - actionsLength, 2);

		coder.writeString(name);

		coder.writeWord(arguments.size(), 2);

		//TODO(code) replace with foreach loop
		for (int i=0; i<arguments.size(); i++) {
			coder.writeString(arguments.get(i));
		}

		coder.writeWord(actionsLength, 2);

		for (Action action : actions) {
			action.encode(coder, context);
		}
		
		//TODO(doc) fix
		if (actions.isEmpty()) {
			coder.writeByte(0);
		}
	}
}
