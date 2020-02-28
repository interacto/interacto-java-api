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
package io.github.interacto.error;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * The singleton ErrorCatcher collects errors.
 * The ErrorCatcher sends the gathered exception to an ErrorNotifier (if one is defined).
 * @author Arnaud BLOUIN
 */
public class ErrorCatcher {
	/** The singleton. */
	private static ErrorCatcher instance = new ErrorCatcher();

	/** The notifier object. */
	private final PublishSubject<Throwable> notifier;

	/**
	 * @return The single instance. Cannot be null.
	 */
	public static ErrorCatcher getInstance() {
		return instance;
	}

	/**
	 * Sets the single instance.
	 * @param newInstance The new single instance. Nothing done if null.
	 */
	public static void setInstance(final ErrorCatcher newInstance) {
		if(newInstance != null) {
			instance = newInstance;
		}
	}


	/**
	 * Creates the error catcher.
	 */
	public ErrorCatcher() {
		super();
		notifier = PublishSubject.create();
	}


	/**
	 * @return An observable stream of errors. Cannot be null.
	 */
	public Observable<Throwable> getErrors() {
		return notifier;
	}


	/**
	 * Gathers exceptions. The notifier is then notified of the exceptions (if defined).
	 * @param throwable The errors to gather.
	 */
	public void reportError(final Throwable throwable) {
		if(throwable != null) {
			notifier.onNext(throwable);
		}
	}
}
