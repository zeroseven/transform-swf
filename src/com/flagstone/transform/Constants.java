package com.flagstone.transform;

/**
 * Constants is where constants used throughout the framework are defined. The 
 * primary use is for numeric values so that literals (magic numbers) can be 
 * avoided. 
 */
public final class Constants {
    
    /**
     * The prime number used to generate the value in the hashCode() method 
     * of immutable objects.
     */
    public static final int PRIME = 31;
    
    private Constants() {
        // This class only contains constants.
    }
}
