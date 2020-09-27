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

import java.util.List;

/**
 * Defines a type of state that can produce as output events.
 * @param <E> The type of events the FSM processes.
 */
public interface OutputState<E> extends State<E> {
	/**
	 * Actions done when a transition of the state is executed so
	 * that this state is left.
	 * @throws CancelFSMException If leaving the state leads to a cancelling of the FSM execution.
	 */
	void exit() throws CancelFSMException;

	/**
	 * Asks to the state to process of the given event.
	 * @param event The event to process. Can be null.
	 * @return True: a transition is found and executed. False otherwise.
	 */
	default boolean process(final E event) {
		for(final Transition<? extends E, E> tr : getTransitions()) {
			try {
				if(tr.execute(event).isPresent()) {
					return true;
				}
			}catch(final CancelFSMException ignored) {
				// Already processed
			}
		}
		return false;
	}

	/**
	 * @return The list of outgoing transitions of the state.
	 */
	List<Transition<? extends E, E>> getTransitions();

	/**
	 * Adds the given transitions to the list of outgoing transitions of the state.
	 * @param tr The transition to add.
	 */
	void addTransition(final Transition<? extends E, E> tr);
}
