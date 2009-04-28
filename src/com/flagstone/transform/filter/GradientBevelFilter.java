package com.flagstone.transform.filter;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.Filter;
import com.flagstone.transform.coder.FilterTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;
import com.flagstone.transform.fillstyle.Gradient;

public final class GradientBevelFilter implements Filter {

	public enum Mode {
		INNER, KNOCKOUT, TOP
	};

	private static final String FORMAT = "GradientBevelFilter: { " +
			"gradients=%s; blurX=%f; blurY=%f; passes=%d " +
			"angle=%d; disance=%d, strength=%d; mode=%s; passes=%d}";
	
	private List<Gradient> gradients;
	private int blurX;
	private int blurY;
	private int angle;
	private int distance;
	private int strength;
	private Mode mode;
	private int passes;

	public GradientBevelFilter(final SWFDecoder coder, final Context context) throws CoderException {
		coder.adjustPointer(8);
		final int count = coder.readByte();
		gradients = new ArrayList<Gradient>(count);
		Color color;
		int ratio;
		
		int colors = coder.getPointer();
		int ratios = coder.getPointer() + (count << 5);
		
		for (int i=0; i<count; i++) {
			coder.setPointer(colors);
			color = new Color(coder, context);
			colors = coder.getPointer();
			
			coder.setPointer(ratios);
			ratio = coder.readByte();
			ratios = coder.getPointer();
			
			gradients.add(new Gradient(ratio, color));
		}
		
		blurX = coder.readWord(4, true);
		blurY = coder.readWord(4, true);
		angle = coder.readWord(4, true);
		distance = coder.readWord(4, true);
		strength = coder.readWord(2, true);
		unpack(coder.readByte());
	}

	public GradientBevelFilter(List<Gradient> list, float blurX, float blurY, 
			float angle, float distance, float strength, Mode mode, int passes) {
		this.gradients = list;
		this.blurX = (int)(blurX * 65536.0f);
		this.blurY = (int)(blurY * 65536.0f);
		this.angle = (int)(angle * 65536.0f);
		this.distance = (int)(distance * 65536.0f);
		this.strength = (int)(strength * 256.0f);;
		this.mode = mode;
		this.passes = passes;
	}

	public GradientBevelFilter(final GradientBevelFilter object) {
		gradients = object.gradients;
		blurX = object.blurX;
		blurY = object.blurY;
		angle = object.angle;
		distance = object.distance;
		strength = object.strength;
		mode = object.mode;
		passes = object.passes;
	}
	
	public List<Gradient> getGradients() {
		return gradients;
	}
	
	public float getBlurX() {
		return blurX / 65536.0f;
	}

	public float getBlurY() {
		return blurY / 65536.0f;
	}
	
	public float getAngle() {
		return angle / 65536.0f;
	}
	
	public float getDistance() {
		return distance / 65536.0f;
	}
	
	public float getStrength() {
		return strength / 256.0f;
	}
	
	public int getPasses() {
		return passes;
	}

	public GradientBevelFilter copy() {
		return new GradientBevelFilter(this);
	}
	
	@Override
	public String toString() {
		return String.format(FORMAT, gradients, angle,distance, strength, 
				getBlurX(), getBlurY(), passes);
	}

	@Override
	public boolean equals(final Object object) {
		boolean result;
		GradientBevelFilter filter;

		if (object == null) {
			result = false;
		} else if (object == this) {
			result = true;
		} else if (object instanceof GradientBevelFilter) {
			filter = (GradientBevelFilter) object;
			result = gradients.equals(filter.gradients) && 
				blurX == filter.blurX && blurY == filter.blurY &&
				angle == filter.angle && distance == filter.distance &&
				strength == filter.strength && mode == filter.mode &&
				passes == filter.passes;
		} else {
			result = false;
		}
		return result;
	}

	@Override
	public int hashCode() {
		return (((((((gradients.hashCode()*31) + 
			blurX)*31 + blurY)*31 + angle*31) + distance)*31 +
			strength)*31 + mode.hashCode())*31 + passes;
	}

	public int prepareToEncode(final SWFEncoder coder, final Context context) {
		return 28;
	}

	public void encode(final SWFEncoder coder, final Context context) throws CoderException {
		coder.writeByte(FilterTypes.GRADIENT_BEVEL);
		coder.writeByte(gradients.size());
		
		for (Gradient gradient : gradients) {
			gradient.getColor().encode(coder, context);
		}
		
		for (Gradient gradient : gradients) {
			coder.writeByte(gradient.getRatio());
		}

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

	private void unpack(final int value) {
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
