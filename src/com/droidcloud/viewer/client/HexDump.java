/* HexDump.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:29 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Manages debug information for all data
 *          sent and received, outputting in hex format
 */
package com.droidcloud.viewer.client;

import com.google.gwt.core.client.GWT;



public class HexDump {


    /**
     * Construct a HexDump object, sets logging level to Debug
     */
    public HexDump() {
    	//logger.setLevel(Level.DEBUG);
    }

    /**
     * Encode data as hex and output as debug messages along with supplied custom message
     * @param data Array of byte data to be encoded
     * @param msg Message to include with outputted hex debug messages
     */
    public static  String encode(byte[] data) {
	int count = 0;
	String index = null;
	String number;
	String packet = "";
	//logger.debug(msg);
	
	while(count < data.length) {
	    index = Integer.toHexString(count);
	    switch(index.length()) {
	    case(1):
		index = "0000000".concat(index);
		break;
	    case(2):
		index = "000000".concat(index);
		break;
	    case(3):
		index = "00000".concat(index);
		break;
	    case(4):
		index = "0000".concat(index);
		break;
	    case(5):
		index = "000".concat(index);
		break;
	    case(6):
		index = "00".concat(index);
		break;
	    case(7):
		index = "0".concat(index);
		break;
	    case(8):
		break;
	    default:
		return (packet+=index );
	    }
	    
	    index += ": ";
	    //out.print(index + ": ");
	    for(int i = 0; i < 16; i++) {
		if(count >= data.length) {
		    break;
		}
		number = Integer.toHexString((data[count]&0x000000ff));
		switch(number.length()) {
		case(1):
		    number = "0".concat(number);
		    break;
		case(2):
		    break;
		default:
		    //logger.debug(index);
		    //out.println("");
		    return (packet+=index );
		}   
		index+= (number + " "); 
		//out.print(number + " ");
		packet+=index;
		count++;
	    }
	//    GWT.log("hexdump: "+index);
	    //logger.debug(index);
	    //out.println("");
	    
	}
	return packet;
    }
    
    public static  String encoder(byte[] data) {
    	int length = data.length;
    	String hex = "";
    	for(int i=0;i<length;i++)
    	{
    		hex += Integer.toHexString(data[i]&0xff);
    	}
    	return hex;
    }
    
    public static void printBuffer(RdpPacket_Localised buffer)
    {
    	byte [] tmpMcs = new byte[buffer.size()];
		buffer.copyToByteArray(tmpMcs, 0, 0, buffer.size());
		GWT.log("hexdump: "+HexDump.encoder(tmpMcs));
    }
}
