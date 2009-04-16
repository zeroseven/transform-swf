package com.flagstone.transform.movie.filter;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.datatype.Color;

public final class BevelFilter implements Filter {
	
	public enum Mode {
		INNER, KNOCKOUT, TOP
	};
	
	private Color shadow;
	private Color highlight;
	private int blurX;
	private int blurY;
	private int angle;
	private int distance;
	private int strength;
	private Mode mode;
	private int passes;

	public BevelFilter(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		coder.adjustPointer(8);
		shadow = new Color(coder, context);
		highlight = new Color(coder, context);
		blurX = coder.readWord(4, true);
		blurY = coder.readWord(4, true);
		angle = coder.readWord(4, true);
		distance = coder.readWord(4, true);
		strength = coder.readWord(2, true);
		unpack(coder.readByte());
	}
	

	
	public BevelFilter(BevelFilter object) {
		shadow = object.shadow;
		highlight = object.highlight;
		blurX = object.blurX;
		blurY = object.blurY;
		angle = object.angle;
		distance = object.distance;
		strength = object.strength;
		mode = object.mode;
		passes = object.passes;
	}
	
	public BevelFilter copy() {
		return new BevelFilter(this);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		return 27;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		coder.writeByte(BEVEL);
		shadow.encode(coder, context);
		highlight.encode(coder, context);
		coder.writeWord(blurX, 4);
		coder.writeWord(blurY, 4);
		coder.writeWord(angle, 4);
		coder.writeWord(distance, 4);
		coder.writeWord(strength, 2);
		coder.writeByte(pack());
	}

	private int pack() {
		int value = passes;
		
		switch (mode) {
		case TOP:
			value |= 0x0030;
			break;
		case KNOCKOUT:
			value |= 0x0060;
			break;
		case INNER:
			value |= 0x00A0;
			break;
		}
		
		return value;
	}
	
	private void unpack(int value) {
		passes = value & 0x0F;
		
		switch ((value & 0x0D) >>> 4) {
		case 1:
			mode = Mode.TOP;
			break;
		case 4:
			mode = Mode.KNOCKOUT;
			break;
		case 8:
			mode = Mode.INNER;
			break;
		}
	}
}
