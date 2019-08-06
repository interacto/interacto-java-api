/*
 * Interacto
 * Copyright (C) 2019 Arnaud Blouin
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
package io.github.interacto.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple value property. We do not use the JFX one (SimpleObjectProperty) since
 * this library is transpiled to other languages where SimpleObjectProperty may not be supported.
 * This also avoids a dependency to JFX.
 * @param <T> The type of the contained object.
 */
public class ObsValue<T> {
	private T value;
	private final Set<Change<T>> handlers;

	public ObsValue() {
		this(null);
	}

	public ObsValue(final T value) {
		super();
		this.value = value;
		handlers = new HashSet<>();
	}

	public T get() {
		return value;
	}

	public void set(final T value) {
		final T oldValue = this.value;
		this.value = value;
		notifyChange(oldValue, value);
	}

	private void notifyChange(final T oldValue, final T newValue) {
		handlers.forEach(handler -> handler.onChange(oldValue, newValue));
	}

	public void obs(final Change<T> handler) {
		if(handler != null) {
			handlers.add(handler);
		}
	}

	public void unobs(final Change<T> handler) {
		if(handler != null) {
			handlers.remove(handler);
		}
	}

	public void unobsAll() {
		handlers.clear();
	}

	public interface Change<T> {
		void onChange(final T oldValue, final T newValue);
	}
}
