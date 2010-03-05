/*
 * NewFunction2.java
 * Transform
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.action;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.exception.StringSizeException;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/**
 * The NewFunction2 action is used to create a user-defined function with
 * optimisations to improve performance.
 *
 * <p>
 * NewFunction2 was added in Flash 7 to improve the performance of function
 * calls by allowing pre-defined variables such as <em>_root</em>,
 * <em>_parent</em>, <em>_global</em>, <em>super</em>, <em>this</em> and the
 * <em>arguments</em> passed to the function to be pre-loaded to a set of up to
 * 256 internal registers.
 * </p>
 *
 * <p>
 * The optimisation attribute is a compound code, containing a number of flags
 * that control which variables are pre-loaded:
 * </p>
 *
 * <table class="datasheet">
 * <tr>
 * <td valign="top">CreateSuper</td>
 * <td>Create and initialise the <em>super</em> variable with the parent class
 * of the function.</td>
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
 * <p>
 * The register numbers that the predefined variables are assigned to are fixed.
 * When specifying which of the functions arguments are also assigned to
 * registers it is important avoid these locations otherwise the variables will
 * be overwritten.
 * </p>
 *
 * <p>
 * User-defined functions are also used to create methods for user-defined
 * objects. The name of the function is omitted and the function definition is
 * assigned to a variable which allows it to be referenced at a alter time. See
 * the example below.
 * </p>
 *
 * <p>
 * The arguments supplied to the function can be referenced by the name supplied
 * in the arguments array.
 * </p>
 *
 * <p>
 * All the action objects added are owned by the function. They will be deleted
 * when the function definition is deleted.
 * </p>
 *
 * @see NewFunction
 */
//TODO(class)
public final class NewFunction2 implements Action {
    private static final String FORMAT = "NewFunction2: { name=%s; "
            + "registerCount=%d; optimizations=%s; arguments=%s; actions=%s }";

    /** TODO(method). */
    public enum Optimization {
        /** Create and initialised the predefined variable, <em>super</em>. */
        CREATE_SUPER,
        /** Create and initialised the predefined variable, <em>arguments</em>. */
        CREATE_ARGUMENTS,
        /** Create and initialised the predefined variable, <em>this</em>. */
        CREATE_THIS,
        /** Load the predefine variable, <em>this</em>, into register 1. */
        LOAD_THIS,
        /** Load the predefine variable, <em>arguments</em>, into register 2. */
        LOAD_ARGUMENTS,
        /** Load the predefine variable, <em>super</em>, into register 3. */
        LOAD_SUPER,
        /** Load the predefine variable, <em>_root</em>, into register 4. */
        LOAD_ROOT,
        /** Load the predefine variable, <em>_parent</em>, into register 5. */
        LOAD_PARENT,
        /** Load the predefine variable, <em>_global</em>, into register 6. */
        LOAD_GLOBAL;
    }

    private String name;
    private int registerCount;
    private int optimizations;
    private Map<String, Integer> arguments;
    private List<Action> actions;

    private transient int length;
    private transient int actionsLength;

    /**
     * Creates and initialises a NewFunction2 definition using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @param context
     *            a Context object used to manage the decoders for different
     *            type of object and to pass information on how objects are
     *            decoded.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    // TODO(optimise)
    public NewFunction2(final SWFDecoder coder, final Context context)
            throws CoderException {
        final SWFFactory<Action> decoder = context.getRegistry()
                .getActionDecoder();

        coder.readByte();
        length = coder.readWord(2, false);

        name = coder.readString();
        final int argumentCount = coder.readWord(2, false);
        registerCount = coder.readByte();
        optimizations = coder.readBits(16, false);

        int index;

        arguments = new LinkedHashMap<String, Integer>(argumentCount);
        actions = new ArrayList<Action>();

        for (int i = 0; i < argumentCount; i++) {
            index = coder.readByte();
            arguments.put(coder.readString(), index);
        }

        actionsLength = coder.readWord(2, false);
        length += actionsLength;

        final int end = coder.getPointer() + (actionsLength << 3);
        actions = new ArrayList<Action>();

        while (coder.getPointer() < end) {
            actions.add(decoder.getObject(coder, context));
        }
    }

    /**
     * Creates a NewFunction with the specified name, argument names and actions
     * to be executed. The order of the Strings in the argument array indicate
     * the order in which the values will be popped off the stack when the
     * function is executed. The first argument is popped from the stack first.
     *
     * @param name
     *            the name of the function. Can be an empty string if the
     *            function is anonymous.
     * @param arguments
     *            an array of RegisterVariable objects listing the names of the
     *            arguments and the registers they are assigned to.
     * @param actions
     *            the array of actions that define the operation performed by
     *            the function.
     */
    public NewFunction2(final String name,
            final Map<String, Integer> arguments, final List<Action> actions) {
        setName(name);
        setArguments(arguments);
        setActions(actions);
    }

    /**
     * Creates and initialises a NewFunction2 action using the values
     * copied from another NewFunction2 action.
     *
     * @param object
     *            a NewFunction2 action from which the values will be
     *            copied. References to immutable objects will be shared.
     */
    public NewFunction2(final NewFunction2 object) {
        name = object.name;
        registerCount = object.registerCount;
        optimizations = object.optimizations;

        arguments = new LinkedHashMap<String, Integer>(object.arguments);
        actions = new ArrayList<Action>(object.actions.size());

        for (final Action action : object.actions) {
            actions.add(action.copy());
        }
    }

    /**
     * Adds the name of an argument to the array of argument names.
     *
     * @param anArgument
     *            the name of an argument passed to the NewFunction object. Must
     *            not be null.
     */
    public NewFunction2 add(final String anArgument) {
        if (anArgument == null) {
            throw new NullPointerException();
        }
        if (anArgument.length() == 0) {
            throw new StringSizeException(0, Short.MAX_VALUE, 0);
        }
        arguments.put(anArgument, 0);
        return this;
    }

    /**
     * Adds the action object to the array of actions.
     *
     * @param anAction
     *            an object belonging to a class derived from Action. Must not
     *            be null.
     */
    public NewFunction2 add(final Action anAction) {
        if (anAction == null) {
            throw new NullPointerException();
        }
        actions.add(anAction);
        return this;
    }

    /**
     * Returns the name of the function.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the function. The name may be an empty string when
     * defining methods.
     *
     * @param aString
     *            the name of the function or null for a method. Must not be
     *            null.
     */
    public void setName(final String aString) {
        if (aString == null) {
            throw new NullPointerException();
        }
        name = aString;
    }

    /**
     * Returns the number of registers to allocate for function variables.
     */
    public int getRegisterCount() {
        return registerCount;
    }

    /**
     * Sets the number of registers to allocate for function variables. Up to
     * 255 registers may be allocated for each function.
     *
     * @param count
     *            the number of registers to allocate. Must be in the range
     *            0..255.
     */
    public void setRegisterCount(final int count) {
        if ((count < 1) || (count > 255)) {
            throw new IllegalArgumentRangeException(1, 255, count);
        }
        registerCount = count;
    }

    /** TODO(method). */
    public Set<Optimization> getOptimizations() {
        final Set<Optimization> set = EnumSet.noneOf(Optimization.class);

        if ((optimizations & 4) != 0) {
            set.add(Optimization.CREATE_SUPER);
        }
        if ((optimizations & 16) != 0) {
            set.add(Optimization.CREATE_ARGUMENTS);
        }
        if ((optimizations & 64) != 0) {
            set.add(Optimization.CREATE_THIS);
        }
        if ((optimizations & 128) != 0) {
            set.add(Optimization.LOAD_THIS);
        }
        if ((optimizations & 32) != 0) {
            set.add(Optimization.LOAD_ARGUMENTS);
        }
        if ((optimizations & 8) != 0) {
            set.add(Optimization.LOAD_SUPER);
        }
        if ((optimizations & 2) != 0) {
            set.add(Optimization.LOAD_ROOT);
        }
        if ((optimizations & 1) != 0) {
            set.add(Optimization.LOAD_PARENT);
        }
        if ((optimizations & 32768) != 0) {
            set.add(Optimization.LOAD_GLOBAL);
        }
        return set;
    }

    /** TODO(method). */
    public void setOptimizations(final Set<Optimization> optimizations) {
        for (final Optimization opt : optimizations) {
            switch (opt) {
            case CREATE_SUPER:
                this.optimizations |= 4;
                break;
            case CREATE_ARGUMENTS:
                this.optimizations |= 16;
                break;
            case CREATE_THIS:
                this.optimizations |= 64;
                break;
            case LOAD_THIS:
                this.optimizations |= 128;
                break;
            case LOAD_ARGUMENTS:
                this.optimizations |= 32;
                break;
            case LOAD_SUPER:
                this.optimizations |= 8;
                break;
            case LOAD_ROOT:
                this.optimizations |= 2;
                break;
            case LOAD_PARENT:
                this.optimizations |= 1;
                break;
            case LOAD_GLOBAL:
                this.optimizations |= 32768;
                break;
            default:
                throw new IllegalArgumentException();
            }
         }
    }

    /**
     * Returns the array of RegisterVariables that define the function arguments
     * and whether they are assigned to internal registers or to local variables
     * in memory.
     */
    public Map<String, Integer> getArguments() {
        return arguments;
    }

    /**
     * TODO(method).
     */
    public void setArguments(final Map<String, Integer> map) {
        if (map == null) {
            throw new NullPointerException();
        }
        arguments = map;
    }

    /**
     * Returns the actions executed by the function.
     */
    public List<Action> getActions() {
        return actions;
    }

    /**
     * Sets the actions.
     *
     * @param anArray
     *            the array of actions that define the operation performed by
     *            the function. Must not be null.
     */
    public void setActions(final List<Action> anArray) {
        if (anArray == null) {
            throw new NullPointerException();
        }
        actions = anArray;
    }

    /** {@inheritDoc} */
    public NewFunction2 copy() {
        return new NewFunction2(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, name, registerCount, optimizations,
                arguments, actions);
    }

    // TODO(optimise)
    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        length = 5 + coder.strlen(name);

        for (final String arg : arguments.keySet()) {
            length += arg.getBytes().length + 2;
        }

        length += 2;

        actionsLength = actions.isEmpty() ? 1 : 0;

        for (final Action action : actions) {
            actionsLength += action.prepareToEncode(coder, context);
        }

        length += actionsLength;

        return 3 + length;
    }

    // TODO(optimise)
    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeWord(ActionTypes.NEW_FUNCTION_2, 1);
        coder.writeWord(length - actionsLength, 2);

        coder.writeString(name);
        coder.writeWord(arguments.size(), 2);
        coder.writeByte(registerCount);
        coder.writeBits(optimizations, 16);

        for (final String arg : arguments.keySet()) {
            coder.writeByte(arguments.get(arg));
            coder.writeString(arg);
        }

        coder.writeWord(actionsLength, 2);

        for (final Action action : actions) {
            action.encode(coder, context);
        }

        if (actions.isEmpty()) {
            coder.writeByte(0);
        }
    }
}
