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
package io.github.interacto.fsm;

import java.util.Optional;
import java.util.Set;

/**
 * The base implementation of a FSM transition.
 * @param <E> The type of events the transition processes.
 * @param <E0> The type of events the FSM processes. Parent of E.
 */
public abstract class Transition<E extends E0, E0> {
	protected final OutputState<E0> src;
	protected final InputState<E0> tgt;

	/**
	 * Creates the transition.
	 * @param srcState The source state of the transition.
	 * @param tgtState The output state of the transition.
	 * @throws IllegalArgumentException If one of the states is null.
	 */
	protected Transition(final OutputState<E0> srcState, final InputState<E0> tgtState) {
		super();

		if(srcState == null || tgtState == null) {
			throw new IllegalArgumentException("States cannot be null");
		}

		src = srcState;
		tgt = tgtState;

		src.addTransition(this);
	}

	/**
	 * Executes the transition.
	 * @param event The event to process.
	 * @return The potential output state.
	 * @throws CancelFSMException If the execution cancels the FSM execution.
	 */
	public Optional<InputState<E0>> execute(final E0 event) throws CancelFSMException {
		final E typedEvent = accept(event);
		if(typedEvent != null && isGuardOK(typedEvent)) {
			src.getFSM().stopCurrentTimeout();
			action(typedEvent);
			src.exit();
			tgt.enter();
			return Optional.of(tgt);
		}

		return Optional.empty();
	}

	/**
	 * The action method of the transition.
	 * Should be overridden to define what to do when the transition
	 * is both accepted and its guard validated.
	 * @param event The event to process.
	 */
	protected void action(final E event) {
	}

	/**
	 * Checks whether the given event of type E0 is of type E.
	 * @param event The event to check.
	 * @return The same event but typed as E.
	 */
	protected abstract E accept(final E0 event);

	/**
	 * Checks whether the transition accepts the given event of type E.
	 * This is the guard of the transition. Differs from `accept`.
	 * @param event The event to check
	 * @return True whether the event is accepted.
	 */
	protected abstract boolean isGuardOK(final E event);

	/**
	 * @return The set of events accepted by the transition.
	 */
	public abstract Set<Object> getAcceptedEvents();

	/**
	 * Clean the transition when not used anymore.
	 */
	public void uninstall() {
	}
}
