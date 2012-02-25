/* WrappedImage.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:19 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Adds functionality to the BufferedImage class, allowing
 *          manipulation of colour indices, making the RGB values
 *          invisible (in the case of Indexed Colour only).
 */

package com.droidcloud.viewer.client;

import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.ImageData;

public class WrappedImage {
	// static Logger logger = Logger.getLogger(RdesktopCanvas.class);
	// IndexColorModel cm = null;
	
	ImageData bi = null;
	
	int width;
	int height;

	public WrappedImage(int width, int height) {
		bi = (ImageData) ImageData.createObject();
		this.width = width;
		this.height = height;	
	}

	/*
	 * public WrappedImage(int arg0, int arg1, int arg2) { bi = new
	 * BufferedImage(arg0, arg1, arg2); bi = (ImageData)
	 * ImageData.createObject();
	 * 
	 * }
	 * 
	 * public WrappedImage(int arg0, int arg1, int arg2, IndexColorModel cm) {
	 * bi = new BufferedImage(arg0,arg1,BufferedImage.TYPE_INT_RGB);
	 * //super(arg0, arg1, BufferedImage.TYPE_INT_RGB); this.cm = cm; }
	 */
	
	public int getWidth() {
		return bi.getWidth();
	}

	public int getHeight() {
		return bi.getHeight();
	}

	// public BufferedImage getBufferedImage(){ return bi; }
	public ImageData getImageData() {
		return bi;
	}

	/*
	 * public Graphics getGraphics(){ return bi.getGraphics(); }
	 */

	public ImageData getSubimage(int x, int y, int width, int height) {
		// return bi.getSubimage(x,y,width,height);
		ImageData subData = (ImageData) ImageData.createObject();
		for (int i = x; i < bi.getWidth(); i++) {
			for (int j = y; j < bi.getHeight(); j++) {
				subData = setRGB(i - x, j - y, bi.getRedAt(i, j),
						bi.getGreenAt(i, j), bi.getBlueAt(i, j),
						bi.getAlphaAt(i, j), subData);
			}
		}
		return subData;
	}

	public static ImageData setRGB(int x, int y, int r, int g, int b,
			int alpha, ImageData data) {
		data.setAlphaAt(alpha, x, y);
		data.setRedAt(r, x, y);
		data.setGreenAt(g, x, y);
		data.setBlueAt(b, x, y);
		return data;
	}

	/**
	 * Force a colour to its true RGB representation (extracting from colour
	 * model if indexed colour)
	 * 
	 * @param color
	 * @return
	 */
	/*
	 * public int checkColor(int color){ if(cm != null) return cm.getRGB(color);
	 * return color; }
	 */
	/**
	 * Set the colour model for this Image
	 * 
	 * @param cm
	 *            Colour model for use with this image
	 */
	/*
	 * public void setIndexColorModel(IndexColorModel cm){ this.cm = cm; }
	 */
	/*
	 * public void setRGB(int x, int y, int color){ //if(x >= bi.getWidth() || x
	 * < 0 || y >= bi.getHeight() || y < 0) return;
	 * 
	 * // if (cm != null) color = cm.getRGB(color); bi.setRGB(x,y,color); }
	 */
	public void setRGB(int x, int y, int r, int g, int b, int alpha) {
		// if(x >= bi.getWidth() || x < 0 || y >= bi.getHeight() || y < 0)
		// return;

		// if (cm != null) color = cm.getRGB(color);
		bi = setRGB(x, y, r, g, b, alpha, bi);
	}

	public void setRGB(int x, int y, int color) {
		// if(x >= bi.getWidth() || x < 0 || y >= bi.getHeight() || y < 0)
		// return;

		// if (cm != null) color = cm.getRGB(color);
		int r = (color) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = (color >> 8) & 0xff;
		int alpha = (color >> 8) & 0xff;
		bi = setRGB(x, y, r, g, b, alpha, bi);
	}

	/**
	 * Apply a given array of colour values to an area of pixels in the image,
	 * do not convert for colour model
	 * 
	 * @param x
	 *            x-coordinate for left of area to set
	 * @param y
	 *            y-coordinate for top of area to set
	 * @param cx
	 *            width of area to set
	 * @param cy
	 *            height of area to set
	 * @param data
	 *            array of pixel colour values to apply to area
	 * @param offset
	 *            offset to pixel data in data
	 * @param w
	 *            width of a line in data (measured in pixels)
	 */
	public void setRGBNoConversion(int startX, int startY, int w, int h,
			int[] rgbArray, int offset, int scansize) {
		// bi.setRGB(x,y,cx,cy,data,offset,w);

		for (int x = startX; x < startX + w; x++) {
			for (int y = startY; y < startY + h; y++) {
				int pixel = rgbArray[offset + (y - startY) + (x - startX)
						* scansize];
				setRGB(x, y, pixel);
			}
		}
	}

	public void setRGB(int startX, int startY, int w, int h, int[] rgbArray,
			int offset, int scansize) {
		setRGBNoConversion(startX, startY, w, h, rgbArray, offset, scansize);
	}

	/*
	 * public int[] getRGB(int x, int y, int cx, int cy, int[] data, int offset,
	 * int width){ return bi.getRGB(x,y,cx,cy,data,offset,width); }
	 */
	public int getRGB(int x, int y) {
		// if(x >= this.getWidth() || x < 0 || y >= this.getHeight() || y < 0)
		// return 0;

		int rgb = 0x00000000;
		rgb = rgb << bi.getAlphaAt(x, y);
		rgb = rgb << bi.getRedAt(x, y);
		rgb = rgb << bi.getGreenAt(x, y);
		rgb = rgb << bi.getBlueAt(x, y);
		/*
		 * if(cm == null) return bi.getRGB(x,y); else{ int pix = bi.getRGB(x,y)
		 * & 0xFFFFFF; int[] vals = {(pix >> 16) & 0xFF,(pix >> 8) & 0xFF,(pix)
		 * & 0xFF}; int out = cm.getDataElement(vals,0); // if(cm.getRGB(out) !=
		 * bi.getRGB(x,y))
		 * //logger.info("Did not get correct colour value for color (" +
		 * Integer.toHexString(pix) + "), got (" + cm.getRGB(out) +
		 * ") instead"); return out; }
		 */
		return rgb;
	}

	int[] getRGB(int startX, int startY, int w, int h, int[] rgbArray,
			int offset, int scansize) {
		int[] data = new int[w * h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				data[w * x + y] = getRGB(x, y);
			}
		}
		return data;
	}

}
