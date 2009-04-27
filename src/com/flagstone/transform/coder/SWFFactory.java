package com.flagstone.transform.coder;

/**
 * The SWFFactory interface is used to generate objects when decoding a movie.
 */
public interface SWFFactory<T> {

	SWFFactory<T> copy();

	T getObject(final SWFDecoder coder, final Context context)
			throws CoderException;
}
