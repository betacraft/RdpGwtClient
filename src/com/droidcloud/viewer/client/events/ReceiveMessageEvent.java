package com.droidcloud.viewer.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class ReceiveMessageEvent extends GwtEvent<RecieveMessageEventHandler> {

	public static final GwtEvent.Type<RecieveMessageEventHandler> TYPE = new GwtEvent.Type<RecieveMessageEventHandler>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RecieveMessageEventHandler> getAssociatedType() {
		
		return TYPE;
	}

	@Override
	protected void dispatch(RecieveMessageEventHandler handler) {
		handler.onReceived(this);
	}

	
}
