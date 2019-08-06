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
package io.github.interacto.error;

/**
 * The singleton ErrorCatcher collects errors.
 * The ErrorCatcher sends the gathered exception to an ErrorNotifier (if one is defined).
 * @author Arnaud BLOUIN
 */
public final class ErrorCatcher {
	/** The singleton. */
	public static final ErrorCatcher INSTANCE = new ErrorCatcher();

	/** The notifier object. */
	private ErrorNotifier notifier;

	/**
	 * Creates the error catcher.
	 */
	private ErrorCatcher() {
		super();
	}

	/**
	 * Sets the notifier that will be notified about the collected exceptions.
	 * @param newNotifier The notifier that will be notified the collected exceptions. Can be null.
	 */
	public void setNotifier(final ErrorNotifier newNotifier) {
		notifier = newNotifier;
	}

	/**
	 * @return The notifier that is notified about the collected exceptions.
	 */
	public ErrorNotifier getErrorNotifier() {
		return notifier;
	}


	/**
	 * Gathers exceptions. The notifier is then notified of the exceptions (if defined).
	 * @param exception The errors to gather.
	 */
	public void reportError(final Exception exception) {
		if(exception != null && notifier != null) {
			notifier.onException(exception);
		}
	}
}
