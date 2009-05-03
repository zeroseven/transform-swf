package com.flagstone.transform.filter;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.Filter;
import com.flagstone.transform.coder.FilterTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;

/** TODO(class). */
public final class GlowFilter implements Filter {

    /** TODO(class). */
    public enum Mode {
        /** TODO(doc). */
        INNER,
        /** TODO(doc). */
        KNOCKOUT
    };

    private static final String FORMAT = "GlowFilter: { "
            + "color=%s; blurX=%f; blurY=%f; passes=%d "
            + "angle=%d; disance=%d, strength=%d; mode=%s; passes=%d}";

    private final Color color;
    private final int blurX;
    private final int blurY;
    private final int strength;
    private Mode mode;
    private int passes;

    /**
     * Creates and initialises a GlowFilter object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @param context
     *            a Context object used to manage the decoders for different
     *            type of object and to pass information on how objects are
     *            decoded.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public GlowFilter(final SWFDecoder coder, final Context context)
            throws CoderException {
        coder.adjustPointer(8);
        color = new Color(coder, context);
        blurX = coder.readWord(4, true);
        blurY = coder.readWord(4, true);
        strength = coder.readWord(2, true);
        unpack(coder.readByte());
    }

    /** TODO(method). */
    public GlowFilter(final Color color, final float blurX, final float blurY,
            final float strength, final Mode mode, final int passes) {
        this.color = color;
        this.blurX = (int) (blurX * 65536.0f);
        this.blurY = (int) (blurY * 65536.0f);
        this.strength = (int) (strength * 256.0f);
        this.mode = mode;
        this.passes = passes;
    }

    /** TODO(method). */
    public Color getColor() {
        return color;
    }

    /** TODO(method). */
    public float getBlurX() {
        return blurX / 65536.0f;
    }

    /** TODO(method). */
    public float getBlurY() {
        return blurY / 65536.0f;
    }

    /** TODO(method). */
    public float getStrength() {
        return strength / 256.0f;
    }

    /** TODO(method). */
    public int getPasses() {
        return passes;
    }

    @Override
    public String toString() {
        return String.format(FORMAT, color, getBlurX(), getBlurY(), strength,
                passes);
    }

    @Override
    public boolean equals(final Object object) {
        boolean result;
        GlowFilter filter;

        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof GlowFilter) {
            filter = (GlowFilter) object;
            result = color.equals(filter.color) && (blurX == filter.blurX)
                    && (blurY == filter.blurY) && (strength == filter.strength)
                    && (mode == filter.mode) && (passes == filter.passes);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return (((((color.hashCode() * 31) + blurX) * 31 + blurY) * 31 + strength) * 31 + mode
                .hashCode())
                * 31 + passes;
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        return 28;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeByte(FilterTypes.GLOW);
        color.encode(coder, context);
        coder.writeWord(blurX, 4);
        coder.writeWord(blurY, 4);
        coder.writeWord(strength, 2);
        coder.writeByte(pack());
    }

    private int pack() throws CoderException {
        int value = passes;

        switch (mode) {
        case KNOCKOUT:
            value |= 0x0060;
            break;
        case INNER:
            value |= 0x00A0;
            break;
        default:
            throw new CoderException(getClass().getName(), 0, 0, 0,
                    Strings.INVALID_ENCODING);
        }

        return value;
    }

    private void unpack(final int value) throws CoderException {
        passes = value & 0x0F;

        switch ((value & 0x0D) >>> 4) {
        case 4:
            mode = Mode.KNOCKOUT;
            break;
        case 8:
            mode = Mode.INNER;
            break;
        default:
            throw new CoderException(getClass().getName(), 0, 0, 0,
                    Strings.INVALID_ENCODING);
        }
    }
}