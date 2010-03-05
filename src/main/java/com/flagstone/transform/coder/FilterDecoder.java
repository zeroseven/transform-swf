package com.flagstone.transform.coder;


import com.flagstone.transform.filter.BevelFilter;
import com.flagstone.transform.filter.BlurFilter;
import com.flagstone.transform.filter.ColorMatrixFilter;
import com.flagstone.transform.filter.ConvolutionFilter;
import com.flagstone.transform.filter.DropShadowFilter;
import com.flagstone.transform.filter.Filter;
import com.flagstone.transform.filter.GlowFilter;
import com.flagstone.transform.filter.GradientBevelFilter;
import com.flagstone.transform.filter.GradientGlowFilter;

/**
 * Factory is the default implementation of an SWFFactory which used to create
 * instances of Transform classes.
 */
//TODO(class)
public final class FilterDecoder implements SWFFactory<Filter> {

    /** TODO(method). */
    public SWFFactory<Filter> copy() {
        return new FilterDecoder();
    }

    /** TODO(method). */
    public Filter getObject(final SWFDecoder coder, final Context context)
            throws CoderException {

        Filter filter;

        switch (coder.scanByte()) {
        case 0:
            filter = new DropShadowFilter(coder, context);
            break;
        case 1:
            filter = new BlurFilter(coder, context);
            break;
        case 2:
            filter = new GlowFilter(coder, context);
            break;
        case 3:
            filter = new BevelFilter(coder, context);
            break;
        case 4:
            filter = new GradientGlowFilter(coder, context);
            break;
        case 5:
            filter = new ConvolutionFilter(coder, context);
            break;
        case 6:
            filter = new ColorMatrixFilter(coder);
            break;
        case 7:
            filter = new GradientBevelFilter(coder, context);
            break;
        default:
            throw new CoderException(getClass().getName(), coder.getPointer(),
                    0, 0, "Unsupported Filter");
        }
        return filter;
    }
}
