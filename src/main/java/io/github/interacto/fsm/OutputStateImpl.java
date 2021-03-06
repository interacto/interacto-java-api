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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base implementation of the OutputState interface.
 * @param <E> The type of events the FSM processes.
 */
public abstract class OutputStateImpl<E> extends StateImpl<E> implements OutputState<E> {
	protected final List<Transition<? extends E, E>> transitions;

	/**
	 * Creates the state.
	 * @param stateMachine The FSM that will contain the state.
	 * @param stateName The name of this state.
	 */
	protected OutputStateImpl(final FSM<E> stateMachine, final String stateName) {
		super(stateMachine, stateName);
		transitions = new ArrayList<>();
	}


	@Override
	public List<Transition<? extends E, E>> getTransitions() {
		return Collections.unmodifiableList(transitions);
	}

	@Override
	public void addTransition(final Transition<? extends E, E> tr) {
		if(tr != null) {
			transitions.add(tr);
		}
	}

	@Override
	public void uninstall() {
		super.uninstall();
		transitions.forEach(tr -> tr.uninstall());
		transitions.clear();
	}
}
