/*
 * BasicAction.java
 * Transform
 *
 * Copyright (c) 2009-2010 Flagstone Software Ltd. All rights reserved.
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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * BasicAction represents all the actions that can be encoded using a single
 * byte-code.
 *
 * Where appropriate the description for an action contains a simple example
 * showing the order of arguments on the stack and any result: e.g. 3, 2 -> 1.
 * Here 3 and 2 are the numbers on the stack with 2 being the top-most. When the
 * action is executed the numbers are popped off and the result to the right of
 * the arrow is the result is pushed onto the stack.
 *
 * <h1>Notes:</h1>
 * <p>
 * FSPush is used to push literals onto the Stack. See also FSRegisterCopy which
 * copies the value on top of the Stack to one of the Flash Player's internal
 * registers.
 * </p>
 *
 * <p>
 * Arithmetic add is supported by two actions. INTEGER_ADD was introduced in
 * Flash 4. It was replaced in Flash 5 by the more flexible ADD action which is
 * able to add any two numbers and also concatenate strings. If a string and a
 * number are added then the number is converted to its string representation
 * before concatenation.
 * </p>
 *
 * <p>
 * For comparison, Flash 4 introduced INTEGER_LESS and INTEGER_EQUALS for
 * comparing numbers and STRING_LESS and STRING_EQUALS for comparing strings.
 * They were superseded in Flash 5 by LESS and EQUALS which work with either
 * strings or numbers.
 * </p>
 */
public enum BasicAction implements Action {
    /** Signals the end of a list of actions. */
    END(ActionTypes.END),
    /** Move to the next frame. */
    NEXT_FRAME(ActionTypes.NEXT_FRAME),
    /** Move to the previous frame. */
    PREV_FRAME(ActionTypes.PREV_FRAME),
    /** Start playing the movie or movie clip. */
    PLAY(ActionTypes.PLAY),
    /** Stop playing the movie or movie clip. */
    STOP(ActionTypes.STOP),
    /** Toggle the movie between high and low quality. */
    TOGGLE_QUALITY(ActionTypes.TOGGLE_QUALITY),
    /** Stop playing all sounds. */
    STOP_SOUNDS(ActionTypes.STOP_SOUNDS),
    /** Add two integers. */
    INTEGER_ADD(ActionTypes.INTEGER_ADD),
    /** Subtract two integers. */
    SUBTRACT(ActionTypes.SUBTRACT),
    /** Multiply two numbers. */
    MULTIPLY(ActionTypes.MULTIPLY),
    /** Divide two numbers. */
    DIVIDE(ActionTypes.DIVIDE),
    /** Test whether two integers are equal. */
    INTEGER_EQUALS(ActionTypes.INTEGER_EQUALS),
    /** Test where one number is less than another. */
    INTEGER_LESS(ActionTypes.INTEGER_LESS),
    /** Logically and two values together. */
    LOGICAL_AND(ActionTypes.LOGICAL_AND),
    /** Logically invert a value. */
    LOGICAL_NOT(ActionTypes.LOGICAL_NOT),
    /** Logically or two values together. */
    LOGICAL_OR(ActionTypes.LOGICAL_OR),
    /** Test whether two strings are equal. */
    STRING_EQUALS(ActionTypes.STRING_EQUALS),
    /** Get the length of an ASCII string. */
    STRING_LENGTH(ActionTypes.STRING_LENGTH),
    /** Substring. */
    STRING_EXTRACT(ActionTypes.STRING_EXTRACT),
    /** Pop value from the top of the stack. */
    POP(ActionTypes.POP),
    /** Convert a value to an integer. */
    TO_INTEGER(ActionTypes.TO_INTEGER),
    /** Get the value of a variable. */
    GET_VARIABLE(ActionTypes.GET_VARIABLE),
    /** Set the value of a variable. "x", 3 -> */
    SET_VARIABLE(ActionTypes.SET_VARIABLE),
    /** Execute the following actions with the named movie clip. */
    SET_TARGET_2(ActionTypes.SET_TARGET_2),
    /** Concatenate two strings. */
    STRING_ADD(ActionTypes.STRING_ADD),
    /** Push the value of the specified property on the stack. */
    GET_PROPERTY(ActionTypes.GET_PROPERTY),
    /** Set the value of a property. */
    SET_PROPERTY(ActionTypes.SET_PROPERTY),
    /** Duplicate a movie clip on the display list. */
    CLONE_SPRITE(ActionTypes.CLONE_SPRITE),
    /** Delete a movie clip. */
    REMOVE_SPRITE(ActionTypes.REMOVE_SPRITE),
    /** Append value to debugging window. */
    TRACE(ActionTypes.TRACE),
    /** Start dragging the mouse. */
    START_DRAG(ActionTypes.START_DRAG),
    /** Stop dragging the mouse. */
    END_DRAG(ActionTypes.END_DRAG),
    /** Test where one string is less than another. */
    STRING_LESS(ActionTypes.STRING_LESS),
    /** Throw an exception. */
    THROW(ActionTypes.THROW),
    /** Casts the type of an object. */
    CAST(ActionTypes.CAST),
    /** Identifies a class implements a defined interface. */
    IMPLEMENTS(ActionTypes.IMPLEMENTS),
    /** FSCommand2 function */
    FS_COMMAND2(ActionTypes.FS_COMMAND2),
    /** Push a random number onto the stack. */
    RANDOM_NUMBER(ActionTypes.RANDOM_NUMBER),
    /** Get the length of an multi-byte string. */
    MB_STRING_LENGTH(ActionTypes.MB_STRING_LENGTH),
    /** Convert the first character of a string to its ASCII value. */
    CHAR_TO_ASCII(ActionTypes.CHAR_TO_ASCII),
    /** Convert the ASCII value to the equivalent character. */
    ASCII_TO_CHAR(ActionTypes.ASCII_TO_CHAR),
    /** Return the elapsed time since the start of the movie. */
    GET_TIME(ActionTypes.GET_TIME),
    /** Substring of a multi-byte string. */
    MB_STRING_EXTRACT(ActionTypes.MB_STRING_EXTRACT),
    /** Convert the first character of string to its Unicode value. */
    MB_CHAR_TO_ASCII(ActionTypes.MB_CHAR_TO_ASCII),
    /** Convert a Unicode value to the equivalent character. */
    MB_ASCII_TO_CHAR(ActionTypes.MB_ASCII_TO_CHAR),
    /** Delete a variable. */
    DELETE_VARIABLE(ActionTypes.DELETE_VARIABLE),
    /** Delete an object or variable. */
    DELETE(ActionTypes.DELETE),
    /** Create and set a variable. */
    INIT_VARIABLE(ActionTypes.INIT_VARIABLE),
    /** Execute a function. */
    EXECUTE_FUNCTION(ActionTypes.EXECUTE_FUNCTION),
    /** Return control from a function. */
    RETURN(ActionTypes.RETURN),
    /** Calculate the modulus of two numbers. */
    MODULO(ActionTypes.MODULO),
    /** Construct an instance of a built-in object. */
    NAMED_OBJECT(ActionTypes.NAMED_OBJECT),
    /** Create a new variable. */
    NEW_VARIABLE(ActionTypes.NEW_VARIABLE),
    /** Create a new array. */
    NEW_ARRAY(ActionTypes.NEW_ARRAY),
    /** Define a new class. */
    NEW_OBJECT(ActionTypes.NEW_OBJECT),
    /** Return the type of an object or value. */
    GET_TYPE(ActionTypes.GET_TYPE),
    /** Return the path to the current movie clip. */
    GET_TARGET(ActionTypes.GET_TARGET),
    /** Enumerate through the attributes of an object. */
    ENUMERATE(ActionTypes.ENUMERATE),
    /** Add two numbers. */
    ADD(ActionTypes.ADD),
    /** Test where one value is less than another. */
    LESS(ActionTypes.LESS),
    /** Test where one value is equal to another. */
    EQUALS(ActionTypes.EQUALS),
    /** Converts the string value to a number. */
    TO_NUMBER(ActionTypes.TO_NUMBER),
    /** Converts the value to a string. */
    TO_STRING(ActionTypes.TO_STRING),
    /** Duplicate the value at the top of the stack. */
    DUPLICATE(ActionTypes.DUPLICATE),
    /** Swap the top two values on the stack. */
    SWAP(ActionTypes.SWAP),
    /** Get the value of an object's attribute. */
    GET_ATTRIBUTE(ActionTypes.GET_ATTRIBUTE),
    /** Set the value of an object's attribute. */
    SET_ATTRIBUTE(ActionTypes.SET_ATTRIBUTE),
    /** Increment a number. */
    INCREMENT(ActionTypes.INCREMENT),
    /** Decrement a number. */
    DECREMENT(ActionTypes.DECREMENT),
    /** Execute a method. */
    EXECUTE_METHOD(ActionTypes.EXECUTE_METHOD),
    /** Define a new method for an object. */
    NEW_METHOD(ActionTypes.NEW_METHOD),
    /** Tests whether an object can be created using the constructor. */
    INSTANCEOF(ActionTypes.INSTANCEOF),
    /** Enumerate through the attributes of an object. */
    ENUMERATE_OBJECT(ActionTypes.ENUMERATE_OBJECT),
    /** Bitwise and tow numbers. */
    BITWISE_AND(ActionTypes.BITWISE_AND),
    /** Bitwise or tow numbers.*/
    BITWISE_OR(ActionTypes.BITWISE_OR),
    /** Bitwise exclusive-or two numbers. */
    BITWISE_XOR(ActionTypes.BITWISE_XOR),
    /** Shift a number left. */
    SHIFT_LEFT(ActionTypes.SHIFT_LEFT),
    /** Arithmetically shift a number right. */
    ARITH_SHIFT_RIGHT(ActionTypes.ARITH_SHIFT_RIGHT),
    /** Shift a number right. -1, 30 -> 3 */
    SHIFT_RIGHT(ActionTypes.SHIFT_RIGHT),
    /** Test whether type and value of two objects are equal. */
    STRICT_EQUALS(ActionTypes.STRICT_EQUALS),
    /** Test whether a number is greater than another. */
    GREATER(ActionTypes.GREATER),
    /** Test whether a string is greater than another. */
    STRING_GREATER(ActionTypes.STRING_GREATER),
    /** Identifies that a class inherits from a class. */
    EXTENDS(ActionTypes.EXTENDS);

    /**
     * Table used to store instances of Basic Actions so only one object is
     * created for each type of action decoded.
     */
    private static final Map<Integer, BasicAction> TABLE
            = new LinkedHashMap<Integer, BasicAction>();

    static {
        for (final BasicAction action : values()) {
            TABLE.put(action.type, action);
        }
    }

    /**
     * Returns the BasicAction for a given type.
     *
     * @param actionType
     *            the type that identifies the action when it is encoded.
     *
     * @return a shared instance of the object representing a given action type.
     */
    public static BasicAction fromInt(final int actionType) {
        return TABLE.get(actionType);
    }

    /** Type used to identify the action when it is encoded. */
    private final int type;

    /**
     * Constructor used to create instances for each type of action.
     *
     * @param actionType the value representing the action when it is encoded.
     */
    private BasicAction(final int actionType) {
        type = actionType;
    }

    /** {@inheritDoc} */
    public BasicAction copy() {
        return this;
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        return 1;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        coder.writeByte(type);

    }
}
