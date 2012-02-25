/* LicenceStore_Localised.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:54 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Java 1.4 specific extension of LicenceStore class
 */
// Created on 05-Aug-2003

package com.droidcloud.viewer.client;

import java.util.prefs.Preferences;

import com.google.gwt.storage.client.Storage;


public class LicenceStore_Localised extends LicenceStore {

    public byte[] load_licence(Options option){
/*        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        return prefs.getByteArray("licence."+ option.getHostname(),null);
*/
    	Storage store = Storage.getLocalStorageIfSupported();
    	if(store != null)
    	{
    		return store.getItem("licence."+ option.getHostname()).getBytes();
    	}
    	else
    		return null;
    		
    }
    
    public void save_licence(byte[] databytes, Options option){
/*        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        prefs.putByteArray("licence."+ option.getHostname(), databytes);
*/    	Storage store = Storage.getLocalStorageIfSupported();
    	if(store != null)
    		store.setItem("licence."+ option.getHostname(), new String(databytes));    	    		
    }
    
}
