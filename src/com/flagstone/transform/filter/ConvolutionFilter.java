package com.flagstone.transform.filter;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

public final class ConvolutionFilter implements Filter {

	public ConvolutionFilter(final SWFDecoder coder, final Context context)
			throws CoderException {
		//TODO Implement
	}

	public ConvolutionFilter(final ConvolutionFilter object) {
		//TODO Implement
	}

	public ConvolutionFilter copy() {
		return new ConvolutionFilter(this);
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		//TODO Implement
		return 0;
	}

	public void encode(final SWFEncoder coder, final Context context)
			throws CoderException {
		//TODO Implement
	}
}
