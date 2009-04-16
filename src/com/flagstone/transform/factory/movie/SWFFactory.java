package com.flagstone.transform.factory.movie;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;

/**
 * The SWFFactory interface is used to generate objects when decoding a movie.
 */
public interface SWFFactory<T> {

	T getObject(final SWFDecoder coder, final SWFContext context) throws CoderException;
}
