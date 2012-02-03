/* ISO.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:21 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: ISO layer of communication
 */
package com.droidcloud.viewer.client;

import java.io.IOException;
import java.util.LinkedList;

import net.zschech.gwt.websockets.client.CloseHandler;
import net.zschech.gwt.websockets.client.ErrorHandler;
import net.zschech.gwt.websockets.client.MessageEvent;
import net.zschech.gwt.websockets.client.MessageHandler;
import net.zschech.gwt.websockets.client.OpenHandler;
import net.zschech.gwt.websockets.client.WebSocket;

import com.droidcloud.viewer.client.crypto.CryptoException;
import com.droidcloud.viewer.client.events.McsRecievedEvent;
import com.droidcloud.viewer.client.events.RdpRecievedEvent;
import com.droidcloud.viewer.client.events.ReceiveMessageEvent;
import com.droidcloud.viewer.client.tools.Base64;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

public abstract class ISO implements OpenHandler, CloseHandler, MessageHandler,
		ErrorHandler {

	@Override
	public void onOpen(WebSocket webSocket) {
		// TODO Auto-generated method stub
		GWT.log("socket opened ");
		Timer timer = new Timer() {

			@Override
			public void run() {
				//					send_connection_request(__option);
				GWT.log("opened");
				while(!toBeSent.isEmpty())
				{
					GWT.log("sending connection request: "+toBeSent.getFirst());
					rdpsock.send(toBeSent.removeFirst());
				}
				setListner(new int[1], __option, __rdpLayer, new McsRecievedEvent());
			}
		};
		timer.schedule(1000);
		opened = true;
	}

	@Override
	public void onClose(WebSocket webSocket) {
		// TODO Auto-generated method stub
		GWT.log("socket closed");
		opened = false;
	}

	@Override
	public void onMessage(WebSocket webSocket, MessageEvent message) {
		// stream.
		if (stream == null)
			stream = new LinkedList<Byte>();

		GWT.log("message received");
		byte[] tmp1 = Base64.decodeBase64(message.getData());
		for (int i = 0; i < tmp1.length; i++) {
			stream.add(tmp1[i]);
		}
		/*
		 * if(in == null) { in = new byte[tmp1.length]; } byte[] tmp = in; int
		 * length = tmp.length + tmp1.length; in = new byte[length]; for (int i
		 * = 0; i < length; i++) { if (i < tmp.length) in[i] = tmp[i]; else
		 * in[i] = tmp1[i - tmp.length]; } tmp = null;
		 */
		String string = new String(tmp1);
		tmp1 = null;
		streamLength = stream.size();
		// GWT.log("message received:"+streamLength+" message:"+string);
	}

	@Override
	public void onError(WebSocket webSocket) {
		GWT.log("websocket error!");
	}

	LinkedList<GwtEvent> events;
	private HexDump dump = null;
	HandlerManager eventBus;
	protected WebSocket rdpsock = null;
	// private DataInputStream in = null;
	// private OutputStream out = null;
	protected volatile byte[] in = null;
	LinkedList<Byte> stream = null;
	int streamLength = 0;
	boolean first = true;
	boolean opened = false;
	/* this for the ISO Layer */
	private static final int CONNECTION_REQUEST = 0xE0;
	private static final int CONNECTION_CONFIRM = 0xD0;
	private static final int DISCONNECT_REQUEST = 0x80;
	private static final int DATA_TRANSFER = 0xF0;
	private static final int ERROR = 0x70;
	private static final int PROTOCOL_VERSION = 0x03;
	private static final int EOT = 0x80;

	/**
	 * Construct ISO object, initialises hex dump
	 */
	public ISO() {
		dump = new HexDump();
		eventBus = DroidCloudViewer.eventBus;
		events = new LinkedList<GwtEvent>();

	}

	/**
	 * Initialise an ISO PDU
	 * 
	 * @param length
	 *            Desired length of PDU
	 * @return Packet configured as ISO PDU, ready to write at higher level
	 */
	public RdpPacket_Localised init(int length) {
		RdpPacket_Localised data = new RdpPacket_Localised(length + 7);// getMemory(length+7);
		data.incrementPosition(7);
		data.setStart(data.getPosition());
		return data;
	}

	/*
	 * protected Socket negotiateSSL(Socket sock) throws Exception{ return sock;
	 * }
	 */

	/**
	 * Create a socket for this ISO object
	 * 
	 * @param host
	 *            Address of server
	 * @param port
	 *            Port on which to connect socket
	 * @throws IOException
	 */
	protected void doSocketConnect(String host, int port) throws IOException {
		GWT.log("connecting to : " + "" + host + ":" + port);
		this.in = new byte[70];
		this.rdpsock = WebSocket.create("ws://" + host + ":" + port, "base64");
		this.rdpsock.setOnOpen(this);
		this.rdpsock.setOnClose(this);
		this.rdpsock.setOnError(this);
		this.rdpsock.setOnMessage(this);
	}

	/**
	 * Connect to a server
	 * 
	 * @param host
	 *            Address of server
	 * @param port
	 *            Port to connect to on server
	 * @param option
	 * @param rdpLayer
	 * @throws IOException
	 * @throws RdesktopException
	 * @throws OrderException
	 * @throws CryptoException
	 */
	public void connect(String host, int port, Options option, Rdp rdpLayer)
			throws IOException, RdesktopException, OrderException,
			CryptoException {
		int[] code = new int[1];
		doSocketConnect(host, port);
		// rdpsock.setTcpNoDelay(option.isLowLatency());
		// this.in = new InputStreamReader(rdpsock.getInputStream());
		/*
		 * this.in = new DataInputStream(new BufferedInputStream(
		 * rdpsock.getInputStream()));
		 */
		/*
		 * this.out = new DataOutputStream(new BufferedOutputStream(
		 * rdpsock.getOutputStream()));
		 */
		this.__option = option;
		this.__rdpLayer = rdpLayer;
		send_connection_request(__option);
		
		// receiveMessage(code, option, rdpLayer);
		/*
		 * if (code[0] != CONNECTION_CONFIRM) { throw new
		 * RdesktopException("Expected CC got:" +
		 * Integer.toHexString(code[0]).toUpperCase()); }
		 */

		/*
		 * if(Options.use_ssl){ try { rdpsock = this.negotiateSSL(rdpsock);
		 * this.in = new DataInputStream(rdpsock.getInputStream()); this.out=
		 * new DataOutputStream(rdpsock.getOutputStream()); } catch (Exception
		 * e) { e.printStackTrace(); throw new
		 * RdesktopException("SSL negotiation failed: " + e.getMessage()); } }
		 */

	}

	/**
	 * Send a self contained iso-pdu
	 * 
	 * @param type
	 *            one of the following CONNECT_RESPONSE, DISCONNECT_REQUEST
	 * @exception IOException
	 *                when an I/O Error occurs
	 */
	private void sendMessage(int type) throws IOException {
		RdpPacket_Localised buffer = new RdpPacket_Localised(11);// getMemory(11);
		byte[] packet = new byte[11];

		buffer.set8(PROTOCOL_VERSION); // send Version Info
		buffer.set8(0); // reserved byte
		buffer.setBigEndian16(11); // Length
		buffer.set8(6); // Length of Header

		buffer.set8(type); // where code = CR or DR
		buffer.setBigEndian16(0); // Destination reference ( 0 at CC and DR)

		buffer.setBigEndian16(0); // source reference should be a reasonable
									// address we use 0
		buffer.set8(0); // service class
		buffer.copyToByteArray(packet, 0, 0, packet.length);
		if(opened)
			rdpsock.send(Base64.encodeBase64String(packet));
		else
			toBeSent.add(Base64.encodeBase64String(packet));

	}

	/**
	 * Send a packet to the server, wrapped in ISO PDU
	 * 
	 * @param buffer
	 *            Packet containing data to send to server
	 * @param option
	 * @throws RdesktopException
	 * @throws IOException
	 */
	public void send(RdpPacket_Localised buffer, Options option)
			throws RdesktopException, IOException {
		if (rdpsock == null)
			return;
		if (buffer.getEnd() < 0) {
			throw new RdesktopException("No End Mark!");
		} else {
			int length = buffer.getEnd();
			byte[] packet = new byte[length];
			// RdpPacket data = this.getMemory(length+7);
			buffer.setPosition(0);
			buffer.set8(PROTOCOL_VERSION); // Version
			buffer.set8(0); // reserved
			buffer.setBigEndian16(length); // length of packet

			buffer.set8(2); // length of header
			buffer.set8(DATA_TRANSFER);
			buffer.set8(EOT);
			buffer.copyToByteArray(packet, 0, 0, buffer.getEnd());
			if (option.isHexdumpDebugEnabled())
				dump.encode(packet, "SEND"/* //System.out */);
			GWT.log("sending: " + Base64.encodeBase64String(packet));
			if(opened)
			{
				rdpsock.send(Base64.encodeBase64String(packet));
			}
			else
			{
				toBeSent.add(Base64.encodeBase64String(packet));
			}
		}
	}

	/**
	 * Receive a data transfer message from the server
	 * 
	 * @return Packet containing message (as ISO PDU)
	 * @throws IOException
	 * @throws RdesktopException
	 * @throws OrderException
	 * @throws CryptoException
	 * @param option
	 * @param rdpLayer
	 */
	public RdpPacket_Localised receive(Options option, Rdp rdpLayer)
			throws IOException, RdesktopException, OrderException,
			CryptoException {
		int[] type = __type;
		GWT.log(" receive called at iso");
		RdpPacket_Localised buffer = __s;// receiveMessage(type, option,
											// rdpLayer);
		GWT.log("receive end iso");
		if (buffer == null)
			return null;
		if (type[0] != DATA_TRANSFER) {
			throw new RdesktopException("Expected DT got:" + type[0]);
		}

		return buffer;
	}

	/**
	 * Receive a specified number of bytes from the server, and store in a
	 * packet
	 * 
	 * @param p
	 *            Packet to append data to, null results in a new packet being
	 *            created
	 * @param length
	 *            Length of data to read
	 * @param option
	 * @return Packet containing read data, appended to original data if
	 *         provided
	 * @throws IOException
	 */
	private RdpPacket_Localised tcp_recv(RdpPacket_Localised p, int length,
			Options option) throws IOException {
		// logger.debug("ISO.tcp_recv");
		RdpPacket_Localised buffer = null;

		byte[] packet = new byte[length];
		for (int i = 0; i < length; i++) {
			packet[i] = stream.removeFirst();
		}
		streamLength = stream.size();
		/*
		 * // in.readFully(packet, 0, length); if (length <= in.length) {
		 * GWT.log("right length: "+length+" datalength:"+in.length); for (int i
		 * = 0; i < length; i++) { packet[i] = in[i]; } GWT.log("done copying");
		 * if (in.length > length) { byte[] tmp = new byte[in.length - length];
		 * for (int j = 0; j < (in.length - length); j++) { tmp[j] = in[length +
		 * j]; } in = tmp; tmp = null; } GWT.log("done resetting"); } else {
		 * GWT.log("wrong length: "+length+" datalength:"+in.length); return
		 * null; }
		 */
		GWT.log("done packet");
		// try{ }
		// catch(IOException e){ //logger.warn("IOException: " +
		// e.getMessage()); return null; }
		if (option.isHexdumpDebugEnabled())
			dump.encode(packet, "RECEIVE" /* //System.out */);
		GWT.log("done dump");
		if (p == null) {
			GWT.log("new packet");
			buffer = new RdpPacket_Localised(length);
			GWT.log("done pakcet initialise");
			buffer.copyFromByteArray(packet, 0, 0, packet.length);
			GWT.log("done copying");
			buffer.markEnd(length);
			GWT.log("done marking");
			buffer.setStart(buffer.getPosition());
			GWT.log("start set");
		} else {
			GWT.log("packet extend");
			buffer = new RdpPacket_Localised((p.getEnd() - p.getStart())
					+ length);
			buffer.copyFromPacket(p, p.getStart(), 0, p.getEnd());
			buffer.copyFromByteArray(packet, 0, p.getEnd(), packet.length);
			buffer.markEnd(p.size() + packet.length);
			buffer.setPosition(p.getPosition());
			buffer.setStart(0);
		}

		return buffer;
	}

	RdpPacket_Localised __s = null;
	int __length, __version;
	int[] __type;
	Options __option;
	Rdp __rdpLayer;

	public void setListner(int[] type, Options option, Rdp rdpLayer,
			GwtEvent event) {
		events.add(event);
		__s = null;
		__type = type;
		__option = option;
		__rdpLayer = rdpLayer;
		if (!events.isEmpty()) {
			Timer timer = new Timer() {

				@Override
				public void run() {
					if (opened) {

						try {
							nextPacket();
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

					} else
						this.schedule(10);
				}
			};
			timer.schedule(10);
		}
	}

	void nextPacket() throws IOException, RdesktopException, OrderException,
			CryptoException {
		Timer timer = new Timer() {

			@Override
			public void run() {
				 GWT.log("streamlength"+streamLength);
				
				if (streamLength >= 4) {
					try {
						__s = tcp_recv(null, 4, __option);
					} catch (IOException e1) {
						// TODO Auto-generated catch block

					}

					if (__s == null)
						return;
					GWT.log("s is not null");
					__version = __s.get8();

					if (__version == 3) {
						GWT.log("version is 3");
						__s.incrementPosition(1); // pad
						__length = __s.getBigEndian16();
					} else {
						GWT.log("version is not 3");
						__length = __s.get8();
						GWT.log("length:" + Integer.toHexString(__length));
						if ((__length & 0x80) != 0) {
							__length &= ~0x80;
							GWT.log("length:" + Integer.toHexString(__length));
							__length = (__length << 8) + __s.get8();
							GWT.log("length:" + Integer.toHexString(__length));
						}
					}
					GWT.log("out of version thing, length:" + __length);
					Timer timer = new Timer() {

						@Override
						public void run() {
							if (streamLength >= (__length - 4)) {
								try {
									__s = tcp_recv(__s, __length - 4, __option);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									GWT.log("tcpioerror");
								}
								if (__s == null)
									return;
								if ((__version & 3) == 0) {
									// logger.info("Processing rdp5 packet");

									try {
										__rdpLayer.rdp5_process(__s,
												(__version & 0x80) != 0,
												__option);
									} catch (RdesktopException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (OrderException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (CryptoException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

									try {
										nextPacket();
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

								} else {

									afterNextPacket();
								}
							} else {
								this.schedule(100);
							}
						}
					};
					timer.schedule(1);
				} else {
					this.schedule(100);
				}
			}
		};
		timer.schedule(1);
	}

	void afterNextPacket() {
		__s.get8();
		/*
		 * if (first) { GWT.log("position:" + __s.getPosition() + " end:" +
		 * __s.getEnd()); byte[] b = new byte[__s.getEnd()];
		 * __s.copyToByteArray(b, 0, 0, b.length); GWT.log("first done: " +
		 * Base64.encodeBase64String(b)); }
		 */

		__type[0] = __s.get8();
		/*
		 * GWT.log("Not confirmed:" + Integer.toBinaryString(__type[0]));
		 * GWT.log("Not confirmed:" +
		 * Integer.toBinaryString(CONNECTION_CONFIRM));
		 */
		if (__type[0] == DATA_TRANSFER) {
			// logger.debug("Data Transfer Packet");
			__s.incrementPosition(1); // eot
			// return __s;
		} else
			__s.incrementPosition(5); // dst_ref, src_ref, class
		if (first) {

			if (__type[0] != CONNECTION_CONFIRM) {
				rdpsock.close();
				GWT.log("Not confirmed:" + Integer.toHexString(__type[0]));
				return;
			}
			first = false;
		}

		GWT.log("calling rdpreceivedEvent");
		eventBus.fireEvent(events.removeFirst());
		if(!events.isEmpty())
		{
			try {
				nextPacket();
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
		// return __s;

	}

	/**
	 * Receive a message from the server
	 * 
	 * @param type
	 *            Array containing message type, stored in type[0]
	 * @param option
	 * @param rdpLayer
	 * @return Packet object containing data of message
	 * @throws IOException
	 * @throws RdesktopException
	 * @throws OrderException
	 * @throws CryptoException
	 */
	private RdpPacket_Localised receiveMessage(int[] type, Options option,
			Rdp rdpLayer) throws IOException, RdesktopException,
			OrderException, CryptoException {
		// logger.debug("ISO.receiveMessage");
		RdpPacket_Localised s = null;
		int length, version;

		next_packet: while (true) {
			// logger.debug("next_packet");
			GWT.log("tcp receive called");
			s = tcp_recv(null, 4, option);
			GWT.log("tcp receive end");
			if (s == null)
				return null;
			GWT.log("s is not null");
			version = s.get8();

			if (version == 3) {
				GWT.log("version is 3");
				s.incrementPosition(1); // pad
				length = s.getBigEndian16();
			} else {
				GWT.log("version is not 3");
				length = s.get8();
				GWT.log("length:" + Integer.toHexString(length));
				if ((length & 0x80) != 0) {
					length &= ~0x80;
					GWT.log("length:" + Integer.toHexString(length));
					length = (length << 8) + s.get8();
					GWT.log("length:" + Integer.toHexString(length));
				}
			}
			GWT.log("out of version thing, length:" + length);
			s = tcp_recv(s, length - 4, option);
			if (s == null)
				return null;
			if ((version & 3) == 0) {
				// logger.info("Processing rdp5 packet");
				rdpLayer.rdp5_process(s, (version & 0x80) != 0, option);
				continue next_packet;
			} else
				break;
		}

		s.get8();
		type[0] = s.get8();

		if (type[0] == DATA_TRANSFER) {
			// logger.debug("Data Transfer Packet");
			s.incrementPosition(1); // eot
			return s;
		}

		s.incrementPosition(5); // dst_ref, src_ref, class
		return s;
	}

	/**
	 * Disconnect from an RDP session, closing all sockets
	 */
	public void disconnect() {
		if (rdpsock == null)
			return;
		try {
			sendMessage(DISCONNECT_REQUEST);
			/*
			 * if (in != null) in.close(); if (out != null) out.close();
			 */
			if (rdpsock != null)
				rdpsock.close();
		} catch (IOException e) {
			/*
			 * in = null; out = null;
			 */rdpsock = null;
			return;
		}
		/*
		 * in = null; out = null;
		 */rdpsock = null;
	}

	/**
	 * Send the server a connection request, detailing client protocol version
	 * 
	 * @throws IOException
	 * @param option
	 */
	void send_connection_request(Options option) throws IOException {

		String uname = option.getUsernameTenCharacterLong();
		int length = 11 + (option.isUsernameSet() ? ("Cookie: mstshash="
				.length() + uname.length() + 2) : 0) + 8;
		GWT.log("length:" + length);
		RdpPacket_Localised buffer = new RdpPacket_Localised(length);
		byte[] packet = new byte[length];

		buffer.set8(PROTOCOL_VERSION); // send Version Info
		buffer.set8(0); // reserved byte
		buffer.setBigEndian16(length); // Length
		buffer.set8(length - 5); // Length of Header
		buffer.set8(CONNECTION_REQUEST);
		buffer.setBigEndian16(0); // Destination reference ( 0 at CC and DR)
		buffer.setBigEndian16(0); // source reference should be a reasonable
									// address we use 0
		buffer.set8(0); // service class
		if (option.isUsernameSet()) {
			// logger.debug("Including username");
			buffer.out_uint8p("Cookie: mstshash=", "Cookie: mstshash=".length());
			buffer.out_uint8p(uname, uname.length());

			buffer.set8(0x0d); // unknown
			buffer.set8(0x0a); // unknown
		}

		/*
		 * // Authentication request? buffer.setLittleEndian16(0x01);
		 * buffer.setLittleEndian16(0x08); // Do we try to use SSL?
		 * buffer.set8(Options.use_ssl? 0x01 : 0x00);
		 * buffer.incrementPosition(3);
		 */
		// GWT.log("packet length:"+packet.length);
		buffer.copyToByteArray(packet, 0, 0, packet.length);
		// GWT.log("packet length after copy:"+packet.length);

		GWT.log(Base64.encodeBase64String(packet));
		if(opened)
			rdpsock.send(Base64.encodeBase64String(packet));
		else
			toBeSent.add(Base64.encodeBase64String(packet));
	}

	public void connected() {
		GWT.log("websocket connected");

	}

	public void disconnected() {
		GWT.log("websocket disconnected");
	}

	public void message(String arg0) {

	}

	/*public void sendToWebsocket(String s)
	{
		if(opened)
		{
			while(!toBeSent.isEmpty())
			{
				rdpsock.send(toBeSent.removeFirst());
			}
			rdpsock.send(s);
		}
		else
		{
			toBeSent.add(s);
		}
	}	*/
	public LinkedList<String> toBeSent = new LinkedList<String>();
}
