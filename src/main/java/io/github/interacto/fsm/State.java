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

/**
 * The base type of an FSM state.
 * @param <E> The type of events the FSM processes.
 */
public interface State<E> {
	String getName();

	FSM<E> getFSM();

	/**
	 * Checks whether the starting state of the fsm is this state.
	 * In this case, the fsm is notified about the starting of the FSM.
	 * @throws CancelFSMException @throws CancelFSMException If the interaction is cancelled by a handler during the starting step.
	 */
	default void checkStartingState() throws CancelFSMException {
		// Triggers the start event only if the starting state is this initial state
		if(!getFSM().isStarted() && getFSM().startingState == this) {
			getFSM().onStarting();
		}
	}

	default void uninstall() {
	}
}
