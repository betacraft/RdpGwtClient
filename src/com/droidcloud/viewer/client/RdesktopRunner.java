package com.droidcloud.viewer.client;

import java.io.IOException;

import com.droidcloud.viewer.client.crypto.CryptoException;
import com.droidcloud.viewer.client.events.ReceiveMessageEvent;
import com.droidcloud.viewer.client.events.RecieveMessageEventHandler;
import com.droidcloud.viewer.client.rdp5.Rdp5;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: Oct 7, 2010 Time:
 * 10:34:13 AM To change this template use File | Settings | File Templates.
 */
public abstract class RdesktopRunner {
	protected Options option;
	private static final String[] LICENSE_ISSUE_MSG = new String[] {
			"The terminal server disconnected before licence negotiation completed.",
			"Possible cause: terminal server could not issue a licence." };
	private Rdp5 rdpLayer;
	private HandlerManager eventBus;

	abstract void reportError(String[] errorMessages);

	abstract void handleRdesktopException(RdesktopException e, Rdp5 rdpLayer);

	abstract void registerDrawingSurface(Rdp5 rdpLayer);

	void disconnectRdpPlayer() {
		GWT.log("came to disconnect");
		if (rdpLayer != null && rdpLayer.isConnected()) {
			GWT.log("disconnecting");
			// logger.info("Disconnecting ...");
			rdpLayer.disconnect();
			// logger.info("Disconnected");
		}
	}

	// SendEvent displayToolsFrame(boolean shouldShowToolsFrame) {
	// SendEvent toolFrame = null;
	// if (shouldShowToolsFrame) {
	// toolFrame = new SendEvent(rdpLayer);
	// toolFrame.setVisible(true);
	// }
	// return toolFrame;
	// }

	// void removeToolsFrame(SendEvent toolsFrame) {
	// if (toolsFrame != null)
	// toolsFrame.dispose();
	// toolsFrame = null;
	// }

	void runRdp(String server) {
		eventBus = DroidCloudViewer.eventBus;

		// Attempt to connect to server on port Options.port

		int logonflags = option.getLogonFlags();
		GWT.log("inside runrdp");
		try {
			rdpLayer.connect(server, logonflags);
			eventBus.addHandler(ReceiveMessageEvent.TYPE, new RecieveMessageEventHandler() {
				
				@Override
				public void onReceived(ReceiveMessageEvent event) {
					GWT.log("going in mainloop");
					boolean[] deactivated = new boolean[1];
					int[] ext_disc_reason = new int[1];
					option.disableEncryptionIfPacketEncyptionNotEnabled();
					try {
						
						rdpLayer.mainLoop(deactivated, ext_disc_reason);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RdesktopException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (OrderException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CryptoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 


		/*
		 * if (Rdesktop.keep_running) {
		 * option.disableEncryptionIfPacketEncyptionNotEnabled(); //
		 * logger.info("Connection successful"); // now show window after
		 * licence negotiation GWT.log("going in!!!");
		 * rdpLayer.mainLoop(deactivated, ext_disc_reason);
		 * GWT.log("came out :("); if (deactivated[0] || ext_disc_reason[0] ==
		 * DisconnnectCodeMapper.exDiscReasonAPIInitiatedDisconnect ||
		 * ext_disc_reason[0] ==
		 * DisconnnectCodeMapper.exDiscReasonAPIInitiatedLogoff) {
		 * Rdesktop.keep_running = false; Common.rdp.sendInput(Input.getTime(),
		 * Input.RDP_INPUT_MOUSE, Input.MOUSE_FLAG_MOVE, 0,0); return; }
		 * 
		 * if (ext_disc_reason[0] >= 2) { String reason =
		 * DisconnnectCodeMapper.textDisconnectReason(ext_disc_reason[0]);
		 * String msg[] = {"Connection terminated", reason}; reportError(msg);
		 * Rdesktop.keep_running = false;
		 * //logger.warn("Connection terminated: " + reason); return; }
		 * Rdesktop.keep_running = false; // exited main loop if
		 * (rdpLayer.isNotReadyToSendData()) { // maybe the licence server was
		 * having a comms // problem, retry? //logger.warn(LICENSE_ISSUE_MSG[0]
		 * + " " + LICENSE_ISSUE_MSG[1]); reportError(LICENSE_ISSUE_MSG); } } }
		 * catch (ConnectionException e) { GWT.log(e.getMessage()); String msg[]
		 * = {"Connection Exception", e.getMessage()}; // reportError(msg);
		 * Rdesktop.keep_running = false; } catch (RdesktopException e) {
		 * GWT.log(e.getMessage()); handleRdesktopException(e, rdpLayer); }
		 * catch (Exception e) { GWT.log(e.getMessage()); String[] msg =
		 * {e.getClass().getName(), e.getMessage()}; // reportError(msg);
		 * Rdesktop.keep_running = false; }
		 */}

	public void run(String server, Secure secureLayer) {
		// logger.debug("keep_running = " + Rdesktop.keep_running);
		rdpLayer = null;
		GWT.log("came to run");
		// while (Rdesktop.keep_running) {
		// logger.debug("Initialising RDP layer...");
		GWT.log("loading RDP5");
		rdpLayer = new Rdp5(option, secureLayer);
		GWT.log("loaded RDP5");
		Common.rdp = rdpLayer;
		if (rdpLayer == null) {
			// logger.fatal("The communications layer could not be initiated!");
			GWT.log("The communications layer could not be initiated!");
			return;
		}
		GWT.log("registering drawing surface");
		registerDrawingSurface(rdpLayer);
		// logger.info("Registered comms layer...");
		// logger.info("Connecting to " + server + ":" + option.getPort() +
		// " ...");
		GWT.log("running rdp server");
		runRdp(server);
		// }
		// disconnectRdpPlayer();
	}

	public static class RdesktopCanvasRunner extends RdesktopRunner {
		RdesktopCanvas_Localised canvas;

		public RdesktopCanvasRunner(Options option,
				RdesktopCanvas_Localised canvas) {
			super();
			this.canvas = canvas;
			this.option = option;
			// initApplet(canvas);
		}

		/*
		 * private void initApplet(RdesktopCanvas_Localised canvas) { if
		 * (Constants.OS == Constants.WINDOWS) { // redraws screen on window
		 * move this.applet.addComponentListener(new
		 * RdesktopComponentAdapter(canvas, this.option)); } this.canvas =
		 * canvas; this.applet.add(canvas); this.applet.validate();
		 * //logger.info("Applet Size:" + this.applet.getSize());
		 * //logger.info("Canvas Size:" +
		 * this.applet.getComponent(0).getSize()); }
		 */
		void reportError(String[] errorMessages) {
			/*
			 * if (option.isErrorHandlerAvailable() &&
			 * Common.isRunningAsApplet()) { //
			 * applet.invokeJavaScriptEventHandler(option.getErrorHandler(),
			 * errorMessages); }
			 */
		}

		void handleRdesktopException(RdesktopException e, Rdp5 rdpLayer) {
			if (rdpLayer.isReadyToSendData()) {
				String msg[] = { e.getMessage() };
				reportError(msg);
				Rdesktop.keep_running = false;
				return;
			}
			// logger.info("Do not retry.");
			Rdesktop.keep_running = false;
			return;
		}

		void registerDrawingSurface(Rdp5 rdpLayer) {
			rdpLayer.registerDrawingSurface(canvas);
		}
	}

}
