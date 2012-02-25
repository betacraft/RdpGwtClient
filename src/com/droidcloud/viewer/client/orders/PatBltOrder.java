/* PatBltOrder.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:35 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: 
 */
package com.droidcloud.viewer.client.orders;

public class PatBltOrder extends DestBltOrder {

    private int bgcolor = 0;
    private int fgcolor = 0;
    private Brush brush = null;

    public PatBltOrder() {
	super();
	brush = new Brush();
    }

    public int getBackgroundColor() {
	return this.bgcolor;
    }
    
    public int getForegroundColor() {
	return this.fgcolor;
    }
    
    public Brush getBrush(){
   	return this.brush;
    }

    public void setBackgroundColor(int bgcolor) {
	this.bgcolor = bgcolor;
    }
    
    public void setForegroundColor(int fgcolor) {
	this.fgcolor = fgcolor;
    }

    public void reset() {
	super.reset();
	bgcolor = 0;
	fgcolor = 0;
	brush.reset();
    }
}
