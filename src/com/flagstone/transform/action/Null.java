package com.flagstone.transform.action;

/**
 * Null is a lightweight object that is used solely to allow null values to
 * be pushed onto the Flash Player stack.
 */
public final class Null {

	private static final String FORMAT = "Null";
	private static final Null instance = new Null();

	/**
	 * Returns a canonical Null object.
	 * 
	 * @return an object that can safely be shared among objects.
	 */
	public static Null getInstance() {
		return instance;
	}

	private Null() {
		// Singleton
	}
	
	@Override
	public String toString() {
		return FORMAT;
	}
}
