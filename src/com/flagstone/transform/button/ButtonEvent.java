package com.flagstone.transform.button;

import java.util.LinkedHashMap;
import java.util.Map;

//TODO(doc) Review
public enum ButtonEvent {
	/**
	 * Code for the button event that occurs when the mouse cursor moves over the
	 * active area of a button.
	 */
	ROLL_OVER(1),

	/**
	 * Code for the button event that occurs when the mouse cursor moves out of
	 * the active area of a button.
	 */
	ROLL_OUT(2),

	/**
	 * Code for the button event that occurs when the mouse button is clicked
	 * while the mouse cursor is over the active area of the button.
	 */
	PRESS(4),

	/**
	 * Code for the button event that occurs when the mouse button is clicked and
	 * released while the mouse cursor is over the active area of the button.
	 */
	RELEASE(8),

	/**
	 * Code for the button event that occurs when the mouse button is clicked and
	 * the mouse cursor is dragged out of the active area of the button.
	 */
	DRAG_OUT(16),

	/**
	 * Code for the button event that occurs when the mouse button is clicked and
	 * the mouse cursor is dragged into the active area of the button.
	 */
	DRAG_OVER(32),

	/**
	 * Code for the button event that occurs when the mouse button is clicked,
	 * the mouse cursor is dragged into the active area of the button and the
	 * mouse button is released.
	 */
	RELEASE_OUT(64),

	/**
	 * Code for the button event that occurs when the mouse button is clicked and
	 * the mouse cursor is dragged into the active area of the menu item.
	 */
	MENU_DRAG_OVER(128),

	/**
	 * Code for the button event that occurs when the mouse button is clicked and
	 * the mouse cursor is dragged out of the active area of the menu item.
	 */
	MENU_DRAG_OUT(256),

	/**
	 * Code for the button event that occurs when the left arrow key is pressed
	 * on the keyboard.
	 */
	LEFT(512),

	/**
	 * Code for the button event that occurs when the right arrow key is pressed
	 * on the keyboard.
	 */
	RIGHT(1024),

	/**
	 * Code for the button event that occurs when the home key is pressed on the
	 * keyboard.
	 */
	HOME(1536),

	/**
	 * Code for the button event that occurs when the end key is pressed on the
	 * keyboard.
	 */
	END(2048),

	/**
	 * Code for the button event that occurs when the insert key is pressed on
	 * the keyboard.
	 */
	INSERT(2560),

	/**
	 * Code for the button event that occurs when the delete key is pressed on
	 * the keyboard.
	 */
	DELETE(3072),

	/**
	 * Code for the button event that occurs when the backspace key is pressed on
	 * the keyboard.
	 */
	BACKSPACE(4096),

	/**
	 * Code for the button event that occurs when the enter key is pressed on the
	 * keyboard.
	 */
	ENTER(6656),

	/**
	 * Code for the button event that occurs when the up arrow key is pressed on
	 * the keyboard.
	 */
	UP(7168),

	/**
	 * Code for the button event that occurs when the down arrow key is pressed
	 * on the keyboard.
	 */
	DOWN(7680),

	/**
	 * Code for the button event that occurs when the page up key is pressed on
	 * the keyboard.
	 */
	PAGE_UP(8192),

	/**
	 * Code for the button event that occurs when the page down key is pressed on
	 * the keyboard.
	 */
	PAGE_DOWN(8704),

	/**
	 * Code for the button event that occurs when the tab key is pressed on the
	 * keyboard.
	 */
	TAB(9216),

	/**
	 * Code for the button event that occurs when the escape key is pressed on
	 * the keyboard.
	 */
	ESCAPE(9728),

	/**
	 * Code for the button event that occurs when the space bar is pressed on the
	 * keyboard.
	 */
	SPACE(16384);

	private static final Map<Integer,ButtonEvent>table 
		= new LinkedHashMap<Integer,ButtonEvent>();

	static {
		for (ButtonEvent type : values()) {
			table.put(type.value, type);
		}
	}
	
	public static ButtonEvent fromInt(int type) {
		return table.get(type);
	}

	private final int value;

	private ButtonEvent(int value) {
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
}

