package com.droidcloud.viewer.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface RecieveMessageEventHandler extends EventHandler {
	
	void onReceived(ReceiveMessageEvent event);
}
