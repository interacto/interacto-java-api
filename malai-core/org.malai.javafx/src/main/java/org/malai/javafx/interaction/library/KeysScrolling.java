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
package org.malai.javafx.interaction.library;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.input.KeyCode;
import org.malai.interaction.CancellingState;
import org.malai.interaction.IntermediaryState;
import org.malai.interaction.TerminalState;
import org.malai.javafx.interaction.KeyPressureTransition;
import org.malai.javafx.interaction.KeyReleaseTransition;


/**
 * A JFX interaction for scrolling with key shortcuts.
 */
public class KeysScrolling extends Scrolling {
	/** The keys pressed while scrolling. */
	protected final List<KeyCode> keys = new ArrayList<>();

	/** The id of the hid used for the keyboard events. */
	protected int keyHIDUsed;

	/**
	 * Creates the interaction.
	 */
	public KeysScrolling() {
		super();
	}


	@Override
	public void reinit() {
		super.reinit();
		keyHIDUsed = -1;
		keys.clear();
	}

	@Override
	protected void initStateMachine() {
		final IntermediaryState keyPressed = new IntermediaryState("keyPressed");
		final CancellingState keyReleased = new CancellingState("keyReleased");
		final TerminalState scrolled = new TerminalState("scrolled");

		addState(keyPressed);
		addState(keyReleased);
		addState(scrolled);

		new ScrollingScrollTransition(initState, scrolled);

		new KeyPressureTransition(initState, keyPressed) {
			@Override
			public void action() {
				KeysScrolling.this.keys.add(event.getCode());
				KeysScrolling.this.setKeyHIDUsed(hid);
			}
		};

		new KeyPressureTransition(keyPressed, keyPressed) {
			@Override
			public void action() {
				KeysScrolling.this.keys.add(event.getCode());
			}

			@Override
			public boolean isGuardRespected() {
				return this.hid == KeysScrolling.this.getKeyHIDUsed();
			}
		};

		new KeyReleaseTransition(keyPressed, keyReleased) {
			@Override
			public boolean isGuardRespected() {
				return KeysScrolling.this.keys.size() == 1 && this.hid == KeysScrolling.this.getKeyHIDUsed() && KeysScrolling.this.keys.contains(event.getCode());
			}
		};

		new KeyReleaseTransition(keyPressed, keyPressed) {
			@Override
			public boolean isGuardRespected() {
				return KeysScrolling.this.keys.size() > 1 && this.hid == KeysScrolling.this.getKeyHIDUsed() && KeysScrolling.this.keys.contains(event.getCode());
			}

			@Override
			public void action() {
				KeysScrolling.this.keys.remove(event.getCode());
			}
		};

		new ScrollingScrollTransition(keyPressed, scrolled);
	}


	/**
	 * @return The keys pressed while scrolling.
	 */
	public List<KeyCode> getKeys() {
		return keys;
	}

	/**
	 * @return the The id of the hid used for the keyboard events.
	 */
	public int getKeyHIDUsed() {
		return keyHIDUsed;
	}

	/**
	 * @param keyHIDUsed The id of the hid used for the keyboard events.
	 */
	protected void setKeyHIDUsed(final int keyHIDUsed) {
		this.keyHIDUsed = keyHIDUsed;
	}
}
