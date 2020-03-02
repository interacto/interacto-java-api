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
import io.github.interacto.fsm.InitState;
import io.github.interacto.fsm.OutputState;
import io.github.interacto.fsm.StdState;
import io.reactivex.subjects.PublishSubject;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestInteractionImpl {
	InteractionStub interaction;
	Logger log;
	Logger formerLog;
	FSM<Object> fsm;
	PublishSubject<Map.Entry<OutputState<Object>, OutputState<Object>>> currentStateObs;
	OutputState<Object> currentState;

	@BeforeEach
	void setUp() {
		currentStateObs = PublishSubject.create();
		fsm = Mockito.mock(FSM.class);
		Mockito.when(fsm.currentState()).thenReturn(currentStateObs);
		Mockito.when(fsm.getCurrentState()).thenAnswer(invok -> currentState);
		interaction = new InteractionStub(fsm);
		formerLog = InteractionImpl.defaultLogger;
		log = Mockito.mock(Logger.class);
	}

	@AfterEach
	void tearDown() {
		InteractionImpl.setLogger(formerLog);
		if(interaction.currThrottleTimeoutFuture != null) {
			interaction.currThrottleTimeoutFuture.cancel(true);
		}
		interaction.uninstall();
	}

	@Nested
	class LoggerTest {
		@Test
		void testSetLoggerNull() {
			InteractionImpl.setLogger(null);
			assertEquals(formerLog, InteractionImpl.defaultLogger);
		}

		@Test
		void testSetLoggerOK() {
			InteractionImpl.setLogger(log);
			assertEquals(log, InteractionImpl.defaultLogger);
		}

		@Test
		void testInteractionLogger() {
			InteractionImpl.setLogger(log);
			interaction.log(true);
			interaction.setActivated(true);
			Mockito.verify(log, Mockito.times(1)).log(Mockito.any(), Mockito.anyString());
		}

		@Test
		void testNoLog() {
			InteractionImpl.setLogger(log);
			interaction.log(true);
			interaction.log(false);
			interaction.setActivated(true);
			Mockito.verify(log, Mockito.never()).log(Mockito.any(), Mockito.anyString());
		}

		@Test
		void testDefaultNoLog() {
			interaction.log(true);
			interaction.setActivated(true);
			assertTrue(interaction.isActivated());
		}

		@Test
		void testCannotChangeLogger() {
			InteractionImpl.setLogger(log);
			interaction.log(true);
			InteractionImpl.setLogger(formerLog);
			interaction.log(true);
			interaction.setActivated(true);
			Mockito.verify(log, Mockito.times(1)).log(Mockito.any(), Mockito.anyString());
		}
	}

	@Test
	void testNullFSM() {
		assertThrows(IllegalArgumentException.class, () -> new InteractionStub(null));
	}

	@Test
	void testFullReinit() {
		interaction.fullReinit();
		Mockito.verify(fsm, Mockito.times(1)).fullReinit();
	}

	@Test
	void testIsRunningNotActivated() {
		interaction.setActivated(false);
		assertFalse(interaction.isRunning());
	}

	@Test
	void testIsRunningInitState() {
		interaction.setActivated(true);
		currentState = Mockito.mock(InitState.class);
		assertFalse(interaction.isRunning());
	}

	@Test
	void testIsRunningOK() {
		interaction.setActivated(true);
		currentState = Mockito.mock(StdState.class);
		assertTrue(interaction.isRunning());
	}

	@Test
	void testSetConsumeEvents() {
		final Object evt = new Object();
		interaction.setConsumeEvents(true);
		interaction.setActivated(true);
		final InteractionStub spy = Mockito.spy(interaction);
		spy.processEvent(evt);
		Mockito.verify(spy, Mockito.times(1)).consumeEvent(evt);
	}

	@Test
	void testNotConsumeEvents() {
		final Object evt = new Object();
		interaction.setActivated(true);
		final InteractionStub spy = Mockito.spy(interaction);
		spy.processEvent(evt);
		Mockito.verify(spy, Mockito.never()).consumeEvent(evt);
	}

	@Test
	void testSetNotConsumeEvents() {
		final Object evt = new Object();
		interaction.setConsumeEvents(false);
		interaction.setActivated(true);
		final InteractionStub spy = Mockito.spy(interaction);
		spy.processEvent(evt);
		Mockito.verify(spy, Mockito.never()).consumeEvent(evt);
	}

	@Test
	void testActivatedByDefault() {
		assertTrue(interaction.isActivated());
	}

	@Test
	void testSetNotActivated() {
		interaction.setActivated(false);
		assertFalse(interaction.isActivated());
	}

	@Test
	void testSetReactivated() {
		interaction.setActivated(false);
		interaction.setActivated(true);
		assertTrue(interaction.isActivated());
	}

	@Test
	void testNotProcessWhenNotActivated() {
		final Object evt = new Object();
		interaction.setConsumeEvents(false);
		interaction.setActivated(false);
		final InteractionStub spy = Mockito.spy(interaction);
		spy.processEvent(evt);
		Mockito.verify(spy, Mockito.never()).consumeEvent(evt);
	}

	@Test
	void testProcessFirstEventOnlyWithThrottling() {
		interaction.setConsumeEvents(true);
		interaction.setActivated(true);
		interaction.setThrottleTimeout(1000);
		final InteractionStub spy = Mockito.spy(interaction);
		spy.processEvent(new Object());
		spy.processEvent(new Object());
		spy.processEvent(new Object());
		Mockito.verify(spy, Mockito.times(1)).consumeEvent(Mockito.any());
	}

	@Test
	void testProcessWithThrottlingDifferentSuccessiveTypes() {
		final Object evt1 = new Object();
		final Object evt2 = "foo";
		interaction.setConsumeEvents(true);
		interaction.setActivated(true);
		interaction.setThrottleTimeout(1000);
		final InteractionStub spy = Mockito.spy(interaction);
		spy.processEvent(evt1);
		spy.processEvent(evt2);
		Mockito.verify(spy, Mockito.times(1)).consumeEvent(evt1);
		Mockito.verify(spy, Mockito.times(1)).consumeEvent(evt2);
	}

	@Test
	void testProcessWithThrottlingDifferentTypes() {
		final Object evt1 = new Object();
		final Object evt2 = "foo";
		interaction.setConsumeEvents(true);
		interaction.setActivated(true);
		interaction.setThrottleTimeout(500);
		final InteractionStub spy = Mockito.spy(interaction);
		spy.processEvent(evt1); // 1
		spy.processEvent(new Object()); // ignored
		spy.processEvent(new Object()); // 2 as the next one is of different type
		spy.processEvent(evt2); // 3
		spy.processEvent("bar"); // ignored
		Mockito.verify(spy, Mockito.times(3)).consumeEvent(Mockito.any());
	}

	@Test
	void testProcessWithThrottlingDifferentTypesAfterTimeout() throws ExecutionException, InterruptedException {
		final Object evt1 = new Object();
		final Object evt2 = "foo";
		interaction.setConsumeEvents(true);
		interaction.setActivated(true);
		interaction.setThrottleTimeout(1000);
		final InteractionStub spy = Mockito.spy(interaction);
		spy.processEvent(evt1); // 1
		spy.processEvent(new Object()); // ignored
		spy.processEvent(new Object()); // 2 as the next one is of different type
		spy.processEvent(evt2); // 3
		spy.processEvent("bar"); // 4
		spy.currThrottleTimeoutFuture.get();
		Mockito.verify(spy, Mockito.times(4)).consumeEvent(Mockito.any());
	}


	@Test
	void testProcessWithThrottlingAfterTimeout() throws ExecutionException, InterruptedException {
		interaction.setConsumeEvents(true);
		interaction.setActivated(true);
		interaction.setThrottleTimeout(200);
		final InteractionStub spy = Mockito.spy(interaction);
		spy.processEvent(new Object());
		spy.currThrottleTimeoutFuture.get();
		Mockito.verify(spy, Mockito.times(1)).consumeEvent(Mockito.any());
	}

	@Test
	void testProcessWithThrottlingAfterTwoTimeouts() throws ExecutionException, InterruptedException {
		interaction.setConsumeEvents(true);
		interaction.setActivated(true);
		interaction.setThrottleTimeout(200);
		final InteractionStub spy = Mockito.spy(interaction);
		spy.processEvent(new Object());
		spy.currThrottleTimeoutFuture.get();
		spy.processEvent(new Object());
		spy.currThrottleTimeoutFuture.get();
		Mockito.verify(spy, Mockito.times(2)).consumeEvent(Mockito.any());
	}

	@Test
	void testProcessWithThrottlingShutdown() {
		interaction.setActivated(true);
		interaction.setThrottleTimeout(10000);
		interaction.processEvent(new Object());
		interaction.uninstall();
		assertTrue(interaction.executor.isShutdown());
		assertTrue(interaction.currThrottleTimeoutFuture.isDone());
	}

	@Test
	void testProcessWithThrottlingShutdownCrash() throws InterruptedException {
		interaction.executor = Mockito.mock(ExecutorService.class);
		Mockito.when(interaction.executor.awaitTermination(5, TimeUnit.SECONDS)).thenThrow(InterruptedException.class);
		interaction.setActivated(true);
		interaction.setThrottleTimeout(10000);
		interaction.processEvent(new Object());
		interaction.uninstall();
	}

	@Test
	void testGetFSM() {
		assertEquals(fsm, interaction.getFsm());
	}

	@Test
	void testReinit() {
		final InteractionStub spy = Mockito.spy(interaction);
		spy.reinit();
		Mockito.verify(fsm, Mockito.times(1)).reinit();
		Mockito.verify(spy, Mockito.times(1)).reinitData();
	}

	@Test
	void testUninstall() {
		final AtomicBoolean ok = new AtomicBoolean(true);
		interaction = new InteractionStub(fsm) {
			@Override
			protected void updateEventsRegistered(final OutputState<Object> n, final OutputState<Object> o) {
				ok.set(false);
			}
		};
		interaction.uninstall();
		currentStateObs.onNext(Map.entry(Mockito.mock(OutputState.class), Mockito.mock(OutputState.class)));
		assertFalse(interaction.isActivated());
		assertTrue(ok.get());
		assertTrue(interaction.disposable.isDisposed());
	}

	@Test
	void testCurrentState() {
		final OutputState<Object> oldState = Mockito.mock(OutputState.class);
		final OutputState<Object> newState = Mockito.mock(OutputState.class);
		final AtomicBoolean ok = new AtomicBoolean(false);

		interaction = new InteractionStub(fsm) {
			@Override
			protected void updateEventsRegistered(final OutputState<Object> n, final OutputState<Object> o) {
				assertEquals(oldState, o);
				assertEquals(newState, n);
				ok.set(true);
			}
		};
		currentStateObs.onNext(Map.entry(oldState, newState));
		assertTrue(ok.get());
	}
}
