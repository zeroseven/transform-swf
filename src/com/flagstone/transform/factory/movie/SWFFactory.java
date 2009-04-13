package com.flagstone.transform.factory.movie;

/**
 * The SWFFactory interface is used to generate objects when decoding a movie.
 */
public interface SWFFactory<T> {

	/**
	 * Returns an instance of an object for the specified type.
	 * 
	 * @param type
	 *            the type that identifies the object class.
	 * 
	 * @return an instance of the class that corresponds to the type for this
	 *         factory.
	 */
	T getObjectOfType(final int type);
}
