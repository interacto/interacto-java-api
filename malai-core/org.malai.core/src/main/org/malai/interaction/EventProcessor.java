package org.malai.interaction;

/**
 * This interface can be used for object that want to gather events (mouse pressed, etc.) produced by HIDs.<br>
 * <br>
 * This file is part of Malai.<br>
 * Copyright (c) 2005-2014 Arnaud BLOUIN<br>
 * <br>
 * Malai is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * <br>
 * Malai is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br>
 * <br>
 * 05/19/2010<br>
 * @author Arnaud BLOUIN
 * @since 0.1
 */
public interface EventProcessor {
	/**
	 * Defines action to do when a timeout is elapsed.
	 * @param timeoutTransition The transition which produced the timeout event.
	 * @since 0.2
	 */
	void onTimeout(final TimeoutTransition timeoutTransition);

	/**
	 * Defines action to do when the button of a mouse or something equivalent is pressed.
	 * @param button The identifier of the pressed button.
	 * @param x The X-coordinate of the pressure location.
	 * @param y The Y-coordinate of the pressure location.
	 * @param idHID The identifier of the used HID.
	 * @param source The object that produces the event.
	 * @since 0.1
	 */
	void onPressure(final int button, final int x, final int y, final int idHID, final Object source);

	/**
	 * Defines action to do when the button of a mouse or something equivalent is released.
	 * @param button The identifier of the released button.
	 * @param x The X-coordinate of the release location.
	 * @param y The Y-coordinate of the release location.
	 * @param idHID The identifier of the used HID.
	 * @param source The object that produces the event.
	 * @since 0.1
	 */
	void onRelease(final int button, final int x, final int y, final int idHID, final Object source);

	/**
	 * Defines action to do when a mouse or something equivalent is moved.
	 * @param button The identifier of the pressed button, if a button is pressed (else -1).
	 * @param x The X-coordinate of the pressure location.
	 * @param y The Y-coordinate of the pressure location.
	 * @param pressed True: a button of the mouse is pressed.
	 * @param idHID The identifier of the used HID.
	 * @param source The object that produces the event.
	 * @since 0.1
	 */
	void onMove(final int button, final int x, final int y, final boolean pressed, final int idHID, final Object source);

	/**
	 * Defines actions to do when a scrolling device (e.g. a mouse wheel) is used.
	 * @param posX The X-coordinate of the position where the event occurred.
	 * @param posY The Y-coordinate of the position where the event occurred.
	 * @param direction Defines if the scrolling is up (positive value) or down (negative value).
	 * @param amount The number of units to scroll by scroll.
	 * @param type The type of scrolling that should take place in response to this event (block or unit increment).
	 * @param idHID The identifier of the used HID.
	 * @param src The object that threw the event.
	 * @since 0.2
	 */
	void onScroll(final int posX, final int posY, final int direction, final int amount,
				  final int type, final int idHID, final Object src);
}
