/* RdpPacket_Localised.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:54 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Java 1.4 specific extension of RdpPacket class
 */
package com.droidcloud.viewer.client;

import com.google.gwt.core.client.GWT;




// The below import is never used
//import org.apache.log4j.Logger;

public class RdpPacket_Localised extends RdpPacket {

	private ByteBuffer bb = null;
    private int size = 0;
    
	public RdpPacket_Localised(int capacity) {
	bb = ByteBuffer.allocateDirect(capacity);
    size = capacity;
	}

   
    public void reset(int length){
        ////logger.info("RdpPacket_Localised.reset(" + length + "), capacity = " + bb.capacity());
        this.end = 0;
        this.start = 0;
        if(bb.capacity() < length) bb = ByteBuffer.allocateDirect(length);
        size = length;
        bb.clear();
    }
    
	public void set8(int where, int what) {
	if (where < 0 || where >= bb.capacity()) { 
		throw new ArrayIndexOutOfBoundsException("memory accessed out of Range!"); 
	}
	bb.put(where,(byte)what);
	}

	public void set8(int what) {
	if (bb.position() >= bb.capacity()) { 
		throw new ArrayIndexOutOfBoundsException("memory accessed out of Range!"); 
	}
	bb.put((byte)what);
	}
   
	// where is a 8-bit offset
	public int get8(int where) {
	if (where < 0 || where >= bb.capacity()) { 
		throw new ArrayIndexOutOfBoundsException("memory accessed out of Range!"); 
	}
	return bb.get(where)&0xff; // treat as unsigned byte
	}

	// where is a 8-bit offset
	public int get8() {
	if (bb.position() >= bb.capacity()) { 
		throw new ArrayIndexOutOfBoundsException("memory accessed out of Range!"); 
	}
	return bb.get()&0xff; // treat as unsigned byte
	}

	public void copyFromByteArray(byte[] array, int array_offset, int mem_offset, int len){
	if ((array_offset >= array.length) || (array_offset + len > array.length) || ((mem_offset + len) > bb.capacity())) {
		GWT.log("arrayoffset:"+array_offset+" memoffset:"+mem_offset+" array length:"+array.length+" length:"+len+" capacity:"+bb.capacity);
		throw new ArrayIndexOutOfBoundsException("memory accessed out of Range!");
	}
	// store position
	int oldpos = getPosition();
	GWT.log("oldpos"+oldpos);
	setPosition(mem_offset);
	
	bb.put(array,array_offset,len);
	GWT.log("put complete");
	// restore position
	setPosition(oldpos);
	}
    
	public void copyToByteArray(byte[] array, int array_offset, int mem_offset, int len) {
		if ((array_offset >= array.length))
			throw new ArrayIndexOutOfBoundsException("Array offset beyond end of array!");
		if(array_offset + len > array.length)
			throw new ArrayIndexOutOfBoundsException("Not enough bytes in array to copy!");
		if(mem_offset + len > bb.capacity())
			throw new ArrayIndexOutOfBoundsException("Memory accessed out of Range!");

	int oldpos = getPosition();
	setPosition(mem_offset);
	bb.get(array,array_offset,len);
	setPosition(oldpos);
	}

	public void copyToPacket(RdpPacket_Localised dst, int srcOffset, int dstOffset, int len) {
	int olddstpos = dst.getPosition();
	int oldpos = getPosition();
	dst.setPosition(dstOffset);
	setPosition(srcOffset);
	for(int i=0;i<len;i++) dst.set8(bb.get());	
	dst.setPosition(olddstpos);
	setPosition(oldpos);
	}

   public void copyFromPacket(RdpPacket_Localised src, int srcOffset, int dstOffset, int len) {
	int oldsrcpos = src.getPosition();
	int oldpos = getPosition();
	src.setPosition(srcOffset);
	setPosition(dstOffset);
	for(int i=0;i<len;i++) bb.put((byte)src.get8());
   src.setPosition(oldsrcpos);
   setPosition(oldpos);
   }
   
   public int capacity(){
       return bb.capacity();
   }
   
	// return size in bytes
	public int size() {
        return size;
	//return bb.capacity(); //this.end - this.start;
	}
   
	public int getPosition() {
	return bb.position();
	}

	public int getLittleEndian16(int where) { 
	bb.order(ByteOrder.LITTLE_ENDIAN);
	return bb.getShort(where);
	 }

	public int getLittleEndian16() { 
		bb.order(ByteOrder.LITTLE_ENDIAN);
	return bb.getShort();
	 }

	public int getBigEndian16(int where) { 
	bb.order(ByteOrder.BIG_ENDIAN);
	return bb.getShort(where);
	}
    
	public int getBigEndian16() { 
		bb.order(ByteOrder.BIG_ENDIAN);
		return bb.getShort();
	}
    
	public void setLittleEndian16(int where, int what)  {
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putShort(where,(short)what);
	}
    
	public void setLittleEndian16(int what)  {
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putShort((short)what);
	}
    
	public void setBigEndian16(int where, int what)  {
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.putShort(where,(short)what);
	}
    
	public void setBigEndian16(int what)  {
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.putShort((short)what);
	}

	public int getLittleEndian32(int where) { 
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt(where);
	}

	public int getLittleEndian32() { 
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}

	 public int getBigEndian32(int where) { 
		bb.order(ByteOrder.BIG_ENDIAN);
		return bb.getInt(where);
	}

	 public int getBigEndian32() { 
		bb.order(ByteOrder.BIG_ENDIAN);
		return bb.getInt();
	 }
	
	public void setLittleEndian32(int where, int what)  {
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(where,what);
	}

	public void setLittleEndian32(int what)  {
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putInt(what);
	}
	
	public void setBigEndian32(int where, int what)  {
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.putInt(where,what);
	}

	public void setBigEndian32(int what)  {
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.putInt(what);
	}
    
	public void incrementPosition(int length) {
	//	GWT.log("length:"+length+" capacity:"+bb.capacity()+" position"+bb.position());
	if (length > bb.capacity() || length+bb.position() > bb.capacity() || length <0) {
		throw new ArrayIndexOutOfBoundsException();
	}
	bb.position(bb.position()+length);
	}

	public void setPosition(int position) {
	if (position > bb.capacity() || position <0) {
		//logger.warn("stream position ="+getPosition()+" end ="+getEnd()+" capacity ="+capacity());		
		//logger.warn("setPosition("+position+") failed");
		throw new ArrayIndexOutOfBoundsException();
		}
	bb.position(position);
	}


}
