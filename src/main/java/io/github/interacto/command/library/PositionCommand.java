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

import io.github.interacto.command.CommandImpl;

/**
 * Defines an abstract command that move an object to the given position.
 * @author Arnaud BLOUIN
 */
public abstract class PositionCommand extends CommandImpl {
	/** The X-coordinate of the location to zoom. */
	protected double px;

	/** The Y-coordinate of the location to zoom. */
	protected double py;

	/**
	 * Creates the command.
	 */
	public PositionCommand() {
		super();
		px = Double.NaN;
		py = Double.NaN;
	}


	/**
	 * @param px The x-coordinate to set.
	 */
	public void setPx(final double px) {
		this.px = px;
	}


	/**
	 * @param py The y-coordinate to set.
	 */
	public void setPy(final double py) {
		this.py = py;
	}
}
