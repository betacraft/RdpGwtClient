package com.droidcloud.viewer.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class SecureRecievedEvent extends GwtEvent<SecureRecievedEventHandler> {

	public static final GwtEvent.Type<SecureRecievedEventHandler> TYPE = new GwtEvent.Type<SecureRecievedEventHandler>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SecureRecievedEventHandler> getAssociatedType() {
		
		return TYPE;
	}

	@Override
	protected void dispatch(SecureRecievedEventHandler handler) {

	}

	
}
