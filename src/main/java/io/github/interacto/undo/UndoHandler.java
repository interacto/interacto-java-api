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
package io.github.interacto.undo;

/**
 * This handler must help object that want to be aware of undone/redone event (for instance, to update some widgets).
 * @author Arnaud BLOUIN
 */
public interface UndoHandler {
	/**
	 * Notifies the handler that the stored undoable objects have been all removed.
	 */
	default void onUndoableCleared() {
	}

	/**
	 * Actions to do when an undoable object is added to the undo register.
	 * @param undoable The undoable object added to the undo register.
	 */
	default void onUndoableAdded(final Undoable undoable) {
	}

	/**
	 * Actions to do when an undoable object is undone.
	 * @param undoable The undone object.
	 */
	default void onUndoableUndo(final Undoable undoable) {
	}

	/**
	 * Actions to do when an undoable object is redone.
	 * @param undoable The redone object.
	 */
	default void onUndoableRedo(final Undoable undoable) {
	}
}
