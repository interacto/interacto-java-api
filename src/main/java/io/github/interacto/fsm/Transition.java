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
 * @param <E> The type of events the FSM processes.
 */
public abstract class Transition<E> {
	protected final OutputState<E> src;
	protected final InputState<E> tgt;

	protected Transition(final OutputState<E> srcState, final InputState<E> tgtState) {
		super();

		if(srcState == null || tgtState == null) {
			throw new IllegalArgumentException("States cannot be null");
		}

		src = srcState;
		tgt = tgtState;

		src.addTransition(this);
	}

	public Optional<InputState<E>> execute(final E event) throws CancelFSMException {
		if(accept(event) && isGuardOK(event)) {
			src.getFSM().stopCurrentTimeout();
			action(event);
			src.exit();
			tgt.enter();
			return Optional.of(tgt);
		}

		return Optional.empty();
	}

	protected void action(final E event) {
	}

	protected abstract boolean accept(final E event);

	protected abstract boolean isGuardOK(final E event);

	public abstract Set<Object> getAcceptedEvents();

	/**
	 * Clean the transition when not used anymore.
	 */
	public void uninstall() {
	}
}
