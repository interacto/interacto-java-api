package org.malai.interaction.library;

import org.malai.interaction.InteractionImpl;
import org.malai.interaction.KeyPressureTransition;
import org.malai.stateMachine.SourceableState;
import org.malai.stateMachine.TargetableState;

/**
 * This abstract interaction should be used to define interaction based on keyboards.<br>
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
 * 12/01/2011<br>
 * @author Arnaud BLOUIN
 * @since 0.2
 */
public abstract class KeyInteraction extends InteractionImpl {
	/** The key pressed. */
	protected int key;

	/** The object that produced the interaction. */
	protected Object object;

	/** The char corresponding to the key. */
	protected char keyChar;


	/**
	 * Creates the interaction.
	 * @since 0.2
	 */
	public KeyInteraction() {
		super();
	}


	@Override
	public void reinit() {
		super.reinit();
		key 	= -1;
		object 	= null;
		keyChar = 0;
	}


	/**
	 * @return The object that produced the interaction.
	 * @since 0.2
	 */
	public Object getObject() {
		return object;
	}


	/**
	 * @return The key pressed.
	 * @since 0.2
	 */
	public int getKey() {
		return key;
	}


	/**
	 * @return The char corresponding to the key.
	 */
	public char getKeyChar() {
		return keyChar;
	}


	/**
	 * @param key The key pressed.
	 * @since 0.2
	 */
	protected void setKey(final int key) {
		this.key = key;
	}


	/**
	 * @param object The object that produced the interaction.
	 * @since 0.2
	 */
	protected void setObject(final Object object) {
		this.object = object;
	}


	/**
	 * @param keyChar The char corresponding to the key.
	 * @since 0.2
	 */
	protected void setKeyChar(final char keyChar) {
		this.keyChar = keyChar;
	}



	/**
	 * Defines a transition modifying the key attribute of the interaction.
	 */
	public class KeyInteractionKeyPressedTransition extends KeyPressureTransition {
		/**
		 * Creates the transition.
		 * @param inputState The source state of the transition.
		 * @param outputState The target state of the transition.
		 */
		public KeyInteractionKeyPressedTransition(final SourceableState inputState, final TargetableState outputState) {
			super(inputState, outputState);
		}

		@Override
		public void action() {
			KeyInteraction.this.object 	= this.source;
			KeyInteraction.this.key 	= this.key;
			KeyInteraction.this.keyChar	= this.keyChar;
			KeyInteraction.this.setLastHIDUsed(this.hid);
		}
	}
}
