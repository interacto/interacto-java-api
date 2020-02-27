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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestStateImpl {
	StateImpl<StubEvent> state;
	FSM<StubEvent> fsm;

	@BeforeEach
	void setUp() {
		fsm = new FSM<>();
		state = new StdState<>(fsm, "s1");
	}

	@Test
	void testFSM() {
		assertEquals(fsm, state.fsm);
	}

	@Test
	void testName() {
		assertEquals("s1", state.getName());
	}

	@Test
	void testToStringNotNull() {
		assertNotNull(state.toString());
	}
}
