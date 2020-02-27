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

public interface OutputState<E> extends State<E> {
	void exit() throws CancelFSMException;

	/**
	 * Asks to the state to process of the given event.
	 * @param event The event to process. Can be null.
	 */
	default boolean process(final E event) {
		for(final Transition<E> tr : getTransitions()) {
			try {
				if(tr.execute(event).isPresent()) {
					return true;
				}
			}catch(final CancelFSMException ignored) {
				// Already processed
			}
		}
		return false;
	}

	List<Transition<E>> getTransitions();

	void addTransition(final Transition<E> tr);
}
