package com.droidcloud.viewer.server;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Vector;

import com.droidcloud.viewer.client.GreetingService;
import com.droidcloud.viewer.client.keymapping.KeyMapException;
import com.droidcloud.viewer.shared.FieldVerifier;
import com.droidcloud.viewer.shared.KeyMapData;
import com.droidcloud.viewer.shared.MapDef;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dev.util.Pair;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid.
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back
			// to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script
		// vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	public KeyMapData getMap(String name) throws IllegalArgumentException{
		InputStream fstream;
		Vector keyMap = new Vector<>();
		int mapCode = -1;
		try {
			GWT.log("path:"+name);
			fstream = this.getClass().getResourceAsStream("en-gb");
//			 fstream = new FileInputStream(this.getClass().getResource("en-gb").toString());
			 int lineNum = 0; // current line number being parsed
		        String line = ""; // contents of line being parsed

		        if (fstream == null)
		            throw new KeyMapException("Could not find specified keymap file");

		        boolean mapCodeSet = false;
		        
			try {
	        	
	            DataInputStream in = new DataInputStream(fstream);

	    //        if (in == null)
	                //logger.warn("in == null");
	            
	            while (in.available() != 0) {
	                lineNum++;
	                line = in.readLine();

	                char fc = 0x0;
	                if ((line != null) && (line.length() > 0))
	                    fc = line.charAt(0);

	                // ignore blank and commented lines
	                if ((line != null) && (line.length() > 0) && (fc != '#') && (fc != 'c')) {
	                    keyMap.add(new MapDef(line)); // parse line into a MapDef
	                    
	                } else if (fc == 'c') {
	                    StringTokenizer st = new StringTokenizer(line);
	                    String s = st.nextToken();

	                    s = st.nextToken();
	                    mapCode = Integer.decode(s).intValue();
	                    mapCodeSet = true;
	                }
	            }
		} catch (FileNotFoundException e) {
			throw new KeyMapException("KeyMap file not found: " + name);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		catch (KeyMapException ke){					
		}
		KeyMapData pair = new KeyMapData();
		pair.setKeyMap(keyMap);
		pair.setMapCode(mapCode);
		return pair;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
