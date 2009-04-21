package com.flagstone.transform.coder;

import java.nio.ByteBuffer;


/**
 * The FLVFactory interface is used to generate objects when decoding a video.
 */
public interface FLVFactory<T> {

	T getObject(final ByteBuffer coder) throws CoderException;
}
