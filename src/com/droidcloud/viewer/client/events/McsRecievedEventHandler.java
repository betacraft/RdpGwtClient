package com.droidcloud.viewer.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface McsRecievedEventHandler extends EventHandler {
	
	void onReceived(McsRecievedEvent event);
}
