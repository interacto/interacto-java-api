package org.malai.action.library;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * This action moves a camera by moving the scroll bars of a scroll panel.<br>
 * The use of this action can be made when an object has a lot of properties which modification
 * follow the same process. Thus, a same action can be used to modify all the properties. To do
 * so, a enumeration of the properties can be defined and used into the action to specify which
 * property will be modified by the current action instance.
 * <br>
 * This file is part of Malai<br>
 * Copyright (c) 2009-2012 Arnaud BLOUIN<br>
 * <br>
 *  Malai is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  any later version.<br>
 * <br>
 *  Malai is distributed without any warranty; without even the
 *  implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 *  PURPOSE. See the GNU General Public License for more details.<br>
 * <br>
 * @author Arnaud Blouin
 * @since 0.2
 */
public class MoveCamera extends PositionAction {
	/** The scroll panel to modify. */
	protected JScrollPane scrollPane;


	/**
	 * Creates the action.
	 */
	public MoveCamera() {
		super();
	}


	@Override
	public boolean isRegisterable() {
		return false;
	}


	@Override
	protected void doActionBody() {
		moveScrollBar(scrollPane.getHorizontalScrollBar(), (int)px);
		moveScrollBar(scrollPane.getVerticalScrollBar(), (int)py);
	}


	/**
	 * Move the given scroll bar.
	 * @param bar The scroll bar to move.
	 * @param gap The translation to perform.
	 */
	private void moveScrollBar(final JScrollBar bar, final int gap) {
		bar.setValue(bar.getValue()+gap);
	}


	@Override
	public boolean canDo() {
		return super.canDo() && scrollPane!=null;
	}


	/**
	 * Sets the scroll panel to modify.
	 * @param scrollpane The scroll panel to modify.
	 */
	public void setScrollPane(final JScrollPane scrollpane) {
		this.scrollPane = scrollpane;
	}
}
