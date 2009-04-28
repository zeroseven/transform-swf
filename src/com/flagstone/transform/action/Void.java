package com.flagstone.transform.action;

/**
 * Void is a lightweight object that is used solely to allow void values to be
 * pushed onto the Flash Player stack.
 */
public final class Void {

    private static final String FORMAT = "Void";
    private static final Void INSTANCE = new Void();

    /**
     * Returns a canonical Void object.
     * 
     * @return an object that can safely be shared among objects.
     */
    public static Void getInstance() {
        return INSTANCE;
    }

    private Void() {
        // Singleton
    }

    @Override
    public String toString() {
        return FORMAT;
    }
}
