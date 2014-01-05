package org.malai.swing.action.library;

import org.malai.swing.ui.UI;

/**
 * This action allows to save abstract presentations and instruments' parameters to a file.
 * <br>
 * This file is part of Malai<br>
 * Copyright (c) 2005-2014 Arnaud BLOUIN<br>
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
public class Save<A extends UI, B extends Object> extends IOAction<A, B> {
	/**
	 * Creates a save action.
	 * @since 0.2
	 */
	public Save() {
		super();
	}


	@Override
	protected void doActionBody() {
		ok = openSaveManager.save(file.getPath(), ui, progressBar, statusWidget);
		ui.setModified(false);
	}
}
