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

public interface FSMHandler {
	/**
	 * When the FSM starts.
	 * @throws CancelFSMException If the FSM must be cancelled.
	 */
	void fsmStarts() throws CancelFSMException;

	/**
	 * When the FSM runs to new state.
	 * @throws CancelFSMException If the FSM must be cancelled.
	 */
	void fsmUpdates() throws CancelFSMException;

	/**
	 * When the FSM enters a terminal state.
	 * @throws CancelFSMException If the FSM must be cancelled.
	 */
	void fsmStops() throws CancelFSMException;

	/**
	 * When the interaction enters a cancelling state.
	 */
	void fsmCancels();
}
