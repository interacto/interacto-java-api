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
package io.github.interacto.error;

import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestErrorCatcher {
	ErrorCatcher instance;

	@BeforeEach
	void setUp() {
		instance = new ErrorCatcher();
	}

	@Test
	void testNotNull() {
		assertNotNull(instance);
	}

	@Test
	void testGetSetInstanceOK() {
		final var newinstance = new ErrorCatcher();
		ErrorCatcher.setInstance(newinstance);
		assertEquals(newinstance, ErrorCatcher.getInstance());
	}

	@Test
	void testGetSetInstanceKO() {
		ErrorCatcher.setInstance(instance);
		ErrorCatcher.setInstance(null);
		assertEquals(instance, ErrorCatcher.getInstance());
	}

	@Test
	void testErrorsOK() {
		assertNotNull(instance.getErrors());
	}

	@Test
	void testErrors() {
		final List<Throwable> errors = new ArrayList<>();
		final Disposable errorStream = instance.getErrors().subscribe(errors::add);
		final var ex = new NullPointerException();
		instance.reportError(ex);
		errorStream.dispose();

		assertEquals(1, errors.size());
		assertEquals(ex, errors.get(0));
	}

	@Test
	void testErrorsKO() {
		final List<Throwable> errors = new ArrayList<>();
		final Disposable errorStream = instance.getErrors().subscribe(errors::add);
		instance.reportError(null);
		errorStream.dispose();

		assertTrue(errors.isEmpty());
	}
}
