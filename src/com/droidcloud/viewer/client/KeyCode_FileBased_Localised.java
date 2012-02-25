/* KeyCode_FileBased_Localised.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:54 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Java 1.4 specific extension of KeyCode_FileBased class
 */
package com.droidcloud.viewer.client;


import java.util.HashMap;

import com.droidcloud.viewer.client.keymapping.KeyCode_FileBased;
import com.droidcloud.viewer.client.keymapping.KeyMapException;
import com.droidcloud.viewer.client.keymapping.KeyPressedEvent;


public class KeyCode_FileBased_Localised extends KeyCode_FileBased {

	private HashMap keysCurrentlyDown = new HashMap();
	
	/**
	 * @param fstream
	 * @param option
     * @throws KeyMapException
	 */

	public KeyCode_FileBased_Localised(String s, Options option) throws KeyMapException{
		super(s, option);
	}
	
	private void updateCapsLock(KeyPressedEvent e){
		/*if(option.isUseLockingKeyStateEnabled()){
			try {
				option.enableUseLockingKeyState();
				capsLockDown = e.getComponent().getToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
			} catch (Exception uoe){ option.disableUseLockingKeyState(); }
		}*/
	}
	
}
