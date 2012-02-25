/* LicenceStore.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:35 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Handle saving and loading of licences
 */
package com.droidcloud.viewer.client;

import java.io.IOException;

import com.google.gwt.storage.client.Storage;

public abstract class LicenceStore {

	/**
	 * Load a licence from a file
	 * 
	 * @return Licence data stored in file @param option
	 */
	public byte[] load_licence(Options option) {
		String path = option.getLicencePath() + "/licence."
				+ option.getHostname();
		byte[] data = null;

		Storage store = Storage.getLocalStorageIfSupported();
		if (store != null) {
			data = store.getItem(path).getBytes();
			return data;
		}
		else
			return null;	
	}

	/**
	 * Save a licence to file
	 * 
	 * @param databytes
	 *            Licence data to store
	 * @param option
	 */
	public void save_licence(byte[] databytes, Options option) {
		/* set and create the directory -- if it doesn't exist. */
		// String home = "/root";
		String dirpath = option.getLicencePath();// home+"/.rdesktop";
		String filepath = dirpath + "/licence." + option.getHostname();
		Storage store = Storage.getLocalStorageIfSupported();
		store.setItem(filepath, new String(databytes));
	}

}
