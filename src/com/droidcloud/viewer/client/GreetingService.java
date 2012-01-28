package com.droidcloud.viewer.client;

import java.util.Vector;

import com.droidcloud.viewer.shared.KeyMapData;
import com.google.gwt.dev.util.Pair;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;
	KeyMapData getMap(String name) throws IllegalArgumentException;
}
