package com.flagstone.transform.coder;


/**
 * The SWFFactory interface is used to generate objects when decoding a movie.
 */
public interface SWFFactory<T> {

	T getObject(final SWFDecoder coder, final SWFContext context) throws CoderException;
}
