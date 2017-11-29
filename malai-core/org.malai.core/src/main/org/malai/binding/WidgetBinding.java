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
package org.malai.binding;

import org.malai.action.Action;
import org.malai.instrument.Instrument;
import org.malai.interaction.Interaction;
import org.malai.interaction.InteractionHandler;

/**
 * The concept of widget binding and its related services.
 * @author Arnaud BLOUIN
 */
public interface WidgetBinding extends InteractionHandler {
	/**
	 * Stops the interaction and clears all its events waiting for a process.
	 */
	void clearEvents();

	/**
	 * After being created by method createAction, the action must be initialised by this method.
	 */
	void initAction();

	/**
	 * Updates the current action. To override.
	 */
	void updateAction();

	/**
	 * @return True if the condition of the widget binding is respected.
	 */
	boolean isConditionRespected();

	/**
	 * @return The interaction.
	 */
	Interaction getInteraction();

	/**
	 * @return The action in progress or null.
	 */
	Action getAction();

	/**
	 * @return True if the widget binding is activated.
	 */
	boolean isActivated();

	/**
	 * @return True: if the widget binding is currently used.
	 */
	boolean isRunning();

	/**
	 * Sometimes the interaction of two different widget bindings can overlap. In this case, the first interaction can
	 * stops while the second is blocked in an intermediary state.
	 * Two solutions are possible to avoid such a problem:<br>
	 * - the use of this function that performs some tests. If the test fails, the starting interaction
	 * is aborted and the resulting action is never created;<br>
	 * - the modification of one of the interactions to avoid the overlapping.
	 * @return True: if the starting interaction must be aborted so that the action is never created.
	 */
	boolean isInteractionMustBeAborted();

	/**
	 * @return True if the action is executed on each evolution of the interaction.
	 */
	boolean isExecute();

	/**
	 * Defines the interim feedback of the widget binding. If overridden, the interim
	 * feedback of its instrument should be define too.
	 */
	void interimFeedback();

	/**
	 * Activates the widget binding.
	 * @param activ True: the widget binding is activated. Otherwise, it is desactivated.
	 */
	void setActivated(final boolean activ);

	/**
	 * @return The instrument that contains the widget binding.
	 */
	Instrument<?> getInstrument();
}
