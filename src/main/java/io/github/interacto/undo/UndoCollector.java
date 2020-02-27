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

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * A collector of undone/redone objects.
 * @author Arnaud BLOUIN
 */
public class UndoCollector {
	/** The default undo/redo collector. */
	private static UndoCollector instance = new UndoCollector();

	/** The standard text for redo. */
	public static final String EMPTY_REDO = "redo";

	/** The standard text for undo. */
	public static final String EMPTY_UNDO = "undo";
	/** Contains the undoable objects. */
	private final Deque<Undoable> undo;
	/** Contains the redoable objects. */
	private final Deque<Undoable> redo;
	/** The maximal number of undo. */
	private int sizeMax;
	private ResourceBundle bundle;
	private final PublishSubject<Optional<Undoable>> undoPublisher;
	private final PublishSubject<Optional<Undoable>> redoPublisher;

	/**
	 * @return The single instance. Cannot be null.
	 */
	public static UndoCollector getInstance() {
		return instance;
	}

	/**
	 * Sets the single instance.
	 * @param newInstance The new single instance. Nothing done if null.
	 */
	public static void setInstance(final UndoCollector newInstance) {
		if(newInstance != null) {
			instance = newInstance;
		}
	}

	/**
	 * Creates the undo collector.
	 */
	public UndoCollector() {
		super();
		undo = new ArrayDeque<>();
		redo = new ArrayDeque<>();
		sizeMax = 30;
		undoPublisher = PublishSubject.create();
		redoPublisher = PublishSubject.create();
	}

	/**
	 * A stream for observing changes regarding the last undoable object.
	 * @return An observable value of optional undoable objects: if empty, this means
	 * that no undoable object are stored anymore.
	 */
	public Observable<Optional<Undoable>> undos() {
		return undoPublisher;
	}

	/**
	 * A stream for observing changes regarding the last redoable object.
	 * @return An observable value of optional redoable objects: if empty, this means
	 * that no redoable object are stored anymore.
	 */
	public Observable<Optional<Undoable>> redos() {
		return redoPublisher;
	}


	/**
	 * Removes all the undoable objects of the collector.
	 */
	public void clear() {
		if(!undo.isEmpty()) {
			undo.clear();
			undoPublisher.onNext(Optional.empty());
		}
		clearRedo();
	}


	private void clearRedo() {
		if(!redo.isEmpty()) {
			redo.clear();
			redoPublisher.onNext(Optional.empty());
		}
	}


	/**
	 * Adds an undoable object to the collector.
	 * @param undoable The undoable object to add.
	 */
	public void add(final Undoable undoable) {
		if(undoable != null && sizeMax > 0) {
			if(undo.size() == sizeMax) {
				undo.removeLast();
			}

			undo.push(undoable);
			undoPublisher.onNext(Optional.of(undoable));
			// The redoable objects must be removed.
			clearRedo();
		}
	}


	/**
	 * Undoes the last undoable object.
	 */
	public void undo() {
		if(!undo.isEmpty()) {
			final Undoable undoable = undo.pop();

			undoable.undo();
			redo.push(undoable);
			undoPublisher.onNext(getLastUndo());
			redoPublisher.onNext(Optional.of(undoable));
		}
	}


	/**
	 * Redoes the last undoable object.
	 */
	public void redo() {
		if(!redo.isEmpty()) {
			final Undoable undoable = redo.pop();

			undoable.redo();
			undo.push(undoable);
			undoPublisher.onNext(Optional.of(undoable));
			redoPublisher.onNext(getLastRedo());
		}
	}


	/**
	 * @return The last undoable object name or null if there is no last object.
	 */
	public Optional<String> getLastUndoMessage() {
		return undo.isEmpty() ? Optional.empty() : Optional.ofNullable(undo.peek().getUndoName(bundle));
	}


	/**
	 * @return The last redoable object name or null if there is no last object.
	 */
	public Optional<String> getLastRedoMessage() {
		return redo.isEmpty() ? Optional.empty() : Optional.ofNullable(redo.peek().getUndoName(bundle));
	}


	/**
	 * @return The last undoable object or null if there is no last object.
	 */
	public Optional<Undoable> getLastUndo() {
		return Optional.ofNullable(undo.peek());
	}


	/**
	 * @return The last redoable object or null if there is no last object.
	 */
	public Optional<Undoable> getLastRedo() {
		return Optional.ofNullable(redo.peek());
	}


	/**
	 * @return The max number of saved undoable objects.
	 */
	public int getSizeMax() {
		return sizeMax;
	}


	/**
	 * @param max The max number of saved undoable objects. Must be great than 0.
	 */
	public void setSizeMax(final int max) {
		if(max >= 0) {
			boolean removed = false;
			for(int i = 0, nb = undo.size() - max; i < nb; i++) {
				undo.removeLast();
				removed = true;
			}
			if(removed && undo.isEmpty()) {
				undoPublisher.onNext(Optional.empty());
			}
			this.sizeMax = max;
		}
	}

	/**
	 * @return The stack of saved undoable objects.
	 */
	public Deque<Undoable> getUndo() {
		return undo;
	}

	/**
	 * @return The stack of saved redoable objects
	 */
	public Deque<Undoable> getRedo() {
		return redo;
	}

	/**
	 * Sets the language bundle to be used by the undo redo manager.
	 * @param bundle The language bundle. Can be null.
	 */
	public void setBundle(final ResourceBundle bundle) {
		this.bundle = bundle;
	}
}
