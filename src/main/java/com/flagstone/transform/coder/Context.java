package com.flagstone.transform.coder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contexts are used to pass information between objects when they are being
 * encoded or decoded.
 */
public final class Context {

    /** TODO(doc). */
    public static final int VERSION = 1;
    /** TODO(doc). */
    public static final int TYPE = 2;
    /** TODO(doc). */
   public static final int TRANSPARENT = 3;
    /** TODO(doc). */
    public static final int WIDE_CODES = 4;
    /** TODO(doc). */
    public static final int ARRAY_EXTENDED = 8;
    /** TODO(doc). */
    public static final int POSTSCRIPT = 9;
    /** TODO(doc). */
    public static final int SCALING_STROKE = 10;
    /** TODO(doc). */
    public static final int FILL_SIZE = 11;
    /** TODO(doc). */
    public static final int LINE_SIZE = 12;
    /** TODO(doc). */
    public static final int ADVANCE_SIZE = 13;
    /** TODO(doc). */
    public static final int GLYPH_SIZE = 14;
    /** TODO(doc). */
    public static final int SHAPE_SIZE = 15;

    private DecoderRegistry registry;
    private Map<Integer, Integer> variables;

    /** TODO(method). */
    public Context() {
        variables = new LinkedHashMap<Integer, Integer>();
    }

    /** TODO(method). */
    public DecoderRegistry getRegistry() {
        return registry;
    }

    /** TODO(method). */
    public void setRegistry(final DecoderRegistry registry) {
        this.registry = registry;
    }

    /** TODO(method). */
    public Map<Integer, Integer> getVariables() {
        return variables;
    }

    /** TODO(method). */
    public void setVariables(final Map<Integer, Integer> map) {
        variables = map;
    }

    /** TODO(method). */
    public Context put(final Integer key, final Integer value) {
        variables.put(key, value);
        return this;
    }
}
