package com.flagstone.transform.coder;


public class FLVEncoder extends BigEndianEncoder 
{
	protected int type;
	public int version;
	protected String encoding;

	public FLVEncoder(int size)
	{
		super(size);
	}	
}
