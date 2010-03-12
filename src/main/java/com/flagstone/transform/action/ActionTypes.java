/*
 * ActionTypes.java
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

/**
 * ActionTypes defines the constants that identify the different types of action
 * when encoded according to the Flash file format specification.
 * <p>
 * IMPORTANT: Only Actions for Actionscript 1.0 and 2.0 are included, actions
 * defined for Actionscript 3.0 are not included.
 * </p>
 */
public final class ActionTypes {
    /** The type for creating the end of a sequence of actions. */
    public static final int END = 0;
    /** The type used to identify NextFrame actions when they are encoded. */
    public static final int NEXT_FRAME = 4;
    /** The type used to identify PrevFrame actions when they are encoded. */
    public static final int PREV_FRAME = 5;
    /** The type used to identify Play actions when they are encoded. */
    public static final int PLAY = 6;
    /** The type used to identify Stop actions when they are encoded. */
    public static final int STOP = 7;
    /** The type used to identify ToggleQuality actions when they are encoded. */
    public static final int TOGGLE_QUALITY = 8;
    /** The type used to identify StopSounds actions when they are encoded. */
    public static final int STOP_SOUNDS = 9;
    /** The type used to identify IntegerAdd actions when they are encoded. */
    public static final int INTEGER_ADD = 10;
    /** The type used to identify Subtract actions when they are encoded. */
    public static final int SUBTRACT = 11;
    /** The type used to identify Multiply actions when they are encoded. */
    public static final int MULTIPLY = 12;
    /** The type used to identify Divide actions when they are encoded. */
    public static final int DIVIDE = 13;
    /** The type used to identify IntegerEquals actions when they are encoded. */
    public static final int INTEGER_EQUALS = 14;
    /** The type used to identify IntegerLess actions when they are encoded. */
    public static final int INTEGER_LESS = 15;
    /** The type used to identify And actions when they are encoded. */
    public static final int LOGICAL_AND = 16;
    /** The type used to identify Not actions when they are encoded. */
    public static final int LOGICAL_NOT = 18;
    /** The type used to identify Or actions when they are encoded. */
    public static final int LOGICAL_OR = 17;
    /** The type used to identify StringEquals actions when they are encoded. */
    public static final int STRING_EQUALS = 19;
    /** The type used to identify StringLength actions when they are encoded. */
    public static final int STRING_LENGTH = 20;
    /** The type used to identify StringExtract actions when they are encoded. */
    public static final int STRING_EXTRACT = 21;
    /** The type used to identify Pop actions when they are encoded. */
    public static final int POP = 23;
    /** The type used to identify ToInteger actions when they are encoded. */
    public static final int TO_INTEGER = 24;
    /** The type used to identify GetVariable actions when they are encoded. */
    public static final int GET_VARIABLE = 28;
    /** The type used to identify SetVariable actions when they are encoded. */
    public static final int SET_VARIABLE = 29;
    /** The type used to identify SetTarget2 actions when they are encoded. */
    public static final int SET_TARGET_2 = 32;
    /** The type used to identify StringAdd actions when they are encoded. */
    public static final int STRING_ADD = 33;
    /** The type used to identify GetProperty actions when they are encoded. */
    public static final int GET_PROPERTY = 34;
    /** The type used to identify SetProperty actions when they are encoded. */
    public static final int SET_PROPERTY = 35;
    /** The type used to identify CloneSprite actions when they are encoded. */
    public static final int CLONE_SPRITE = 36;
    /** The type used to identify RemoveSprite actions when they are encoded. */
    public static final int REMOVE_SPRITE = 37;
    /** The type used to identify Trace actions when they are encoded. */
    public static final int TRACE = 38;
    /** The type used to identify StartDrag actions when they are encoded. */
    public static final int START_DRAG = 39;
    /** The type used to identify EndDrag actions when they are encoded. */
    public static final int END_DRAG = 40;
    /** The type used to identify StringLess actions when they are encoded. */
    public static final int STRING_LESS = 41;
    /** The type used to identify Throw actions when they are encoded. */
    public static final int THROW = 42;
    /** The type used to identify Cast actions when they are encoded. */
    public static final int CAST = 43;
    /** The type used to identify Implements actions when they are encoded. */
    public static final int IMPLEMENTS = 44;
    /** The type used to identify RandomNumber actions when they are encoded. */
    public static final int RANDOM_NUMBER = 48;
    /** The type used to identify MBStringLength actions when they are encoded. */
    public static final int MB_STRING_LENGTH = 49;
    /** The type used to identify CharToAscii actions when they are encoded. */
    public static final int CHAR_TO_ASCII = 50;
    /** The type used to identify AsciiToChar actions when they are encoded. */
    public static final int ASCII_TO_CHAR = 51;
    /** The type used to identify GetTime actions when they are encoded. */
    public static final int GET_TIME = 52;
    /** The type used to identify MBStringExtract actions when they are encoded. */
    public static final int MB_STRING_EXTRACT = 53;
    /** The type used to identify MBCharToAscii actions when they are encoded. */
    public static final int MB_CHAR_TO_ASCII = 54;
    /** The type used to identify MBAsciiToChar actions when they are encoded. */
    public static final int MB_ASCII_TO_CHAR = 55;
    /** The type used to identify DeleteVariable actions when they are encoded. */
    public static final int DELETE_VARIABLE = 58;
    /** The type used to identify Delete actions when they are encoded. */
    public static final int DELETE = 59;
    /** The type used to identify InitVariable actions when they are encoded. */
    public static final int INIT_VARIABLE = 60;
    /** The type used to identify ExecuteFunction actions when they are encoded. */
    public static final int EXECUTE_FUNCTION = 61;
    /** The type used to identify Return actions when they are encoded. */
    public static final int RETURN = 62;
    /** The type used to identify Modulo actions when they are encoded. */
    public static final int MODULO = 63;
    /** The type used to identify NamedObject actions when they are encoded. */
    public static final int NAMED_OBJECT = 64;
    /** The type used to identify NewVariable actions when they are encoded. */
    public static final int NEW_VARIABLE = 65;
    /** The type used to identify NewArray actions when they are encoded. */
    public static final int NEW_ARRAY = 66;
    /** The type used to identify NewObject actions when they are encoded. */
    public static final int NEW_OBJECT = 67;
    /** The type used to identify GetType actions when they are encoded. */
    public static final int GET_TYPE = 68;
    /** The type used to identify GetTarget actions when they are encoded. */
    public static final int GET_TARGET = 69;
    /** The type used to identify Enumerate actions when they are encoded. */
    public static final int ENUMERATE = 70;
    /** The type used to identify Add actions when they are encoded. */
    public static final int ADD = 71;
    /** The type used to identify Less actions when they are encoded. */
    public static final int LESS = 72;
    /** The type used to identify Equals actions when they are encoded. */
    public static final int EQUALS = 73;
    /** The type used to identify ToNumber actions when they are encoded. */
    public static final int TO_NUMBER = 74;
    /** The type used to identify ToString actions when they are encoded. */
    public static final int TO_STRING = 75;
    /** The type used to identify Duplicate actions when they are encoded. */
    public static final int DUPLICATE = 76;
    /** The type used to identify Swap actions when they are encoded. */
    public static final int SWAP = 77;
    /** The type used to identify GetAttribute actions when they are encoded. */
    public static final int GET_ATTRIBUTE = 78;
    /** The type used to identify SetAttribute actions when they are encoded. */
    public static final int SET_ATTRIBUTE = 79;
    /** The type used to identify Increment actions when they are encoded. */
    public static final int INCREMENT = 80;
    /** The type used to identify Decrement actions when they are encoded. */
    public static final int DECREMENT = 81;
    /** The type used to identify ExecuteMethod actions when they are encoded. */
    public static final int EXECUTE_METHOD = 82;
    /** The type used to identify NewMethod actions when they are encoded. */
    public static final int NEW_METHOD = 83;
    /** The type used to identify InstanceOf actions when they are encoded. */
    public static final int INSTANCEOF = 84;
    /** The type used to identify EnumerateObject actions when they are encoded. */
    public static final int ENUMERATE_OBJECT = 85;
    /** The type used to identify BitwiseAnd actions when they are encoded. */
    public static final int BITWISE_AND = 96;
    /** The type used to identify BitwiseOr actions when they are encoded. */
    public static final int BITWISE_OR = 97;
    /** The type used to identify BitwiseXOr actions when they are encoded. */
    public static final int BITWISE_XOR = 98;
    /** The type used to identify LogicalShiftLeft actions when they are encoded. */
    public static final int SHIFT_LEFT = 99;
    /** The type used to identify ArithmeticShiftRight actions when they are encoded. */
    public static final int ARITH_SHIFT_RIGHT = 100;
    /** The type used to identify LogicalShiftRight actions when they are encoded. */
    public static final int SHIFT_RIGHT = 101;
    /** The type used to identify StrictEquals actions when they are encoded. */
    public static final int STRICT_EQUALS = 102;
    /** The type used to identify Greater actions when they are encoded. */
    public static final int GREATER = 103;
    /** The type used to identify StringGreater actions when they are encoded. */
    public static final int STRING_GREATER = 104;
    /** The type used to identify Extends actions when they are encoded. */
    public static final int EXTENDS = 105;
    /** The type used to identify GotoFrame actions when they are encoded. */
    public static final int GOTO_FRAME = 129;
    /** The type used to identify GetUrl actions when they are encoded. */
    public static final int GET_URL = 131;
    /** The type used to identify RegisterCopy actions when they are encoded. */
    public static final int REGISTER_COPY = 135;
    /** The type used to identify Table actions when they are encoded. */
    public static final int TABLE = 136;
    /** The type used to identify WaitForFrame actions when they are encoded. */
    public static final int WAIT_FOR_FRAME = 138;
    /** The type used to identify SetTarget actions when they are encoded. */
    public static final int SET_TARGET = 139;
    /** The type used to identify GotoLabel actions when they are encoded. */
    public static final int GOTO_LABEL = 140;
    /** The type used to identify WaitForFrame2 actions when they are encoded. */
    public static final int WAIT_FOR_FRAME_2 = 141;
    /** The type used to identify NewFunction2 actions when they are encoded. */
    public static final int NEW_FUNCTION_2 = 142;
    /** The type used to identify ExceptionHandler actions when they are encoded. */
    public static final int EXCEPTION_HANDLER = 143;
    /** The type used to identify With actions when they are encoded. */
    public static final int WITH = 148;
    /** The type used to identify Push actions when they are encoded. */
    public static final int PUSH = 150;
    /** The type used to identify Jump actions when they are encoded. */
    public static final int JUMP = 153;
    /** The type used to identify GetUrl2 actions when they are encoded. */
    public static final int GET_URL_2 = 154;
    /** The type for creating an If action. */
    public static final int IF = 157;
    /** The type used to identify Call actions when they are encoded. */
    public static final int CALL = 158;
    /** The type used to identify GotoFrame2 actions when they are encoded. */
    public static final int GOTO_FRAME_2 = 159;
    /** The type used to identify NewFunction actions when they are encoded. */
    public static final int NEW_FUNCTION = 155;

    private ActionTypes() {
        // Class only contains constants
    }
}
