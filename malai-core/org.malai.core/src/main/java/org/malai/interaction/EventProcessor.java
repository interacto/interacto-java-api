/*
 * This file is part of Malai.
 * Copyright (c) 2005-2017 Arnaud BLOUIN
 * Malai is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * Malai is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */
package org.malai.interaction;

/**
 * This interface can be used for object that want to gather events (mouse pressed, etc.) produced by HIDs.
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
}
