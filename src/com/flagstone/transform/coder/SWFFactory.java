package com.flagstone.transform.coder;

/**
 * The SWFFactory interface is used to generate objects when decoding a movie.
 */
public interface SWFFactory<T> {

    /** TODO(method). */
    SWFFactory<T> copy();

    /** TODO(method). */
    T getObject(final SWFDecoder coder, final Context context)
            throws CoderException;
}
