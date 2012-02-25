package com.droidcloud.viewer.shared;

import java.io.Serializable;
import java.util.Vector;


public class KeyMapData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5177437353000586571L;
	int mapCode;
	public int getMapCode() {
		return mapCode;
	}
	public void setMapCode(int mapCode) {
		this.mapCode = mapCode;
	}
	public Vector<MapDef> getKeyMap() {
		return keyMap;
	}
	public void setKeyMap(Vector<MapDef> keyMap) {
		this.keyMap = keyMap;
	}
	Vector<MapDef> keyMap; 

}
