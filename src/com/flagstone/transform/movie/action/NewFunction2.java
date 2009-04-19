/*
 * NewFunction2.java
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

package com.flagstone.transform.movie.action;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Strings;


//TODO(doc) Review
/**
 * The NewFunction2 action is used to create a user-defined function with 
 * optimisations to improve performance.
 * 
 * <p>NewFunction2 was added in Flash 7 to improve the performance of function 
 * calls by allowing pre-defined variables such as <em>_root</em>, <em>_parent</em>, 
 * <em>_global</em>, <em>super</em>, <em>this</em> and the <em>arguments</em> 
 * passed to the function to be pre-loaded to a set of up to 256 internal 
 * registers.</p>
 * 
 * <p>The optimisation attribute is a compound code, containing a number of flags
 * that control which variables are pre-loaded:</p>
 * 
 * <table class="datasheet">
 * <tr>
 * <td valign="top">CreateSuper</td>
 * <td>Create and initialise the <em>super</em> variable with the parent
 * class of the function.</td>
 * </tr>
 * <tr>
 * <td valign="top">CreateArguments</td>
 * <td>Create the <em>arguments</em> variable which contains the arguments
 * passed to the function.</td>
 * </tr>
 * <tr>
 * <td valign="top">CreateThis</td>
 * <td>Create the and initialise the <em>this</em> variable with the object.</td>
 * </tr>
 * <tr>
 * <td valign="top">LoadThis</td>
 * <td>Pre-load the <em>this</em> variable into register number 1.</td>
 * </tr>
 * <tr>
 * <td valign="top">LoadArguments</td>
 * <td>Pre-load the <em>parent</em> variable into register number 2.</td>
 * </tr>
 * <tr>
 * <td valign="top">LoadSuper</td>
 * <td>Pre-load the <em>super</em> variable into register number 3.</td>
 * </tr>
 * <tr>
 * <td valign="top">LoadRoot</td>
 * <td>Pre-load the <em>_root</em> variable into register number 4.</td>
 * </tr>
 * <tr>
 * <td valign="top">LoadParent</td>
 * <td>Pre-load the <em>_parent</em> variable into register number 5.</td>
 * </tr>
 * <tr>
 * <td valign="top">LoadGlobal</td>
 * <td>Pre-load the <em>_global</em> variable into register number 5.</td>
 * </tr>
 * </table>
 * 
 * <p>The register numbers that the predefined variables are assigned to are fixed. 
 * When specifying which of the functions arguments are also assigned to registers 
 * it is important avoid these locations otherwise the variables will be 
 * overwritten.</p>
 * 
 * <p>User-defined functions are also used to create methods for user-defined
 * objects. The name of the function is omitted and the function definition is
 * assigned to a variable which allows it to be referenced at a alter time. See
 * the example below.
 * </p>
 * 
 * <p>The arguments supplied to the function can be referenced by the name supplied
 * in the arguments array.
 * </p>
 * 
 * <p>All the action objects added are owned by the function. They will be deleted
 * when the function definition is deleted.
 * </p>
 * 
 * @see NewFunction
 */
//TODO(api) replace constructors with factory methods for functions and methods.
public final class NewFunction2 implements Action
{
	private static final String FORMAT = "NewFunction2: { name=%s; registerCount=%d; optimizations=%s; arguments=%s; actions=%s }";

	//TODO(doc)
	public enum Optimization {
		/** Create and initialised the predefined variable, <em>super</em>. */
		CREATE_SUPER(4),
		/** Create and initialised the predefined variable, <em>arguments</em>. */
		CREATE_ARGUMENTS(16),
		/** Create and initialised the predefined variable, <em>this</em>. */
		CREATE_THIS(64),
		/** Load the predefine variable, <em>this</em>, into register 1. */
		LOAD_THIS(128),
		/** Load the predefine variable, <em>arguments</em>, into register 2. */
		LOAD_ARGUMENTS(32),
		/** Load the predefine variable, <em>super</em>, into register 3. */
		LOAD_SUPER(8),
		/** Load the predefine variable, <em>_root</em>, into register 4. */
		LOAD_ROOT(2),
		/** Load the predefine variable, <em>_parent</em>, into register 5. */
		LOAD_PARENT(1),
		/** Load the predefine variable, <em>_global</em>, into register 6. */
		LOAD_GLOBAL(32768);

		private static final Map<Integer,Optimization>table 
			= new LinkedHashMap<Integer,Optimization>();

		static {
			for (Optimization opt: values()) {
				table.put(opt.value, opt);
			}
		}
		
		public static Optimization fromInt(int type) {
			return table.get(type);
		}

		private final int value;

		private Optimization(int value) {
			this.value = value;
		}
		
		//TODO(doc)
		public int getValue()
		{
			return value;
		}
	}

	private String name;
	private int registerCount;
	private int optimizations;
	private Map<String,Integer> arguments;
	private List<Action> actions;

	private transient int length;
	private transient int actionsLength;

	//TODO(doc)
	//TODO(optimise)
	public NewFunction2(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		coder.readByte();
		length = coder.readWord(2, false);

		name = coder.readString();
		int argumentCount = coder.readWord(2, false);
		registerCount = coder.readByte();
		optimizations = coder.readBits(16, false);

		int index;
		
		arguments = new LinkedHashMap<String,Integer>(argumentCount);
		actions = new ArrayList<Action>();

		for (int i = 0; i < argumentCount; i++) 
		{
			index = coder.readByte();
			arguments.put(coder.readString(), index);
		}

		actionsLength = coder.readWord(2, false);
		length += actionsLength;
		
		int end = coder.getPointer() + (actionsLength << 3);
		
		actions = new ArrayList<Action>();

		while (coder.getPointer() < end) {
			actions.add(context.actionOfType(coder, context));
		}
	}

	/**
	 * Creates a NewFunction with the specified name, argument names and
	 * actions to be executed. The order of the Strings in the argument array
	 * indicate the order in which the values will be popped off the stack when
	 * the function is executed. The first argument is popped from the stack
	 * first.
	 * 
	 * @param name
	 *            the name of the function.
	 * @param arguments
	 *            an array of RegisterVariable objects listing the names of the 
	 *            arguments and the registers they are assigned to.
	 * @param actions
	 *            the array of actions that define the operation performed by
	 *            the function.
	 */
	public NewFunction2(String name, Map<String,Integer> arguments, List<Action> actions)
	{
		setName(name);
		setArguments(arguments);
		setActions(actions);
	}

	/**
	 * Creates a anonymous NewFunction with the specified argument names
	 * and actions to be executed. Use this constructor when defining functions
	 * that will be assigned to object variables and used as methods.
	 * 
	 * @param arguments
	 *            an array of RegisterVariable objects listing the names of the 
	 *            arguments and the registers they are assigned to.
	 * @param actions
	 *            the array of actions that define the operation performed by
	 *            the function.
	 */
	public NewFunction2(Map<String,Integer> arguments, List<Action> actions)
	{
		name = "";
		setArguments(arguments);
		setActions(actions);
	}
	
	//TODO(doc)
	public NewFunction2(NewFunction2 object)
	{
		name = object.name;
		registerCount = object.registerCount;
		optimizations = object.optimizations;
		
		arguments = new LinkedHashMap<String,Integer>(object.arguments);
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
	 *            Must not be null.
	 */
	public NewFunction2 add(String anArgument)
	{
		if (anArgument == null || anArgument.length() == 0) {
			throw new IllegalArgumentException(Strings.STRING_NOT_SET);
		}
		arguments.put(anArgument, 0);
		return this;
	}

	/**
	 * Adds the action object to the array of actions.
	 * 
	 * @param anAction
	 *            an object belonging to a class derived from Action.
	 *            Must not be null.
	 */
	public NewFunction2 add(Action anAction)
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
	 * Sets the name of the function. The name may be an empty string when defining
	 * methods.
	 * 
	 * @param aString
	 *            the name of the function or null for a method. Must not be null.
	 */
	public void setName(String aString)
	{
		if (aString == null) {
			throw new IllegalArgumentException(Strings.STRING_CANNOT_BE_NULL);
		}
		name = aString;
	}

	/**
	 * Returns the number of registers to allocate for function variables.
	 */
	public int getRegisterCount()
	{
		return registerCount;
	}

	/**
	 * Sets the number of registers to allocate for function variables. Up to
	 * 256 registers may be allocated for each function.
	 * 
	 * @param count
	 *            the number of registers to allocate. Must be in the range 0..255.
	 */
	public void setRegisterCount(int count)
	{
		if (count < 0 || count > 256) {
			throw new IllegalArgumentException(Strings.REGISTER_OUT_OF_RANGE);
		}
		registerCount = count;
	}

	//TODO(doc)
	public Set<Optimization> getOptimizations()
	{
		Set<Optimization>set = EnumSet.allOf(Optimization.class);
		
		for (Iterator<Optimization>iter = set.iterator(); iter.hasNext();) {
			if ((optimizations & iter.next().getValue()) == 0) {
				iter.remove();
			}
		}
		return set;
	}

	//TODO(doc)
	public void setOptimizations(Set<Optimization> optimizations)
	{
		for (Optimization opt : optimizations) {
			this.optimizations |= opt.getValue();
		}
	}

	/**
	 * Returns the array of RegisterVariables that define the function arguments
	 * and whether they are assigned to internal registers or to local variables
	 * in memory.
	 */
	public Map<String,Integer> getArguments()
	{
		return arguments;
	}

	/**
	 * Sets the array of RegisterVariables that define the function arguments
	 * and whether they are assigned to internal registers or to local variables
	 * in memory.
	 * 
	 * @param anArray
	 *            an array of Strings listing the names of the arguments.
	 *            Must not be null.
	 */
	public void setArguments(Map<String,Integer> map)
	{
		if (map == null) {
			throw new IllegalArgumentException(Strings.ARRAY_CANNOT_BE_NULL);
		}
		arguments = map;
	}

	/**
	 * Returns the actions executed by the function.
	 */
	public List<Action> getActions()
	{
		return actions;
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

	public NewFunction2 copy() 
	{
		return new NewFunction2(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, name, registerCount, optimizations,
				arguments, actions);
	}

	//TODO(optimise)
	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		length = 5 + coder.strlen(name);

		for (String arg : arguments.keySet()) {
			length += arg.getBytes().length+2;
		}

		length += 2;

		Iterator<Action> iAction = actions.iterator();
		actionsLength = actions.isEmpty() ? 1:0;
		
		while (iAction.hasNext()) {
			actionsLength += iAction.next().prepareToEncode(coder, context);
		}
		
		length += actionsLength;

		return 3 + length;
	}

	//TODO(optimise)
	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		coder.writeWord(ActionTypes.NEW_FUNCTION_2, 1);
		coder.writeWord(length - actionsLength, 2);

		coder.writeString(name);
		coder.writeWord(arguments.size(), 2);
		coder.writeWord(registerCount, 1);
		coder.writeBits(optimizations, 16);

		for (String arg : arguments.keySet()) {
			coder.writeByte(arguments.get(arg));
			coder.writeString(arg);
		}

		coder.writeWord(actionsLength, 2);

		Iterator<Action> iAction = actions.iterator();
		
		while (iAction.hasNext()) {
			iAction.next().encode(coder, context);
		}
		
		if (actions.isEmpty()) {
			coder.writeByte(0);
		}
	}
}
