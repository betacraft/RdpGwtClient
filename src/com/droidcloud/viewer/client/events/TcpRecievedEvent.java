package com.droidcloud.viewer.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class TcpRecievedEvent extends GwtEvent<TcpRecievedEventHandler> {

	public static final GwtEvent.Type<TcpRecievedEventHandler> TYPE = new GwtEvent.Type<TcpRecievedEventHandler>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<TcpRecievedEventHandler> getAssociatedType() {
		
		return TYPE;
	}

	@Override
	protected void dispatch(TcpRecievedEventHandler handler) {

	}

	
}
