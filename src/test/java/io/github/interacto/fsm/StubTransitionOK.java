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

import java.util.Set;
import org.mockito.internal.util.collections.Sets;

public class StubTransitionOK extends Transition<StubEvent> {
	public boolean guard;

	protected StubTransitionOK(final OutputState<StubEvent> srcState, final InputState<StubEvent> tgtState) {
		this(srcState, tgtState, true);
	}

	protected StubTransitionOK(final OutputState<StubEvent> srcState, final InputState<StubEvent> tgtState, final boolean guard) {
		super(srcState, tgtState);
		this.guard = guard;
	}

	@Override
	protected boolean accept(final StubEvent event) {
		return true;
	}

	@Override
	protected boolean isGuardOK(final StubEvent event) {
		return guard;
	}

	@Override
	public Set<Object> getAcceptedEvents() {
		return Sets.newSet(StubEvent.class);
	}
}

class SubStubTransition1 extends StubTransitionOK {
	protected SubStubTransition1(final OutputState<StubEvent> srcState, final InputState<StubEvent> tgtState, final boolean guard) {
		super(srcState, tgtState, guard);
	}

	@Override
	public boolean accept(final StubEvent event) {
		return event instanceof StubSubEvent1;
	}

	@Override
	public Set<Object> getAcceptedEvents() {
		return Sets.newSet(StubSubEvent1.class);
	}
}

class SubStubTransition2 extends StubTransitionOK {
	protected SubStubTransition2(final OutputState<StubEvent> srcState, final InputState<StubEvent> tgtState, final boolean guard) {
		super(srcState, tgtState, guard);
	}

	@Override
	public boolean accept(final StubEvent event) {
		return event instanceof StubSubEvent2;
	}

	@Override
	public Set<Object> getAcceptedEvents() {
		return Sets.newSet(StubSubEvent2.class);
	}
}

class SubStubTransition3 extends StubTransitionOK {
	protected SubStubTransition3(final OutputState<StubEvent> srcState, final InputState<StubEvent> tgtState, final boolean guard) {
		super(srcState, tgtState, guard);
	}

	@Override
	public boolean accept(final StubEvent event) {
		return event instanceof StubSubEvent3;
	}

	@Override
	public Set<Object> getAcceptedEvents() {
		return Sets.newSet(StubSubEvent3.class);
	}
}
