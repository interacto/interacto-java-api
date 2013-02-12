package org.malai.kevoree.examples.drawingEditor.model.impl;

import org.malai.kevoree.examples.drawingEditor.model.interfaces.*;

import java.util.ArrayList;


/**
 * Defines a view of a polyline.<br>
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
 * 02/13/2008<br>
 * @author Arnaud BLOUIN
 * @version 3.0
 * @since 3.0
 */
class LPolyline extends LPolygon implements IPolyline {
	/**
	 * Creates a model with no point.
	 * @param uniqueID True: the shape will have a unique ID.
	 */
	protected LPolyline(final boolean uniqueID) {
		super(uniqueID);

		arrows = new ArrayList<IArrow>();
		arrows.add(new LArrow(this));
		arrows.add(new LArrow(this));
	}



	/**
	 * Creates a model with two points.
	 * @param uniqueID True: the shape will have a unique ID.
	 */
	protected LPolyline(final IPoint point, final IPoint point2, final boolean uniqueID) {
		this(uniqueID);

		if(point==null || point2==null)
			throw new IllegalArgumentException();

		addPoint(point);
		addPoint(point2);
	}


	@Override
	public IPolyline duplicate() {
		final IShape sh = super.duplicate();
		return sh instanceof IPolyline ? (IPolyline)sh : null;
	}


	@Override
	public ILine getArrowLine(final IArrow arrow) {
		final int index = arrows.indexOf(arrow);
		final ILine line;

		switch(index) {
			case 0: line = new LLine(points.get(0), points.get(1)); break;
			case 1: line = new LLine(points.get(points.size()-1), points.get(points.size()-2)); break;
			case -1 :
			default : line = null;
		}

		return line;
	}


	@Override
	public boolean isFillable() {
		return getNbPoints()>2;
	}



	@Override
	public boolean isArrowable() {
		return true;
	}


	@Override
	public boolean shadowFillsShape() {
		return false;
	}
}
