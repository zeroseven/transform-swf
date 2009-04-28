package com.flagstone.transform.coder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contexts are used to pass information between objects when they are being
 * encoded or decoded.
 */
public final class Context {

    public final static int VERSION = 1;
    public final static int TYPE = 2;
    public final static int TRANSPARENT = 3;
    public final static int WIDE_CODES = 4;
    public final static int ARRAY_EXTENDED = 8;
    public final static int POSTSCRIPT = 9;
    public final static int SCALING_STROKE = 10;
    public final static int FILL_SIZE = 11;
    public final static int LINE_SIZE = 12;
    public final static int ADVANCE_SIZE = 13;
    public final static int GLYPH_SIZE = 14;
    public final static int SHAPE_SIZE = 15;

    private DecoderRegistry registry;
    private Map<Integer, Integer> variables;

    public Context() {
        variables = new LinkedHashMap<Integer, Integer>();
    }

    public DecoderRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(final DecoderRegistry registry) {
        this.registry = registry;
    }

    public Map<Integer, Integer> getVariables() {
        return variables;
    }

    public void setVariables(final Map<Integer, Integer> map) {
        variables = map;
    }

    public Context put(final Integer key, final Integer value) {
        variables.put(key, value);
        return this;
    }
}
