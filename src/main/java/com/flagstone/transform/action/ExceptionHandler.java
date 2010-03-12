/*
 * ExceptionHandler.java 
 * Transform 
 * 
 * Copyright (c) 2001-2010 Flagstone Software Ltd. All rights reserved. 
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
import java.util.List;


import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.exception.StringSizeException;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/**
 * The ExceptionHandler class is used to represent try..catch blocks in
 * Actionscript.
 *
 * <p>
 * When an exception is thrown, the object can be assigned to either one of the 
 * Flash Player's 256 internal registers or to a variable in memory.
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
    
    private static final String FORMAT = "ExceptionHandler: { variable=%s;"
    		+ " register=%d try=%s; catch=%s; final=%s }";

    private int register;
    private String variable;
    private List<Action> tryActions;
    private List<Action> catchActions;
    private List<Action> finalActions;

    private transient int length;
    private transient int tryLength;
    private transient int catchLength;
    private transient int finalLength;

    /**
     * Creates and initialises an ExceptionHandler action using values encoded
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
    public ExceptionHandler(final SWFDecoder coder, final Context context)
            throws CoderException {
        coder.readByte();
        // TODO(optimise)
        length = coder.readWord(2, false);

        coder.readBits(5, false);
        final boolean containsVariable = coder.readBits(1, false) == 1;
        final boolean containsFinal = coder.readBits(1, false) == 1;
        final boolean containsCatch = coder.readBits(1, false) == 1;

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
        final SWFFactory<Action> decoder = context.getRegistry()
                .getActionDecoder();

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

    public ExceptionHandler(final String name, final List<Action> tryArray,
            final List<Action> catchArray, final List<Action> finallyArray) {
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
    public ExceptionHandler(final int index, final List<Action> tryArray,
            final List<Action> catchArray, final List<Action> finallyArray) {
        setRegister(index);
        setTryActions(tryArray);
        setCatchActions(catchArray);
        setFinalActions(finallyArray);
    }

    /**
     * Creates and initialises an ExceptionHandler action using the values
     * copied from another ExceptionHandler.
     *
     * @param object
     *            an ExceptionHandler object from which the values will be
     *            copied. References to immutable objects will be shared.
     */
    public ExceptionHandler(final ExceptionHandler object) {
        variable = object.variable;
        register = object.register;

        tryActions = new ArrayList<Action>(object.tryActions.size());

        for (final Action action : object.tryActions) {
            tryActions.add(action.copy());
        }

        catchActions = new ArrayList<Action>(object.catchActions.size());

        for (final Action action : object.catchActions) {
            catchActions.add(action.copy());
        }

        finalActions = new ArrayList<Action>(object.finalActions.size());

        for (final Action action : object.finalActions) {
            finalActions.add(action.copy());
        }
    }

    /**
     * Adds the action object to the array of actions for the try block.
     *
     * @param anAction
     *            an action. Must not be null.
     */
    public ExceptionHandler addToTry(final Action anAction) {
        if (anAction == null) {
            throw new NullPointerException();
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
    public ExceptionHandler addToCatch(final Action anAction) {
        if (anAction == null) {
            throw new NullPointerException();
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
    public ExceptionHandler addToFinally(final Action anAction) {
        if (anAction == null) {
            throw new NullPointerException();
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
     *            will be signed to a register, but not empty.
     */
    public void setVariable(final String name) {

        if (name.length() == 0) {
            throw new StringSizeException(0, Short.MAX_VALUE, 0);
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
    public void setRegister(final int index) {
        if ((index < 0) || (index > 255)) {
            throw new IllegalArgumentRangeException(0, 255, index);
        }
        register = index;
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
    public void setTryActions(final List<Action> array) {
        if (array == null) {
            throw new NullPointerException();
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
    public void setCatchActions(final List<Action> array) {
        if (array == null) {
            throw new NullPointerException();
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
    public void setFinalActions(final List<Action> array) {
        if (array == null) {
            throw new NullPointerException();
        }
        finalActions = array;
    }

    /** {@inheritDoc} */
    public ExceptionHandler copy() {
        return new ExceptionHandler(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, variable, register, tryActions,
                catchActions, finalActions);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        // TODO(optimise)
        length = 7;
        length += (register == 0) ? coder.strlen(variable) : 1;

        tryLength = 0;
        catchLength = 0;
        finalLength = 0;

        for (final Action action : tryActions) {
            tryLength += action.prepareToEncode(coder, context);
        }

        for (final Action action : catchActions) {
            catchLength += action.prepareToEncode(coder, context);
        }

        for (final Action action : finalActions) {
            finalLength += action.prepareToEncode(coder, context);
        }

        length += tryLength;
        length += catchLength;
        length += finalLength;

        return 3 + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {

        // TODO(optimise)
        coder.writeByte(ActionTypes.EXCEPTION_HANDLER);
        coder.writeWord(length, 2);
        coder.writeBits(0, 5);
        coder.writeBits(register == 0 ? 1 : 0, 1);
        coder.writeBits(finalLength > 0 ? 1 : 0, 1);
        coder.writeBits(catchLength > 0 ? 1 : 0, 1);

        coder.writeWord(tryLength, 2);
        coder.writeWord(catchLength, 2);
        coder.writeWord(finalLength, 2);

        if (register == 0) {
            coder.writeString(variable);
        } else {
            coder.writeByte(register);
        }

        for (final Action action : tryActions) {
            action.encode(coder, context);
        }

        for (final Action action : catchActions) {
            action.encode(coder, context);
        }

        for (final Action action : finalActions) {
            action.encode(coder, context);
        }
    }
}
