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
package io.github.interacto.command.library;

import io.github.interacto.properties.Zoomable;

/**
 * A command for zooming in/out a zoomable object.
 * @author Arnaud BLOUIN
 */
public class Zoom extends PositionCommand {
	/** The object to zoom. */
	protected final Zoomable zoomable;

	/** The zooming level. */
	protected double zoomLevel;


	/**
	 * Initialises a Zoom command.
	 * @param zoomable The object to zoom.
	 */
	public Zoom(final Zoomable zoomable) {
		super();
		zoomLevel = Double.NaN;
		this.zoomable = zoomable;
	}


	@Override
	public boolean canDo() {
		return zoomable != null && zoomLevel >= zoomable.getMinZoom() && zoomLevel <= zoomable.getMaxZoom();
	}


	@Override
	protected void doCmdBody() {
		zoomable.setZoom(px, py, zoomLevel);
	}

	/**
	 * @param newZoomLevel the zoomLevel to set.
	 */
	public void setZoomLevel(final double newZoomLevel) {
		zoomLevel = newZoomLevel;
	}
}
