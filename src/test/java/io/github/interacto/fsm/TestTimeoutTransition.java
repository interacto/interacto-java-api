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

import io.github.interacto.error.ErrorCatcher;
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestTimeoutTransition {
	private final Object lock = new Object();
	TimeoutTransition<StubEvent> evt;
	OutputState<StubEvent> src;
	InputState<StubEvent> tgt;
	FSM<StubEvent> fsm;

	@BeforeEach
	void setUp() {
		fsm = Mockito.mock(FSM.class);
		src = Mockito.mock(OutputState.class);
		tgt = Mockito.mock(InputState.class);
		Mockito.when(src.getFSM()).thenReturn(fsm);
		Mockito.when(tgt.getFSM()).thenReturn(fsm);
		evt = new TimeoutTransition<>(src, tgt, () -> 50L);
	}

	@AfterEach
	void tearDown() throws InterruptedException {
		for(final Thread th : getTimeoutThreads()) {
			th.interrupt();
			th.join();
		};
	}

	List<Thread> getTimeoutThreads() {
		return Thread.getAllStackTraces().keySet()
			.stream()
			.filter(thread -> thread.getName().startsWith(TimeoutTransition.TIMEOUT_THREAD_NAME_BASE))
			.collect(Collectors.toList());
	}

	void waitForTimeoutThreads() throws InterruptedException {
		for(final var t : getTimeoutThreads()) {
			t.join(1000L);
		}
	}

	@Test
	void testConstructorKO() {
		assertThrows(IllegalArgumentException.class, () -> new TimeoutTransition<>(src, tgt, null));
	}

	@Test
	void testIsGuardOKAfterTimeout() throws InterruptedException {
		evt.startTimeout();
		waitForTimeoutThreads();
		assertTrue(evt.isGuardOK(null));
	}

	@Test
	void testIsGuardKOBeforeTimeout() {
		evt.startTimeout();
		assertFalse(evt.isGuardOK(null));
	}

	@Test
	void testacceptOKAfterTimeout() throws InterruptedException {
		evt.startTimeout();
		waitForTimeoutThreads();
		assertTrue(evt.accept(null));
	}

	@Test
	void testacceptKOBeforeTimeout() {
		evt.startTimeout();
		assertFalse(evt.accept(null));
	}

	@Test
	void testStopTimeout() throws InterruptedException {
		evt.startTimeout();
		evt.stopTimeout();
		waitForTimeoutThreads();
		assertFalse(evt.isGuardOK(null));
	}

	@Test
	void testStopTimeout0() {
		evt = new TimeoutTransition<>(src, tgt, () -> 0L);
		evt.startTimeout();
		assertEquals(0L, getTimeoutThreads().size());
		evt.stopTimeout();
		assertFalse(evt.isGuardOK(null));
	}

	@Test
	void testTwoConsecutiveStarts() {
		evt = new TimeoutTransition<>(src, tgt, () -> 300L);
		evt.startTimeout();
		evt.startTimeout();
		assertEquals(1L, getTimeoutThreads().size());
		evt.stopTimeout();
		assertFalse(evt.isGuardOK(null));
	}

	@Test
	void testStopWhenNotStarted() {
		evt.stopTimeout();
		assertEquals(0L, getTimeoutThreads().size());
		assertFalse(evt.isGuardOK(null));
	}

	@Test
	void testGetAcceptEventsEmpty() {
		assertTrue(evt.getAcceptedEvents().isEmpty());
	}

	@Test
	void testExecuteWithoutTimeout() throws CancelFSMException {
		assertTrue(evt.execute(null).isEmpty());
	}

	@Test
	void testExecuteWithTimeout() throws CancelFSMException, InterruptedException {
		evt.startTimeout();
		waitForTimeoutThreads();
		assertEquals(tgt, evt.execute(null).orElseThrow());
	}

	@Test
	void testExecuteAndGuardNotOK() throws CancelFSMException, InterruptedException {
		evt = new TimeoutTransition<>(src, tgt, () -> 50L) {
			@Override
			protected boolean isGuardOK(final StubEvent event) {
				return false;
			}
		};
		evt.startTimeout();
		waitForTimeoutThreads();
		assertTrue(evt.execute(null).isEmpty());
	}

	@Test
	void testExecuteCancels() throws CancelFSMException, InterruptedException {
		Mockito.doThrow(CancelFSMException.class).when(tgt).enter();
		evt.startTimeout();
		waitForTimeoutThreads();
		assertThrows(CancelFSMException.class, () -> evt.execute(null));
	}

	@Test
	void testFSMThrowsExceptionInThread() throws InterruptedException {
		final var ex = new IllegalArgumentException("foo");
		final List<Throwable> errors = new ArrayList<>();
		final Disposable disposable = ErrorCatcher.getInstance().getErrors().subscribe(errors::add);
		Mockito.doThrow(ex).when(fsm).onTimeout();
		evt.startTimeout();
		waitForTimeoutThreads();
		disposable.dispose();
		assertEquals(1, errors.size());
		assertSame(ex, errors.get(0));
	}

	@Test
	void testExecuteCallFSMTimeout() throws InterruptedException {
		evt.startTimeout();
		waitForTimeoutThreads();
		Mockito.verify(fsm, Mockito.times(1)).onTimeout();
	}

	@Test
	void testExecuteCallsStatesMethods() throws InterruptedException, CancelFSMException {
		evt.startTimeout();
		waitForTimeoutThreads();
		evt.execute(null);
		Mockito.verify(src, Mockito.times(1)).exit();
		Mockito.verify(tgt, Mockito.times(1)).enter();
	}
}
