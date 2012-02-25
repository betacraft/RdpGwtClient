package com.droidcloud.viewer.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class RdpRecievedEvent extends GwtEvent<RdpRecievedEventHandler> {

	public static final GwtEvent.Type<RdpRecievedEventHandler> TYPE = new GwtEvent.Type<RdpRecievedEventHandler>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RdpRecievedEventHandler> getAssociatedType() {
		
		return TYPE;
	}

	@Override
	protected void dispatch(RdpRecievedEventHandler handler) {
		handler.onReceived(this);
	}

	
}
