package com.flagstone.transform.exception;

public class IllegalArgumentRangeException extends IllegalArgumentException {

    private static final long serialVersionUID = -9208368642722953411L;
    
    private int lower;
    private int upper;
    private int value;

    public IllegalArgumentRangeException(int lower, int upper, int value) {
        super("Lower Bound: " + lower + " Upper Bound: "+ upper
                + " Value: " + value);
        this.lower = lower;
        this.upper = upper;
        this.value = value;
    }
    
    public int getLower() {
        return lower;
    }
    
    public int getUpper() {
        return upper;
    }
    
    public int getValue() {
        return value;
    }
}
