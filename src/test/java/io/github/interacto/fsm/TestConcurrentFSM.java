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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestConcurrentFSM {
	ConcurrentFSM<String, StubTouchFSM> fsm;
	StubTouchFSM fsm1;
	StubTouchFSM fsm2;
	FSMHandler handler;
	FSMHandler handler1;
	FSMHandler handler2;

	@BeforeEach
	void setUp() {
		fsm1 = new StubTouchFSM(1);
		fsm2 = new StubTouchFSM(2);
		handler = Mockito.mock(FSMHandler.class);
		handler1 = Mockito.mock(FSMHandler.class);
		handler2 = Mockito.mock(FSMHandler.class);
		fsm1.addHandler(handler1);
		fsm2.addHandler(handler2);
	}

	@Test
	void testConsKO() {
		assertThrows(IllegalArgumentException.class, () -> new ConcurrentFSM<>(Set.of(fsm1)));
	}

	@Test
	void testConsKONull() {
		assertThrows(IllegalArgumentException.class, () -> new ConcurrentFSM<>(null));
	}

	@Nested
	class Concurr2 {
		@BeforeEach
		void setUp() {
			fsm = new ConcurrentFSM<>(Set.of(fsm1, fsm2));
			fsm.addHandler(handler);
		}

		@Test
		void testNbFSMsOK() {
			assertEquals(2, fsm.getConccurFSMs().size());
		}

		@Test
		void testReturnsCopyFSMs() {
			assertThrows(UnsupportedOperationException.class, () -> fsm.getConccurFSMs().remove(0));
		}

		@Test
		void testLogOK() {
			fsm.log(true);
			assertNotNull(fsm.logger);
			assertNotNull(fsm1.logger);
			assertNotNull(fsm2.logger);
		}

		@Test
		void testLogFalse() {
			fsm.log(true);
			fsm.log(false);
			assertNull(fsm.logger);
			assertNull(fsm1.logger);
			assertNull(fsm2.logger);
		}

		@Test
		void testIncorrectEventDoesNothing() {
			fsm.process("touchNothing");
			assertFalse(fsm.isStarted());
			assertFalse(fsm1.isStarted());
			assertFalse(fsm2.isStarted());
		}

		@Test
		void testOneEvent1DoesNotStart() {
			fsm.process("touch1");
			assertFalse(fsm.isStarted());
			assertTrue(fsm1.isStarted());
			assertFalse(fsm2.isStarted());
		}

		@Test
		void testOneEvent2DoesNotStart() {
			fsm.process("touch2");
			assertFalse(fsm.isStarted());
			assertFalse(fsm1.isStarted());
			assertTrue(fsm2.isStarted());
		}

		@Test
		void testTwoDifferntsTouchEventsStart() {
			fsm.process("touch1");
			fsm.process("touch2");
			assertTrue(fsm.isStarted());
			assertTrue(fsm1.isStarted());
			assertTrue(fsm2.isStarted());
		}

		@Test
		void testTwoDifferntsTouchEventsStart2() {
			fsm.process("touch2");
			fsm.process("touch1");
			assertTrue(fsm.isStarted());
			assertTrue(fsm1.isStarted());
			assertTrue(fsm2.isStarted());
		}

		@Test
		void testOneFullSequenceDoesNotRunTheFSM() throws CancelFSMException {
			fsm.process("touch1");
			fsm.process("move1");
			fsm.process("release1");
			Mockito.verify(handler, Mockito.never()).fsmStarts();
			Mockito.verify(handler1, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler1, Mockito.times(1)).fsmStops();
			Mockito.verify(handler2, Mockito.never()).fsmStarts();
		}

		@Test
		void testOneSequencePlusOtherStartedOK() throws CancelFSMException {
			fsm.process("touch1");
			fsm.process("move1");
			fsm.process("touch2");
			fsm.process("release1");
			Mockito.verify(handler, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler, Mockito.times(1)).fsmUpdates();
			Mockito.verify(handler, Mockito.times(1)).fsmStops();
			Mockito.verify(handler1, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler1, Mockito.times(1)).fsmStops();
			Mockito.verify(handler2, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler2, Mockito.times(1)).fsmUpdates();
			Mockito.verify(handler2, Mockito.never()).fsmStops();
			assertTrue(fsm2.isStarted());
		}

		@Test
		void testRecyclingEventsOK() {
			fsm.process("touch1");
			fsm.process("move1");
			fsm.process("touch2");
			fsm.process("release1");
			fsm.process("touch1");
			assertTrue(fsm.isStarted());
			assertTrue(fsm1.isStarted());
			assertTrue(fsm2.isStarted());
		}

		@Test
		void testRecyclingEventsOK2() throws CancelFSMException {
			fsm.process("touch1");
			fsm.process("move1");
			fsm.process("touch2");
			fsm.process("move2");
			fsm.process("release1");
			fsm.process("touch1");
			fsm.process("release2");
			assertFalse(fsm.isStarted());
			assertTrue(fsm1.isStarted());
			assertFalse(fsm2.isStarted());
			Mockito.verify(handler, Mockito.times(2)).fsmStarts();
			Mockito.verify(handler, Mockito.times(3)).fsmUpdates();
			Mockito.verify(handler, Mockito.times(2)).fsmStops();
			Mockito.verify(handler1, Mockito.times(2)).fsmStarts();
			Mockito.verify(handler1, Mockito.times(1)).fsmStops();
			Mockito.verify(handler1, Mockito.times(3)).fsmUpdates();
			Mockito.verify(handler2, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler2, Mockito.times(1)).fsmStops();
			Mockito.verify(handler2, Mockito.times(2)).fsmUpdates();
		}

		@Test
		void testOneSequencePlusOtherThenCancel() throws CancelFSMException {
			fsm.process("touch1");
			fsm.process("move1");
			fsm.process("touch2");
			fsm.process("cancel1");
			Mockito.verify(handler, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler, Mockito.times(1)).fsmCancels();
			Mockito.verify(handler1, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler1, Mockito.times(1)).fsmCancels();
			Mockito.verify(handler2, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler2, Mockito.never()).fsmCancels();
			assertTrue(fsm2.isStarted());
		}
	}

	@Nested
	class CheckClean {
		@BeforeEach
		void setUp() {
			fsm1 = Mockito.mock(StubTouchFSM.class);
			fsm2 = Mockito.mock(StubTouchFSM.class);
			fsm = new ConcurrentFSM<>(Set.of(fsm1, fsm2));
			fsm.addHandler(handler);
		}

		@Test
		void testReinit() {
			fsm.reinit();
			Mockito.verify(fsm1, Mockito.never()).reinit();
			Mockito.verify(fsm2, Mockito.never()).reinit();
		}

		@Test
		void testFullReinit() {
			fsm.fullReinit();
			Mockito.verify(fsm1, Mockito.never()).fullReinit();
			Mockito.verify(fsm2, Mockito.never()).fullReinit();
		}

		@Test
		void testUninstall() {
			fsm.uninstall();
			Mockito.verify(fsm1, Mockito.times(1)).uninstall();
			Mockito.verify(fsm2, Mockito.times(1)).uninstall();
		}
	}

	static class StubTouchFSM extends FSM<String> {
		int cpt;
		StubTouchFSM(final int cpt) {
			super();
			this.cpt = cpt;
			final StdState<String> touched = new StdState<>(this, "touched");
			final StdState<String> moved = new StdState<>(this, "mouved");
			final TerminalState<String> released = new TerminalState<>(this, "released");
			final CancellingState<String> cancelled = new CancellingState<>(this, "cancelled");
			addState(touched);
			addState(moved);
			addState(released);
			addState(cancelled);
			new Transition<>(initState, touched) {
				@Override
				protected String accept(final String event) {
					return event;
				}
				@Override
				protected boolean isGuardOK(final String event) {
					return ("touch" + cpt).equals(event);
				}
				@Override
				public Set<Object> getAcceptedEvents() {
					return Set.of("touch" + cpt);
				}
			};
			new Transition<>(touched, moved) {
				@Override
				protected String accept(final String event) {
					return event;
				}
				@Override
				protected boolean isGuardOK(final String event) {
					return ("move" + cpt).equals(event);
				}

				@Override
				public Set<Object> getAcceptedEvents() {
					return Set.of("move" + cpt);
				}
			};
			new Transition<>(moved, moved) {
				@Override
				protected String accept(final String event) {
					return event;
				}
				@Override
				protected boolean isGuardOK(final String event) {
					return ("move" + cpt).equals(event);
				}

				@Override
				public Set<Object> getAcceptedEvents() {
					return Set.of("move" + cpt);
				}
			};
			new Transition<>(moved, released) {
				@Override
				protected String accept(final String event) {
					return event;
				}
				@Override
				protected boolean isGuardOK(final String event) {
					return ("release" + cpt).equals(event);
				}

				@Override
				public Set<Object> getAcceptedEvents() {
					return Set.of("release" + cpt);
				}
			};
			new Transition<>(moved, cancelled) {
				@Override
				protected String accept(final String event) {
					return event;
				}
				@Override
				protected boolean isGuardOK(final String event) {
					return ("cancel" + cpt).equals(event);
				}

				@Override
				public Set<Object> getAcceptedEvents() {
					return Set.of("cancel" + cpt);
				}
			};
		}
	}
}
