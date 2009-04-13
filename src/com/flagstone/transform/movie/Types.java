/*
 * MovieTag.java
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

package com.flagstone.transform.movie;

/**
 * The SWF interface defines constants for all the different types of object
 * defined in the Flash file format specification.
 */
@SuppressWarnings({
	"PMD.LongVariable"
})
public interface Types {
	/** The type used to create ShowFrame objects. */
	int SHOW_FRAME = 1;
	/** The type used to create DefineShape objects. */
	int DEFINE_SHAPE = 2;
	/** The type used to create Free objects. */
	int FREE = 3;
	/** The type used to create Place objects. */
	int PLACE = 4;
	/** The type used to create Remove objects. */
	int REMOVE = 5;
	/** The type used to create DefineJPEGImage objects. */
	int DEFINE_JPEG_IMAGE = 6;
	/** The type used to create DefineButton objects. */
	int DEFINE_BUTTON = 7;
	/** The type used to create JPEGTables objects. */
	int JPEG_TABLES = 8;
	/** The type used to create SetBackgroundColor objects. */
	int SET_BACKGROUND_COLOR = 9;
	/** The type used to create DefineFont objects. */
	int DEFINE_FONT = 10;
	/** The type used to create DefineText objects. */
	int DEFINE_TEXT = 11;
	/** The type used to create DoAction objects. */
	int DO_ACTION = 12;
	/** The type used to create FontInfo objects. */
	int FONT_INFO = 13;
	/** The type used to create DefineSound objects. */
	int DEFINE_SOUND = 14;
	/** The type used to create StartSound objects. */
	int START_SOUND = 15;
	/** The type used to create ButtonSound objects. */
	int BUTTON_SOUND = 17;
	/** The type used to create SoundStreamHead objects. */
	int SOUND_STREAM_HEAD = 18;
	/** The type used to create SoundStreamBlock objects. */
	int SOUND_STREAM_BLOCK = 19;
	/** The type used to create DefineImage objects. */
	int DEFINE_IMAGE = 20;
	/** The type used to create DefineJPEGImage2 objects. */
	int DEFINE_JPEG_IMAGE_2 = 21;
	/** The type used to create DefineShape2 objects. */
	int DEFINE_SHAPE_2 = 22;
	/** The type used to create ButtonColorTransform objects. */
	int BUTTON_COLOR_TRANSFORM = 23;
	/** The type used to create Protect objects. */
	int PROTECT = 24;
	/** The type used to create PathsArePostscript objects. */
	int PATHS_ARE_POSTSCRIPT = 25;
	/** The type used to create Place2 objects. */
	int PLACE_2 = 26;
	/** The type used to create Remove2 objects. */
	int REMOVE_2 = 28;
	/** The type used to create DefineShape3 objects. */
	int DEFINE_SHAPE_3 = 32;
	/** The type used to create DefineText2 objects. */
	int DEFINE_TEXT_2 = 33;
	/** The type used to create DefineButton2 objects. */
	int DEFINE_BUTTON_2 = 34;
	/** The type used to create DefineJPEGImage3 objects. */
	int DEFINE_JPEG_IMAGE_3 = 35;
	/** The type used to create DefineImage2 objects. */
	int DEFINE_IMAGE_2 = 36;
	/** The type used to create DefineTextField objects. */
	int DEFINE_TEXT_FIELD = 37;
	/** The type used to create QuicktimeMovie objects. */
	int QUICKTIME_MOVIE = 38;
	/** The type used to create DefineMovieClip objects. */
	int DEFINE_MOVIE_CLIP = 39;
	/** The type used to create SerialNumber objects. */
	int SERIAL_NUMBER = 41;
	/** The type used to create FrameLabel objects. */
	int FRAME_LABEL = 43;
	/** The type used to create SoundStreamHead2 objects. */
	int SOUND_STREAM_HEAD_2 = 45;
	/** The type used to create DefineMorphShape objects. */
	int DEFINE_MORPH_SHAPE = 46;
	/** The type used to create DefineFont2 objects. */
	int DEFINE_FONT_2 = 48;
	/** The type used to create Export objects. */
	int EXPORT = 56;
	/** The type used to create Import objects. */
	int IMPORT = 57;
	/** The type used to create EnableDebugger objects. */
	int ENABLE_DEBUGGER = 58;
	/** The type used to create Initialise objects. */
	int INITIALIZE = 59;
	/** The type used to create DefineVideo objects. */
	int DEFINE_VIDEO = 60;
	/** The type used to create VideoFrame objects. */
	int VIDEO_FRAME = 61;
	/** The type used to create FontInfo2 objects. */
	int FONT_INFO_2 = 62;
	/** The type used to create EnableDebugger2 objects. */
	int ENABLE_DEBUGGER_2 = 64;
	/** The type used to create LimitScript objects. */
	int LIMIT_SCRIPT = 65;
	/** The type used to create TabOrder objects. */
	int TAB_ORDER = 66;
	/** The type used to create FileAttributes objects. */
	int FILE_ATTRIBUTES = 69;
	/** The type used to create Place3 objects. */
	int PLACE_3 = 70;
	/** The type used to create Import2 objects. */
	int IMPORT_2 = 71;
	/** The type used to create DefineFont3 objects. */
	int DEFINE_FONT_3 = 75;
	/** The type used to create Symbol objects. */
	int SYMBOL = 76;
	/** The type used to create MetaData objects. */
	int METADATA = 77;
	/** The type used to create DefineScalingGrid objects. */
	int DEFINE_SCALING_GRID = 78;
	/** The type used to create DoABC objects. */
	int DO_ABC = 82;
	/** The type used to create DefineShape4 objects. */
	int DEFINE_SHAPE_4 = 83;
	/** The type used to create DefineMorphShape2 objects. */
	int DEFINE_MORPH_SHAPE_2 = 84;
	/** The type used to create DefineScenes objects. */
	int SCENES_AND_LABELS = 86;
	/** The type used to create DefineBinaryData objects. */
	int DEFINE_BINARY_DATA = 87;
	/** The type used to create DefineFontName objects. */
	int DEFINE_FONT_NAME = 88;
	/** The type used to create StartSound2 objects. */
	int START_SOUND_2 = 89;
	
	/* 
	 * IMPORTANT: Only Actions for Actionscript 1.0 and 2.0 are included, actions
	 * defined for Actionscript 3.0 are not included.
	 */

	/** The type for creating the end of a sequence of actions. */
	int END = 0;
	/** The type for creating a NextFrame stack-based action. */
	int NEXT_FRAME = 4;
	/** The type for creating a PrevFrame stack-based action. */
	int PREV_FRAME = 5;
	/** The type for creating a Play stack-based action. */
	int PLAY = 6;
	/** The type for creating a Stop stack-based action. */
	int STOP = 7;
	/** The type for creating a ToggleQuality stack-based action. */
	int TOGGLE_QUALITY = 8;
	/** The type for creating a StopSounds stack-based action. */
	int STOP_SOUNDS = 9;
	/** The type for creating an IntegerAdd stack-based action. */
	int INTEGER_ADD = 10;
	/** The type for creating a Subtract stack-based action. */
	int SUBTRACT = 11;
	/** The type for creating a Multiply stack-based action. */
	int MULTIPLY = 12;
	/** The type for creating a Divide stack-based action. */
	int DIVIDE = 13;
	/** The type for creating an IntegerEquals stack-based action. */
	int INTEGER_EQUALS = 14;
	/** The type for creating an IntegerLess stack-based action. */
	int INTEGER_LESS = 15;
	/** The type for creating an And stack-based action. */
	int LOGICAL_AND = 16;
	/** The type for creating a Not stack-based action. */
	int LOGICAL_NOT = 18;
	/** The type for creating an Or stack-based action. */
	int LOGICAL_OR = 17;
	/** The type for creating a StringEquals stack-based action. */
	int STRING_EQUALS = 19;
	/** The type for creating a StringLength stack-based action. */
	int STRING_LENGTH = 20;
	/** The type for creating a StringExtract stack-based action. */
	int STRING_EXTRACT = 21;
	/** The type for creating a Pop stack-based action. */
	int POP = 23;
	/** The type for creating a ToInteger stack-based action. */
	int TO_INTEGER = 24;
	/** The type for creating a GetVariable stack-based action. */
	int GET_VARIABLE = 28;
	/** The type for creating a SetVariable stack-based action. */
	int SET_VARIABLE = 29;
	/** The type for creating a SetTarget2 stack-based action. */
	int SET_TARGET_2 = 32;
	/** The type for creating a StringAdd stack-based action. */
	int STRING_ADD = 33;
	/** The type for creating a GetProperty stack-based action. */
	int GET_PROPERTY = 34;
	/** The type for creating a SetProperty stack-based action. */
	int SET_PROPERTY = 35;
	/** The type for creating a CloneSprite stack-based action. */
	int CLONE_SPRITE = 36;
	/** The type for creating a RemoveSprite stack-based action. */
	int REMOVE_SPRITE = 37;
	/** The type for creating a Trace stack-based action. */
	int TRACE = 38;
	/** The type for creating a StartDrag stack-based action. */
	int START_DRAG = 39;
	/** The type for creating a EndDrag stack-based action. */
	int END_DRAG = 40;
	/** The type for creating a StringLess stack-based action. */
	int STRING_LESS = 41;
	/** The type for creating a Throw stack-based action. */
	int THROW = 42;
	/** The type for creating a Cast stack-based action. */
	int CAST = 43;
	/** The type for creating an Implements stack-based action. */
	int IMPLEMENTS = 44;
	/** The type for creating a RandomNumber stack-based action. */
	int RANDOM_NUMBER = 48;
	/** The type for creating a MBStringLength stack-based action. */
	int MB_STRING_LENGTH = 49;
	/** The type for creating a CharToAscii stack-based action. */
	int CHAR_TO_ASCII = 50;
	/** The type for creating a AsciiToChar stack-based action. */
	int ASCII_TO_CHAR = 51;
	/** The type for creating a GetTime stack-based action. */
	int GET_TIME = 52;
	/** The type for creating an MBStringExtract stack-based action. */
	int MB_STRING_EXTRACT = 53;
	/** The type for creating an MBCharToAscii stack-based action. */
	int MB_CHAR_TO_ASCII = 54;
	/** The type for creating an MBAsciiToChar stack-based action. */
	int MB_ASCII_TO_CHAR = 55;
	/** The type for creating a DeleteVariable stack-based action. */
	int DELETE_VARIABLE = 58;
	/** The type for creating a Delete stack-based action. */
	int DELETE = 59;
	/** The type for creating a InitVariable stack-based action. */
	int INIT_VARIABLE = 60;
	/** The type for creating a ExecuteFunction stack-based action. */
	int EXECUTE_FUNCTION = 61;
	/** The type for creating a Return stack-based action. */
	int RETURN = 62;
	/** The type for creating a Modulo stack-based action. */
	int MODULO = 63;
	/** The type for creating a NamedObject stack-based action. */
	int NAMED_OBJECT = 64;
	/** The type for creating a NewVariable stack-based action. */
	int NEW_VARIABLE = 65;
	/** The type for creating a NewArray stack-based action. */
	int NEW_ARRAY = 66;
	/** The type for creating a NewObject stack-based action. */
	int NEW_OBJECT = 67;
	/** The type for creating a GetType stack-based action. */
	int GET_TYPE = 68;
	/** The type for creating a GetTarget stack-based action. */
	int GET_TARGET = 69;
	/** The type for creating an Enumerate stack-based action. */
	int ENUMERATE = 70;
	/** The type for creating an Add stack-based action. */
	int ADD = 71;
	/** The type for creating a Less stack-based action. */
	int LESS = 72;
	/** The type for creating an Equals stack-based action. */
	int EQUALS = 73; // NOPMD
	/** The type for creating a ToNumber stack-based action. */
	int TO_NUMBER = 74;
	/** The type for creating a ToString stack-based action. */
	int TO_STRING = 75;
	/** The type for creating a Duplicate stack-based action. */
	int DUPLICATE = 76;
	/** The type for creating a Swap stack-based action. */
	int SWAP = 77;
	/** The type for creating a GetAttribute stack-based action. */
	int GET_ATTRIBUTE = 78;
	/** The type for creating a SetAttribute stack-based action. */
	int SET_ATTRIBUTE = 79;
	/** The type for creating an Increment stack-based action. */
	int INCREMENT = 80;
	/** The type for creating a Decrement stack-based action. */
	int DECREMENT = 81;
	/** The type for creating an ExecuteMethod stack-based action. */
	int EXECUTE_METHOD = 82;
	/** The type for creating a NewMethod stack-based action. */
	int NEW_METHOD = 83;
	/** The type for creating an InstanceOf stack-based action. */
	int INSTANCEOF = 84;
	/** The type for creating an EnumerateObject stack-based action. */
	int ENUMERATE_OBJECT = 85;
	/** The type for creating a BitwiseAnd stack-based action. */
	int BITWISE_AND = 96;
	/** The type for creating a BitwiseOr stack-based action. */
	int BITWISE_OR = 97;
	/** The type for creating a BitwiseXOr stack-based action. */
	int BITWISE_XOR = 98;
	/** The type for creating a LogicalShiftLeft stack-based action. */
	int SHIFT_LEFT = 99;
	/** The type for creating an ArithmeticShiftRight stack-based action. */
	int ARITH_SHIFT_RIGHT = 100;
	/** The type for creating a LogicalShiftRight stack-based action. */
	int SHIFT_RIGHT = 101;
	/** The type for creating a StrictEquals stack-based action. */
	int STRICT_EQUALS = 102;
	/** The type for creating a Greater stack-based action. */
	int GREATER = 103;
	/** The type for creating a StringGreater stack-based action. */
	int STRING_GREATER = 104;
	/** The type for creating an Extends stack-based action. */
	int EXTENDS = 105;
	/** The type for creating a GotoFrame action. */
	int GOTO_FRAME = 129;
	/** The type for creating a GetUrl action. */
	int GET_URL = 131;
	/** The type for creating a RegisterCopy action. */
	int REGISTER_COPY = 135;
	/** The type for creating a Table action. */
	int TABLE = 136;
	/** The type for creating a WaitForFrame action. */
	int WAIT_FOR_FRAME = 138;
	/** The type for creating a SetTarget action. */
	int SET_TARGET = 139;
	/** The type for creating a GotoLabel action. */
	int GOTO_LABEL = 140;
	/** The type for creating a WaitForFrame2 action. */
	int WAIT_FOR_FRAME_2 = 141;
	/** The type for creating a NewFunction2 action. */
	int NEW_FUNCTION_2 = 142;
	/** The type for creating a ExceptionHandler action. */
	int EXCEPTION_HANDLER = 143;
	/** The type for creating a With action. */
	int WITH = 148;
	/** The type for creating a Push action. */
	int PUSH = 150;
	/** The type for creating a Jump action. */
	int JUMP = 153;
	/** The type for creating a GetUrl2 action. */
	int GET_URL_2 = 154;
	/** The type for creating an If action. */
	int IF = 157; // NOPMD
	/** The type for creating a Call action. */
	int CALL = 158;
	/** The type for creating a GotoFrame2 action. */
	int GOTO_FRAME_2 = 159;
	/** The type for creating a NewFunction action. */
	int NEW_FUNCTION = 155;

}
