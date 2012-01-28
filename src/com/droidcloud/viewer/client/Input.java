/* Input.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.2 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/14 23:26:08 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Handles input events and sends relevant input data
 *          to server
 */

package com.droidcloud.viewer.client;

import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.util.Vector;

import com.droidcloud.viewer.client.keymapping.KeyCode;
import com.droidcloud.viewer.client.keymapping.KeyCode_FileBased;
import com.droidcloud.viewer.client.keymapping.KeyMapException;
import com.droidcloud.viewer.client.keymapping.KeyPressedEvent;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class Input {

	KeyCode_FileBased newKeyMapper = null;
	Canvas mCanvas;

	protected Vector pressedKeys;

	protected static boolean capsLockOn = false;
	protected static boolean numLockOn = false;
	protected static boolean scrollLockOn = false;

	protected static boolean serverAltDown = false;
	protected static boolean altDown = false;
	protected static boolean ctrlDown = false;

	protected static long last_mousemove = 0;

	// Using this flag value (0x0001) seems to do nothing, and after running
	// through other possible values, the RIGHT flag does not appear to be
	// implemented
	protected static final int KBD_FLAG_RIGHT = 0x0001;
	protected static final int KBD_FLAG_EXT = 0x0100;

	// QUIET flag is actually as below (not 0x1000 as in rdesktop)
	protected static final int KBD_FLAG_QUIET = 0x200;
	protected static final int KBD_FLAG_DOWN = 0x4000;
	protected static final int KBD_FLAG_UP = 0x8000;

	public static final int RDP_KEYPRESS = 0;
	public static final int RDP_KEYRELEASE = KBD_FLAG_DOWN | KBD_FLAG_UP;
	public static final int MOUSE_FLAG_MOVE = 0x0800;

	public static final int MOUSE_FLAG_BUTTON1 = 0x1000;
	public static final int MOUSE_FLAG_BUTTON2 = 0x2000;
	public static final int MOUSE_FLAG_BUTTON3 = 0x4000;

	public static final int MOUSE_FLAG_BUTTON4 = 0x0280; // wheel up -
															// rdesktop 1.2.0
	public static final int MOUSE_FLAG_BUTTON5 = 0x0380; // wheel down -
															// rdesktop 1.2.0
	public static final int MOUSE_FLAG_DOWN = 0x8000;

	protected static final int RDP_INPUT_SYNCHRONIZE = 0;
	protected static final int RDP_INPUT_CODEPOINT = 1;
	protected static final int RDP_INPUT_VIRTKEY = 2;
	protected static final int RDP_INPUT_SCANCODE = 4;
	public static final int RDP_INPUT_MOUSE = 0x8001;

	protected static int time = 0;

	public KeyCodeEvent lastKeyEvent = null;

	public boolean modifiersValid = false;
	public boolean keyDownWindows = false;

	protected RdesktopCanvas canvas = null;
	protected Rdp rdp = null;
	KeyCode keys = null;
	protected Options option;

	/**
	 * Create a new Input object with a given keymap object
	 * 
	 * @param c
	 *            Canvas on which to listen for input events
	 * @param r
	 *            Rdp layer on which to send input messages
	 * @param k
	 *            Key map to use in handling keyboard events
	 * @param option
	 */
	public Input(RdesktopCanvas c, Rdp r, KeyCode_FileBased k, Options option) {
		newKeyMapper = k;
		canvas = c;
		rdp = r;

		if (rdp != null) {
			rdp.sendInput(Input.getTime(), RDP_INPUT_MOUSE, MOUSE_FLAG_MOVE
					| MOUSE_FLAG_DOWN, 0, 0);
		}

		this.option = option;
		this.mCanvas = c.getCanvas();
		addInputListeners();
		pressedKeys = new Vector();
	}

	/**
	 * Create a new Input object, using a keymap generated from a specified file
	 * 
	 * @param c
	 *            Canvas on which to listen for input events
	 * @param r
	 *            Rdp layer on which to send input messages
	 * @param keymapFile
	 *            Path to file containing keymap data
	 * @param option
	 */

	public Input(RdesktopCanvas c, Rdp r, String keymapFile, Options option) {
		try {
			newKeyMapper = new KeyCode_FileBased_Localised(keymapFile, option);
		} catch (KeyMapException kmEx) {
			System.err.println(kmEx.getMessage());
			/*if (!Common.underApplet)
				System.exit(-1);*/
		}

		canvas = c;
		rdp = r;
		this.mCanvas = c.getCanvas();
		/*
		 * if (option.isKeyboardDebugEnabled()) logger.setLevel(Level.DEBUG);
		 */
		addInputListeners();
		this.option = option;
		pressedKeys = new Vector();
	}

	/**
	 * Add all relevant input listeners to the canvas
	 */
	public void addInputListeners() {		
		mCanvas.addMouseDownHandler(new RDesktopMouseDownHandler());
		mCanvas.addMouseUpHandler(new RDesktopMouseUpHandler());
		mCanvas.addMouseMoveHandler(new RDesktopMouseMoveHandler());
		mCanvas.addKeyDownHandler(new RDesktopKeyDownAdapter());
		mCanvas.addKeyUpHandler(new RDesktopKeyUpAdapter());

		/*
		 * canvas.addMouseListener(new RdesktopMouseAdapter());
		 * canvas.addMouseMotionListener(new RdesktopMouseMotionAdapter());
		 * canvas.addKeyListener(new RdesktopKeyAdapter());
		 */
	}

	/**
	 * Send a sequence of key actions to the server
	 * 
	 * @param pressSequence
	 *            String representing a sequence of key actions. Actions are
	 *            represented as a pair of consecutive characters, the first
	 *            character's value (cast to integer) being the scancode to
	 *            send, the second (cast to integer) of the pair representing
	 *            the action (0 == UP, 1 == DOWN, 2 == QUIET UP, 3 == QUIET
	 *            DOWN).
	 */
	public void sendKeyPresses(String pressSequence) {
		try {
			String debugString = "Sending keypresses: ";
			for (int i = 0; i < pressSequence.length(); i += 2) {
				int scancode = (int) pressSequence.charAt(i);
				int action = (int) pressSequence.charAt(i + 1);
				int flags = 0;

				if (action == KeyCode_FileBased.UP)
					flags = RDP_KEYRELEASE;
				else if (action == KeyCode_FileBased.DOWN)
					flags = RDP_KEYPRESS;
				else if (action == KeyCode_FileBased.QUIETUP)
					flags = RDP_KEYRELEASE | KBD_FLAG_QUIET;
				else if (action == KeyCode_FileBased.QUIETDOWN)
					flags = RDP_KEYPRESS | KBD_FLAG_QUIET;

				long t = getTime();

				debugString += "(0x"
						+ Integer.toHexString(scancode)
						+ ", "
						+ ((action == KeyCode_FileBased.UP || action == KeyCode_FileBased.QUIETUP) ? "up"
								: "down")
						+ ((flags & KBD_FLAG_QUIET) != 0 ? " quiet" : "")
						+ " at " + t + ")";
				// System.out.println("Scancode is " + scancode);
				sendScancode(t, flags, scancode);
			}
			// System.out.println("debug keys: "+debugString);
			// if (pressSequence.length() > 0)
			// logger.debug(debugString);
		} catch (Exception ex) {
			return;
		}
	}

	/**
	 * Retrieve the next "timestamp", by incrementing previous stamp (up to the
	 * maximum value of an integer, at which the timestamp is reverted to 1)
	 * 
	 * @return New timestamp value
	 */
	public static int getTime() {
		time++;
		if (time == Integer.MAX_VALUE)
			time = 1;
		return time;
	}

	/**
	 * Handle loss of focus to the main canvas. Clears all depressed keys
	 * (sending release messages to the server.
	 */
	public void lostFocus() {
		clearKeys();
		modifiersValid = false;
	}

	/**
	 * Handle the main canvas gaining focus. Check locking key states.
	 */
	public void gainedFocus() {
		doLockKeys(); // ensure lock key states are correct
	}

	/**
	 * Send a keyboard event to the server
	 * 
	 * @param time
	 *            Time stamp to identify this event
	 * @param flags
	 *            Flags defining the nature of the event (eg:
	 *            press/release/quiet/extended)
	 * @param scancode
	 *            Scancode value identifying the key in question
	 */
	public void sendScancode(long time, int flags, int scancode) {
		// System.out.print("flags: " + flags + "scancode" + scancode);
		if (scancode == 0x38) { // be careful with alt
			if ((flags & RDP_KEYRELEASE) != 0) {
				// //logger.info("Alt release, serverAltDown = " +
				// serverAltDown);
				serverAltDown = false;
			}
			if ((flags == RDP_KEYPRESS)) {
				// //logger.info("Alt press, serverAltDown = " + serverAltDown);
				serverAltDown = true;
			}
		}

		if ((scancode & KeyCode.SCANCODE_EXTENDED) != 0) {
			rdp.sendInput((int) time, RDP_INPUT_SCANCODE, flags | KBD_FLAG_EXT,
					scancode & ~KeyCode.SCANCODE_EXTENDED, 0);
		} else
			rdp.sendInput((int) time, RDP_INPUT_SCANCODE, flags, scancode, 0);
		// System.out.println("scan code: "+scancode+" falgs: "+flags);
	}

	/**
	 * Release any modifier keys that may be depressed.
	 */
	public void clearKeys() {
		if (!modifiersValid)
			return;

		altDown = false;
		ctrlDown = false;

		if (lastKeyEvent == null)
			return;

		if (lastKeyEvent.isShiftKeyDown())
			sendScancode(getTime(), RDP_KEYRELEASE, 0x2a); // shift
		if (lastKeyEvent.isAltKeyDown() || serverAltDown) {
			sendScancode(getTime(), RDP_KEYRELEASE, 0x38); // ALT
			sendScancode(getTime(), RDP_KEYPRESS | KBD_FLAG_QUIET, 0x38); // ALT
			sendScancode(getTime(), RDP_KEYRELEASE | KBD_FLAG_QUIET, 0x38); // l.alt
		}
		if (lastKeyEvent.isControlKeyDown()) {
			sendScancode(getTime(), RDP_KEYRELEASE, 0x1d); // l.ctrl
			// sendScancode(getTime(), RDP_KEYPRESS | KBD_FLAG_QUIET, 0x1d); //
			// Ctrl
			// sendScancode(getTime(), RDP_KEYRELEASE | KBD_FLAG_QUIET, 0x1d);
			// // ctrl
		}

	}

	/**
	 * Send keypress events for any modifier keys that are currently down
	 */
	public void setKeys() {
		if (!modifiersValid)
			return;

		if (lastKeyEvent == null)
			return;

		if (lastKeyEvent.isShiftKeyDown())
			sendScancode(getTime(), RDP_KEYPRESS, 0x2a); // shift
		if (lastKeyEvent.isAltKeyDown())
			sendScancode(getTime(), RDP_KEYPRESS, 0x38); // l.alt
		if (lastKeyEvent.isControlKeyDown())
			sendScancode(getTime(), RDP_KEYPRESS, 0x1d); // l.ctrl
	}

	class RDesktopMouseDownHandler implements MouseDownHandler {

		@Override
		public void onMouseDown(MouseDownEvent e) {
			mouseMoving = true;
			int time = getTime();
			if (rdp != null) {
				rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_BUTTON1
						| MOUSE_FLAG_DOWN, e.getX(), e.getY());

			}
			time = getTime();

			if (rdp != null) {
				if (e.getNativeButton() == NativeEvent.BUTTON_LEFT) {
					rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_BUTTON1
							| MOUSE_FLAG_DOWN, e.getX(), e.getY());
				} else if (e.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
					rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_BUTTON2
							| MOUSE_FLAG_DOWN, e.getX(), e.getY());
				} else if (e.getNativeButton() == NativeEvent.BUTTON_MIDDLE) {
					// middleButtonPressed(e);
				}
			}

		}

	}

	class RDesktopMouseUpHandler implements MouseUpHandler {

		@Override
		public void onMouseUp(MouseUpEvent e) {
			mouseMoving = false;
			int time = getTime();
			if (rdp != null) {
				if (e.getNativeButton() == NativeEvent.BUTTON_LEFT) {
					rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_BUTTON1
							| MOUSE_FLAG_DOWN, e.getX(), e.getY());
				} else if (e.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
					rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_BUTTON2
							| MOUSE_FLAG_DOWN, e.getX(), e.getY());
				} else if (e.getNativeButton() == NativeEvent.BUTTON_MIDDLE) {
					// middleButtonPressed(e);
				}
			}

		}

	}

	boolean mouseMoving = false;

	class RDesktopMouseDraggedHandler implements DragHandler {
		private boolean send_mouse_drag_event = true;

		@Override
		public void onDrag(DragEvent e) {
			/*
			 * if (!this.send_mouse_drag_event) { this.send_mouse_drag_event =
			 * true; return; } this.send_mouse_drag_event = false; int time =
			 * getTime();
			 * 
			 * if (rdp != null) { rdp.sendInput(time, RDP_INPUT_MOUSE,
			 * MOUSE_FLAG_MOVE, e.getX(), e.getY()); }
			 */

		}

	}

	class RDesktopMouseMoveHandler implements MouseMoveHandler {
		boolean send_mouse_drag_event = true;

		@Override
		public void onMouseMove(MouseMoveEvent e) {
			if (mouseMoving) {
				if (!this.send_mouse_drag_event) {
					this.send_mouse_drag_event = true;
					return;
				}
				this.send_mouse_drag_event = false;
				int time = getTime();

				if (rdp != null) {
					rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_MOVE,
							e.getX(), e.getY());
				}
			}
		}

	}

	class RDesktopKeyDownAdapter implements KeyDownHandler {

		@Override
		public void onKeyDown(KeyDownEvent e) {
			lastKeyEvent = e;
			modifiersValid = true;
			long time = getTime();

			// Some java versions have keys that don't generate keyPresses -
			// here we add the key so we can later check if it happened
			pressedKeys.addElement(new Integer(e.getNativeKeyCode()));
			// System.out.print("PRESSED keychar='" + e.getKeyChar() +
			// "' keycode=0x"
			// + Integer.toHexString(e.getKeyCode()) + " char='"
			// + ((char) e.getKeyCode()) + "'\n");
			// logger.debug("PRESSED keychar='" + e.getKeyChar() +
			// "' keycode=0x"
			// + Integer.toHexString(e.getKeyCode()) + " char='"
			// + ((char) e.getKeyCode()) + "'");

			// System.out.println("PRESSED keychar='" + e.getKeyChar() +
			// "' keycode=0x"
			// + Integer.toHexString(e.getKeyCode()) + " char='"
			// + ((char) e.getKeyCode()) + "'");
			KeyPressedEvent event = new KeyPressedEvent();
			event.setEvent(e);
			event.setID(KeyPressedEvent.KEY_RELEASED);
			if (rdp != null) {
				if (!handleSpecialKeys(time, e, true)) {
					sendKeyPresses(newKeyMapper.getKeyStrokes(event));
				}

			}

		}
	}

	class RDesktopKeyUpAdapter implements KeyUpHandler {

		@Override
		public void onKeyUp(KeyUpEvent e) {
			Integer keycode = new Integer(e.getNativeKeyCode());
			if (!pressedKeys.contains(keycode)) {

			}

			pressedKeys.removeElement(keycode);
			lastKeyEvent = e;
			modifiersValid = true;
			KeyPressedEvent event = new KeyPressedEvent();
			event.setEvent(e);
			event.setID(KeyPressedEvent.KEY_RELEASED);
			long time = getTime();

			// logger.debug("RELEASED keychar='" + e.getKeyChar() +
			// "' keycode=0x"
			// + Integer.toHexString(e.getKeyCode()) + " char='"
			// + ((char) e.getKeyCode()) + "'");
			if (rdp != null) {
				if (!handleSpecialKeys(time, e, false))
					sendKeyPresses(newKeyMapper.getKeyStrokes(event));
				// sendScancode(time, RDP_KEYRELEASE, keys.getScancode(e));
			}

		}

	}

	class RDesktopKeyPressAdapter implements KeyPressHandler {

		@Override
		public void onKeyPress(KeyPressEvent event) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Act on any keyboard shortcuts that a specified KeyEvent may describe
	 * 
	 * @param time
	 *            Time stamp for event to send to server
	 * @param e
	 *            Keyboard event to be checked for shortcut keys
	 * @param pressed
	 *            True if key was pressed, false if released
	 * @return True if a shortcut key combination was detected and acted upon,
	 *         false otherwise
	 */
	public boolean handleShortcutKeys(long time, KeyCodeEvent e, boolean pressed) {
		if (!e.isAltKeyDown())
			return false;

		if (!altDown)
			return false; // all of the below have ALT on

		switch (e.getNativeKeyCode()) {

		/*
		 * case KeyEvent.VK_M: if(pressed) ((RdesktopFrame_Localised)
		 * canvas.getParent()).toggleMenu(); break;
		 */

		case KeyCodes.KEY_ENTER:
			sendScancode(time, RDP_KEYRELEASE, 0x38);
			altDown = false;
			// ((RdesktopFrame_Localised)
			// mCanvas.getParent()).toggleFullScreen();
			break;

		/*
		 * The below case block handles "real" ALT+TAB events. Once the TAB in
		 * an ALT+TAB combination has been pressed, the TAB is sent to the
		 * server with the quiet flag on, as is the subsequent ALT-up.
		 * 
		 * This ensures that the initial ALT press is "undone" by the server.
		 * 
		 * --- Tom Elliott, 7/04/05
		 */

		case KeyCodes.KEY_TAB: // Alt+Tab received, quiet combination

			sendScancode(time, (pressed ? RDP_KEYPRESS : RDP_KEYRELEASE)
					| KBD_FLAG_QUIET, 0x0f);
			if (!pressed) {
				sendScancode(time, RDP_KEYRELEASE | KBD_FLAG_QUIET, 0x38); // Release
																			// Alt
			}
			/*
			 * if (pressed)
			 * logger.debug("Alt + Tab pressed, ignoring, releasing tab");
			 */
			break;
		case KeyCodes.KEY_PAGEUP: // Alt + PgUp = Alt-Tab
			sendScancode(time, pressed ? RDP_KEYPRESS : RDP_KEYRELEASE, 0x0f); // TAB
			/*
			 * if (pressed) logger.debug("shortcut pressed: sent ALT+TAB");
			 */
			break;
		case KeyCodes.KEY_PAGEDOWN: // Alt + PgDown = Alt-Shift-Tab
			if (pressed) {
				sendScancode(time, RDP_KEYPRESS, 0x2a); // Shift
				sendScancode(time, RDP_KEYPRESS, 0x0f); // TAB
				// logger.debug("shortcut pressed: sent ALT+SHIFT+TAB");
			} else {
				sendScancode(time, RDP_KEYRELEASE, 0x0f); // TAB
				sendScancode(time, RDP_KEYRELEASE, 0x2a); // Shift
			}

			break;
		/*
		 * case KeyCodes.KEY_: // Alt + Insert = Alt + Esc sendScancode(time,
		 * pressed ? RDP_KEYPRESS : RDP_KEYRELEASE, 0x01); // ESC if (pressed)
		 * logger.debug("shortcut pressed: sent ALT+ESC"); break;
		 */
		case KeyCodes.KEY_HOME: // Alt + Home = Ctrl + Esc (Start)
			if (pressed) {
				sendScancode(time, RDP_KEYRELEASE, 0x38); // ALT
				sendScancode(time, RDP_KEYPRESS, 0x1d); // left Ctrl
				sendScancode(time, RDP_KEYPRESS, 0x01); // Esc
				// logger.debug("shortcut pressed: sent CTRL+ESC (Start)");

			} else {
				sendScancode(time, RDP_KEYRELEASE, 0x01); // escape
				sendScancode(time, RDP_KEYRELEASE, 0x1d); // left ctrl
				// sendScancode(time,RDP_KEYPRESS,0x38); // ALT
			}

			break;
		case KeyCodes.KEY_END: // Ctrl+Alt+End = Ctrl+Alt+Del
			if (ctrlDown) {
				sendScancode(time, pressed ? RDP_KEYPRESS : RDP_KEYRELEASE,
						0x53 | KeyCode.SCANCODE_EXTENDED); // DEL
				// if (pressed)
				// logger.debug("shortcut pressed: sent CTRL+ALT+DEL");
			}
			break;
		case KeyCodes.KEY_DELETE: // Alt + Delete = Menu
			if (pressed) {
				sendScancode(time, RDP_KEYRELEASE, 0x38); // ALT
				// need to do another press and release to shift focus from
				// to/from menu bar
				sendScancode(time, RDP_KEYPRESS, 0x38); // ALT
				sendScancode(time, RDP_KEYRELEASE, 0x38); // ALT
				sendScancode(time, RDP_KEYPRESS,
						0x5d | KeyCode.SCANCODE_EXTENDED); // Menu
				// logger.debug("shortcut pressed: sent MENU");
			} else {
				sendScancode(time, RDP_KEYRELEASE,
						0x5d | KeyCode.SCANCODE_EXTENDED); // Menu
				// sendScancode(time,RDP_KEYPRESS,0x38); // ALT
			}
			break;
		/*
		 * case KeyCodes.KEY_: // Ctrl + Alt + Minus (on NUM KEYPAD) = //
		 * Alt+PrtSc if (ctrlDown) { if (pressed) { sendScancode(time,
		 * RDP_KEYRELEASE, 0x1d); // Ctrl sendScancode(time, RDP_KEYPRESS, 0x37
		 * | KeyCode.SCANCODE_EXTENDED); // PrtSc //
		 * logger.debug("shortcut pressed: sent ALT+PRTSC"); } else {
		 * sendScancode(time, RDP_KEYRELEASE, 0x37 | KeyCode.SCANCODE_EXTENDED);
		 * // PrtSc sendScancode(time, RDP_KEYPRESS, 0x1d); // Ctrl } } break;
		 * case KeyEvent.VK_ADD: // Ctrl + ALt + Plus (on NUM KEYPAD) = PrtSc
		 * case KeyEvent.VK_EQUALS: // for laptops that can't do Ctrl-Alt+Plus
		 * if (ctrlDown) { if (pressed) { sendScancode(time, RDP_KEYRELEASE,
		 * 0x38); // Alt sendScancode(time, RDP_KEYRELEASE, 0x1d); // Ctrl
		 * sendScancode(time, RDP_KEYPRESS, 0x37 | KeyCode.SCANCODE_EXTENDED);
		 * // PrtSc // logger.debug("shortcut pressed: sent PRTSC"); } else {
		 * sendScancode(time, RDP_KEYRELEASE, 0x37 | KeyCode.SCANCODE_EXTENDED);
		 * // PrtSc sendScancode(time, RDP_KEYPRESS, 0x1d); // Ctrl
		 * sendScancode(time, RDP_KEYPRESS, 0x38); // Alt } } break;
		 */
		default:
			return false;
		}
		return true;
	}

	/**
	 * Deal with modifier keys as control, alt or caps lock
	 * 
	 * @param time
	 *            Time stamp for key event
	 * @param e
	 *            Key event to check for special keys
	 * @param pressed
	 *            True if key was pressed, false if released
	 * @return
	 */
	public boolean handleSpecialKeys(long time, KeyCodeEvent e, boolean pressed) {
		if (handleShortcutKeys(time, e, pressed))
			return true;

		switch (e.getNativeKeyCode()) {
		case KeyCodes.KEY_CTRL:
			ctrlDown = pressed;
			return false;
		case KeyCodes.KEY_ALT:
			altDown = pressed;
			return false;
			/*
			 * case KeyCodes.KEY_: if (pressed &&
			 * option.isCapsSendsUpAndDownEnabled()) capsLockOn = !capsLockOn;
			 * if (option.isCapsSendsUpAndDownNotEnabled()) { if (pressed)
			 * capsLockOn = true; else capsLockOn = false; } return false;
			 */
			/*
			 * case KeyCodes.KEY_: if (pressed) numLockOn = !numLockOn; return
			 * false; case KeyEvent.VK_SCROLL_LOCK: if (pressed) scrollLockOn =
			 * !scrollLockOn; return false; case KeyEvent.VK_PAUSE: // untested
			 * if (pressed) { // E1 1D 45 E1 9D C5 rdp.sendInput((int) time,
			 * RDP_INPUT_SCANCODE, RDP_KEYPRESS, 0xe1, 0); rdp.sendInput((int)
			 * time, RDP_INPUT_SCANCODE, RDP_KEYPRESS, 0x1d, 0);
			 * rdp.sendInput((int) time, RDP_INPUT_SCANCODE, RDP_KEYPRESS, 0x45,
			 * 0); rdp.sendInput((int) time, RDP_INPUT_SCANCODE, RDP_KEYPRESS,
			 * 0xe1, 0); rdp.sendInput((int) time, RDP_INPUT_SCANCODE,
			 * RDP_KEYPRESS, 0x9d, 0); rdp.sendInput((int) time,
			 * RDP_INPUT_SCANCODE, RDP_KEYPRESS, 0xc5, 0); } else { // release
			 * left ctrl rdp.sendInput((int) time, RDP_INPUT_SCANCODE,
			 * RDP_KEYRELEASE, 0x1d, 0); } break;
			 */

			// Removed, as java on MacOS send the option key as VK_META
			/*
			 * case KeyEvent.VK_META: // Windows key //logger.debug("Windows key
			 * received"); if(pressed){ sendScancode(time, RDP_KEYPRESS, 0x1d);
			 * // left ctrl sendScancode(time, RDP_KEYPRESS, 0x01); // escape }
			 * else{ sendScancode(time, RDP_KEYRELEASE, 0x01); // escape
			 * sendScancode(time, RDP_KEYRELEASE, 0x1d); // left ctrl } break;
			 */

			// haven't found a way to detect BREAK key in java - VK_BREAK
			// doesn't
			// exist
			/*
			 * case KeyEvent.VK_BREAK: if(pressed){
			 * sendScancode(time,RDP_KEYPRESS,(KeyCode.SCANCODE_EXTENDED |
			 * 0x46)); sendScancode(time,RDP_KEYPRESS,(KeyCode.SCANCODE_EXTENDED
			 * | 0xc6)); } // do nothing on release break;
			 */
		default:
			return false; // not handled - use sendScancode instead
		}
	//	return true; // handled - no need to use sendScancode
	}

	/**
	 * Turn off any locking key, check states if available
	 */
	public void triggerReadyToSend() {
		capsLockOn = false;
		numLockOn = false;
		scrollLockOn = false;
		doLockKeys(); // ensure lock key states are correct
	}

	protected void doLockKeys() {
	}

	/**
	 * Send CTRL-ALT-DEL combination.
	 */
	public void sendCtrlAltDel() {
		sendScancode(getTime(), RDP_KEYPRESS, 0x1d); // CTRL
		sendScancode(getTime(), RDP_KEYPRESS, 0x38); // ALT
		sendScancode(getTime(), RDP_KEYPRESS, 0x53 | KeyCode.SCANCODE_EXTENDED); // DEL

		sendScancode(getTime(), RDP_KEYRELEASE,
				0x53 | KeyCode.SCANCODE_EXTENDED); // DEL
		sendScancode(getTime(), RDP_KEYRELEASE, 0x38); // ALT
		sendScancode(getTime(), RDP_KEYRELEASE, 0x1d); // CTRL
	}

	/**
	 * Handle pressing of the middle mouse button, sending relevent event data
	 * to the server
	 * 
	 * @param e
	 *            MouseEvent detailing circumstances under which middle button
	 *            was pressed
	 */
	protected void middleButtonPressed(MouseEvent e) {

		rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_BUTTON3
				| MOUSE_FLAG_DOWN, e.getX(), e.getY());
	}

	/**
	 * Handle release of the middle mouse button, sending relevent event data to
	 * the server
	 * 
	 * @param e
	 *            MouseEvent detailing circumstances under which middle button
	 *            was released
	 */
	protected void middleButtonReleased(MouseEvent e) {
		/* if (!Options.paste_hack || !ctrlDown) */
		rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_BUTTON3, e.getX(),
				e.getY());
	}

}
