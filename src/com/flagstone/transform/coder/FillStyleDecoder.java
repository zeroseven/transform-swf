package com.flagstone.transform.coder;

import com.flagstone.transform.Strings;
import com.flagstone.transform.fillstyle.BitmapFill;
import com.flagstone.transform.fillstyle.GradientFill;
import com.flagstone.transform.fillstyle.SolidFill;

/**
 * Factory is the default implementation of an SWFFactory which used to create
 * instances of Transform classes.
 */
public final class FillStyleDecoder implements SWFFactory<FillStyle> {

    public SWFFactory<FillStyle> copy() {
        return new FillStyleDecoder();
    }

    public FillStyle getObject(final SWFDecoder coder, final Context context)
            throws CoderException {

        FillStyle style;

        switch (coder.scanByte()) {
        case 0:
            style = new SolidFill(coder, context);
            break;
        case 16:
            style = new GradientFill(coder, context);
            break;
        case 18:
            style = new GradientFill(coder, context);
            break;
        case 0x40:
            style = new BitmapFill(coder);
            break;
        case 0x41:
            style = new BitmapFill(coder);
            break;
        case 0x42:
            style = new BitmapFill(coder);
            break;
        case 0x43:
            style = new BitmapFill(coder);
            break;
        default:
            throw new CoderException(getClass().getName(), coder.getPointer(),
                    0, 0, Strings.INVALID_FILLSTYLE);
        }
        return style;
    }
}
