package com.flagstone.transform.movie.filter;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class GradientGlowFilter implements Filter {

	public GradientGlowFilter(GradientGlowFilter object) {
		
	}
	
	public GradientGlowFilter copy() {
		return new GradientGlowFilter(this);
	}

	@Override
	public int prepareToEncode(final SWFEncoder coder)
	{
		return 0;
	}

	@Override
	public void encode(final SWFEncoder coder) throws CoderException
	{
	}

	@Override
	public void decode(final SWFDecoder coder) throws CoderException
	{
	}
}
