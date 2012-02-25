package com.droidcloud.viewer.client;

public class SharedImageBackstore {
	
	private WrappedImage image;
	private boolean isChanged;
	
	public synchronized void set(int[] pixelArray, int width, int height, int left, int top, int cx, int cy)
	{
		this.image.setRGB(left, top, cx, cy, pixelArray, 0, width);
	}
	
	public synchronized WrappedImage get()
	{
		return this.image;		
	}

}
