package com.flagstone.transform.exception;

import java.util.Arrays;

public class IllegalArgumentValueException extends IllegalArgumentException {

    private static final long serialVersionUID = 3748031731035981638L;

    private int[] set;
    private int value;

    public IllegalArgumentValueException(int[] set, int value) {
        super("Valid values: " + set + " Value: " + value);
        this.set = Arrays.copyOf(set, set.length);
        this.value = value;
    }
    
    public int[] getSet() {
        return Arrays.copyOf(set, set.length);
    }
     
    public int getValue() {
        return value;
    }
}
