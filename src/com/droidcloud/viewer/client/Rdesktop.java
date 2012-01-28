/* Rdesktop.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:22 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Main class, launches session
 */
package com.droidcloud.viewer.client;



import com.droidcloud.viewer.client.keymapping.KeyCode_FileBased;
import com.droidcloud.viewer.client.keymapping.KeyMapException;
import com.droidcloud.viewer.client.rdp5.VChannels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;



import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Rdesktop {



    static boolean keep_running;

    private static final String KEY_MAP_PATH = "keymaps/";

    static String PROGRAM_NAME = "DroidCloudViewer";
//    static ViewerApplet parentApplet = null;
    static RdesktopRunner runner;


  /*  static {
        BasicConfigurator.configure();
    }
*/
    /**
     * Outputs version and usage information via System.err
     */
    public static void usage() {
        System.err.println("Version " + Version.version);
        System.err.println("Usage: java com.droidcloud.viewer.Rdesktop [options] server[:port]");
        System.err.println("	-b 							 bandwidth saving (good for 56k modem, but higher latency");
        System.err.println("	-c DIR						 working directory");
        System.err.println("	-d DOMAIN					 logon domain");
        System.err.println("	-f[l]						 full-screen mode [with Linux KDE optimization]");
        System.err.println("	-g WxH						 desktop geometry");
        System.err.println("	-m MAPFILE					 keyboard mapping file for terminal server");
        System.err.println("	-l LEVEL					 logging level {DEBUG, INFO, WARN, ERROR, FATAL}");
        System.err.println("	-n HOSTNAME					 client hostname");
        System.err.println("	-p PASSWORD					 password");
        System.err.println("	-s SHELL					 shell");
        System.err.println("	-t NUM						 RDP port (default 3389)");
        System.err.println("	-T TITLE					 window title");
        System.err.println("	-u USERNAME					 user name");
        System.err.println("	-o BPP						 bits-per-pixel for display");
        System.err.println("    -r path                      path to load licence from (requests and saves licence from server if not found)");
        System.err.println("    --save_licence               request and save licence from server");
        System.err.println("    --load_licence               load licence from file");
        System.err.println("    --console                    connect to console");
        System.err.println("	--debug_key 				 show scancodes sent for each keypress etc");
        System.err.println("	--debug_hex 				 show bytes sent and received");
        System.err.println("	--no_remap_hash 			 disable hash remapping");
        System.err.println("	--quiet_alt 				 enable quiet alt fix");
        System.err.println("	--no_encryption				 disable encryption from client to server");
        System.err.println("	--use_rdp4					 use RDP version 4");
        //System.err.println("    --enable_menu               enable menu bar");
        System.err.println("	--log4j_config=FILE			 use FILE for log4j configuration");
        //System.err.println("    --error_handler=errorHandler javscript handler for error handling");
        //System.err.println("    --disconnect_handler=disconnectHandler,javscript handler for error handling");
        System.err.println("Example: java com.droidcloud.viewer.Rdesktop -g 800x600 -l WARN m52.propero.int");
        exitIfApplication(0, true);
    }


    /**
     * @param args
     */
/*    public static void main(String[] args) {
        main(args);
    }
*/

/*    static boolean isSuccessRunningNativeRDPClient(String[] args) {
        RDPClientChooser Chooser = new RDPClientChooser();
        Options option = new Options();
        return Chooser.RunNativeRDPClient(args, null);
    }*/

   static VChannels initializeChannels(Options option) throws RdesktopException{
        VChannels channels = new VChannels();        
        // Initialise all RDP5 channels
       /* if (option.shouldUseRdp5() && option.isClipboardMappingEnabled()) {
                channels.register(clipChannel, option);

        }*/
        return channels;
    }


    static KeyCode_FileBased loadKeyMap(Options option) throws KeyMapException, IOException {
        KeyCode_FileBased keyMap = null;
        //logger.info("looking for: " + "/" + KEY_MAP_PATH + option.getMapFile());
        // //logger.info("istr = " + istr);

            //logger.debug("Loading keymap from filename");
            keyMap = new KeyCode_FileBased_Localised(KEY_MAP_PATH + option.getMapFile(), option);
        return keyMap;
    }

    static RdesktopCanvas_Localised createCanvas(Options option) {
    	/*Storage store = Storage.getSessionStorageIfSupported();
        String width = store.getItem("width");
        String height = store.getItem("height");
        option.setWidth(Integer.parseInt(width));
        option.setHeight(Integer.parseInt(height));*/
/*        option.setWidth(option.isEmbededInBrowser() ? parentApplet.getWidth() : option.getWidth());
        option.setHeight(option.isEmbededInBrowser() ? parentApplet.getHeight() : option.getHeight());*/
        //logger.info("Option Height" + option.getHeight() + "Option width" + option.getWidth());
        RdesktopCanvas_Localised canvas = new RdesktopCanvas_Localised(
                option.getWidth(), option.getHeight(), option);

        return canvas;
    }

    /**
     * Disconnects from the server connected to through rdp and destroys the
     * RdesktopFrame window.
     * <p/>
     * Exits the application iff sysexit == true, providing return value n to
     * the operating system.
     *
     * @param n
     * @param sysexit
     * @param window
     * @param option
     */
    public static synchronized void exit(int n, boolean sysexit,  Options option) {
///        closeDesktopFrame(window, option);
        if (sysexit) {
            Common.rdp = null;
            //Common.frame = null;
            //find a way to destory below objects.
            Common.canvas = null;
  //          parentApplet = null;
        }

        exitIfApplication(n, sysexit);
        return;
    }

    public static synchronized  void stopApplet(String disconnectHandler) {
        keep_running = false;
        //logger.info("Stop Applet");
       
        if ( disconnectHandler != null && ! disconnectHandler.isEmpty()) {
   //         parentApplet.invokeJavaScriptEventHandler(disconnectHandler, null);
        }

    }

    private static void exitIfApplication(int n, boolean sysexit) {
        //logger.info("isRunningUnderApplet:" + Common.underApplet + " Exiting");
        if (sysexit && Common.isRunningAsApplication()) {
                //logger.info("Exiting Application");
         //       System.exit(n);
        }
        return;
    }

/*    private static void closeDesktopFrame(RdesktopFrame window, Options option) {
        if ( Common.isRunningAsApplet()) {
            stopApplet(option.getDisconnectHandler());
        }
        if (window != null) {
            window.setVisible(false);
            window.removeAll();
            window.dispose();
        }
    }
*/

    //Not sure, why we need this ????
    public static void error(Exception e, boolean sysexit, Options option) {
        //logger.fatal(e.getMessage());
        String[] msg = {e.getClass().getName(), e.getMessage()};
        reportError( msg, option);
        keep_running = false;
//        closeDesktopFrame(window, option);
        if (sysexit) {
            Common.rdp = null;
            //Common.frame = null;
            //find a way to destory below objects.
            //Common.canvas = null;
//            parentApplet = null;
        }
        exitIfApplication(0, sysexit);
    }

    static void reportError(String[] errorMessages, Options option) {
        if (option.isErrorHandlerAvailable() && Common.isRunningAsApplet()) {
//            parentApplet.invokeJavaScriptEventHandler(option.getErrorHandler(), errorMessages);
        } else {
          }
    }

    public static void stopRdesktop() {
        keep_running = false;
        runner.disconnectRdpPlayer();
    }

    /**
     * @param args
     * @param parentApplet
     */
    public static void main(String[] args) {

        if (args.length == 0)
            usage();

/*        if (isSuccessRunningNativeRDPClient(args)) {
            if (!Common.underApplet) System.exit(0);
        }
*/
        keep_running = true;
        //logger.setLevel(Level.INFO);

    //   OptionsBuilder parser = new OptionsBuilder(args);
//        Options option = parser.populate();
 //       option.setUsername("droidcloud");
  //      option.setPassword("droidcloud1511");
       String server = "127.0.0.1";
        Options option = new Options();
        option.setPort(3381);
        option.setWidth(320);
        option.setHeight(480);        
        
  //      logger.info("Version " + Version.version + "embed" + option.isEmbededInBrowser());

    //    RdesktopFrame window = null;
      //  Rdesktop.parentApplet = parentApplet;
        RdesktopCanvas_Localised canvas = createCanvas(option);
        
        if ( ! ( option.isEmbededInBrowser() && option.isNotFullscreen()) ) {
//            window = new RdesktopFrame_Localised(canvas, option);
        }

        // Configure a keyboard layout
        GWT.log("loading keymaps");
        KeyCode_FileBased keyMap = null;
        try {
            keyMap = loadKeyMap(option);
        } catch (Exception kmEx) {
        	GWT.log("keymap load error");
/*            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            kmEx.printStackTrace(pw);
            reportError( new String[]{sw.toString()}, option);
            exitIfApplication(0, true);*/
            return;
        }
        GWT.log("loading keymaps done");
/*        VChannels channels;
        ClipChannel clipChannel = new ClipChannel(option);

        try {
            channels = initializeChannels(clipChannel, option);
        } catch( RdesktopException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            reportError( new String[]{sw.toString()}, option);
            exitIfApplication(0, true);
            return;
        }

        Secure secureLayer = new Secure(channels,option);
*/
//        canvas.addFocusListener(clipChannel);
//        canvas.addFocusListener(new RdesktopFocusListener(canvas, option));
        GWT.log("loading channels");
        VChannels channels = null;
        try {
			channels  = initializeChannels(option);
		} catch (RdesktopException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        GWT.log("loading secure connection");
        Secure secureLayer = new Secure(channels, option);
        GWT.log("registering keyboard");
        canvas.registerKeyboard(keyMap);

     //   logger.debug("Registering keyboard...");
        option.setKeylayout(keyMap.getMapCode()); 
        GWT.log("create runner");
        runner = new RdesktopRunner.RdesktopCanvasRunner(option, canvas);
        GWT.log("run rdp");
        runner.run(server, secureLayer);
/*        if (option.isEmbededInBrowser() && option.isNotFullscreen()) {
            runner = new RdesktopRunner.RdesktopCanvasRunner(option, canvas);
            runner.run(server, secureLayer);
        } else {
            runner = new RdesktopRunner.RdesktopFrameRunner(parentApplet,option,window);
            runner.run(server, secureLayer);
        }
        logger.info("Exit main");
*/        exit(0, true,  option);
    }

}
