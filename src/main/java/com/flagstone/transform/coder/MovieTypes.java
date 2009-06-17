/*
 * MovieTypes.java
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
package com.flagstone.transform.coder;

/**
 * MovieTypes defines the constants that identify a MovieTag when it is encoded 
 * according to the Flash file format specification.
 */
public final class MovieTypes {
    /** The type used to identify ShowFrame objects when they are encoded. */
    public static final int SHOW_FRAME = 1;
    /** The type used to identify DefineShape objects when they are encoded. */
    public static final int DEFINE_SHAPE = 2;
    /** The type used to identify Free objects when they are encoded. */
    public static final int FREE = 3;
    /** The type used to identify Place objects when they are encoded. */
    public static final int PLACE = 4;
    /** The type used to identify Remove objects when they are encoded. */
    public static final int REMOVE = 5;
    /** The type used to identify DefineJPEGImage objects when they are encoded. */
    public static final int DEFINE_JPEG_IMAGE = 6;
    /** The type used to identify DefineButton objects when they are encoded. */
    public static final int DEFINE_BUTTON = 7;
    /** The type used to identify JPEGTables objects when they are encoded. */
    public static final int JPEG_TABLES = 8;
    /** The type used to identify SetBackgroundColor objects when they are encoded. */
    public static final int SET_BACKGROUND_COLOR = 9;
    /** The type used to identify DefineFont objects when they are encoded. */
    public static final int DEFINE_FONT = 10;
    /** The type used to identify DefineText objects when they are encoded. */
    public static final int DEFINE_TEXT = 11;
    /** The type used to identify DoAction objects when they are encoded. */
    public static final int DO_ACTION = 12;
    /** The type used to identify FontInfo objects when they are encoded. */
    public static final int FONT_INFO = 13;
    /** The type used to identify DefineSound objects when they are encoded. */
    public static final int DEFINE_SOUND = 14;
    /** The type used to identify StartSound objects when they are encoded. */
    public static final int START_SOUND = 15;
    /** The type used to identify ButtonSound objects when they are encoded. */
    public static final int BUTTON_SOUND = 17;
    /** The type used to identify SoundStreamHead objects when they are encoded. */
    public static final int SOUND_STREAM_HEAD = 18;
    /** The type used to identify SoundStreamBlock objects when they are encoded. */
    public static final int SOUND_STREAM_BLOCK = 19;
    /** The type used to identify DefineImage objects when they are encoded. */
    public static final int DEFINE_IMAGE = 20;
    /** The type used to identify DefineJPEGImage2 objects when they are encoded. */
    public static final int DEFINE_JPEG_IMAGE_2 = 21;
    /** The type used to identify DefineShape2 objects when they are encoded. */
    public static final int DEFINE_SHAPE_2 = 22;
    /** The type used to identify ButtonColorTransform objects when they are encoded. */
    public static final int BUTTON_COLOR_TRANSFORM = 23;
    /** The type used to identify Protect objects when they are encoded. */
    public static final int PROTECT = 24;
    /** The type used to identify PathsArePostscript objects when they are encoded. */
    public static final int PATHS_ARE_POSTSCRIPT = 25;
    /** The type used to identify Place2 objects when they are encoded. */
    public static final int PLACE_2 = 26;
    /** The type used to identify Remove2 objects when they are encoded. */
    public static final int REMOVE_2 = 28;
    /** The type used to identify DefineShape3 objects when they are encoded. */
    public static final int DEFINE_SHAPE_3 = 32;
    /** The type used to identify DefineText2 objects when they are encoded. */
    public static final int DEFINE_TEXT_2 = 33;
    /** The type used to identify DefineButton2 objects when they are encoded. */
    public static final int DEFINE_BUTTON_2 = 34;
    /** The type used to identify DefineJPEGImage3 objects when they are encoded. */
    public static final int DEFINE_JPEG_IMAGE_3 = 35;
    /** The type used to identify DefineImage2 objects when they are encoded. */
    public static final int DEFINE_IMAGE_2 = 36;
    /** The type used to identify DefineTextField objects when they are encoded. */
    public static final int DEFINE_TEXT_FIELD = 37;
    /** The type used to identify QuicktimeMovie objects when they are encoded. */
    public static final int QUICKTIME_MOVIE = 38;
    /** The type used to identify DefineMovieClip objects when they are encoded. */
    public static final int DEFINE_MOVIE_CLIP = 39;
    /** The type used to identify SerialNumber objects when they are encoded. */
    public static final int SERIAL_NUMBER = 41;
    /** The type used to identify FrameLabel objects when they are encoded. */
    public static final int FRAME_LABEL = 43;
    /** The type used to identify SoundStreamHead2 objects when they are encoded. */
    public static final int SOUND_STREAM_HEAD_2 = 45;
    /** The type used to identify DefineMorphShape objects when they are encoded. */
    public static final int DEFINE_MORPH_SHAPE = 46;
    /** The type used to identify DefineFont2 objects when they are encoded. */
    public static final int DEFINE_FONT_2 = 48;
    /** The type used to identify Export objects when they are encoded. */
    public static final int EXPORT = 56;
    /** The type used to identify Import objects when they are encoded. */
    public static final int IMPORT = 57;
    /** The type used to identify EnableDebugger objects when they are encoded. */
    public static final int ENABLE_DEBUGGER = 58;
    /** The type used to identify Initialise objects when they are encoded. */
    public static final int INITIALIZE = 59;
    /** The type used to identify DefineVideo objects when they are encoded. */
    public static final int DEFINE_VIDEO = 60;
    /** The type used to identify VideoFrame objects when they are encoded. */
    public static final int VIDEO_FRAME = 61;
    /** The type used to identify FontInfo2 objects when they are encoded. */
    public static final int FONT_INFO_2 = 62;
    /** The type used to identify EnableDebugger2 objects when they are encoded. */
    public static final int ENABLE_DEBUGGER_2 = 64;
    /** The type used to identify LimitScript objects when they are encoded. */
    public static final int LIMIT_SCRIPT = 65;
    /** The type used to identify TabOrder objects when they are encoded. */
    public static final int TAB_ORDER = 66;
    /** The type used to identify FileAttributes objects when they are encoded. */
    public static final int FILE_ATTRIBUTES = 69;
    /** The type used to identify Place3 objects when they are encoded. */
    public static final int PLACE_3 = 70;
    /** The type used to identify Import2 objects when they are encoded. */
    public static final int IMPORT_2 = 71;
    /** The type used to identify FontAlignment objects. */
    public static final int FONT_ALIGNMENT = 73;
    /** The type used to identify TextSettings objects. */
    public static final int TEXT_SETTINGS = 74;
    /** The type used to identify DefineFont3 objects when they are encoded. */
    public static final int DEFINE_FONT_3 = 75;
    /** The type used to identify SymbolClass objects when they are encoded. */
    public static final int SYMBOL = 76;
    /** The type used to identify MovieMetaData objects when they are encoded. */
    public static final int METADATA = 77;
    /** The type used to identify ScalingGrid objects when they are encoded. */
    public static final int DEFINE_SCALING_GRID = 78;
    /** The type used to identify DoABC objects when they are encoded. */
    public static final int DO_ABC = 82;
    /** The type used to identify DefineShape4 objects when they are encoded. */
    public static final int DEFINE_SHAPE_4 = 83;
    /** The type used to identify DefineMorphShape2 objects when they are encoded. */
    public static final int DEFINE_MORPH_SHAPE_2 = 84;
    /** The type used to identify ScenesAndLabels objects when they are encoded. */
    public static final int SCENES_AND_LABELS = 86;
    /** The type used to identify DefineData objects when they are encoded. */
    public static final int DEFINE_BINARY_DATA = 87;
    /** The type used to identify DefineFontName objects when they are encoded. */
    public static final int DEFINE_FONT_NAME = 88;
    /** The type used to identify StartSound2 objects when they are encoded. */
    public static final int START_SOUND_2 = 89;

    private MovieTypes() {
        // Class only contains constants
    }
}
