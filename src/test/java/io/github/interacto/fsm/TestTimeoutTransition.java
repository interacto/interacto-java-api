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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestTimeoutTransition {
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

	@Test
	void testConstructorKO() {
		assertThrows(IllegalArgumentException.class, () -> new TimeoutTransition<>(src, tgt, null));
	}

	@Test
	void testIsGuardOKAfterTimeout() throws InterruptedException {
		evt.startTimeout();
		Thread.sleep(100L);
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
		Thread.sleep(100L);
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
		Thread.sleep(100L);
		assertFalse(evt.isGuardOK(null));
	}

	@Test
	void testGetAcceptEventsEmpty() {
		assertTrue(evt.getAcceptedEvents().isEmpty());
	}

	@Test
	void testExecuteWithoutTimeout() throws CancelFSMException {
		assertFalse(evt.execute(null).isPresent());
	}

	@Test
	void testExecuteWithTimeout() throws CancelFSMException, InterruptedException {
		evt.startTimeout();
		Thread.sleep(100L);
		assertEquals(tgt, evt.execute(null).get());
	}

	@Test
	void testExecuteCallFSMTimeout() throws InterruptedException {
		evt.startTimeout();
		Thread.sleep(100L);
		Mockito.verify(fsm, Mockito.times(1)).onTimeout();
	}

	@Test
	void testExecuteCallsStatesMethods() throws InterruptedException, CancelFSMException {
		evt.startTimeout();
		Thread.sleep(100L);
		evt.execute(null);
		Mockito.verify(src, Mockito.times(1)).exit();
		Mockito.verify(tgt, Mockito.times(1)).enter();
	}
}
