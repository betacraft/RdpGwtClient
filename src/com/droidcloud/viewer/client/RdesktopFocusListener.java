package com.droidcloud.viewer.client;


import com.google.gwt.event.dom.client.FocusHandler;

/**
 * Created by IntelliJ IDEA.
* User: Administrator
* Date: Sep 17, 2010
* Time: 11:29:52 AM
* To change this template use File | Settings | File Templates.
*/
public class RdesktopFocusListener implements FocusHandler {
    private RdesktopCanvas canvas;
    private Options option;

    public RdesktopFocusListener(RdesktopCanvas canvas, Options option) {
        this.canvas = canvas;
        this.option = option;
    }



	@Override
	public void onFocus(com.google.gwt.event.dom.client.FocusEvent event) {
		
		
	}
}
