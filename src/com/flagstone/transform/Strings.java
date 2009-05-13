/*
 * Strings.java
 * Transform
 *
 * Copyright (c) 2009 Flagstone Software Ltd. All rights reserved.
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
package com.flagstone.transform;

/**
 * The Strings class contains all the strings used in the Transform framework.
 */
// TODO(optimise) check all values are used.
public final class Strings {
    /** TODO(doc). */
    public static final String ACTION_TYPE_ERROR = "The type for a Action must be in the range 0..127";
    /** TODO(doc). */
    public static final String NEGATIVE_NUMBER = "The value cannot be negative.";
    /** TODO(doc). */
    public static final String IDENTIFIER_RANGE = "Unique identifier must be in the range 1..65535";
    /** TODO(doc). */
    public static final String LAYER_RANGE = "Layer number must be in the range 0..65535";
    /** TODO(doc). */
    public static final String COLOR_RANGE = "The value for the color channel must be in the range 0..255";
    /** TODO(doc). */
    public static final String REGISTER_RANGE = "The value for the register number must be in the range 0..255";
    /** TODO(doc). */
    public static final String RATIO_RANGE = "The value for the gradient ratio must be in the range 0..255";
    /** TODO(doc). */
    public static final String FRAME_RANGE = "The value for the frame number must be in the range 1..65535";
    /** TODO(doc). */
    public static final String COORDINATES_RANGE = "Coordinates must be in the range -65535..65535.";
    /** TODO(doc). */
    public static final String CHAR_CODE_RANGE = "Character code must be in the range 0..65535.";
    /** TODO(doc). */
    public static final String GLYPH_INDEX_RANGE = "The index for the glyph must be in the range 0..65535.";
    /** TODO(doc). */
    public static final String SIGNED_RANGE = "The value must be in the range -32768..32767.";
    /** TODO(doc). */
    public static final String UNSIGNED_RANGE = "The value must be in the range 0..65535.";
    /** TODO(doc). */
    public static final String DATA_IS_NULL = "Data cannot be null.";
    /** TODO(doc). */
    public static final String DATA_IS_EMPTY = "Data cannot be empty.";
    /** TODO(doc). */
    public static final String DATA_NOT_SET = "Data cannot be null or empty.";
    /** TODO(doc). */
    public static final String ACTIONS_MUST_END = "Encoded actions must end with an END action.";
    /** TODO(doc). */
    public static final String MAX_GRADIENTS = "The array cannot contain more than 15 gradients.";
    /** TODO(doc). */
    public static final String OBJECT_IS_NULL = "Object cannot be null.";
    /** TODO(doc). */
    public static final String STRING_IS_NULL = "String cannot be null.";
    /** TODO(doc). */
    public static final String STRING_IS_EMPTY = "String cannot be empty.";
    /** TODO(doc). */
    public static final String STRING_NOT_SET = "The String cannot be null or empty.";
    /** TODO(doc). */
    public static final String ARRAY_IS_NULL = "Array cannot be null.";
    /** TODO(doc). */
    public static final String TABLE_IS_NULL = "Table cannot be null.";
    /** TODO(doc). */
    public static final String SOUND_RATE_RANGE = "Sound rate must be one of: 5512, 11025, 22050 or 44100 Hertz.";
    /** TODO(doc). */
    public static final String CHANNEL_RANGE = "Number of channels must be 1 or 2.";
    /** TODO(doc). */
    public static final String SAMPLE_SIZE_RANGE = "Sample size must be 1 or 2 bytes.";
    /** TODO(doc). */
    public static final String EVENT_CODE_RANGE = "Event code must be in the range 1..65535.";
    /** TODO(doc). */
    public static final String BUTTON_RANGE = "The code for the button state must be in the range 1..15.";
    /** TODO(doc). */
    public static final String INVALID_FORMAT = "The format cannot be decoded.";
    /** TODO(doc). */
    public static final String INVALID_IMAGE = "The BufferedImage contains an unsupported format.";
    /** TODO(doc). */
    public static final String INVALID_ENCODING = "The character set is not supported: %s.";
    /** TODO(doc). */
    public static final String INVALID_FILLSTYLE = "The shape contains an supported fill style.";
    /** TODO(doc). */
    public static final String NOT_SWF_SIGNATURE = "Data does not start with a valid Flash signature.";
    /** TODO(doc). */
    public static final String NOT_FLV_SIGNATURE = "Data does not start with a valid Flash Video signature.";
    /** TODO(doc). */
    public static final String CANNOT_COMPRESS = "Cannot compress the data.";
    /** TODO(doc). */
    public static final String INVALID_POINTER = "The pointer must be set to a valid buffer index.";
    /** TODO(doc). */
    public static final String TIMESTAMP_RANGE = "The timestamp must be in the range 0..16777215.";
    /** TODO(doc). */
    public static final String BUTTON_TYPE_RANGE = "The type for a button must be either MENU or PUSH.";
    /** TODO(doc). */
    public static final String INVALID_OBJECT = "The object is not supported.";
    /** TODO(doc). */
    public static final String VALUE_NOT_SET = "The value is not set.";
    /** TODO(doc). */
    public static final String VALUE_RANGE = "The value contains an invalid value.";
    /** TODO(doc). */
    public static final String INVALID_FILTER = "The object contains an unsupported Filter.";

    private Strings() {
        // constructor used to suppress PMD warning, rather than remove rule.
    }
}
