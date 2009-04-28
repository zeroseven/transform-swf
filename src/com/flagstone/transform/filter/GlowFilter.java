package com.flagstone.transform.filter;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.Filter;
import com.flagstone.transform.coder.FilterTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;

public final class GlowFilter implements Filter {

    public enum Mode {
        INNER, KNOCKOUT
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

    public GlowFilter(final SWFDecoder coder, final Context context)
            throws CoderException {
        coder.adjustPointer(8);
        color = new Color(coder, context);
        blurX = coder.readWord(4, true);
        blurY = coder.readWord(4, true);
        strength = coder.readWord(2, true);
        unpack(coder.readByte());
    }

    public GlowFilter(final Color color, final float blurX, final float blurY,
            final float strength, final Mode mode, final int passes) {
        this.color = color;
        this.blurX = (int) (blurX * 65536.0f);
        this.blurY = (int) (blurY * 65536.0f);
        this.strength = (int) (strength * 256.0f);
        ;
        this.mode = mode;
        this.passes = passes;
    }

    public GlowFilter(final GlowFilter object) {
        color = object.color;
        blurX = object.blurX;
        blurY = object.blurY;
        strength = object.strength;
        mode = object.mode;
        passes = object.passes;
    }

    public Color getColor() {
        return color;
    }

    public float getBlurX() {
        return blurX / 65536.0f;
    }

    public float getBlurY() {
        return blurY / 65536.0f;
    }

    public float getStrength() {
        return strength / 256.0f;
    }

    public int getPasses() {
        return passes;
    }

    public GlowFilter copy() {
        return new GlowFilter(this);
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

    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        return 28;
    }

    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeByte(FilterTypes.GLOW);
        color.encode(coder, context);
        coder.writeWord(blurX, 4);
        coder.writeWord(blurY, 4);
        coder.writeWord(strength, 2);
        coder.writeByte(pack());
    }

    private int pack() {
        int value = passes;

        switch (mode) {
        case KNOCKOUT:
            value |= 0x0060;
            break;
        case INNER:
            value |= 0x00A0;
            break;
        }

        return value;
    }

    private void unpack(final int value) {
        passes = value & 0x0F;

        switch ((value & 0x0D) >>> 4) {
        case 4:
            mode = Mode.KNOCKOUT;
            break;
        case 8:
            mode = Mode.INNER;
            break;
        }
    }
}