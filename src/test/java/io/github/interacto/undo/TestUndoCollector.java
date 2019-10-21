package io.github.interacto.undo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUndoCollector {
	Undoable undoable;

	@BeforeEach
	public void setUp() {
		UndoCollector.INSTANCE.clear();
		UndoCollector.INSTANCE.setSizeMax(10);
		undoable = Mockito.mock(Undoable.class);
	}

	@Test
	void testUndoCallundo() {
		UndoCollector.INSTANCE.add(undoable);
		UndoCollector.INSTANCE.undo();
		Mockito.verify(undoable, Mockito.times(1)).undo();
	}

	@Test
	void testRedowithUndoDonewithGlobalUndoable() {
		UndoCollector.INSTANCE.add(undoable);
		UndoCollector.INSTANCE.undo();
		UndoCollector.INSTANCE.redo();
		Mockito.verify(undoable, Mockito.times(1)).undo();
		Mockito.verify(undoable, Mockito.times(1)).redo();
	}

	@Test
	void testRedowhenRedoEmpty() {
		UndoCollector.INSTANCE.redo();
		Mockito.verify(undoable, Mockito.never()).redo();
	}


	@Test
	void testUndowhenUndoEmpty() {
		UndoCollector.INSTANCE.undo();
		Mockito.verify(undoable, Mockito.never()).undo();
	}


	@Test
	void testRedoCallredo() {
		UndoCollector.INSTANCE.add(undoable);
		UndoCollector.INSTANCE.undo();
		UndoCollector.INSTANCE.redo();
		Mockito.verify(undoable, Mockito.times(1)).redo();
		assertEquals(undoable, UndoCollector.INSTANCE.getLastUndo().get());
	}

	@Test
	void testSetSizeMaxKO() {
		UndoCollector.INSTANCE.setSizeMax(-1);
		UndoCollector.INSTANCE.add(undoable);
		assertEquals(undoable, UndoCollector.INSTANCE.getLastUndo().orElseThrow());
	}

	@Test
	void testSetSizeMax0KO() {
		UndoCollector.INSTANCE.setSizeMax(0);
		UndoCollector.INSTANCE.add(undoable);
		assertTrue(UndoCollector.INSTANCE.getLastUndo().isEmpty());
	}


	@Test
	void testAddUndoablewith0SizeUndoable() {
		UndoCollector.INSTANCE.setSizeMax(0);
		UndoCollector.INSTANCE.add(undoable);
		assertTrue(UndoCollector.INSTANCE.getUndo().isEmpty());
		assertTrue(UndoCollector.INSTANCE.getRedo().isEmpty());
	}

	@Test
	void testAddUndoablewithNullUndoable() {
		UndoCollector.INSTANCE.setSizeMax(5);
		UndoCollector.INSTANCE.add(null);
		assertTrue(UndoCollector.INSTANCE.getUndo().isEmpty());
		assertTrue(UndoCollector.INSTANCE.getRedo().isEmpty());
	}

	@Test
	void testAddUndoablewithLimitedUndoSize() {
		final Undoable undoable2 = Mockito.mock(Undoable.class);
		UndoCollector.INSTANCE.setSizeMax(1);
		UndoCollector.INSTANCE.add(undoable);
		UndoCollector.INSTANCE.add(undoable2);
		assertEquals(1, UndoCollector.INSTANCE.getUndo().size());
		assertEquals(undoable2, UndoCollector.INSTANCE.getUndo().getFirst());
	}


	@Test
	void testGetRedos() {
		assertNotNull(UndoCollector.INSTANCE.getRedo());
	}

	@Test
	void testGetUndos() {
		assertNotNull(UndoCollector.INSTANCE.getUndo());
	}


	@Test
	void testSizeMaxMutatorsUndoableRemoved() {
		UndoCollector.INSTANCE.setSizeMax(5);
		UndoCollector.INSTANCE.add(undoable);
		assertTrue(UndoCollector.INSTANCE.getLastUndo().isPresent());
	}

	@Test
	void testSizeMaxMutatorsUndoableRemovedWhen0() {
		UndoCollector.INSTANCE.setSizeMax(5);
		UndoCollector.INSTANCE.add(undoable);
		UndoCollector.INSTANCE.setSizeMax(0);
		assertFalse(UndoCollector.INSTANCE.getLastUndo().isPresent());
	}

	@Test
	void testSizeMaxMutatorsSizeOK() {
		UndoCollector.INSTANCE.setSizeMax(21);
		assertEquals(21, UndoCollector.INSTANCE.getSizeMax());
	}

	@Test
	void testSizeMaxMutatorsSizeKO() {
		UndoCollector.INSTANCE.setSizeMax(5);
		UndoCollector.INSTANCE.setSizeMax(-1);
		assertEquals(5, UndoCollector.INSTANCE.getSizeMax());
	}

	@Test
	void testGetLastRedoNothingStart() {
		assertFalse(UndoCollector.INSTANCE.getLastRedo().isPresent());
	}

	@Test
	void testGetLastRedoNothingOnNewUndoable() {
		UndoCollector.INSTANCE.add(undoable);
		assertFalse(UndoCollector.INSTANCE.getLastRedo().isPresent());
	}

	@Test
	void testGetLastRedoOKOnRedo() {
		UndoCollector.INSTANCE.add(undoable);
		UndoCollector.INSTANCE.undo();
		assertEquals(undoable, UndoCollector.INSTANCE.getLastRedo().get());
	}

	@Test
	void testGetLastUndoNothingAtStart() {
		assertFalse(UndoCollector.INSTANCE.getLastUndo().isPresent());
	}

	@Test
	void testGetLastUndoOKOnAdd() {
		UndoCollector.INSTANCE.add(undoable);
		assertEquals(undoable, UndoCollector.INSTANCE.getLastUndo().get());
	}

	@Test
	void testGetLastUndoMessageNothingOnStart() {
		assertFalse(UndoCollector.INSTANCE.getLastUndoMessage().isPresent());
	}

	@Test
	void testGetLastUndoMessageOK() {
		Mockito.when(undoable.getUndoName(Mockito.any())).thenReturn("undoredomsg");
		UndoCollector.INSTANCE.add(undoable);
		assertEquals("undoredomsg", UndoCollector.INSTANCE.getLastUndoMessage().get());
	}

	@Test
	void testGetLastRedoMessageNothingOnStart() {
		assertFalse(UndoCollector.INSTANCE.getLastRedoMessage().isPresent());
	}

	@Test
	void testGetLastRedoMessageOK() {
		Mockito.when(undoable.getUndoName(Mockito.any())).thenReturn("undoredomsg");
		UndoCollector.INSTANCE.add(undoable);
		UndoCollector.INSTANCE.undo();
		assertEquals("undoredomsg", UndoCollector.INSTANCE.getLastRedoMessage().get());
	}

	@Test
	void testClear() {
		UndoCollector.INSTANCE.add(undoable);
		UndoCollector.INSTANCE.add(Mockito.mock(Undoable.class));
		UndoCollector.INSTANCE.undo();
		UndoCollector.INSTANCE.clear();
		assertFalse(UndoCollector.INSTANCE.getLastRedo().isPresent());
		assertFalse(UndoCollector.INSTANCE.getLastUndo().isPresent());
	}

	@Test
	void testUndosOK() {
		assertNotNull(UndoCollector.INSTANCE.undos());
	}

	@Test
	void testRedosOK() {
		assertNotNull(UndoCollector.INSTANCE.redos());
	}

	@Test
	void testUndosAdded() {
		final List<Optional<Undoable>> undos = new ArrayList<>();
		final var disposable = UndoCollector.INSTANCE.undos().subscribe(undos::add);
		UndoCollector.INSTANCE.add(undoable);
		disposable.dispose();
		assertEquals(1, undos.size());
		assertEquals(undoable, undos.get(0).orElseThrow());
	}

	@Test
	void testUndoRedoAdded() {
		final List<Optional<Undoable>> undos = new ArrayList<>();
		final List<Optional<Undoable>> redos = new ArrayList<>();
		final var disposable1 = UndoCollector.INSTANCE.undos().subscribe(undos::add);
		final var disposable2 = UndoCollector.INSTANCE.redos().subscribe(redos::add);
		UndoCollector.INSTANCE.add(undoable);
		UndoCollector.INSTANCE.undo();
		disposable1.dispose();
		disposable2.dispose();
		assertEquals(2, undos.size());
		assertTrue(undos.get(1).isEmpty());
		assertEquals(1, redos.size());
		assertEquals(undoable, redos.get(0).orElseThrow());
	}
}
