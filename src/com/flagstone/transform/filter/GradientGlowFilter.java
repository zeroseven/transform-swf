package com.flagstone.transform.filter;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class GradientGlowFilter implements Filter {

	public GradientGlowFilter(final SWFDecoder coder, final Context context) throws CoderException {
		
	}
	
	public GradientGlowFilter(GradientGlowFilter object) {
		
	}
	
	public GradientGlowFilter copy() {
		return new GradientGlowFilter(this);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context)
	{
		return 0;
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException
	{
	}
}
