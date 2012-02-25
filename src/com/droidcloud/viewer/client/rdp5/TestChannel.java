/* TestChannel.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:39 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Dummy RDP5 channel for testing purposes
 */
package com.droidcloud.viewer.client.rdp5;

import com.droidcloud.viewer.client.RdpPacket;

/**
 * @author Tom Elliott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestChannel extends VChannel {

	public TestChannel(String name, int flags){
		this.name = name;
		this.flags = flags;
	}
		
	private String name;
	private int flags;
	
	public String name() {
		return name;
	}

	public int flags() {
		return flags;
	}

	public void process(RdpPacket data) {
	}

}
