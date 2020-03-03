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
 * A state of an FSM.
 * A cancelling state cancels the FSM of a user interaction.
 * @param <E> The type of events the FSM processes.
 */
public class CancellingState<E> extends StateImpl<E> implements InputState<E> {
	public CancellingState(final FSM<E> stateMachine, final String stateName) {
		super(stateMachine, stateName);
	}

	@Override
	public void enter() {
		fsm.onCancelling();
	}
}
