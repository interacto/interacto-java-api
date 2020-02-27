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
package io.github.interacto.undo;

import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUndoCollector {
	Undoable undoable;
	UndoCollector instance;

	@BeforeEach
	public void setUp() {
		instance = new UndoCollector();
		instance.setSizeMax(10);
		undoable = Mockito.mock(Undoable.class);
	}

	@Test
	void testGetSetInstanceKO() {
		UndoCollector.setInstance(instance);
		UndoCollector.setInstance(null);
		assertSame(instance, UndoCollector.getInstance());
	}

	@Test
	void testGetSetInstanceOK() {
		final var newinstance = new UndoCollector();
		UndoCollector.setInstance(newinstance);
		assertSame(newinstance, UndoCollector.getInstance());
	}

	@Test
	void testUndoCallundo() {
		instance.add(undoable);
		instance.undo();
		Mockito.verify(undoable, Mockito.times(1)).undo();
	}

	@Test
	void testRedowithUndoDonewithGlobalUndoable() {
		instance.add(undoable);
		instance.undo();
		instance.redo();
		Mockito.verify(undoable, Mockito.times(1)).undo();
		Mockito.verify(undoable, Mockito.times(1)).redo();
	}

	@Test
	void testRedowhenRedoEmpty() {
		instance.redo();
		Mockito.verify(undoable, Mockito.never()).redo();
	}


	@Test
	void testUndowhenUndoEmpty() {
		instance.undo();
		Mockito.verify(undoable, Mockito.never()).undo();
	}


	@Test
	void testRedoCallredo() {
		instance.add(undoable);
		instance.undo();
		instance.redo();
		Mockito.verify(undoable, Mockito.times(1)).redo();
		assertEquals(undoable, instance.getLastUndo().get());
	}

	@Test
	void testSetSizeMaxKO() {
		instance.setSizeMax(-1);
		instance.add(undoable);
		assertEquals(undoable, instance.getLastUndo().orElseThrow());
	}

	@Test
	void testSetSizeMax0KO() {
		instance.setSizeMax(0);
		instance.add(undoable);
		assertTrue(instance.getLastUndo().isEmpty());
	}


	@Test
	void testAddUndoablewith0SizeUndoable() {
		instance.setSizeMax(0);
		instance.add(undoable);
		assertTrue(instance.getUndo().isEmpty());
		assertTrue(instance.getRedo().isEmpty());
	}

	@Test
	void testAddUndoablewithNullUndoable() {
		instance.setSizeMax(5);
		instance.add(null);
		assertTrue(instance.getUndo().isEmpty());
		assertTrue(instance.getRedo().isEmpty());
	}

	@Test
	void testAddUndoablewithLimitedUndoSize() {
		final Undoable undoable2 = Mockito.mock(Undoable.class);
		instance.setSizeMax(1);
		instance.add(undoable);
		instance.add(undoable2);
		assertEquals(1, instance.getUndo().size());
		assertEquals(undoable2, instance.getUndo().getFirst());
	}


	@Test
	void testGetRedos() {
		assertNotNull(instance.getRedo());
	}

	@Test
	void testGetUndos() {
		assertNotNull(instance.getUndo());
	}


	@Test
	void testSizeMaxMutatorsUndoableRemoved() {
		instance.setSizeMax(5);
		instance.add(undoable);
		assertTrue(instance.getLastUndo().isPresent());
	}

	@Test
	void testSizeMaxRemovedWhen0() {
		final List<Optional<Undoable>> undos = new ArrayList<>();
		instance.setSizeMax(5);
		instance.add(undoable);
		final Disposable undosStream = instance.undos().subscribe(undos::add);
		instance.setSizeMax(0);
		undosStream.dispose();
		assertFalse(instance.getLastUndo().isPresent());
		assertEquals(1, undos.size());
		assertTrue(undos.get(0).isEmpty());
	}

	@Test
	void testSizeMaxRemovedWhen1() {
		final List<Optional<Undoable>> undos = new ArrayList<>();
		instance.setSizeMax(5);
		instance.add(Mockito.mock(Undoable.class));
		instance.add(undoable);
		final Disposable undosStream = instance.undos().subscribe(undos::add);
		instance.setSizeMax(1);
		undosStream.dispose();
		assertTrue(instance.getLastUndo().isPresent());
		assertEquals(undoable, instance.getLastUndo().get());
		assertTrue(undos.isEmpty());
	}

	@Test
	void testSizeMaxMutatorsSizeOK() {
		instance.setSizeMax(21);
		assertEquals(21, instance.getSizeMax());
	}

	@Test
	void testSizeMaxMutatorsSizeKO() {
		instance.setSizeMax(5);
		instance.setSizeMax(-1);
		assertEquals(5, instance.getSizeMax());
	}

	@Test
	void testGetLastRedoNothingStart() {
		assertFalse(instance.getLastRedo().isPresent());
	}

	@Test
	void testGetLastRedoNothingOnNewUndoable() {
		instance.add(undoable);
		assertFalse(instance.getLastRedo().isPresent());
	}

	@Test
	void testGetLastRedoOKOnRedo() {
		instance.add(undoable);
		instance.undo();
		assertEquals(undoable, instance.getLastRedo().get());
	}

	@Test
	void testGetLastUndoNothingAtStart() {
		assertFalse(instance.getLastUndo().isPresent());
	}

	@Test
	void testGetLastUndoOKOnAdd() {
		instance.add(undoable);
		assertEquals(undoable, instance.getLastUndo().get());
	}

	@Test
	void testGetLastUndoMessageNothingOnStart() {
		assertFalse(instance.getLastUndoMessage().isPresent());
	}

	@Test
	void testGetLastUndoMessageOK() {
		Mockito.when(undoable.getUndoName(Mockito.any())).thenReturn("undoredomsg");
		instance.add(undoable);
		assertEquals("undoredomsg", instance.getLastUndoMessage().get());
	}

	@Test
	void testGetLastRedoMessageNothingOnStart() {
		assertFalse(instance.getLastRedoMessage().isPresent());
	}

	@Test
	void testGetLastRedoMessageOK() {
		Mockito.when(undoable.getUndoName(Mockito.any())).thenReturn("undoredomsg");
		instance.add(undoable);
		instance.undo();
		assertEquals("undoredomsg", instance.getLastRedoMessage().get());
	}

	@Test
	void testClear() {
		instance.add(undoable);
		instance.add(Mockito.mock(Undoable.class));
		instance.undo();
		instance.clear();
		assertFalse(instance.getLastRedo().isPresent());
		assertFalse(instance.getLastUndo().isPresent());
	}

	@Test
	void testUndosOK() {
		assertNotNull(instance.undos());
	}

	@Test
	void testRedosOK() {
		assertNotNull(instance.redos());
	}

	@Test
	void testUndosAdded() {
		final List<Optional<Undoable>> undos = new ArrayList<>();
		final var disposable = instance.undos().subscribe(undos::add);
		instance.add(undoable);
		disposable.dispose();
		assertEquals(1, undos.size());
		assertEquals(undoable, undos.get(0).orElseThrow());
	}

	@Test
	void testUndoRedoAdded() {
		final List<Optional<Undoable>> undos = new ArrayList<>();
		final List<Optional<Undoable>> redos = new ArrayList<>();
		final var disposable1 = instance.undos().subscribe(undos::add);
		final var disposable2 = instance.redos().subscribe(redos::add);
		instance.add(undoable);
		instance.undo();
		disposable1.dispose();
		disposable2.dispose();
		assertEquals(2, undos.size());
		assertTrue(undos.get(1).isEmpty());
		assertEquals(1, redos.size());
		assertEquals(undoable, redos.get(0).orElseThrow());
	}

	@Test
	void testSetBundleOKUndo() {
		final ResourceBundle bundle = Mockito.mock(ResourceBundle.class);
		instance.setBundle(bundle);
		instance.add(undoable);
		instance.getLastUndoMessage();
		Mockito.verify(undoable, Mockito.times(1)).getUndoName(bundle);
	}

	@Test
	void testSetBundleOKRedo() {
		final ResourceBundle bundle = Mockito.mock(ResourceBundle.class);
		instance.setBundle(bundle);
		instance.add(undoable);
		instance.undo();
		instance.getLastRedoMessage();
		Mockito.verify(undoable, Mockito.times(1)).getUndoName(bundle);
	}
}
