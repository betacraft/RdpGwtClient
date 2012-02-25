package com.droidcloud.viewer.client;

import java.util.Vector;

import com.droidcloud.viewer.shared.KeyMapData;
import com.google.gwt.dev.util.Pair;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void getMap(String name, AsyncCallback<KeyMapData> asyncCallback);
}
