package org.malai.javafx.interaction;

import javafx.scene.input.MouseEvent;

import org.malai.interaction.TransitionImpl;
import org.malai.stateMachine.SourceableState;
import org.malai.stateMachine.TargetableState;

/**
 * This abstract transition defines a model for transitions based on pointing device events.<br>
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
 * 2014-09-23<br>
 * @author Arnaud BLOUIN
 */
public abstract class PointingDeviceTransition<T extends MouseEvent> extends TransitionImpl {
	protected T event;

	/**
	 * Defines a transition.
	 * @param inputState The source state of the transition.
	 * @param outputState The target state of the transition.
	 * @throws IllegalArgumentException If one of the given parameters is null or not valid.
	 */
	public PointingDeviceTransition(final SourceableState inputState, final TargetableState outputState) {
		super(inputState, outputState);
	}

	/**
	 * @return The events at the origin of the transition. Cannot be null.
	 */
	public T getEvent() {
		return event;
	}
	
	/**
	 * Sets the event of the transition.
	 * @param evt The new transition. Cannot be null.
	 */
	public void setEvent(final T evt) {
		if(evt!=null)
			event = evt;
	}
}
