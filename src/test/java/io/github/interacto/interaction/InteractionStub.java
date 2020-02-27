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
package io.github.interacto.interaction;

import io.github.interacto.fsm.FSM;
import io.github.interacto.fsm.OutputState;

public class InteractionStub extends InteractionImpl<InteractionData, Object, FSM<Object>> {
	public InteractionStub() {
		super(new FSM<>());
	}

	public InteractionStub(final FSM<Object> fsm) {
		super(fsm);
	}

	@Override
	public InteractionData getData() {
		return null;
	}

	@Override
	protected void updateEventsRegistered(final OutputState<Object> newState, final OutputState<Object> oldState) {
	}

	@Override
	protected void consumeEvent(final Object event) {
	}

	@Override
	protected boolean isEventsOfSameType(final Object evt1, final Object evt2) {
		return evt1.getClass() == evt2.getClass();
	}

	@Override
	protected void runInUIThread(final Runnable cmd) {
		cmd.run();
	}

	@Override
	protected void reinitData() {
	}
}
