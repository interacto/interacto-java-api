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
package io.github.interacto.binding;

/**
 * This exception must be launched when a command which is not undoable want to be undone or redone.
 * @author Arnaud BLOUIN
 */
public class MustBeUndoableCmdException extends RuntimeException {
	/**
	 * The default constructor of the exception.
	 * @param clazz The class of the command that want to be undone/redone.
	 */
	public MustBeUndoableCmdException(final Class<?> clazz) {
		super("The following command must be undoable: " + (clazz == null ? "" : " " + clazz.getName()));
	}
}
