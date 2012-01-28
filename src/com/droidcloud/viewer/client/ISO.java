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

import net.zschech.gwt.websockets.client.CloseHandler;
import net.zschech.gwt.websockets.client.ErrorHandler;
import net.zschech.gwt.websockets.client.MessageEvent;
import net.zschech.gwt.websockets.client.MessageHandler;
import net.zschech.gwt.websockets.client.OpenHandler;
import net.zschech.gwt.websockets.client.WebSocket;

import com.droidcloud.viewer.client.crypto.CryptoException;
import com.droidcloud.viewer.client.tools.Base64;
import com.google.gwt.core.client.GWT;

public abstract class ISO implements OpenHandler,CloseHandler,MessageHandler,ErrorHandler {

	@Override
	public void onOpen(WebSocket webSocket) {
		// TODO Auto-generated method stub
		GWT.log("socket opened ");
	}

	@Override
	public void onClose(WebSocket webSocket) {
		// TODO Auto-generated method stub
		GWT.log("socket closed");
	}

	@Override
	public void onMessage(WebSocket webSocket, MessageEvent message) {
		GWT.log("message received");
		byte[] tmp1 = Base64.decodeBase64(message.getData());
		if(in == null)
		{
			in = new byte[tmp1.length];
		}
		byte[] tmp = in;
		int length = tmp.length + tmp1.length;		
		in = new byte[length];
		for (int i = 0; i < length; i++) {
			if (i < tmp.length)
				in[i] = tmp[i];
			else
				in[i] = tmp1[i - tmp.length];
		}
		tmp = null;
		tmp1 = null;
	}

	@Override
	public void onError(WebSocket webSocket) {
		GWT.log("websocket error!");		
	}

	private HexDump dump = null;

	protected WebSocket rdpsock = null;
	// private DataInputStream in = null;
	// private OutputStream out = null;
	protected volatile byte[] in = null;
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
		this.rdpsock = WebSocket.create("ws://" + host + ":" + port,"base64");
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
		 */send_connection_request(option);

		receiveMessage(code, option, rdpLayer);
		if (code[0] != CONNECTION_CONFIRM) {
			throw new RdesktopException("Expected CC got:"
					+ Integer.toHexString(code[0]).toUpperCase());
		}

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
		rdpsock.send(Base64.encodeBase64String(packet));
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
			rdpsock.send(Base64.encodeBase64String(packet));
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
		int[] type = new int[1];
		GWT.log(" receive called at iso");
		RdpPacket_Localised buffer = receiveMessage(type, option, rdpLayer);
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

		// in.readFully(packet, 0, length);
		if (length <= in.length) {
			GWT.log("right length: "+length+" datalength:"+in.length);
			for (int i = 0; i < length; i++) {
				packet[i] = in[i];
			}
			GWT.log("done copying");
			if (in.length > length) {
				byte[] tmp = new byte[in.length - length];
				for (int j = 0; j < (in.length - length); j++) {
					tmp[j] = in[length + j];
				}
				in = tmp;
				tmp = null;
			}
			GWT.log("done resetting");
		}
		else
		{
			GWT.log("wrong length: "+length+" datalength:"+in.length);
			return null;
		}
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
				GWT.log("length:"+Integer.toHexString(length));
				if ((length & 0x80) != 0) {					
					length &= ~0x80;
					GWT.log("length:"+Integer.toHexString(length));
					length = (length << 8) + s.get8();
					GWT.log("length:"+Integer.toHexString(length));
				}				
			}
			GWT.log("out of version thing, length:"+length);
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
		buffer.copyToByteArray(packet, 0, 0, packet.length);

		rdpsock.send(Base64.encodeBase64String(packet));
	}

	public void connected() {
		GWT.log("websocket connected");

	}

	public void disconnected() {
		GWT.log("websocket disconnected");
	}

	public void message(String arg0) {

	}

}
