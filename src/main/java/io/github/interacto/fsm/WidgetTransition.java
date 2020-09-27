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

/**
 * This transition must be used to use a widget within an interaction.
 * @author Arnaud BLOUIN
 */
public abstract class WidgetTransition<E extends E0, E0, T> extends Transition<E, E0> {
	/** The pressed button. */
	protected T widget;

	/**
	 * Creates the transition.
	 * @param srcState The source state of the transition.
	 * @param tgtState The output state of the transition.
	 * @throws IllegalArgumentException If one of the states is null.
	 */
	public WidgetTransition(final OutputState<E0> srcState, final InputState<E0> tgtState) {
		super(srcState, tgtState);
	}

	/**
	 * @return The widget used.
	 */
	public T getWidget() {
		return widget;
	}

	/**
	 * Sets the widget.
	 * @param widget The widget to set. Nothing done if null.
	 */
	public void setWidget(final T widget) {
		if(widget != null) {
			this.widget = widget;
		}
	}
}
