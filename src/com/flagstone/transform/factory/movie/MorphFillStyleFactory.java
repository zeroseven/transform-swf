package com.flagstone.transform.factory.movie;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.movie.fillstyle.FillStyle;
import com.flagstone.transform.movie.fillstyle.MorphBitmapFill;
import com.flagstone.transform.movie.fillstyle.MorphGradientFill;
import com.flagstone.transform.movie.fillstyle.MorphSolidFill;

/**
 * Factory is the default implementation of an SWFFactory which used to create 
 * instances of Transform classes.
 */
@SuppressWarnings("PMD")
public final class MorphFillStyleFactory implements SWFFactory<FillStyle> {

	public FillStyle getObject(final SWFDecoder coder, final SWFContext context) throws CoderException {

		FillStyle style;

		switch (coder.scanByte()) {
		case 0:
			style = new MorphSolidFill(coder, context);
			break;
		case 16:
			style = new MorphGradientFill(coder, context);
			break;
		case 18:
			style = new MorphGradientFill(coder, context);
			break;
		case 0x40:
			style = new MorphBitmapFill(coder, context);
			break;
		case 0x41:
			style = new MorphBitmapFill(coder, context);
			break;
		case 0x42:
			style = new MorphBitmapFill(coder, context);
			break;
		case 0x43:
			style = new MorphBitmapFill(coder, context);
			break;
		default:
			style = null; //TODO(code) fix
			break;
		}
		return style;
	}
}
