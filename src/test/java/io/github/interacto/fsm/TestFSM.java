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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestFSM {
	FSM<StubEvent> fsm;
	FSMHandler handler;

	@BeforeEach
	void setUp() {
		fsm = new FSM<>();
		handler = Mockito.mock(FSMHandler.class);
	}

	@Test
	void testInitState() {
		assertEquals(1, fsm.states.size());
		assertTrue(fsm.states.iterator().next() instanceof InitState);
	}

	@Test
	void testInner() {
		assertFalse(fsm.inner);
	}

	@Test
	void testStartingState() {
		assertEquals(fsm.initState, fsm.startingState);
	}

	@Test
	void testCurrentStateAtStart() {
		assertEquals(fsm.initState, fsm.getCurrentState());
	}

	@Test
	void testAddState() {
		final StdState<StubEvent> state = new StdState<>(fsm, "s1");
		fsm.addState(state);
		assertEquals(2, fsm.states.size());
	}

	@Test
	void testLog() {
		fsm.log(true);
		assertNotNull(fsm.logger);
	}

	@Test
	void testNoLog() {
		fsm.log(true);
		fsm.log(false);
		assertNull(fsm.logger);
	}

	@Nested
	class TestProcessUniqueEvent {
		StdState<StubEvent> std;
		TerminalState<StubEvent> terminal;
		CancellingState<StubEvent> cancelling;

		@BeforeEach
		void setUp() {
			fsm.addHandler(handler);
			fsm.log(true);
			std = new StdState<>(fsm, "s1");
			terminal = new TerminalState<>(fsm, "t1");
			cancelling = new CancellingState<>(fsm, "c1");
			new StubTransitionOK(fsm.initState, std);
			new StubTransitionOK(std, terminal);
			fsm.addState(std);
			fsm.addState(terminal);
			fsm.addState(cancelling);
		}

		@Test
		void testGetStates() {
			assertEquals(new HashSet<>(Arrays.asList(fsm.initState, std, terminal, cancelling)), fsm.getStates());
		}

		@Test
		void testcurrentStateProp() {
			assertNotNull(fsm.currentStatePublisher);
		}

		@Test
		void testFireEventKO() {
			fsm.process(null);
			assertEquals(fsm.initState, fsm.getCurrentState());
		}

		@Test
		void testFireEventChangeState() {
			fsm.process(new StubEvent());
			assertEquals(std, fsm.getCurrentState());
		}

		@Test
		void testGetterCurrentState() {
			fsm.process(new StubEvent());
			assertEquals(fsm.currentState, fsm.getCurrentState());
		}

		@Test
		void testFireEventTriggerFSMStartUpdate() throws CancelFSMException {
			fsm.process(new StubEvent());
			Mockito.verify(handler, Mockito.times(1)).fsmUpdates();
			Mockito.verify(handler, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler, Mockito.never()).fsmCancels();
			Mockito.verify(handler, Mockito.never()).fsmStops();
		}

		@Test
		void testFire2EventsToEnd() {
			fsm.process(new StubEvent());
			fsm.process(new StubEvent());
			assertEquals(fsm.initState, fsm.getCurrentState());
		}

		@Test
		void testFireEventTriggerFSMUpdate() throws CancelFSMException {
			fsm.process(new StubEvent());
			fsm.process(new StubEvent());
			Mockito.verify(handler, Mockito.times(1)).fsmUpdates();
			Mockito.verify(handler, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler, Mockito.times(1)).fsmStops();
			Mockito.verify(handler, Mockito.never()).fsmCancels();
		}

		@Test
		void testFireThreeEventRestartOK() throws CancelFSMException {
			fsm.process(new StubEvent());
			fsm.process(new StubEvent());
			fsm.process(new StubEvent());
			Mockito.verify(handler, Mockito.times(2)).fsmStarts();
		}

		@Test
		void testCancellation() throws CancelFSMException {
			std.transitions.clear();
			new StubTransitionOK(std, cancelling);
			fsm.process(new StubEvent());
			fsm.process(new StubEvent());
			assertEquals(fsm.initState, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.times(1)).fsmCancels();
			Mockito.verify(handler, Mockito.never()).fsmStops();
		}

		@Test
		void testRecycleEvent() throws CancelFSMException {
			fsm.process(new StubEvent());
			fsm.addRemaningEventsToProcess(new StubEvent());
			fsm.process(new StubEvent());
			assertEquals(std, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.times(2)).fsmStarts();
			assertTrue(fsm.eventsToProcess.isEmpty());
		}

		@Test
		void testNoRecycleEventOnCancel() throws CancelFSMException {
			std.transitions.clear();
			new StubTransitionOK(std, cancelling);
			fsm.process(new StubEvent());
			fsm.addRemaningEventsToProcess(new StubEvent());
			fsm.process(new StubEvent());
			assertEquals(fsm.initState, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler, Mockito.times(1)).fsmCancels();
			assertTrue(fsm.eventsToProcess.isEmpty());
		}

		@Test
		void testReinit() {
			fsm.process(new StubEvent());
			fsm.reinit();
			assertEquals(fsm.initState, fsm.getCurrentState());
		}

		@Test
		void testFullReinit() {
			fsm.process(new StubEvent());
			fsm.addRemaningEventsToProcess(new StubEvent());
			fsm.fullReinit();
			assertTrue(fsm.eventsToProcess.isEmpty());
			assertEquals(fsm.initState, fsm.getCurrentState());
		}

		@Test
		void testCancelOnStart() throws CancelFSMException {
			Mockito.doThrow(new CancelFSMException()).when(handler).fsmStarts();
			fsm.process(new StubEvent());
			assertEquals(fsm.initState, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler, Mockito.times(1)).fsmCancels();
			Mockito.verify(handler, Mockito.never()).fsmUpdates();
			Mockito.verify(handler, Mockito.never()).fsmStops();
		}

		@Test
		void testCancelOnUpdate() throws CancelFSMException {
			Mockito.doThrow(new CancelFSMException()).when(handler).fsmUpdates();
			fsm.process(new StubEvent());
			assertEquals(fsm.initState, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler, Mockito.times(1)).fsmCancels();
			Mockito.verify(handler, Mockito.times(1)).fsmUpdates();
			Mockito.verify(handler, Mockito.never()).fsmStops();
		}

		@Test
		void testCancelOnEnd() throws CancelFSMException {
			Mockito.doThrow(new CancelFSMException()).when(handler).fsmStops();
			fsm.process(new StubEvent());
			fsm.process(new StubEvent());
			assertEquals(fsm.initState, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler, Mockito.times(1)).fsmCancels();
			Mockito.verify(handler, Mockito.times(1)).fsmUpdates();
			Mockito.verify(handler, Mockito.times(1)).fsmStops();
		}

		@Test
		void testHasStartedReinit() {
			fsm.process(new StubEvent());
			fsm.process(new StubEvent());
			assertFalse(fsm.started);
		}

		@Test
		void testHasStarted() {
			fsm.process(new StubEvent());
			assertTrue(fsm.started);
			assertTrue(fsm.isStarted());
		}
	}

	@Test
	void testAddHandlerNull() {
		fsm.addHandler(null);
		assertTrue(fsm.handlers.isEmpty());
	}

	@Test
	void testRemoveHandlerNull() {
		fsm.addHandler(handler);
		fsm.removeHandler(null);
		assertEquals(Set.of(handler), fsm.handlers);
	}

	@Test
	void testAddRemainingNull() {
		fsm.addRemaningEventsToProcess(null);
		assertTrue(fsm.eventsToProcess.isEmpty());
	}

	@Test
	void testAddRemainingNotNull() {
		final var evt = new StubEvent();
		fsm.addRemaningEventsToProcess(evt);
		assertEquals(List.of(evt), fsm.eventsToProcess);
	}

	@Test
	void testIsInner() {
		assertFalse(fsm.isInner());
	}

	@Test
	void testSetInnerTrue() {
		fsm.setInner(true);
		assertTrue(fsm.isInner());
	}

	@Test
	void testSetInnerFalse() {
		fsm.setInner(true);
		fsm.setInner(false);
		assertFalse(fsm.isInner());
	}

	@Test
	void testProcessRemainingEvents() {
		final var evt = new StubEvent();
		fsm.addRemaningEventsToProcess(evt);
		fsm.processRemainingEvents();
		assertTrue(fsm.eventsToProcess.isEmpty());
	}

	@Test
	void testOnTerminatingIfStarted() throws CancelFSMException {
		fsm.onStarting();
		fsm.addHandler(handler);
		fsm.onTerminating();
		Mockito.verify(handler, Mockito.times(1)).fsmStops();
	}

	@Test
	void testOnTerminatingNotStarted() throws CancelFSMException {
		fsm.addHandler(handler);
		fsm.onTerminating();
		Mockito.verify(handler, Mockito.never()).fsmStops();
	}

	@Test
	void testOnUpdatingIfStarted() throws CancelFSMException {
		fsm.onStarting();
		fsm.addHandler(handler);
		fsm.onUpdating();
		Mockito.verify(handler, Mockito.times(1)).fsmUpdates();
	}

	@Test
	void testOnUpdatingNotStarted() throws CancelFSMException {
		fsm.addHandler(handler);
		fsm.onUpdating();
		Mockito.verify(handler, Mockito.never()).fsmUpdates();
	}

	@Test
	void testAddNullState() {
		final var state = Mockito.mock(InputState.class);
		fsm.addState(state);
		fsm.addState(null);
		assertEquals(Set.of(fsm.initState, state), fsm.getStates());
	}

	@Test
	void testLogWithAlreadyLog() {
		fsm.log(true);
		fsm.log(true);
		assertNotNull(fsm.logger);
	}

	@Test
	void testOnTimeoutWithoutTimeout() {
		fsm.logger = Mockito.mock(Logger.class);
		fsm.onTimeout();
		Mockito.verify(fsm.logger, Mockito.never()).log(Mockito.any(), Mockito.anyString());
	}


	@Test
	void testUninstall() {
		final var s1 = Mockito.mock(InputState.class);
		final var disposed = new AtomicBoolean();
		fsm.addState(s1);
		fsm.eventsToProcess.add(new StubEvent());
		fsm.uninstall();

		assertTrue(fsm.states.isEmpty());
		assertTrue(fsm.eventsToProcess.isEmpty());
		assertTrue(fsm.currentStatePublisher.hasComplete());
		assertNull(fsm.logger);
		assertNull(fsm.startingState);
		assertNull(fsm.currentSubFSM);
		Mockito.verify(s1, Mockito.times(1)).uninstall();
	}

	@Test
	void testCurrentStateNotNull() {
		assertNotNull(fsm.currentState());
	}

	@Test
	void testCurrentStateChanged() {
		final List<Map.Entry<OutputState<StubEvent>, OutputState<StubEvent>>> changes = new ArrayList<>();
		final var newCurr = Mockito.mock(OutputState.class);
		fsm.currentState().subscribe(changes::add);
		fsm.setCurrentState(newCurr);
		assertEquals(1, changes.size());
		assertEquals(newCurr, changes.get(0).getValue());
		assertEquals(fsm.initState, changes.get(0).getKey());
	}

	@Nested
	class TestMultipleTransitionChoice {
		StdState<StubEvent> std;
		TerminalState<StubEvent> terminal;
		CancellingState<StubEvent> cancel;
		StubTransitionOK iToS;
		Transition<StubEvent> sToT;
		Transition<StubEvent> sToC;
		Transition<StubEvent> recur;

		@BeforeEach
		void setUp() {
			fsm.addHandler(handler);
			fsm.log(true);
			std = new StdState<>(fsm, "s1");
			terminal = new TerminalState<>(fsm, "t1");
			cancel = new CancellingState<>(fsm, "c1");
			iToS = new StubTransitionOK(fsm.initState, std);
			sToT = new SubStubTransition1(std, terminal, true);
			sToC = new SubStubTransition2(std, cancel, true);
			recur = new SubStubTransition3(std, std, true);
			fsm.addState(std);
			fsm.addState(terminal);
			fsm.addState(cancel);
		}

		@Test
		void testNotTriggeredIfGuardKO() throws CancelFSMException {
			iToS.guard = false;
			fsm.process(new StubEvent());
			assertEquals(fsm.initState, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.never()).fsmStarts();
		}

		@Test
		void testNotTriggeredIfNotGoodEvent() throws CancelFSMException {
			fsm.process(new StubEvent());
			fsm.process(new StubEvent());
			assertEquals(std, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.never()).fsmCancels();
			Mockito.verify(handler, Mockito.never()).fsmStops();
			Mockito.verify(handler, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler, Mockito.times(1)).fsmUpdates();
		}

		@Test
		void testTriggerGoodChoice() throws CancelFSMException {
			fsm.process(new StubEvent());
			fsm.process(new StubSubEvent2());
			assertEquals(fsm.initState, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.times(1)).fsmCancels();
			Mockito.verify(handler, Mockito.never()).fsmStops();
			Mockito.verify(handler, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler, Mockito.times(1)).fsmUpdates();
		}

		@Test
		void testHasStartedReinitOnCancel() {
			fsm.process(new StubEvent());
			fsm.process(new StubSubEvent2());
			assertFalse(fsm.started);
		}

		@Test
		void testTriggerGoodChoice2() throws CancelFSMException {
			fsm.process(new StubEvent());
			fsm.process(new StubSubEvent1());
			assertEquals(fsm.initState, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.times(1)).fsmStops();
			Mockito.verify(handler, Mockito.never()).fsmCancels();
			Mockito.verify(handler, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler, Mockito.times(1)).fsmUpdates();
		}


		@Test
		@DisplayName("Check onstart not called when starting state diff")
		void testStartingStateNotTriggered() throws CancelFSMException {
			fsm.startingState = terminal;
			fsm.process(new StubEvent());
			Mockito.verify(handler, Mockito.never()).fsmStarts();
		}

		@Test
		void testStartingStateNotTriggeredSoNoUpdate() throws CancelFSMException {
			fsm.startingState = terminal;
			fsm.process(new StubEvent());
			Mockito.verify(handler, Mockito.never()).fsmUpdates();
		}

		@Test
		void testStartingStateNotTriggeredSoNoCancel() {
			fsm.startingState = terminal;
			fsm.process(new StubEvent());
			fsm.process(new StubSubEvent2());
			Mockito.verify(handler, Mockito.never()).fsmCancels();
		}

		@Test
		void testStartingStateTriggeredOnTerminal() throws CancelFSMException {
			fsm.startingState = terminal;
			fsm.process(new StubEvent());
			fsm.process(new StubSubEvent1());
			Mockito.verify(handler, Mockito.times(1)).fsmStarts();
			Mockito.verify(handler, Mockito.times(1)).fsmStops();
		}

		@Test
		void testStartingStateOnRecursion() throws CancelFSMException {
			fsm.startingState = std;
			fsm.process(new StubEvent());
			fsm.process(new StubSubEvent3());
			Mockito.verify(handler, Mockito.times(1)).fsmStarts();
		}
	}

	@Nested
	class TestWithTimeoutTransition {
		StdState<StubEvent> std;
		StdState<StubEvent> std2;
		TerminalState<StubEvent> terminal;
		Transition<StubEvent> iToS;
		Transition<StubEvent> sToT;
		TimeoutTransition<StubEvent> timeout;

		@BeforeEach
		void setUp() {
			fsm.addHandler(handler);
			std = new StdState<>(fsm, "s1");
			std2 = new StdState<>(fsm, "s2");
			terminal = new TerminalState<>(fsm, "t1");
			iToS = new StubTransitionOK(fsm.initState, std);
			sToT = new StubTransitionOK(std, terminal);
			timeout = new TimeoutTransition<>(std, std2, () -> 100L);
			new StubTransitionOK(std2, std);
			fsm.addState(std);
			fsm.addState(std2);
			fsm.addState(terminal);
		}

		@Test
		void testTimeoutChangeState() throws InterruptedException {
			fsm.log(true);
			fsm.process(new StubEvent());
			Thread.sleep(200);
			assertEquals(std2, fsm.getCurrentState());
		}

		@Test
		void testTimeoutStoppedOnOtherTransition() throws InterruptedException {
			fsm.process(new StubEvent());
			Thread.sleep(10);
			fsm.process(new StubEvent());
			Thread.sleep(100);
			assertEquals(fsm.initState, fsm.getCurrentState());
		}

		@Test
		void testTimeoutStoppedOnOtherTransitionWithLog() throws InterruptedException {
			fsm.log(true);
			fsm.process(new StubEvent());
			Thread.sleep(10);
			fsm.process(new StubEvent());
			Thread.sleep(100);
			assertEquals(fsm.initState, fsm.getCurrentState());
		}

		@Test
		void testTimeoutChangeStateThenCancel() throws InterruptedException, CancelFSMException {
			fsm.process(new StubEvent());
			Mockito.doThrow(new CancelFSMException()).when(handler).fsmUpdates();
			Thread.sleep(300);
			assertEquals(fsm.initState, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.times(1)).fsmCancels();
		}
	}

	@Nested
	class TestWithSubFSM {
		FSM<StubEvent> fsm;
		FSM<StubEvent> mainfsm;
		StdState<StubEvent> s1;
		StdState<StubEvent> subS1;
		StdState<StubEvent> subS2;
		TerminalState<StubEvent> subT;
		CancellingState<StubEvent> subC;

		@BeforeEach
		void setUp() {
			fsm = new FSM<>();
			mainfsm = new FSM<>();
			s1 = new StdState<>(mainfsm, "s1");
			mainfsm.addState(s1);
			new SubFSMTransition<>(mainfsm.initState, s1, fsm);
			mainfsm.addHandler(handler);
			subS1 = new StdState<>(fsm, "sub1");
			subS2 = new StdState<>(fsm, "sub2");
			subT = new TerminalState<>(fsm, "t1");
			subC = new CancellingState<>(fsm, "c1");
			new SubStubTransition1(fsm.initState, subS1, true);
			new SubStubTransition2(subS1, subS2, true);
			new SubStubTransition1(subS2, subT, true);
			new SubStubTransition2(subS2, subC, true);
			fsm.addState(subS1);
			fsm.addState(subS2);
			fsm.addState(subT);
			fsm.addState(subC);
		}

		@Test
		void testEntersSubGoodCurrState() throws CancelFSMException {
			mainfsm.process(new StubSubEvent1());
			assertEquals(subS1, mainfsm.getCurrentState());
			assertEquals(subS1, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.times(1)).fsmStarts();
		}

		@Test
		void testNextSubStarteChangesMainCurrState() throws CancelFSMException {
			mainfsm.process(new StubSubEvent1());
			mainfsm.process(new StubSubEvent2());
			assertEquals(subS2, mainfsm.getCurrentState());
			assertEquals(subS2, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.times(2)).fsmUpdates();
		}

		@Test
		void testEntersSubTerminalGoNextMain() throws CancelFSMException {
			mainfsm.process(new StubSubEvent1());
			mainfsm.process(new StubSubEvent2());
			mainfsm.process(new StubSubEvent1());
			assertEquals(s1, mainfsm.getCurrentState());
			assertEquals(fsm.initState, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.never()).fsmStops();
			Mockito.verify(handler, Mockito.never()).fsmCancels();
		}

		@Test
		void testEntersSubCancelCancelsMain() throws CancelFSMException {
			mainfsm.process(new StubSubEvent1());
			mainfsm.process(new StubSubEvent2());
			mainfsm.process(new StubSubEvent2());
			assertEquals(mainfsm.initState, mainfsm.getCurrentState());
			assertEquals(fsm.initState, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.never()).fsmStops();
			Mockito.verify(handler, Mockito.times(1)).fsmCancels();
		}

		@Test
		void testReinitAlsoSubFSM() {
			mainfsm.process(new StubSubEvent1());
			mainfsm.process(new StubSubEvent2());
			mainfsm.fullReinit();
			assertEquals(fsm.initState, fsm.getCurrentState());
		}

		@Test
		void testExitSubGoIntoCancelling() {
			final CancellingState<StubEvent> cancel = new CancellingState<>(mainfsm, "cancel1");
			mainfsm.addState(cancel);
			mainfsm.initState.transitions.clear();
			new SubFSMTransition<>(mainfsm.initState, cancel, fsm);
			mainfsm.process(new StubSubEvent1());
			mainfsm.process(new StubSubEvent2());
			mainfsm.process(new StubSubEvent1());
			assertEquals(mainfsm.initState, mainfsm.getCurrentState());
			assertEquals(fsm.initState, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.times(1)).fsmCancels();
		}

		@Test
		void testExitKOStateType() {
			final InputState<StubEvent> stateKO = new InputState<>() {
				@Override
				public void enter() {
				}
				@Override
				public String getName() {
					return "foo";
				}
				@Override
				public FSM<StubEvent> getFSM() {
					return mainfsm;
				}
			};
			mainfsm.addState(stateKO);
			mainfsm.initState.transitions.clear();
			new SubFSMTransition<>(mainfsm.initState, stateKO, fsm);
			mainfsm.process(new StubSubEvent1());
			mainfsm.process(new StubSubEvent2());
			mainfsm.process(new StubSubEvent1());
			assertEquals("sub2", mainfsm.getCurrentState().getName());
			assertEquals(fsm.initState, fsm.getCurrentState());
		}

		@Test
		void testExitSubGoIntoTerminal() throws CancelFSMException {
			final TerminalState<StubEvent> terminal = new TerminalState<>(mainfsm, "terminal1");
			mainfsm.addState(terminal);
			mainfsm.initState.transitions.clear();
			new SubFSMTransition<>(mainfsm.initState, terminal, fsm);
			mainfsm.process(new StubSubEvent1());
			mainfsm.process(new StubSubEvent2());
			mainfsm.process(new StubSubEvent1());
			assertEquals(mainfsm.initState, mainfsm.getCurrentState());
			assertEquals(fsm.initState, fsm.getCurrentState());
			Mockito.verify(handler, Mockito.times(1)).fsmStops();
		}
	}
}
