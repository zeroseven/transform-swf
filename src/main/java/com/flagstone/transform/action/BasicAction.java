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

import java.util.LinkedHashMap;
import java.util.Map;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * BasicAction represents all the actions that can be encoded using a single
 * byte-code.
 */
public enum BasicAction implements Action {
    
    END(ActionTypes.END),
    
    NEXT_FRAME(ActionTypes.NEXT_FRAME),
    
    PREV_FRAME(ActionTypes.PREV_FRAME),
    
    PLAY(ActionTypes.PLAY),
    
    STOP(ActionTypes.STOP),
    
    TOGGLE_QUALITY(ActionTypes.TOGGLE_QUALITY),
    
    STOP_SOUNDS(ActionTypes.STOP_SOUNDS),
    
    INTEGER_ADD(ActionTypes.INTEGER_ADD),
    
    SUBTRACT(ActionTypes.SUBTRACT),
    
    MULTIPLY(ActionTypes.MULTIPLY),
    
    DIVIDE(ActionTypes.DIVIDE),
    
    INTEGER_EQUALS(ActionTypes.INTEGER_EQUALS),
    
    INTEGER_LESS(ActionTypes.INTEGER_LESS),
    
    LOGICAL_AND(ActionTypes.LOGICAL_AND),
    
    LOGICAL_NOT(ActionTypes.LOGICAL_NOT),
    
    LOGICAL_OR(ActionTypes.LOGICAL_OR),
    
    STRING_EQUALS(ActionTypes.STRING_EQUALS),
    
    STRING_LENGTH(ActionTypes.STRING_LENGTH),
    
    STRING_EXTRACT(ActionTypes.STRING_EXTRACT),
    
    POP(ActionTypes.POP),
    
    TO_INTEGER(ActionTypes.TO_INTEGER),
    
    GET_VARIABLE(ActionTypes.GET_VARIABLE),
    
    SET_VARIABLE(ActionTypes.SET_VARIABLE),
    
    SET_TARGET_2(ActionTypes.SET_TARGET_2),
    
    STRING_ADD(ActionTypes.STRING_ADD),
    
    GET_PROPERTY(ActionTypes.GET_PROPERTY),
    
    SET_PROPERTY(ActionTypes.SET_PROPERTY),
    
    CLONE_SPRITE(ActionTypes.CLONE_SPRITE),
    
    REMOVE_SPRITE(ActionTypes.REMOVE_SPRITE),
    
    TRACE(ActionTypes.TRACE),
    
    START_DRAG(ActionTypes.START_DRAG),
    
    END_DRAG(ActionTypes.END_DRAG),
    
    STRING_LESS(ActionTypes.STRING_LESS),
    
    THROW(ActionTypes.THROW),
    
    CAST(ActionTypes.CAST),
    
    IMPLEMENTS(ActionTypes.IMPLEMENTS),
    
    RANDOM_NUMBER(ActionTypes.RANDOM_NUMBER),
    
    MB_STRING_LENGTH(ActionTypes.MB_STRING_LENGTH),
    
    CHAR_TO_ASCII(ActionTypes.CHAR_TO_ASCII),
    
    ASCII_TO_CHAR(ActionTypes.ASCII_TO_CHAR),
    
    GET_TIME(ActionTypes.GET_TIME),
    
    MB_STRING_EXTRACT(ActionTypes.MB_STRING_EXTRACT),
    
    MB_CHAR_TO_ASCII(ActionTypes.MB_CHAR_TO_ASCII),
    
    MB_ASCII_TO_CHAR(ActionTypes.MB_ASCII_TO_CHAR),
    
    DELETE_VARIABLE(ActionTypes.DELETE_VARIABLE),
    
    DELETE(ActionTypes.DELETE),
    
    INIT_VARIABLE(ActionTypes.INIT_VARIABLE),
    
    EXECUTE_FUNCTION(ActionTypes.EXECUTE_FUNCTION),
    
    RETURN(ActionTypes.RETURN),
    
    MODULO(ActionTypes.MODULO),
    
    NAMED_OBJECT(ActionTypes.NAMED_OBJECT),
    
    NEW_VARIABLE(ActionTypes.NEW_VARIABLE),
    
    NEW_ARRAY(ActionTypes.NEW_ARRAY),
    
    NEW_OBJECT(ActionTypes.NEW_OBJECT),
    
    GET_TYPE(ActionTypes.GET_TYPE),
    
    GET_TARGET(ActionTypes.GET_TARGET),
    
    ENUMERATE(ActionTypes.ENUMERATE),
    
    ADD(ActionTypes.ADD),
    
    LESS(ActionTypes.LESS),
    
    EQUALS(ActionTypes.EQUALS),
    
    TO_NUMBER(ActionTypes.TO_NUMBER),
    
    TO_STRING(ActionTypes.TO_STRING),
    
    DUPLICATE(ActionTypes.DUPLICATE),
    
    SWAP(ActionTypes.SWAP),
    
    GET_ATTRIBUTE(ActionTypes.GET_ATTRIBUTE),
    
    SET_ATTRIBUTE(ActionTypes.SET_ATTRIBUTE),
    
    INCREMENT(ActionTypes.INCREMENT),
    
    DECREMENT(ActionTypes.DECREMENT),
    
    EXECUTE_METHOD(ActionTypes.EXECUTE_METHOD),
    
    NEW_METHOD(ActionTypes.NEW_METHOD),
    
    INSTANCEOF(ActionTypes.INSTANCEOF),
    
    ENUMERATE_OBJECT(ActionTypes.ENUMERATE_OBJECT),
    
    BITWISE_AND(ActionTypes.BITWISE_AND),
    
    BITWISE_OR(ActionTypes.BITWISE_OR),
    
    BITWISE_XOR(ActionTypes.BITWISE_XOR),
    
    SHIFT_LEFT(ActionTypes.SHIFT_LEFT),
    
    ARITH_SHIFT_RIGHT(ActionTypes.ARITH_SHIFT_RIGHT),
    
    SHIFT_RIGHT(ActionTypes.SHIFT_RIGHT),
    
    STRICT_EQUALS(ActionTypes.STRICT_EQUALS),
    
    GREATER(ActionTypes.GREATER),
    
    STRING_GREATER(ActionTypes.STRING_GREATER),
    
    EXTENDS(ActionTypes.EXTENDS),
    /** Undocumented action. */
    //TODO
    CODE1(ActionTypes.CODE_1),
    /** Undocumented action. */
    //TODO
    CODE2(ActionTypes.CODE_2),
    /** Undocumented action. */
    //TODO
    CODE22(ActionTypes.CODE_22),
    /** Undocumented action. */
    //TODO
    CODE27(ActionTypes.CODE_27),
    /** Undocumented action. */
    //TODO
    CODE3(ActionTypes.CODE_3),
    /** Undocumented action. */
    //TODO
    CODE30(ActionTypes.CODE_30),
    /** Undocumented action. */
    //TODO
    CODE45(ActionTypes.CODE_45),
    /** Undocumented action. */
    //TODO
    CODE46(ActionTypes.CODE_46),
    /** Undocumented action. */
    //TODO
    CODE47(ActionTypes.CODE_47),
    /** Undocumented action. */
    //TODO
    CODE56(ActionTypes.CODE_56),
    /** Undocumented action. */
    //TODO
    CODE57(ActionTypes.CODE_57),
    /** Undocumented action. */
    //TODO
    CODE86(ActionTypes.CODE_86),
    /** Undocumented action. */
    //TODO
    CODE87(ActionTypes.CODE_87),
    /** Undocumented action. */
    //TODO
    CODE88(ActionTypes.CODE_88),
    /** Undocumented action. */
    //TODO
    CODE89(ActionTypes.CODE_89),
    /** Undocumented action. */
    //TODO
    CODE91(ActionTypes.CODE_91),
    /** Undocumented action. */
    //TODO
    CODE92(ActionTypes.CODE_92),
    /** Undocumented action. */
    //TODO
    CODE93(ActionTypes.CODE_93),
    /** Undocumented action. */
    //TODO
    CODE95(ActionTypes.CODE_95),
    /** Undocumented action. */
    //TODO
    CODE106(ActionTypes.CODE_106),
    /** Undocumented action. */
    //TODO
    CODE107(ActionTypes.CODE_107),
    /** Undocumented action. */
    //TODO
    CODE108(ActionTypes.CODE_108),
    /** Undocumented action. */
    //TODO
    CODE109(ActionTypes.CODE_109),
    /** Undocumented action. */
    //TODO
    CODE110(ActionTypes.CODE_110),
    /** Undocumented action. */
    //TODO
    CODE111(ActionTypes.CODE_111),
    /** Undocumented action. */
    //TODO
    CODE112(ActionTypes.CODE_112),
    /** Undocumented action. */
    //TODO
    CODE113(ActionTypes.CODE_113),
    /** Undocumented action. */
    //TODO
    CODE114(ActionTypes.CODE_114),
    /** Undocumented action. */
    //TODO
    CODE115(ActionTypes.CODE_115),
    /** Undocumented action. */
    //TODO
    CODE116(ActionTypes.CODE_116),
    /** Undocumented action. */
    //TODO
    CODE117(ActionTypes.CODE_117),
    /** Undocumented action. */
    //TODO
    CODE118(ActionTypes.CODE_118),
    /** Undocumented action. */
    //TODO
    CODE119(ActionTypes.CODE_119),
    /** Undocumented action. */
    //TODO
    CODE120(ActionTypes.CODE_120),
    /** Undocumented action. */
    //TODO
    CODE121(ActionTypes.CODE_121),
    /** Undocumented action. */
    //TODO
    CODE122(ActionTypes.CODE_122),
    /** Undocumented action. */
    //TODO
    CODE125(ActionTypes.CODE_125),
    /** Undocumented action. */
    //TODO
    CODE126(ActionTypes.CODE_126),
    /** Undocumented action. */
    //TODO
    CODE127(ActionTypes.CODE_127);

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
            throws CoderException {
        coder.writeByte(type);

    }
}
