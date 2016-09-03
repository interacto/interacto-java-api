package org.malai.javafx.instrument.library;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import org.malai.javafx.action.library.ShowNode;
import org.malai.javafx.instrument.JfxInstrument;
import org.malai.javafx.instrument.JfxMenuItemInteractor;
import org.malai.javafx.interaction.library.MenuItemPressed;

/**
 * An interactor that opens a URL using a menu item.<br>
 * <br>
 * This file is part of Malai.<br>
 * Copyright (c) 2005-2016 Arnaud BLOUIN<br>
 * <br>
 * Malai is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * <br>
 * Malai is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br>
 * <br>
 * 2016/05/22<br>
 * @author Arnaud BLOUIN
 * @since 2.0
 */
public class MenuItem2ShowNode extends JfxMenuItemInteractor<ShowNode, MenuItemPressed, JfxInstrument> {
	protected Node nodeToShow;

	protected boolean show;

	/**
	 * Creates the interactor.
	 * @param ins The instrument that will contain the interactor.
	 * @param menuItem he menu item that will be uses to create the action.
	 * @param node The node to show or hide
	 * @throws IllegalArgumentException If one of the given parameters is null.
	 * @throws IllegalAccessException If no free-parameter constructor is available.
	 * @throws InstantiationException If an error occurs during instantiation of the interaction/action.
	 * @since 2.0
	 */
	public MenuItem2ShowNode(final JfxInstrument ins, final MenuItem menuItem, final Node node, final boolean toshow)
								throws InstantiationException, IllegalAccessException {
		super(ins, false, ShowNode.class, MenuItemPressed.class, menuItem);

		if(node==null)
			throw new IllegalArgumentException();

		nodeToShow = node;
		show = toshow;
	}

	@Override
	public void initAction() {
		action.setWidget(nodeToShow);
		action.setVisible(show);
	}
}