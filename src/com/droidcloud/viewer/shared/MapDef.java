/* MapDef.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:38 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Encapsulates an individual key mapping
 */
package com.droidcloud.viewer.shared;


import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.droidcloud.viewer.client.Constants;
import com.droidcloud.viewer.client.Options;
import com.droidcloud.viewer.client.keymapping.KeyMapException;
import com.droidcloud.viewer.client.keymapping.KeyPressedEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;

public class MapDef {

	// Flag masks for use in generating an integer modifiers value (for text definition output)
	private final int FLAG_SHIFT = 0x01; // flag mask for a shift modifier
	private final int FLAG_CTRL = 0x02; // flag mask for a control modifier
	private final int FLAG_ALT = 0x04; // flag mask for an alt modifier
	private final int FLAG_CAPSLOCK = 0x08; // flag mask for a capslock modifier
	
	  private int scancode;
	  private boolean ctrlDown;
	  private boolean shiftDown;
	  private boolean altDown;
	  private boolean capslockDown;
	  
	  private char keyChar;
	  private int keyCode;
	  private boolean characterDef;
	  
	  private int keyLocation;
	  	  
	  /**
	   * Constructor for a character-defined mapping definition
	   * 
	   * @param keyChar
	   * @param keyLocation
	   * @param scancode
	   * @param ctrlDown
	   * @param shiftDown
	   * @param altDown
	   * @param capslockDown
	   */
	  public MapDef(char keyChar, int keyLocation, int scancode, boolean ctrlDown, boolean shiftDown, boolean altDown, boolean capslockDown) {
	  	this.keyChar = keyChar;
	  	this.characterDef = true;
	  	
	  	this.keyLocation = keyLocation;
	  	this.scancode = scancode;
	  	this.ctrlDown = ctrlDown;
	  	this.altDown = altDown;
	  	this.shiftDown = shiftDown;
	  	this.capslockDown = capslockDown;
	  }
	  
	  /**
	   * Constructor for a keycode-defined mapping definition
	   * 
	   * @param keyCode
	   * @param keyLocation
	   * @param scancode
	   * @param ctrlDown
	   * @param shiftDown
	   * @param altDown
	   * @param capslockDown
	   */
	  public MapDef(int keyCode, int keyLocation, int scancode, boolean ctrlDown, boolean shiftDown, boolean altDown, boolean capslockDown) {
	  	this.keyCode = keyCode;
	  	this.characterDef = false;
	  	
	  	this.keyLocation = keyLocation;
	  	this.scancode = scancode;
	  	this.ctrlDown = ctrlDown;
	  	this.altDown = altDown;
	  	this.shiftDown = shiftDown;
	  	this.capslockDown = capslockDown;
	  }
	  
	  public int getKeyCode(){ return keyCode; }
	  
	  public char getKeyChar(){ return keyChar; }
	  
	  /**
	   * Return the scancode associated with this mapping
	   * @return
	   */
	  public int getScancode(){ return scancode; }
	  
	  /**
	   * Return true if this mapping is defined by a character, false otherwise
	   * @return
	   */
	  public boolean isCharacterDef(){ return characterDef; }
	  
	  /**
	   * Return true if the keystroke defined in this mapping requires that the
	   * Control key be down
	   * 
	   * @return
	   */
	  public boolean isCtrlDown(){ return ctrlDown; }

	  /**
	   * Return true if the keystroke defined in this mapping requires that the
	   * Alt key be down
	   * 
	   * @return
	   */
	  public boolean isAltDown(){ return altDown; }
	  
	  /**
	   * Return true if the keystroke defined in this mapping requires that the
	   * Shift key be down
	   * 
	   * @return
	   */
	  public boolean isShiftDown(){ return shiftDown; }
	  
	  /**
	   * Return true if the keystroke defined in this mapping requires that
	   * Caps Lock is on
	   * 
	   * @return
	   */
	  public boolean isCapslockOn(){ return capslockDown; }	  
	  
	  /**
	   * Return the number of modifiers that would need to be changed to
	   * send the specified character/key using this particular mapping.
	   * 
	   * @param e Key event which was received by Java
	   * @param capslock Is the Caps Lock key down?
	   * @return The number of modifier changes to make
	   */
	  public int modifierDistance(KeyPressedEvent e, boolean capslock){
		//boolean capslock = e.getComponent().getToolkit().getLockingKeyState( KeyPressEvent.VK_CAPS_LOCK);		

	  	if(!characterDef) return 0;
	  	
	  	int dist = 0;
	  	if(ctrlDown != e.isControlKeyDown()) dist += 1;
	  	if(altDown != e.isAltKeyDown()) dist += 1;
	  	if(shiftDown != e.isShiftKeyDown()) dist += 1;
	  	if(capslockDown != capslock) dist += 1;
	  	return dist;
	  }
	  
	  public boolean appliesTo(char c){
	  	return ((characterDef) && (this.keyChar == c) && !(capslockDown));
	  }
	  
	  /**
	   * 
	   * Return true if this map definition applies to the supplied key event
	   * 
	   * @param e KeyPressEvent to check definition against
	   * @return
	   */
	  protected boolean appliesToTyped(KeyPressEvent e){		  		  
		 // TODO
  			return ((characterDef) && (this.keyChar == e.getCharCode()));		
	  }

	  
	  public boolean appliesToTyped(KeyPressedEvent e, boolean capslock, Options option){
	  	
	  	if(Constants.OS == Constants.MAC){
	  		// Remap the hash key to §
	  		if(option.isRemapHashEnabled() && (e.getCharCode() == '§')){
	  			return ((characterDef) && (this.keyChar == '#'));
	  		}
	  		
	  		
	  		// Handle unreported shifted capitals (with capslock) on a Mac	  	
	  		if(capslock && Character.isLetter((char)e.getCharCode()) && Character.isUpperCase((char)e.getCharCode()) && e.isShiftKeyDown()){
	  			char c = Character.toLowerCase((char)e.getCharCode());
	  			return ((characterDef) && (this.keyChar == c));
	  		}
	  	}
	  	
		return ((characterDef) && (this.keyChar == e.getCharCode()));		
	  }

	  
	  /**
	   * 
	   * Return true if this map definition applies to the supplied key event
	   * 
	   * @param e KeyPressEvent to check definition against
	   * @return
	   */
	  public boolean appliesToPressed(KeyPressedEvent e){
	  	// only match special characters if the modifiers are consistent
	  	if(!characterDef){
	  		if(!((ctrlDown && e.isControlKeyDown()) || !ctrlDown)) return false;
	  		if(!((altDown && e.isAltKeyDown()) || !altDown)) return false;
	  	}
	  	
	  	return ((!characterDef) && (this.keyCode == e.getEvent().getNativeKeyCode()));
	  }
	  
	  /**
	   * Constructor for a mapping definition based on a given string
	   * representation (as would be output to a stream by the writeToStream method).
	   * 
	   * @param definition One-line definition string
	   * @throws KeyMapException Any parsing errors which may occur
	   */
	  public MapDef(String definition) throws KeyMapException{
//	  	StringTokenizer st = new StringTokenizer(definition);
	  	String [] tokens = definition.split(" \t\n\r\f");
	  	ArrayList<String> tok = new ArrayList<String>();
	  	for(int i=0;i<tokens.length;i++)
	  	{
	  		tok.add(tokens[i]);
	  	}
	  	Iterator<String> st = tok.iterator();
	  	try{
	  			// determine whether the definition is character-oriented
	  			characterDef = ( (Integer.parseInt(st.next()) == 1) ? true  : false );
	  			
	  			// read in the character or keycode
	  			if(characterDef) keyChar = (char) Integer.parseInt(st.next());
	  			else keyCode = Integer.parseInt(st.next());
	  			
	  			// read in the key location
	  			keyLocation = Integer.parseInt(st.next());
	  			
	  			// read in the scancode (from a HEX string)
	  			scancode = Integer.decode(st.next()).intValue();
	  			
	  			// read in the modifiers and interpret
	  			int modifiers = Integer.parseInt(st.next());
	  			shiftDown = ((modifiers & this.FLAG_SHIFT) != 0);
	  			ctrlDown = ((modifiers & this.FLAG_CTRL) != 0);
	  			altDown = ((modifiers & this.FLAG_ALT) != 0);
	  			capslockDown = ((modifiers & this.FLAG_CAPSLOCK) != 0);
	  			 			
	  	}
	  	catch(NumberFormatException nfEx){ throw new KeyMapException("" + nfEx.getMessage() + " is not numeric"); }
	  	catch(NoSuchElementException nseEx){ throw new KeyMapException("Not enough parameters in definition"); }

	  }
	  
	  /**
	   * Output this mapping definition to a stream, formatted
	   * as a single line
	   * (characterDef character/keycode location scancode modifiers [description]) 
	   * 
	   * @param p Stream to write to
	   */
	  public void writeToStream(PrintStream p){
	  	
	  	// create definition string with first character 1 if the
	  	// mapping is character-defined, 0 otherwise
	  	String definition = "" + (characterDef?1:0);
	  	
	  	// add character or keycode
	  	definition += "\t";
	  	if(characterDef) definition += (int) keyChar;
		else definition += keyCode;
	  		
	    // add key location
	  	definition += "\t" + keyLocation;
	  	definition += "\t0x" + Integer.toHexString(scancode);
	  	
	  	// build and add modifiers as a set of flags in an integer value
	  	int modifiers = 0;
	  	modifiers |= (shiftDown?FLAG_SHIFT:0);
	  	modifiers |= (ctrlDown?FLAG_CTRL:0);
	  	modifiers |= (altDown?FLAG_ALT:0);
	  	modifiers |= (capslockDown?FLAG_CAPSLOCK:0);
	  	definition += "\t" + modifiers;
	  	
	  	// add additional information if available (and necessary)
//	  	if(!characterDef) definition += "\t" + KeyPressEvent.getKeyText(this.keyCode);	  	
	  	
	  	// output the definition to the specified stream
	  	p.println(definition);
	  }
	  
	}
