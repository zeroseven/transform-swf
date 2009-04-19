package com.flagstone.transform.movie.image;

import java.util.Arrays;

import com.flagstone.transform.movie.video.DefineVideo;

/**
 * ImageBlock is used to sub-divide an image into a set of blocks so they can 
 * be streamed using Screen Video. Image blocks are compared so only pixel 
 * information for the portions of the image that change are sent. 
 * 
 * <p>An image is divided by tiling the blocks across the image from top-left to
 * bottom right. If the image is not covered an integer number of blocks then
 * the size of the blocks along the right and bottom edges of the image are
 * reduced in size.</p>
 * 
 * @see DefineVideo
 */
//TODO(api) Move to video package ?
public final class ImageBlock implements Cloneable
{
	private int width;
	private int height;
	private byte[] block;

	/**
	 * Create a new image block with the specified width and height and image
	 * data. The image is compressed using the zip format.
	 * 
	 * @param width
	 *            the width of the block in pixels.
	 * @param height
	 *            the height of the block in pixels
	 * @param data
	 *            the pixels covered by the block, compressed using the zip
	 *            format.
	 */
	public ImageBlock(int width, int height, byte[] data)
	{
		this.width = width;
		this.height = height;
		block = data;
	}
	
	//TODO(doc)
	public ImageBlock(ImageBlock object) {
		width = object.width;
		height = object.height;
		block = Arrays.copyOf(object.block, object.block.length);
	}

	/**
	 * Return the width of the block. although the block size is specified in
	 * parent ScreenVideoPacket object the actual block size used may vary if
	 * the tiled array of blocks overlaps the edge of the image.
	 */
	public int getWidth()
	{
		return width;
	}
	
	/**
	 * Set the width of the block.
	 * 
	 * @param size the width of the block in pixels.
	 */
	public void setWidth(int size)
	{
		width = size;
	}

	/**
	 * Return the height of the block. although the block size is specified in
	 * parent ScreenVideoPacket object the actual block size used may vary if
	 * the tiled array of blocks overlaps the edge of the image.
	 */
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * Set the height of the block.
	 * 
	 * @param size the height of the block in pixels.
	 */
	public void setHeight(int size)
	{
		height = size;
	}

	/**
	 * Returns the zipped image data for the block.
	 */
	public byte[] getBlock()
	{
		return block;
	}
	
	/**
	 * Set the zipped image block
	 * 
	 * @param data the zipped image block.
	 */
	public void setBlock(byte[] data)
	{
		block = data;
	}

	/**
	 * When a ScreenVideo stream is created only the image blocks that change
	 * are included. The blocks that do not change are encoded as empty blocks
	 * which have width and height of zero and do not contain any image data.
	 * This convenience method is used to determine when an image block contains
	 * any valid image data.
	 * 
	 * @return true if the block covers an area of the image that changed or
	 *         false if no image data is included.
	 */
	public boolean isEmpty()
	{
		return width == 0 || height == 0 || block == null || block.length == 0;
	}

	public ImageBlock copy()
	{
		return new ImageBlock(this);
	}
}
