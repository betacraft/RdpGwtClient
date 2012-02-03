package com.droidcloud.viewer.client;

import java.util.LinkedList;

import com.droidcloud.viewer.client.events.SecureRecievedEvent;
import com.droidcloud.viewer.client.events.SecureRecievedEventHandler;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DroidCloudViewer implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	public static HandlerManager eventBus;
	LinkedList<GwtEvent> events = new LinkedList<GwtEvent>();
	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	/*private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);
*/
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		eventBus = new HandlerManager(this);
		
		eventBus.addHandler(SecureRecievedEvent.TYPE, new SecureRecievedEventHandler() {
			
			@Override
			public void onReceived(SecureRecievedEvent event) {
				GWT.log("secureevent recieved");
				
			}
		});
		events.add(new SecureRecievedEvent());
		eventBus.fireEvent(events.removeFirst());
		/*
		StringTokenizer tok = new StringTokenizer(argLine, " ");
		for (Object[] obj = { tok, args = new String[tok.countTokens()],
				new int[] { 0 } }; ((StringTokenizer) obj[0]).hasMoreTokens(); ((String[]) obj[1])[((int[]) obj[2])[0]++] = ((StringTokenizer) obj[0])
				.nextToken()) {
		}*/
		
		Timer timer = new Timer() {
			
			@Override
			public void run() {
				String argLine = "";
				argLine += genParam("-m", "keymap");
				argLine += " " + "-u" + " droidcloud";
				argLine += " " + "-p" + " droidcloud1511";
				// argLine += " " + genParam("-u", "username");
				// argLine += " " + genParam("-p", "password");
				argLine += " " + genParam("-n", "hostname");
				argLine += " " + genParam("-t", "port");
				argLine += " " + genParam("-l", "debug_level");
				argLine += " " + genParam("-s", "shell");
				argLine += " " + genParam("-T", "title");
				argLine += " " + genParam("-c", "command");
				argLine += " " + genParam("-d", "domain");
				argLine += " " + genParam("-o", "bpp");
				argLine += " " + genParam("-g", "geometry");
				argLine += " " + genParam("-s", "shell");
				argLine += " " + genFlag("--console", "console");
				argLine += " " + genFlag("--use_rdp4", "rdp4");
				argLine += " " + genFlag("--debug_key", "debug_key");
				argLine += " " + genFlag("--debug_hex", "debug_hex");
				argLine += " " + genFlag("--no_remap_hash", "no_remap_hash");
				argLine += " " + genFlag("--no_remap_hash", "no_remap_hash");
				argLine += " " + genFlag("--enable_menu", "disable_menu");
				argLine += " " + genFlag("--embed_in_browser", "embed_in_browser");
				argLine += " " + genParam("--error_handler", "error_handler");
				argLine += " " + genParam("--disconnect_handler", "disconnect_handler");
				argLine += " " + genParam("-f", "fullscreen");
				argLine += " " + genParam("", "server");
				String[] args;
				args = argLine.split(" ");
				Rdesktop.main(args);
				
			}
		};
				timer.schedule(500);		
		/*
		 * final Button sendButton = new Button("Send"); final TextBox nameField
		 * = new TextBox(); nameField.setText("GWT User"); final Label
		 * errorLabel = new Label();
		 * 
		 * // We can add style names to widgets
		 * sendButton.addStyleName("sendButton");
		 * 
		 * // Add the nameField and sendButton to the RootPanel // Use
		 * RootPanel.get() to get the entire body element
		 * RootPanel.get("nameFieldContainer").add(nameField);
		 * RootPanel.get("sendButtonContainer").add(sendButton);
		 * RootPanel.get("errorLabelContainer").add(errorLabel);
		 * 
		 * // Focus the cursor on the name field when the app loads
		 * nameField.setFocus(true); nameField.selectAll();
		 * 
		 * // Create the popup dialog box final DialogBox dialogBox = new
		 * DialogBox(); dialogBox.setText("Remote Procedure Call");
		 * dialogBox.setAnimationEnabled(true); final Button closeButton = new
		 * Button("Close"); // We can set the id of a widget by accessing its
		 * Element closeButton.getElement().setId("closeButton"); final Label
		 * textToServerLabel = new Label(); final HTML serverResponseLabel = new
		 * HTML(); VerticalPanel dialogVPanel = new VerticalPanel();
		 * dialogVPanel.addStyleName("dialogVPanel"); dialogVPanel.add(new
		 * HTML("<b>Sending name to the server:</b>"));
		 * dialogVPanel.add(textToServerLabel); dialogVPanel.add(new
		 * HTML("<br><b>Server replies:</b>"));
		 * dialogVPanel.add(serverResponseLabel);
		 * dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		 * dialogVPanel.add(closeButton); dialogBox.setWidget(dialogVPanel);
		 * 
		 * // Add a handler to close the DialogBox
		 * closeButton.addClickHandler(new ClickHandler() { public void
		 * onClick(ClickEvent event) { dialogBox.hide();
		 * sendButton.setEnabled(true); sendButton.setFocus(true); } });
		 * 
		 * // Create a handler for the sendButton and nameField class MyHandler
		 * implements ClickHandler, KeyUpHandler {
		 *//**
		 * Fired when the user clicks on the sendButton.
		 */
		/*
		 * public void onClick(ClickEvent event) { sendNameToServer(); }
		 *//**
		 * Fired when the user types in the nameField.
		 */
		/*
		 * public void onKeyUp(KeyUpEvent event) { if (event.getNativeKeyCode()
		 * == KeyCodes.KEY_ENTER) { sendNameToServer();
		 * 
		 * } }
		 *//**
		 * Send the name from the nameField to the server and wait for a
		 * response.
		 */
		/*
		 * private void sendNameToServer() { // First, we validate the input.
		 * errorLabel.setText(""); String textToServer = nameField.getText(); if
		 * (!FieldVerifier.isValidName(textToServer)) {
		 * errorLabel.setText("Please enter at least four characters"); return;
		 * }
		 * 
		 * // Then, we send the input to the server.
		 * sendButton.setEnabled(false);
		 * textToServerLabel.setText(textToServer);
		 * serverResponseLabel.setText("");
		 * greetingService.greetServer(textToServer, new AsyncCallback<String>()
		 * { public void onFailure(Throwable caught) { // Show the RPC error
		 * message to the user dialogBox
		 * .setText("Remote Procedure Call - Failure"); serverResponseLabel
		 * .addStyleName("serverResponseLabelError");
		 * serverResponseLabel.setHTML(SERVER_ERROR); dialogBox.center();
		 * closeButton.setFocus(true); }
		 * 
		 * public void onSuccess(String result) {
		 * dialogBox.setText("Remote Procedure Call"); serverResponseLabel
		 * .removeStyleName("serverResponseLabelError");
		 * serverResponseLabel.setHTML(result); dialogBox.center();
		 * closeButton.setFocus(true); } }); } }
		 * 
		 * // Add a handler to send the name to the server MyHandler handler =
		 * new MyHandler(); sendButton.addClickHandler(handler);
		 * nameField.addKeyUpHandler(handler);
		 */
	}

	private String genFlag(String flag, String parameter) {
		String s = this.getParameter(parameter);
		if (s != null) {
			if (s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("true"))
				return flag;
		}
		return "";
	}

	private String genParam(String name, String parameter) {
		String s = this.getParameter(parameter);
		if (s != null) {
			if (name != "")
				return name + " " + s;
			else
				return s;
		} else
			return "";
	}

	private String getParameter(String parameter) {
		if(parameter.equals("width"))
		{
			return ""+320;
		}
		else if(parameter.equals("height"))
		{
			return ""+480;
		}
		else if(parameter.equals("keymap"))
		{
			return "en-us";
		}
		else if(parameter.equals("server"))
		{
			return "ws://127.0.0.1";
		}
		else if(parameter.equals("port"))
		{
			return ""+3381;
		}
		/*switch (parameter) {
		case "width":
			return "" + 320;
		case "height":
			return "" + 480;
		case "keymap":
			return "en-us";
		case "server":
			return "ws://127.0.0.1";
		case "port":
			return "" + 3381;
		default:
			return null;
		}*/
		return null;
	}
}
