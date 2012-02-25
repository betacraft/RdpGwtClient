package com.droidcloud.viewer.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class McsRecievedEvent extends GwtEvent<McsRecievedEventHandler> {

	public static final GwtEvent.Type<McsRecievedEventHandler> TYPE = new GwtEvent.Type<McsRecievedEventHandler>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<McsRecievedEventHandler> getAssociatedType() {
		
		return TYPE;
	}

	@Override
	protected void dispatch(McsRecievedEventHandler handler) {
		handler.onReceived(this);
	}

	
}
