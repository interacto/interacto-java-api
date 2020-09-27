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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestWidgetTransition {
	WidgetTransition<StubSubEvent1, StubEvent, Object> transition;

	@BeforeEach
	void setUp() {
		transition = new WidgetTransition<StubSubEvent1, StubEvent, Object>(Mockito.mock(OutputState.class), Mockito.mock(InputState.class)) {
			@Override
			protected StubSubEvent1 accept(final StubEvent event) {
				return null;
			}
			@Override
			protected boolean isGuardOK(final StubSubEvent1 event) {
				return false;
			}
			@Override
			public Set<Object> getAcceptedEvents() {
				return null;
			}
		};
	}

	@Test
	void testSetWidget() {
		final var w = new Object();
		transition.setWidget(w);
		assertEquals(w, transition.getWidget());
	}

	@Test
	void testSetWidgetNull() {
		final var w = new Object();
		transition.setWidget(w);
		transition.setWidget(null);
		assertEquals(w, transition.getWidget());
	}
}
