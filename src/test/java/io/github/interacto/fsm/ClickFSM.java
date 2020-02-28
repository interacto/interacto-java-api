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

public class ClickFSM extends FSM<String> {
	public ClickFSM() {
		super();
		final StdState<String> pressed = new StdState<>(this, "pressed");
		final TerminalState<String> released = new TerminalState<>(this, "released");
		final CancellingState<String> moved = new CancellingState<>(this, "moved");
		addState(pressed);
		addState(pressed);
		addState(moved);
		new Transition<>(initState, pressed) {
			@Override
			protected boolean accept(final String event) {
				return true;
			}
			@Override
			protected boolean isGuardOK(final String event) {
				return "press".equals(event);
			}

			@Override
			public Set<Object> getAcceptedEvents() {
				return Set.of("press");
			}
		};
		new Transition<>(pressed, released) {
			@Override
			protected boolean accept(final String event) {
				return true;
			}
			@Override
			protected boolean isGuardOK(final String event) {
				return "release".equals(event);
			}

			@Override
			public Set<Object> getAcceptedEvents() {
				return Set.of("release");
			}
		};
		new Transition<>(pressed, moved) {
			@Override
			protected boolean accept(final String event) {
				return true;
			}
			@Override
			protected boolean isGuardOK(final String event) {
				return "move".equals(event);
			}

			@Override
			public Set<Object> getAcceptedEvents() {
				return Set.of("move");
			}
		};
	}
}
