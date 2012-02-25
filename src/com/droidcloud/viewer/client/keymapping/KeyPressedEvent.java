package com.droidcloud.viewer.client.keymapping;

import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.shared.EventHandler;

public class KeyPressedEvent  {

	public static final int KEY_PRESSED = 0;
	public static final int KEY_RELEASED = 1;
	
	int ID = KEY_PRESSED;
	private KeyCodeEvent event = null;
	
	public KeyCodeEvent getEvent() {
		return event;
	}
	public void setEvent(KeyCodeEvent event) {
		this.event = event;
	}
	public int getID()
	{
		return ID;
	}
	public void setID(int id)
	{
		this.ID = id;
	}
	public boolean isShiftKeyDown() {
		return event.isShiftKeyDown();
	}
	public boolean isAltKeyDown() {
		return event.isAltKeyDown();
	}
	public int getCharCode() {		
		return event.getNativeKeyCode();
	}
	public boolean isControlKeyDown() {
		// TODO Auto-generated method stub
		return event.isControlKeyDown();
	}
}
