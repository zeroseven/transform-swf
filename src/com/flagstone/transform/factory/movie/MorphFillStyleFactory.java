package com.flagstone.transform.factory.movie;

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

	public FillStyle getObjectOfType(final int type) {

		FillStyle style;

		switch (type) {
		case FillStyle.SOLID:
			style = new MorphSolidFill();
			break;
		case FillStyle.LINEAR:
			style = new MorphGradientFill(FillStyle.LINEAR);
			break;
		case FillStyle.RADIAL:
			style = new MorphGradientFill(FillStyle.RADIAL);
			break;
		case FillStyle.TILED:
			style = new MorphBitmapFill(FillStyle.TILED);
			break;
		case FillStyle.CLIPPED:
			style = new MorphBitmapFill(FillStyle.CLIPPED);
			break;
		case FillStyle.UNSMOOTHED_TILED:
			style = new MorphBitmapFill(FillStyle.UNSMOOTHED_TILED);
			break;
		case FillStyle.UNSMOOTHED_CLIPPED:
			style = new MorphBitmapFill(FillStyle.UNSMOOTHED_CLIPPED);
			break;
		default:
			style = null; // NOPMD
			break;
		}
		return style;
	}
}
