package com.flagstone.transform.movie;

/**
 * The Strings class contains all the strings used in the Transform framework.
 */ 
public final class Strings
{
	public static final String ACTION_TYPE_OUT_OF_RANGE = "The type for a Action must be in the range 0..127";
	public static final String NUMBER_CANNOT_BE_NEGATIVE = "The value cannot be negative.";
	public static final String IDENTIFIER_OUT_OF_RANGE = "Unique identifier must be in the range 1..65535";
	public static final String LAYER_OUT_OF_RANGE = "Layer number must be in the range 0..65535";
	public static final String COLOR_OUT_OF_RANGE = "The value for the color channel must be in the range 0..255";
	public static final String REGISTER_OUT_OF_RANGE = "The value for the register number must be in the range 0..255";
	public static final String RATIO_OUT_OF_RANGE = "The value for the gradient ratio must be in the range 0..255";
	public static final String FRAME_OUT_OF_RANGE = "The value for the frame number must be in the range 1..65535";
	public static final String COORDINATES_OUT_OF_RANGE = "Coordinates must be in the range -65535..65535.";
	public static final String CHARACTER_CODE_OUT_OF_RANGE = "Character code must be in the range 0..65535.";
	public static final String GLYPH_INDEX_OUT_OF_RANGE = "The index for the glyph must be in the range 0..65535.";
	public static final String SIGNED_VALUE_OUT_OF_RANGE = "The value must be in the range -32768..32767.";
	public static final String UNSIGNED_VALUE_OUT_OF_RANGE = "The value must be in the range 0..65535.";
	public static final String DATA_CANNOT_BE_NULL = "Data cannot be null";
	public static final String DATA_CANNOT_BE_EMPTY = "Data cannot be empty";
	public static final String ENCODED_ACTIONS_MUST_END = "Encoded actions must end with an END action.";
	public static final String OBJECT_CANNOT_BE_NULL = "Object cannot be null.";
	public static final String STRING_CANNOT_BE_NULL = "String cannot be null.";
	public static final String STRING_CANNOT_BE_EMPTY = "String cannot be empty.";
	public static final String STRING_NOT_SET = "The String cannot be null or empty.";
	public static final String ARRAY_CANNOT_BE_NULL = "Array cannot be null.";
	public static final String TABLE_CANNOT_BE_NULL = "Table cannot be null.";
	public static final String SOUND_RATE_OUT_OF_RANGE = "Sound rate must be one of: 5512, 11025, 22050 or 44100 Hertz.";
	public static final String CHANNEL_COUNT_OUT_OF_RANGE = "Number of channels must be 1 or 2.";
	public static final String SAMPLE_SIZE_OUT_OF_RANGE = "Sample size must be 1 or 2 bytes.";
	public static final String EVENT_CODE_OUT_OF_RANGE = "Event code must be in the range 1..65535.";
	public static final String BUTTON_STATE_OUT_OF_RANGE = "The code for the button state must be in the range 1..15.";
	public static final String INVALID_SOUND_EVENT_CODE = "The event code for the sound is not valid.";
	public static final String UNSUPPORTED_FILE_FORMAT = "The format cannot be decoded.";
	public static final String UNSUPPORTED_IMAGE_FORMAT = "The BufferedImage contains an unsupported format.";
	public static final String UNSUPPORTED_ENCODING = "The character set is not supported: %s.";
	public static final String UNSUPPORTED_FILL_STYLE = "The shape contains an supported fill style.";
	public static final String INVALID_FLASH_SIGNATURE = "Data does not start with a valid Flash signature.";
	public static final String INVALID_FLASH_VIDEO_SIGNATURE = "Data does not start with a valid Flash Video signature.";
	public static final String CANNOT_COMPRESS = "Cannot compress the data.";
	public static final String CODER_POINTER_OUT_OF_BOUNDS = "The pointer must be set to a valid buffer index.";
	public static final String TIMESTAMP_OUT_OF_RANGE = "The timestamp must be in the range 0..16777215.";
	public static final String INVALID_BUTTON_TYPE = "The type for a button must be either MENU or PUSH.";
	
	private Strings()
	{
		// constructor used to suppress PMD warning, rather than remove rule.
	}
}
