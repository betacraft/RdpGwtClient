/* Common.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:14 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Provide a static interface to individual network layers
 *          and UI components
 */
package com.droidcloud.viewer.client;


import com.droidcloud.viewer.client.rdp5.Rdp5;

public class Common {

	public static boolean underApplet = false;
	public static Rdp5 rdp;	
	// public static Secure secure;
//	public static RdesktopFrame frame;
	public static RdesktopCanvas_Localised canvas;
	/*
	 * Droidcloud mouse handling variables kept in common so that we could
	 * handle a few events from outside
	 */
	public static WrappedImage backstore;
	public static boolean update = false;	
	/*
	 * end of changes
	 */

	public static int mouseX = 0;
	public static int mouseY = 0;

	public static boolean isRunningAsApplication() {
		return !underApplet;
	}

	public static boolean isRunningAsApplet() {
		return underApplet;
	}
}
