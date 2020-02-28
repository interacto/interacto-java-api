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

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestOutputStateImpl {
	OutputStateImpl<String> state;
	FSM<String> fsm;

	@BeforeEach
	void setUp() {
		Mockito.mock(FSM.class);
		state = new OutputStateImpl<>(fsm, "os") {
			@Override
			public void exit() {
			}
		};
	}

	@Test
	void testGetTransitions() {
		final List<Transition<String>> tr = state.getTransitions();
		assertNotNull(tr);
		assertThrows(UnsupportedOperationException.class, () -> tr.clear());
	}

	@Test
	void testAddTransitionOK() {
		final Transition<String> t1 = Mockito.mock(Transition.class);
		final Transition<String> t2 = Mockito.mock(Transition.class);
		state.addTransition(t2);
		state.addTransition(t1);
		assertEquals(List.of(t2, t1), state.getTransitions());
	}

	@Test
	void testAddTransitionKO() {
		state.addTransition(null);
		assertTrue(state.getTransitions().isEmpty());
	}

	@Test
	void testUninstall() {
		final Transition<String> t1 = Mockito.mock(Transition.class);
		final Transition<String> t2 = Mockito.mock(Transition.class);
		state.addTransition(t1);
		state.addTransition(t2);
		state.uninstall();
		Mockito.verify(t1, Mockito.times(1)).uninstall();
		Mockito.verify(t2, Mockito.times(1)).uninstall();
		assertTrue(state.getTransitions().isEmpty());
	}
}
