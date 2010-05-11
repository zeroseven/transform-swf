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
    /** TODO(method). */
    END(ActionTypes.END),
    /** TODO(method). */
    NEXT_FRAME(ActionTypes.NEXT_FRAME),
    /** TODO(method). */
    PREV_FRAME(ActionTypes.PREV_FRAME),
    /** TODO(method). */
    PLAY(ActionTypes.PLAY),
    /** TODO(method). */
    STOP(ActionTypes.STOP),
    /** TODO(method). */
    TOGGLE_QUALITY(ActionTypes.TOGGLE_QUALITY),
    /** TODO(method). */
    STOP_SOUNDS(ActionTypes.STOP_SOUNDS),
    /** TODO(method). */
    INTEGER_ADD(ActionTypes.INTEGER_ADD),
    /** TODO(method). */
    SUBTRACT(ActionTypes.SUBTRACT),
    /** TODO(method). */
    MULTIPLY(ActionTypes.MULTIPLY),
    /** TODO(method). */
    DIVIDE(ActionTypes.DIVIDE),
    /** TODO(method). */
    INTEGER_EQUALS(ActionTypes.INTEGER_EQUALS),
    /** TODO(method). */
    INTEGER_LESS(ActionTypes.INTEGER_LESS),
    /** TODO(method). */
    LOGICAL_AND(ActionTypes.LOGICAL_AND),
    /** TODO(method). */
    LOGICAL_NOT(ActionTypes.LOGICAL_NOT),
    /** TODO(method). */
    LOGICAL_OR(ActionTypes.LOGICAL_OR),
    /** TODO(method). */
    STRING_EQUALS(ActionTypes.STRING_EQUALS),
    /** TODO(method). */
    STRING_LENGTH(ActionTypes.STRING_LENGTH),
    /** TODO(method). */
    STRING_EXTRACT(ActionTypes.STRING_EXTRACT),
    /** TODO(method). */
    POP(ActionTypes.POP),
    /** TODO(method). */
    TO_INTEGER(ActionTypes.TO_INTEGER),
    /** TODO(method). */
    GET_VARIABLE(ActionTypes.GET_VARIABLE),
    /** TODO(method). */
    SET_VARIABLE(ActionTypes.SET_VARIABLE),
    /** TODO(method). */
    SET_TARGET_2(ActionTypes.SET_TARGET_2),
    /** TODO(method). */
    STRING_ADD(ActionTypes.STRING_ADD),
    /** TODO(method). */
    GET_PROPERTY(ActionTypes.GET_PROPERTY),
    /** TODO(method). */
    SET_PROPERTY(ActionTypes.SET_PROPERTY),
    /** TODO(method). */
    CLONE_SPRITE(ActionTypes.CLONE_SPRITE),
    /** TODO(method). */
    REMOVE_SPRITE(ActionTypes.REMOVE_SPRITE),
    /** TODO(method). */
    TRACE(ActionTypes.TRACE),
    /** TODO(method). */
    START_DRAG(ActionTypes.START_DRAG),
    /** TODO(method). */
    END_DRAG(ActionTypes.END_DRAG),
    /** TODO(method). */
    STRING_LESS(ActionTypes.STRING_LESS),
    /** TODO(method). */
    THROW(ActionTypes.THROW),
    /** TODO(method). */
    CAST(ActionTypes.CAST),
    /** TODO(method). */
    IMPLEMENTS(ActionTypes.IMPLEMENTS),
    /** TODO(method). */
    RANDOM_NUMBER(ActionTypes.RANDOM_NUMBER),
    /** TODO(method). */
    MB_STRING_LENGTH(ActionTypes.MB_STRING_LENGTH),
    /** TODO(method). */
    CHAR_TO_ASCII(ActionTypes.CHAR_TO_ASCII),
    /** TODO(method). */
    ASCII_TO_CHAR(ActionTypes.ASCII_TO_CHAR),
    /** TODO(method). */
    GET_TIME(ActionTypes.GET_TIME),
    /** TODO(method). */
    MB_STRING_EXTRACT(ActionTypes.MB_STRING_EXTRACT),
    /** TODO(method). */
    MB_CHAR_TO_ASCII(ActionTypes.MB_CHAR_TO_ASCII),
    /** TODO(method). */
    MB_ASCII_TO_CHAR(ActionTypes.MB_ASCII_TO_CHAR),
    /** TODO(method). */
    DELETE_VARIABLE(ActionTypes.DELETE_VARIABLE),
    /** TODO(method). */
    DELETE(ActionTypes.DELETE),
    /** TODO(method). */
    INIT_VARIABLE(ActionTypes.INIT_VARIABLE),
    /** TODO(method). */
    EXECUTE_FUNCTION(ActionTypes.EXECUTE_FUNCTION),
    /** TODO(method). */
    RETURN(ActionTypes.RETURN),
    /** TODO(method). */
    MODULO(ActionTypes.MODULO),
    /** TODO(method). */
    NAMED_OBJECT(ActionTypes.NAMED_OBJECT),
    /** TODO(method). */
    NEW_VARIABLE(ActionTypes.NEW_VARIABLE),
    /** TODO(method). */
    NEW_ARRAY(ActionTypes.NEW_ARRAY),
    /** TODO(method). */
    NEW_OBJECT(ActionTypes.NEW_OBJECT),
    /** TODO(method). */
    GET_TYPE(ActionTypes.GET_TYPE),
    /** TODO(method). */
    GET_TARGET(ActionTypes.GET_TARGET),
    /** TODO(method). */
    ENUMERATE(ActionTypes.ENUMERATE),
    /** TODO(method). */
    ADD(ActionTypes.ADD),
    /** TODO(method). */
    LESS(ActionTypes.LESS),
    /** TODO(method). */
    EQUALS(ActionTypes.EQUALS),
    /** TODO(method). */
    TO_NUMBER(ActionTypes.TO_NUMBER),
    /** TODO(method). */
    TO_STRING(ActionTypes.TO_STRING),
    /** TODO(method). */
    DUPLICATE(ActionTypes.DUPLICATE),
    /** TODO(method). */
    SWAP(ActionTypes.SWAP),
    /** TODO(method). */
    GET_ATTRIBUTE(ActionTypes.GET_ATTRIBUTE),
    /** TODO(method). */
    SET_ATTRIBUTE(ActionTypes.SET_ATTRIBUTE),
    /** TODO(method). */
    INCREMENT(ActionTypes.INCREMENT),
    /** TODO(method). */
    DECREMENT(ActionTypes.DECREMENT),
    /** TODO(method). */
    EXECUTE_METHOD(ActionTypes.EXECUTE_METHOD),
    /** TODO(method). */
    NEW_METHOD(ActionTypes.NEW_METHOD),
    /** TODO(method). */
    INSTANCEOF(ActionTypes.INSTANCEOF),
    /** TODO(method). */
    ENUMERATE_OBJECT(ActionTypes.ENUMERATE_OBJECT),
    /** TODO(method). */
    BITWISE_AND(ActionTypes.BITWISE_AND),
    /** TODO(method). */
    BITWISE_OR(ActionTypes.BITWISE_OR),
    /** TODO(method). */
    BITWISE_XOR(ActionTypes.BITWISE_XOR),
    /** TODO(method). */
    SHIFT_LEFT(ActionTypes.SHIFT_LEFT),
    /** TODO(method). */
    ARITH_SHIFT_RIGHT(ActionTypes.ARITH_SHIFT_RIGHT),
    /** TODO(method). */
    SHIFT_RIGHT(ActionTypes.SHIFT_RIGHT),
    /** TODO(method). */
    STRICT_EQUALS(ActionTypes.STRICT_EQUALS),
    /** TODO(method). */
    GREATER(ActionTypes.GREATER),
    /** TODO(method). */
    STRING_GREATER(ActionTypes.STRING_GREATER),
    /** TODO(method). */
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
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        return 1;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeByte(type);

    }
}
