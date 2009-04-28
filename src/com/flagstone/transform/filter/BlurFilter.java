package com.flagstone.transform.filter;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.Filter;
import com.flagstone.transform.coder.FilterTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class BlurFilter implements Filter {

    private static final String FORMAT = "BlurFilter: { blurX=%f; blurY=%f; passes=%d }";

    private final int blurX;
    private final int blurY;
    private final int passes;

    public BlurFilter(final SWFDecoder coder, final Context context)
            throws CoderException {
        coder.readByte();
        blurX = coder.readWord(4, true);
        blurY = coder.readWord(4, true);
        passes = (coder.readByte() & 0x00FF) >>> 3;
    }

    public BlurFilter(final float blurX, final float blurY, final int passes) {
        this.blurX = (int) (blurX * 65536);
        this.blurY = (int) (blurY * 65536);

        if ((passes < 0) || (passes > 31)) {
            throw new IllegalArgumentException(Strings.VALUE_RANGE);
        }
        this.passes = passes;
    }

    public BlurFilter(final BlurFilter object) {
        blurX = object.blurX;
        blurY = object.blurY;
        passes = object.passes;
    }

    public float getBlurX() {
        return blurX / 65536.0f;
    }

    public float getBlurY() {
        return blurY / 65536.0f;
    }

    public int getPasses() {
        return passes;
    }

    public BlurFilter copy() {
        return new BlurFilter(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, getBlurX(), getBlurY(), passes);
    }

    @Override
    public boolean equals(final Object object) {
        boolean result;
        BlurFilter filter;

        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof BlurFilter) {
            filter = (BlurFilter) object;
            result = (blurX == filter.blurX) && (blurY == filter.blurY)
                    && (passes == filter.passes);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return ((blurX * 31) + blurY) * 31 + passes;
    }

    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        return 10;
    }

    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeByte(FilterTypes.BLUR);
        coder.writeWord(blurX, 4);
        coder.writeWord(blurY, 4);
        coder.writeByte(passes << 3);

    }
}
