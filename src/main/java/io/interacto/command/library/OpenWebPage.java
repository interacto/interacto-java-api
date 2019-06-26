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
package io.interacto.command.library;

import io.interacto.command.CommandImpl;
import io.interacto.error.ErrorCatcher;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * This command opens an URI in the default browser.
 * @author Arnaud BLOUIN
 */
public class OpenWebPage extends CommandImpl {
	/** The URI to open. */
	protected URI uri;

	protected boolean browsed;

	/**
	 * Creates the command.
	 */
	public OpenWebPage() {
		super();
		browsed = false;
	}


	@Override
	public void flush() {
		uri = null;
	}


	@Override
	protected void doCmdBody() {
		try {
			Desktop.getDesktop().browse(uri);
			browsed = true;
		}catch(final IOException exception) {
			ErrorCatcher.INSTANCE.reportError(exception);
			browsed = false;
		}
	}


	@Override
	public boolean canDo() {
		return uri != null && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
	}

	@Override
	public boolean hadEffect() {
		return super.hadEffect() && browsed;
	}

	/**
	 * @param newURI The URI to open.
	 */
	public void setUri(final URI newURI) {
		uri = newURI;
	}
}
