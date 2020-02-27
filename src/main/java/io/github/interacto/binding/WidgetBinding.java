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
package io.github.interacto.binding;

import io.github.interacto.command.Command;
import io.github.interacto.fsm.FSMHandler;
import io.github.interacto.interaction.InteractionImpl;
import io.reactivex.Observable;

/**
 * The concept of widget binding and its related services.
 * @author Arnaud BLOUIN
 */
public interface WidgetBinding<C extends Command> extends FSMHandler {
	/**
	 * Stops the interaction and clears all its events waiting for a process.
	 */
	void clearEvents();

	/**
	 * After being created by createCommand, the command can be initialised by this method.
	 */
	void first();

	/**
	 * Updates the current command. To override.
	 */
	void then();

	/**
	 * On end
	 */
	void end();

	/**
	 * On cancellation
	 */
	void cancel();

	/**
	 * On end or cancellation
	 */
	void endOrCancel();

	/**
	 * Called when an executed command did not had effect
	 */
	void ifCmdHadNoEffect();

	/**
	 * Called when an executed command had effects
	 */
	void ifCmdHadEffects();

	/**
	 * Called when an ongoing command cannot be executed
	 */
	void ifCannotExecuteCmd();


	/**
	 * @return True if the condition of the widget binding is respected.
	 */
	boolean when();

	/**
	 * @return The interaction.
	 */
	InteractionImpl<?, ?, ?> getInteraction();

	/**
	 * @return The command in progress or null.
	 */
	C getCommand();

	/**
	 * @return True if the widget binding is activated.
	 */
	boolean isActivated();

	/**
	 * Activates the widget binding.
	 * @param activated True: the widget binding is activated. Otherwise, it is desactivated.
	 */
	void setActivated(final boolean activated);

	/**
	 * @return True: if the widget binding is currently used.
	 */
	boolean isRunning();

	/**
	 * States whether the interaction must continue to run while the condition of the binding is not fulfilled at the interaction start.
	 */
	boolean isStrictStart();

	/**
	 * @return True: the command must be executed on each step of the interaction.
	 */
	boolean isContinuousCmdExec();

	/** Uninstall the binding. The binding cannot be used after that. */
	void uninstallBinding();

	/** An RX observable objects that will provide the commands produced by the binding. */
	Observable<C> produces();

	/**
	 * Information method.
	 * @return The number of times the widget binding successfully ended (nevermind a command was created or not).
	 */
	long getTimesEnded();

	/**
	 * Information method.
	 * @return The number of times the widget binding was cancelled (nevermind a command was created or not).
	 */
	long getTimesCancelled();
}
