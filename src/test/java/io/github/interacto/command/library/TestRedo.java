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

import io.github.interacto.undo.UndoCollector;
import io.github.interacto.undo.Undoable;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRedo {
	Redo cmd;
	UndoCollector collector;
	UndoCollector mementoCollector;

	@BeforeEach
	void setUp() {
		collector = Mockito.mock(UndoCollector.class);
		mementoCollector = UndoCollector.getInstance();
		UndoCollector.setInstance(collector);
		cmd = new Redo();
	}

	@Test
	void testCannotDo() {
		Mockito.when(collector.getLastRedo()).thenReturn(Optional.empty());
		assertFalse(cmd.canDo());
	}

	@Nested
	class CanDo {
		Undoable undoable;

		@BeforeEach
		void setUp() {
			undoable = Mockito.mock(Undoable.class);
			Mockito.when(collector.getLastRedo()).thenReturn(Optional.of(undoable));
		}

		@Test
		void testCanDo() {
			assertTrue(cmd.canDo());
		}

		@Test
		void testDo() {
			cmd.doIt();
			Mockito.verify(collector, Mockito.times(1)).redo();
		}

		@Test
		void testHadEffects() {
			cmd.doIt();
			cmd.done();
			assertTrue(cmd.hadEffect());
		}
	}

	@AfterEach
	void tearDown() {
		UndoCollector.setInstance(mementoCollector);
	}
}
