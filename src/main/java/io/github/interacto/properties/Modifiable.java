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
package io.github.interacto.properties;

/**
 * Defines an interface for object that can be modified and set as modified. This interface can also be used
 * to notify objects that the Modifiable object as been modified.
 * @author Arnaud BLOUIN
 */
public interface Modifiable {
	/**
	 * Sets the Modifiable object as modified.
	 * @param modified True: the element is will tagged as modified.
	 */
	void setModified(final boolean modified);

	/**
	 * @return True: the object has been modified. False otherwise.
	 */
	boolean isModified();
}
