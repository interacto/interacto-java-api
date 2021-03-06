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
 * Defines a type of state that can receive input events.
 * @param <E> The type of events the FSM processes.
 */
public interface InputState<E> extends State<E> {
	/**
	 * Actions done while entering this state.
	 * @throws CancelFSMException If entering the state leads to a cancelling of the FSM execution.
	 */
	void enter() throws CancelFSMException;
}
