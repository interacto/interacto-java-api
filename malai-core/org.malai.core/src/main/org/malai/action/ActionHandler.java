package org.malai.action;

import org.malai.undo.UndoHandler;

/**
 * This interface allows to create a bridge between an action and an
 * object that want to be aware about events on actions (such as creation or
 * deletion of an action).<br>
 * This file is part of Malai.<br>
 * Copyright (c) 2005-2015 Arnaud BLOUIN<br>
 * <br>
 * Malai is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * <br>
 * Malai is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br>
 * <br>
 * @author Arnaud Blouin
 * @since 0.1
 */
public interface ActionHandler extends UndoHandler {
	/**
	 * Notifies the handler when the given action is cancelled.
	 * @param IAction The cancelled action.
	 * @since 0.2
	 */
	void onActionCancelled(final Action IAction);

	/**
	 * Notifies the handler when the given action is added to the registry.
	 * @param IAction The added action.
	 * @since 0.2
	 */
	void onActionAdded(final Action IAction);

	/**
	 * Notifies the handler when the given action is aborted.
	 * @param IAction The aborted action.
	 * @since 0.2
	 */
	void onActionAborted(final Action IAction);

	/**
	 * Notifies the handler when the given action is executed.
	 * @param IAction The executed action.
	 * @since 0.2
	 */
	void onActionExecuted(final Action IAction);

	/**
	 * Notifies the handler when the given action is done.
	 * @param IAction The action that ends.
	 * @since 0.2
	 */
	void onActionDone(final Action IAction);
}
