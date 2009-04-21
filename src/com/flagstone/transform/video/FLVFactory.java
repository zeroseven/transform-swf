package com.flagstone.transform.video;

import java.nio.ByteBuffer;

import com.flagstone.transform.coder.CoderException;

/**
 * The FLVFactory interface is used to generate objects when decoding a video.
 */
public interface FLVFactory<T> {

	T getObject(final ByteBuffer coder) throws CoderException;
}
