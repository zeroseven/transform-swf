package com.flagstone.transform;

/**
 * The Strings class contains all the strings used in the Transform framework.
 */
// TODO(optimise) check all values are used.
public final class Strings {
	public static final String ACTION_TYPE_ERROR = "The type for a Action must be in the range 0..127";
	public static final String NEGATIVE_NUMBER = "The value cannot be negative.";
	public static final String IDENTIFIER_RANGE = "Unique identifier must be in the range 1..65535";
	public static final String LAYER_RANGE = "Layer number must be in the range 0..65535";
	public static final String COLOR_RANGE = "The value for the color channel must be in the range 0..255";
	public static final String REGISTER_RANGE = "The value for the register number must be in the range 0..255";
	public static final String RATIO_RANGE = "The value for the gradient ratio must be in the range 0..255";
	public static final String FRAME_RANGE = "The value for the frame number must be in the range 1..65535";
	public static final String COORDINATES_RANGE = "Coordinates must be in the range -65535..65535.";
	public static final String CHAR_CODE_RANGE = "Character code must be in the range 0..65535.";
	public static final String GLYPH_INDEX_RANGE = "The index for the glyph must be in the range 0..65535.";
	public static final String SIGNED_RANGE = "The value must be in the range -32768..32767.";
	public static final String UNSIGNED_RANGE = "The value must be in the range 0..65535.";
	public static final String DATA_IS_NULL = "Data cannot be null.";
	public static final String DATA_IS_EMPTY = "Data cannot be empty.";
	public static final String DATA_NOT_SET = "Data cannot be null or empty.";
	public static final String ACTIONS_MUST_END = "Encoded actions must end with an END action.";
	public static final String MAX_GRADIENTS = "The array cannot contain more than 15 gradients.";
	public static final String OBJECT_IS_NULL = "Object cannot be null.";
	public static final String STRING_IS_NULL = "String cannot be null.";
	public static final String STRING_IS_EMPTY = "String cannot be empty.";
	public static final String STRING_NOT_SET = "The String cannot be null or empty.";
	public static final String ARRAY_IS_NULL = "Array cannot be null.";
	public static final String TABLE_IS_NULL = "Table cannot be null.";
	public static final String SOUND_RATE_RANGE = "Sound rate must be one of: 5512, 11025, 22050 or 44100 Hertz.";
	public static final String CHANNEL_RANGE = "Number of channels must be 1 or 2.";
	public static final String SAMPLE_SIZE_RANGE = "Sample size must be 1 or 2 bytes.";
	public static final String EVENT_CODE_RANGE = "Event code must be in the range 1..65535.";
	public static final String BUTTON_RANGE = "The code for the button state must be in the range 1..15.";
	public static final String INVALID_FORMAT = "The format cannot be decoded.";
	public static final String INVALID_IMAGE = "The BufferedImage contains an unsupported format.";
	public static final String INVALID_ENCODING = "The character set is not supported: %s.";
	public static final String INVALID_FILLSTYLE = "The shape contains an supported fill style.";
	public static final String NOT_SWF_SIGNATURE = "Data does not start with a valid Flash signature.";
	public static final String NOT_FLV_SIGNATURE = "Data does not start with a valid Flash Video signature.";
	public static final String CANNOT_COMPRESS = "Cannot compress the data.";
	public static final String INVALID_POINTER = "The pointer must be set to a valid buffer index.";
	public static final String TIMESTAMP_RANGE = "The timestamp must be in the range 0..16777215.";
	public static final String BUTTON_TYPE_RANGE = "The type for a button must be either MENU or PUSH.";
	public static final String INVALID_OBJECT = "The object is not supported.";
	public static final String VALUE_NOT_SET = "The value is not set.";
	public static final String VALUE_RANGE = "The value contains an invalid value.";
	public static final String INVALID_FILTER = "The object contains an unsupported Filter.";

	private Strings() {
		// constructor used to suppress PMD warning, rather than remove rule.
	}
}
