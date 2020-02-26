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
package io.github.interacto.command.library;

import io.github.interacto.properties.Zoomable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestZoom {
	Zoom cmd;
	Zoomable zoomable;

	@BeforeEach
	void setUp() {
		zoomable = Mockito.mock(Zoomable.class);
		Mockito.when(zoomable.getMinZoom()).thenReturn(-1d);
		Mockito.when(zoomable.getMaxZoom()).thenReturn(10d);
		cmd = new Zoom(zoomable);
	}

	@Test
	void testCannotDo() {
		cmd = new Zoom(null);
		assertFalse(cmd.canDo());
	}

	@Test
	void testCannotDoZoomLevelBad() {
		assertFalse(cmd.canDo());
	}

	@ParameterizedTest
	@ValueSource(doubles = {-1.01, 10.01, Double.NaN})
	void testCannotDoZoomLevel(final double value) {
		cmd.setZoomLevel(value);
		assertFalse(cmd.canDo());
	}

	@ParameterizedTest
	@ValueSource(doubles = {9.99, -0.99, -1, 10, 0})
	void testCanDo(final double value) {
		cmd.setZoomLevel(value);
		assertTrue(cmd.canDo());
	}

	@Test
	void testDoWithPositions() {
		cmd.setZoomLevel(2);
		cmd.setPx(99);
		cmd.setPy(-12);
		cmd.doIt();
		Mockito.verify(zoomable, Mockito.times(1)).setZoom(99, -12, 2);
	}

	@Test
	void testDoNoPosition() {
		cmd.setZoomLevel(3);
		cmd.doIt();
		Mockito.verify(zoomable, Mockito.times(1)).setZoom(Double.NaN, Double.NaN, 3);
	}
}
