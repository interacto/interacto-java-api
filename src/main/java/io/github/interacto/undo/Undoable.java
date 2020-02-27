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

import java.util.ResourceBundle;

/**
 * An interface for undoable objects.
 * @author Arnaud BLOUIN
 */
public interface Undoable {
	/**
	 * Cancels the command.
	 */
	void undo();

	/**
	 * Redoes the cancelled command.
	 */
	void redo();

	/**
	 * @return The name of the undo command.
	 * @param bundle The language bundle. Can be null.
	 */
	String getUndoName(final ResourceBundle bundle);
}
