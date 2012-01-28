package com.droidcloud.viewer.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class IsoRecievedEvent extends GwtEvent<IsoRecievedEventHandler> {

	public static final GwtEvent.Type<IsoRecievedEventHandler> TYPE = new GwtEvent.Type<IsoRecievedEventHandler>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<IsoRecievedEventHandler> getAssociatedType() {
		
		return TYPE;
	}

	@Override
	protected void dispatch(IsoRecievedEventHandler handler) {

	}

	
}
