package org.malai.kevoree.examples.drawingEditor.model.impl;


import org.malai.kevoree.examples.drawingEditor.model.interfaces.IPoint;
import org.malai.kevoree.examples.drawingEditor.model.interfaces.IRhombus;
import org.malai.kevoree.examples.drawingEditor.model.interfaces.IShape;

/**
 * Defines a model of a rhombus.<br>
 * <br>
 * This file is part of LaTeXDraw.<br>
 * Copyright (c) 2005-2012 Arnaud BLOUIN<br>
 * <br>
 * LaTeXDraw is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * <br>
 * LaTeXDraw is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br>
 * <br>
 * 07/05/2009<br>
 * @author Arnaud BLOUIN
 * @version 3.0
 * @since 3.0
 */
class LRhombus extends LRectangularShape implements IRhombus {
	/**
	 * Creates a rhombus at the position (0,0).
	 * @param uniqueID True: the rhombus will have a unique ID.
	 */
	protected LRhombus(final boolean uniqueID) {
		this(new LPoint(), 1, 1, uniqueID);
	}


	/**
	 * Creates a rhombus.
	 * @param pos The north-west point of the rhombus.
	 * @param width The width of the rhombus.
	 * @param height The height of the rhombus.
	 * @param uniqueID True: the rhombus will have a unique ID.
	 * @throws IllegalArgumentException If the width or the height is not valid.
	 */
	protected LRhombus(final IPoint pos, final double width, final double height, final boolean uniqueID) {
		this(pos, pos==null ? null : new LPoint(pos.getX()+width, pos.getY()+height), uniqueID);
	}


	/**
	 * Creates a rhombus.
	 * @param tl The top left point of the rhombus.
	 * @param br The bottom right point of the rhombus.
	 * @param uniqueID True: the rhombus will have a unique ID.
	 */
	protected LRhombus(final IPoint tl, final IPoint br, final boolean uniqueID) {
		super(tl, br, uniqueID);
	}
	
	
	@Override
	public IRhombus duplicate() {
		final IShape sh = super.duplicate();
		return sh instanceof IRhombus ? (IRhombus)sh : null;
	}
}

