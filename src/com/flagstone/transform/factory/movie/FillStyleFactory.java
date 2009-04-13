package com.flagstone.transform.factory.movie;

import com.flagstone.transform.movie.fillstyle.BitmapFill;
import com.flagstone.transform.movie.fillstyle.FillStyle;
import com.flagstone.transform.movie.fillstyle.GradientFill;
import com.flagstone.transform.movie.fillstyle.SolidFill;

/**
 * Factory is the default implementation of an SWFFactory which used to create 
 * instances of Transform classes.
 */
@SuppressWarnings("PMD")
public final class FillStyleFactory implements SWFFactory<FillStyle> {

	public FillStyle getObjectOfType(final int type) {

		FillStyle style;

		switch (type) {
		case FillStyle.SOLID:
			style = new SolidFill();
			break;
		case FillStyle.LINEAR:
			style = new GradientFill(FillStyle.LINEAR);
			break;
		case FillStyle.RADIAL:
			style = new GradientFill(FillStyle.RADIAL);
			break;
		case FillStyle.TILED:
			style = new BitmapFill(FillStyle.TILED);
			break;
		case FillStyle.CLIPPED:
			style = new BitmapFill(FillStyle.CLIPPED);
			break;
		case FillStyle.UNSMOOTHED_TILED:
			style = new BitmapFill(FillStyle.UNSMOOTHED_TILED);
			break;
		case FillStyle.UNSMOOTHED_CLIPPED:
			style = new BitmapFill(FillStyle.UNSMOOTHED_CLIPPED);
			break;
		default:
			style = null; // NOPMD
			break;
		}
		return style;
	}
}
