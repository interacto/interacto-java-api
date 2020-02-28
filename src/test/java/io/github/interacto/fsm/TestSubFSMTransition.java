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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestSubFSMTransition {
	SubFSMTransition<StubEvent> tr;
	FSM<StubEvent> fsm;
	FSM<StubEvent> mainfsm;
	StdState<StubEvent> s1;
	StdState<StubEvent> s2;
	TerminalState<StubEvent> subS;

	@BeforeEach
	void setUp() {
		fsm = new FSM<>();
		mainfsm = new FSM<>();
		s1 = Mockito.mock(StdState.class);
		s2 = Mockito.mock(StdState.class);
		Mockito.when(s1.getFSM()).thenReturn(mainfsm);
		Mockito.when(s2.getFSM()).thenReturn(mainfsm);
		mainfsm.addState(s1);
		mainfsm.addState(s2);
		tr = new SubFSMTransition<>(s1, s2, fsm);

		subS = new TerminalState<>(fsm, "sub1");
		new SubStubTransition1(fsm.initState, subS, true);
		fsm.addState(subS);
	}

	@Test
	void testIAEConstructor() {
		assertThrows(IllegalArgumentException.class, () -> new SubFSMTransition<>(s1, s2, null));
	}

	@Test
	void testInner() {
		assertTrue(fsm.inner);
		assertFalse(mainfsm.inner);
	}

	@Test
	void testAcceptFirstEvent() {
		assertTrue(tr.accept(new StubSubEvent1()));
	}

	@Test
	void testNotAcceptFirstEvent() {
		assertFalse(tr.accept(new StubSubEvent2()));
	}

	@Test
	void testGuardOKFirstEvent() {
		assertTrue(tr.isGuardOK(new StubSubEvent1()));
	}

	@Test
	void testGuardKOFirstEvent() {
		assertFalse(tr.isGuardOK(new StubSubEvent2()));
	}

	@Test
	void testExecuteFirstEventReturnsSubState() {
		final Optional<InputState<StubEvent>> state = tr.execute(new StubSubEvent1());
		assertTrue(state.isPresent());
		assertEquals(subS, state.get());
	}

	@Test
	void testExecuteNoTransition() {
		final Optional<InputState<StubEvent>> state = tr.execute(new StubSubEvent2());
		assertFalse(state.isPresent());
	}

	@Test
	void testExecuteFirstEventKO() {
		final Optional<InputState<StubEvent>> state = tr.execute(new StubSubEvent2());
		assertFalse(state.isPresent());
	}

	@Test
	void testExecuteExitSrcState() {
		tr.execute(new StubSubEvent1());
		Mockito.verify(s1, Mockito.times(1)).exit();
	}

	@Test
	void testExecuteEnterTgtState() throws CancelFSMException {
		tr.execute(new StubSubEvent1());
		Mockito.verify(s2, Mockito.times(1)).enter();
	}

	@Test
	void testUninstall() {
		fsm = Mockito.mock(FSM.class);
		tr = new SubFSMTransition<>(s1, s2, fsm);
		tr.uninstall();
		Mockito.verify(fsm, Mockito.times(1)).uninstall();
	}

	@Test
	void testGetAcceptedEvents() {
		final var tr2 = Mockito.mock(Transition.class);
		fsm.initState.addTransition(tr2);
		Mockito.when(tr2.getAcceptedEvents()).thenReturn(Set.of("foo", "bar"));
		final Set<Object> evts = tr.getAcceptedEvents();
		assertEquals(3, evts.size());
		assertTrue(evts.contains("foo"));
		assertTrue(evts.contains("bar"));
		assertTrue(evts.contains(fsm.initState.getTransitions().get(0).getAcceptedEvents().iterator().next()));
	}

	@Nested
	class DoubleClickFSMExample {
		DoubleClickFSM dbleFSM;

		@BeforeEach
		void setUp() {
			dbleFSM = new DoubleClickFSM();
		}

		@Test
		void testPress() {
			dbleFSM.process("press");
			assertEquals("pressed", dbleFSM.getCurrentState().getName());
		}

		@Test
		void testClick() {
			final var dbleFSM = new DoubleClickFSM();
			dbleFSM.process("press");
			dbleFSM.process("release");
			assertEquals("clicked", dbleFSM.getCurrentState().getName());
		}

		@Test
		void testClickPress() {
			final var dbleFSM = new DoubleClickFSM();
			dbleFSM.process("press");
			dbleFSM.process("release");
			dbleFSM.process("press");
			assertEquals("pressed", dbleFSM.getCurrentState().getName());
		}

		@Test
		void testClickClick() throws CancelFSMException {
			final FSMHandler handler = Mockito.mock(FSMHandler.class);
			final var dbleFSM = new DoubleClickFSM();
			dbleFSM.firstClickFSM.addHandler(handler);
			dbleFSM.sndClick.addHandler(handler);
			dbleFSM.process("press");
			dbleFSM.process("release");
			dbleFSM.process("press");
			dbleFSM.process("release");
			assertEquals("init", dbleFSM.getCurrentState().getName());
			Mockito.verify(handler, Mockito.times(2)).fsmStops();
		}

		@Test
		void testPressMove() {
			final FSMHandler handler = Mockito.mock(FSMHandler.class);
			final var dbleFSM = new DoubleClickFSM();
			dbleFSM.firstClickFSM.addHandler(handler);
			dbleFSM.process("press");
			dbleFSM.process("move");
			assertEquals("init", dbleFSM.getCurrentState().getName());
			Mockito.verify(handler, Mockito.times(1)).fsmCancels();
		}
	}
}
