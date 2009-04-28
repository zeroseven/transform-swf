package com.flagstone.transform.coder;

import com.flagstone.transform.Strings;
import com.flagstone.transform.filter.*;

/**
 * Factory is the default implementation of an SWFFactory which used to create
 * instances of Transform classes.
 */
public final class FilterDecoder implements SWFFactory<Filter> {

	public SWFFactory<Filter> copy() {
		return new FilterDecoder();
	}

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
					0, 0, Strings.INVALID_FILTER);
		}
		return filter;
	}
}
