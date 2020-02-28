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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTransition {
	Transition<StubEvent> tr;
	StdState<StubEvent> state1;
	StdState<StubEvent> state2;

	@BeforeEach
	void setUp() {
		final FSM<StubEvent> fsm = new FSM<>();
		state1 = new StdState<>(fsm, "s1");
		state2 = new StdState<>(fsm, "s2");
	}

	@Nested
	class TestBadConstructorCall {
		@Test
		void testNullSrc() {
			assertThrows(IllegalArgumentException.class, () -> new StubTransitionOK(null, state2));
		}

		@Test
		void testNullTgt() {
			assertThrows(IllegalArgumentException.class, () -> new StubTransitionOK(state1, null));
		}
	}

	@Nested
	class TestConstructorOK {
		@BeforeEach
		void setUp() {
			tr = new StubTransitionOK(state1, state2);
		}

		@Test
		void testGoodSrc() {
			assertEquals(state1, tr.src);
		}

		@Test
		void testGoodTgt() {
			assertEquals(state2, tr.tgt);
		}

		@Test
		void testSrcStateTransitionAdded() {
			assertEquals(1, state1.transitions.size());
			assertEquals(tr, state1.transitions.get(0));
		}

		@Test
		void testUninstall() {
			final List<Throwable> errors = new ArrayList<>();
			final Disposable disposable = ErrorCatcher.getInstance().getErrors().subscribe(errors::add);
			tr.uninstall();
			disposable.dispose();
			assertTrue(errors.isEmpty());
		}
	}
}
