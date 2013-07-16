package org.malai.jinput.interaction.library;

import net.java.games.input.Component;

import org.malai.interaction.TerminalState;
import org.malai.jinput.interaction.ButtonPressedTransition;
import org.malai.swing.interaction.SwingInteraction;

/**
 * A ButtonPressed interaction occurs when a button is pressed.<br>
 * <br>
 * This file is part of Malai.<br>
 * Copyright (c) 2009-2013 Arnaud BLOUIN<br>
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
public class ButtonPressed extends SwingInteraction {
	/** The pressed button. */
	protected Component.Identifier.Button button;

	/**
	 * Creates the interaction.
	 */
	public ButtonPressed() {
		super();
		initStateMachine();
	}


	@Override
	public void reinit() {
		super.reinit();

		button = null;
	}


	@SuppressWarnings("unused")
	@Override
	protected void initStateMachine() {
		final TerminalState pressed = new TerminalState("pressed"); //$NON-NLS-1$

		addState(pressed);

		new ButtonPressedTransition(initState, pressed) {
			@Override
			public void action() {
				super.action();

				ButtonPressed.this.button = this.button;
			}
		};
	}


	/**
	 * @return The pressed button.
	 * @since 0.1
	 */
	public Component.Identifier.Button getButton() {
		return button;
	}
}
