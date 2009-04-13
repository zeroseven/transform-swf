/*
 * BasicTypes.java
 * Transform
 * 
 * Copyright (c) 2009 Flagstone Software Ltd. All rights reserved.
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

import java.util.LinkedHashMap;
import java.util.Map;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Types;


public enum BasicAction implements Action
{
	END(Types.END),
	NEXT_FRAME(Types.NEXT_FRAME),
	PREV_FRAME(Types.PREV_FRAME),
	PLAY(Types.PLAY),
	STOP(Types.STOP),
	TOGGLE_QUALITY(Types.TOGGLE_QUALITY),
	STOP_SOUNDS(Types.STOP_SOUNDS),
	INTEGER_ADD(Types.INTEGER_ADD),
	SUBTRACT(Types.SUBTRACT),
	MULTIPLY(Types.MULTIPLY),
	DIVIDE(Types.DIVIDE),
	INTEGER_EQUALS(Types.INTEGER_EQUALS),
	INTEGER_LESS(Types.INTEGER_LESS),
	LOGICAL_AND(Types.LOGICAL_AND),
	LOGICAL_NOT(Types.LOGICAL_NOT),
	LOGICAL_OR(Types.LOGICAL_OR),
	STRING_EQUALS(Types.STRING_EQUALS),
	STRING_LENGTH(Types.STRING_LENGTH),
	STRING_EXTRACT(Types.STRING_EXTRACT),
	POP(Types.POP),
	TO_INTEGER(Types.TO_INTEGER),
	GET_VARIABLE(Types.GET_VARIABLE),
	SET_VARIABLE(Types.SET_VARIABLE),
	SET_TARGET_2(Types.SET_TARGET_2),	
	STRING_ADD(Types.STRING_ADD),
	GET_PROPERTY(Types.GET_PROPERTY),
	SET_PROPERTY(Types.SET_PROPERTY),
	CLONE_SPRITE(Types.CLONE_SPRITE),
	REMOVE_SPRITE(Types.REMOVE_SPRITE),
	TRACE(Types.TRACE),
	START_DRAG(Types.START_DRAG),
	END_DRAG(Types.END_DRAG),
	STRING_LESS(Types.STRING_LESS),
	THROW(Types.THROW),
	CAST(Types.CAST),
	IMPLEMENTS(Types.IMPLEMENTS),
	RANDOM_NUMBER(Types.RANDOM_NUMBER),
	MB_STRING_LENGTH(Types.MB_STRING_LENGTH),
	CHAR_TO_ASCII(Types.CHAR_TO_ASCII),
	ASCII_TO_CHAR(Types.ASCII_TO_CHAR),
	GET_TIME(Types.GET_TIME),
	MB_STRING_EXTRACT(Types.MB_STRING_EXTRACT),
	MB_CHAR_TO_ASCII(Types.MB_CHAR_TO_ASCII),
	MB_ASCII_TO_CHAR(Types.MB_ASCII_TO_CHAR),
	DELETE_VARIABLE(Types.DELETE_VARIABLE),
	DELETE(Types.DELETE),
	INIT_VARIABLE(Types.INIT_VARIABLE),
	EXECUTE_FUNCTION(Types.EXECUTE_FUNCTION),
	RETURN(Types.RETURN),
	MODULO(Types.MODULO),
	NAMED_OBJECT(Types.NAMED_OBJECT),
	NEW_VARIABLE(Types.NEW_VARIABLE),
	NEW_ARRAY(Types.NEW_ARRAY),	
	NEW_OBJECT(Types.NEW_OBJECT),
	GET_TYPE(Types.GET_TYPE),
	GET_TARGET(Types.GET_TARGET),
	ENUMERATE(Types.ENUMERATE),
	ADD(Types.ADD),
	LESS(Types.LESS),
	EQUALS(Types.EQUALS),
	TO_NUMBER(Types.TO_NUMBER),
	TO_STRING(Types.TO_STRING),
	DUPLICATE(Types.DUPLICATE),
	SWAP(Types.SWAP),
	GET_ATTRIBUTE(Types.GET_ATTRIBUTE),
	SET_ATTRIBUTE(Types.SET_ATTRIBUTE),
	INCREMENT(Types.INCREMENT),
	DECREMENT(Types.DECREMENT),
	EXECUTE_METHOD(Types.EXECUTE_METHOD),
	NEW_METHOD(Types.NEW_METHOD),
	INSTANCEOF(Types.INSTANCEOF),
	ENUMERATE_OBJECT(Types.ENUMERATE_OBJECT),	
	BITWISE_AND(Types.BITWISE_AND),	
	BITWISE_OR(Types.BITWISE_OR),	
	BITWISE_XOR(Types.BITWISE_XOR),
	SHIFT_LEFT(Types.SHIFT_LEFT),
	ARITH_SHIFT_RIGHT(Types.ARITH_SHIFT_RIGHT),
	SHIFT_RIGHT(Types.SHIFT_RIGHT),
	STRICT_EQUALS(Types.STRICT_EQUALS),
	GREATER(Types.GREATER),
	STRING_GREATER(Types.STRING_GREATER),
	EXTENDS(Types.EXTENDS);
	
	private static final Map<Integer,BasicAction>table 
		= new LinkedHashMap<Integer,BasicAction>();
	
	static {
		for (BasicAction action : values()) {
			table.put(action.type, action);
		}
	}
	
	public static BasicAction fromInt(int type) {
		return table.get(type);
	}
	
	private final int type;
	
	private BasicAction(int type) {
		this.type = type;
	}
	
	public BasicAction copy() {
		return this;
	}
	
	public int prepareToEncode(final SWFEncoder coder) {
		return 1;
	}

	public void decode(final SWFDecoder coder) throws CoderException {
		coder.adjustPointer(8);
	}

	public void encode(final SWFEncoder coder) throws CoderException {
		coder.writeByte(type);
		
	}
}
