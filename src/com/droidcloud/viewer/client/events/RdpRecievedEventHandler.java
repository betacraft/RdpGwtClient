package com.droidcloud.viewer.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface RdpRecievedEventHandler extends EventHandler {
	
	void onReceived(RdpRecievedEvent event);
}
