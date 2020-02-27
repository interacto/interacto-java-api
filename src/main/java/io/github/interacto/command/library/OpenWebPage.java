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

import io.github.interacto.error.ErrorCatcher;
import io.github.interacto.command.CommandImpl;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * This command opens an URI in the default browser.
 * @author Arnaud BLOUIN
 */
public class OpenWebPage extends CommandImpl {
	protected final Desktop desktop;
	/** The URI to open. */
	protected final URI uri;
	protected boolean browsed;

	/**
	 * Creates the command.
	 * @param desktop The desktop to use
	 * @param uri The URI to open
	 */
	public OpenWebPage(final Desktop desktop, final URI uri) {
		super();
		this.desktop = desktop;
		this.uri = uri;
		browsed = false;
	}


	@Override
	protected void doCmdBody() {
		try {
			desktop.browse(uri);
			browsed = true;
		}catch(final IOException exception) {
			ErrorCatcher.getInstance().reportError(exception);
			browsed = false;
		}
	}


	@Override
	public boolean canDo() {
		return uri != null
			&& desktop != null
			&& desktop.isSupported(Desktop.Action.BROWSE);
	}

	@Override
	public boolean hadEffect() {
		return browsed;
	}
}
