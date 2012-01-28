package com.droidcloud.viewer.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface TcpRecievedEventHandler extends EventHandler {
	
	void onReceived(TcpRecievedEvent event);
}
