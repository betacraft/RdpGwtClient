package com.droidcloud.viewer.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;

public class ByteBuffer {

	ArrayList<Byte> barray = new ArrayList<Byte>();
	int capacity = 0;
	int currentPosition = 0;
	/*
	 * public static final int BIG_ENDIAN16 = 0; public static final int
	 * LITTLE_ENDIAN16 = 1;
	 */
	public static final int BIG_ENDIAN32 = ByteOrder.BIG_ENDIAN;
	public static final int LITTLE_ENDIAN32 = ByteOrder.LITTLE_ENDIAN;
	int ORDER = BIG_ENDIAN32;

	// private static ByteBuffer bb = new ByteBuffer();

	public static ByteBuffer allocateDirect(int capacity) {
		ByteBuffer bb = new ByteBuffer();
		bb.barray = new ArrayList<Byte>(capacity);
		for (int i = 0; i < capacity; i++)
			bb.barray.add((byte) 0x00);
		GWT.log("" + capacity);
		bb.capacity = capacity;
		bb.currentPosition = 0;
		return bb;
	}

	public int capacity() {
		// TODO Auto-generated method stub
		return capacity;
	}

	public void clear() {
		barray.clear();

	}

	public void put(int where, byte what) {
		// GWT.log("currentpos " + currentPosition + " size" + barray.size());
		if (where < barray.size())
			barray.set(where, what);
		else {
			barray.add(what);
			capacity = barray.size();
		}
	}

	public int position() {

		return currentPosition;
	}

	public void put(byte what) {
		// GWT.log("currentpos " + currentPosition);
		if (currentPosition < barray.size())
			barray.set(currentPosition++, what);
		else {
			barray.add(what);
			currentPosition = barray.size() - 1;
		}
		// capacity = barray.size();
	}

	public int get(int where) {
		// TODO Auto-generated method stub
		return barray.get(where);
	}

	public int get() {

		return barray.get(currentPosition++);
	}

	public void put(byte[] array, int array_offset, int len) {
		for (int i = 0; i < len; i++) {
			if ((currentPosition + array_offset + i) < capacity)
				barray.set(currentPosition + array_offset + i, array[i]);
			else {
				barray.add(array[i]);
				capacity = barray.size();
			}
		}
	}

	public void get(byte[] array, int array_offset, int len) {
		/*
		 * if (array != null) { if (array.length < len) array = new byte[len]; }
		 * else array = new byte[len];
		 */
		GWT.log("copying bytes: " + len);
		for (int i = 0; i < len; i++) {
			array[i] = barray.get(currentPosition + array_offset + i);
		}
	}

	public void order(int endian) {
		ORDER = endian;
	}

	public short getShort(int where) {
		if (where <= (capacity - 2)) {
			short r = 0xff;
			if (ORDER == BIG_ENDIAN32)
				r = (short) ((barray.get(where) << 8) | barray.get(where + 1));
			else if (ORDER == LITTLE_ENDIAN32)
				r = (short) ((barray.get(where + 1) << 8) | barray.get(where));
			return r;
		}

		throw new IndexOutOfBoundsException("get short index out of bounds");
	}

	public short getShort() {
		short r = (short) getShort(currentPosition);
		currentPosition += 2;
		return r;
	}

	public void putShort(int where, short what) {
		byte first;
		byte second;
		/*
		 * first = (byte) (what & 0x0f); second = (byte) ((what >> 8) & 0x0f);
		 */
		second = short1(what);
		first = short0(what);
		/*
		 * GWT.log("currentpos " + currentPosition + " putting short at " +
		 * where + " size " + barray.size());
		 */
		if (ORDER == BIG_ENDIAN32) {
			if (where > (capacity - 2)) {
				barray.add(second);
				barray.add(first);
			} else {
				barray.set(where++, second);
				barray.set(where, first);
			}
		} else {
			if (where > (capacity - 2)) {
				barray.add(first);
				barray.add(second);
				capacity += 2;
			} else {
				barray.set(where++, first);
				barray.set(where, second);
			}
		}
	}

	public void putShort(short what) {
		// GWT.log("currentpos putshort" + currentPosition);
		putShort(currentPosition, what);
		currentPosition += 2;

	}

	public int getInt(int where) {
		if (where < (barray.size() - 4)) {
			int r = 0xff;
			if (ORDER == BIG_ENDIAN32)
				r = (int) ((barray.get(where) << 24)
						| barray.get(where + 1) << 16
						| barray.get(where + 2) << 8 | barray.get(where + 3));
			else if (ORDER == LITTLE_ENDIAN32)
				r = (int) ((barray.get(where + 3) << 24)
						| barray.get(where + 2) << 16
						| barray.get(where + 1) << 8 | barray.get(where));

			return r;
		}
		throw new IndexOutOfBoundsException(
				"get int index out of bounds, where:" + where + " size:"
						+ barray.size() + " capacity:" + capacity);
	}

	public int getInt() {
		int r = getInt(currentPosition);
		currentPosition += 4;
		return r;
	}

	public void putInt(int where, int what) {		
		byte first;
		byte second;
		byte third;
		byte fourth;
		/*first = (byte) (what >> 0);
		second = (byte) ((what >> 8));
		third = (byte) ((what >> 16));
		fourth = (byte) ((what >> 24));*/
		first = int0(what);
		second = int1(what);
		third = int2(what);
		fourth = int3(what);
		if (ORDER == BIG_ENDIAN32) {
			barray.set(where, fourth);
			barray.set(where + 3, third);
			barray.set(where + 2, second);
			barray.set(where + 1, first);
		} else {
			barray.set(where + 3, fourth);
			barray.set(where + 2, third);
			barray.set(where + 1, second);
			barray.set(where, first);
		}
	}

	public void putInt(int what) {
		putInt(currentPosition, what);
		
		currentPosition += 4;
		
	}

	public void position(int i) {
		currentPosition = i;
	}

	private static byte short1(short x) {
		return (byte) (x >> 8);
	}

	private static byte short0(short x) {
		return (byte) (x >> 0);
	}

	private static byte int3(int x) {
		return (byte) (x >> 24);
	}

	private static byte int2(int x) {
		return (byte) (x >> 16);
	}

	private static byte int1(int x) {
		return (byte) (x >> 8);
	}

	private static byte int0(int x) {
		return (byte) (x >> 0);
	}
}
