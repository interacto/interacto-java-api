/*
 * Interacto
 * Copyright (C) 2020 Arnaud Blouin
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
package io.github.interacto.command;

/**
 * A command is produced and executed in reaction of a user interaction.
 * It follows the command design pattern.
 * It contains statements to execute to perform the command.
 * The interface Undoable can be used to add undo/redo features to a command.
 * @author Arnaud Blouin
 */
public interface Command {
	/**
	 * Execute if possible (canDo) the given command (if not null) and flush it.
	 * @param cmd The command to execute. Nothing done if null.
	 */
	static void executeAndFlush(final Command cmd) {
		if(cmd == null) {
			return;
		}
		if(cmd.canDo()) {
			cmd.doIt();
		}
		cmd.flush();
	}

	/**
	 * Flushes the command.
	 * Can be useful to close streams, free objects, etc.
	 * A command should flushed manually only when it is not managed by the cmd registry of the application.
	 * When a command is gathered and managed by a command registry, it is automatically flushed when the
	 * command registry removes the cmd.
	 */
	void flush();

	/**
	 * Specifies whether the command must be saved in the cmd register. For instance,
	 * some commands, such as a scroll, should not be saved or put in the undo/redo manager. Such commands should not be registrable.
	 * @return The registration policy.
	 */
	RegistrationPolicy getRegistrationPolicy();

	/**
	 * This method manages the execution of the command.
	 * @return True: the execution of the command is OK.
	 */
	boolean doIt();

	/**
	 * Checks whether the command can be executed.
	 * @return True if the command can be executed.
	 * @since 0.1
	 */
	boolean canDo();

	/**
	 * State whether the execution of this command has effects on the system.
	 * @return True: the command has effects on the system.
	 */
	boolean hadEffect();


	/**
	 * Checks whether the current command can be cancelled by the given one.
	 * @param cmd The command to check whether it can cancel the current cmd.
	 * @return True: The given command can cancel the current cmd.
	 */
	boolean unregisteredBy(final Command cmd);

	/**
	 * Marks the command as "done" and sends it to the cmd registry.
	 * @since 0.1
	 */
	void done();

	/**
	 * To know whether the command has been marked as 'done'.
	 * @return True: the command has been marked as 'done'.
	 */
	boolean isDone();

	/**
	 * Marks the command has aborted.
	 */
	void cancel();

	/**
	 * Provides the status of the command.
	 * @return The status of the command.
	 * @since 0.2
	 */
	CmdStatus getStatus();

	/**
	 * Defines the registration policy of the command.
	 */
	enum RegistrationPolicy {
		/** The command is never registered. */
		NONE,
		/** The command is registered in the cmd register. The cmd is not flushed when the registry wants to free some commands. */
		UNLIMITED,
		/** The command is registered in the cmd register. The cmd can be flushed by the registry. */
		LIMITED
	}

	/**
	 * Defines the different states of the command.
	 * @since 0.2
	 */
	enum CmdStatus {
		/** When the command is created but not executed yet. */
		CREATED,
		/** When the command has been created and executed one time. */
		EXECUTED,
		/** When the command has been cancelled. */
		CANCELLED,
		/** When the command has been marked as done. */
		DONE,
		/** The command has been flushed. In this case, the cmd must not be used anymore. */
		FLUSHED
	}
}
