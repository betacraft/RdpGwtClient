package com.droidcloud.viewer.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface IsoRecievedEventHandler extends EventHandler {
	
	void onReceived(IsoRecievedEvent event);
}
