package com.flagstone.transform.exception;

public class StringSizeException extends IllegalArgumentException {

    private static final long serialVersionUID = 7376207620131593711L;

    private int min;
    private int max;
    private int size;

    public StringSizeException(int min, int max, int size) {
        super("Minimum size: " + min + " Minimum size: "+ max
                + " Size: " + size);
        this.min = min;
        this.max = max;
        this.size = size;
    }
    
    public int getMin() {
        return min;
    }
    
    public int getMax() {
        return max;
    }
    
    public int getSize() {
        return size;
    }
}
