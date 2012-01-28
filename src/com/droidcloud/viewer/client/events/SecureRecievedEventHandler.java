package com.droidcloud.viewer.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface SecureRecievedEventHandler extends EventHandler {
	
	void onReceived(SecureRecievedEvent event);
}
