/*
 * Interacto
 * Copyright (C) 2019 Arnaud Blouin
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.interacto.command;

import io.interacto.undo.UndoHandler;

/**
 * This interface allows to create a bridge between a command and an object that want to be aware about events on commands
 * (such as creation or deletion of a command).
 * @author Arnaud Blouin
 */
public interface CmdHandler extends UndoHandler {
	/**
	 * Notifies the handler when the given command is added to the registry.
	 * @param cmd The added command.
	 */
	default void onCmdAdded(final Command cmd) {
	}

	/**
	 * Notifies the handler when the given command is cancelled.
	 * @param cmd The cancelled command.
	 */
	default void onCmdCancelled(final Command cmd) {
	}

	/**
	 * Notifies the handler when the given command is executed.
	 * @param cmd The executed command.
	 */
	default void onCmdExecuted(final Command cmd) {
	}

	/**
	 * Notifies the handler when the given command is done.
	 * @param cmd The command that ends.
	 */
	default void onCmdDone(final Command cmd) {
	}
}
