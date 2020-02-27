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
package io.github.interacto.fsm;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.LongSupplier;

public class TimeoutTransition<E> extends Transition<E> {
	/** The base name (starts with) of the threads created for the timeout. */
	public static final String TIMEOUT_THREAD_NAME_BASE = "malai-timeout-transition-";
	/** The timeoutDuration in ms. */
	private final LongSupplier timeoutDuration;

	/** The current thread in progress. */
	private Thread timeoutThread;

	private boolean timeouted;

	public TimeoutTransition(final OutputState<E> srcState, final InputState<E> tgtState, final LongSupplier timeout) {
		super(srcState, tgtState);

		if(timeout == null) {
			throw new IllegalArgumentException();
		}

		timeoutDuration = timeout;
		timeouted = false;
	}

	/**
	 * Launches the timer.
	 */
	public void startTimeout() {
		if(timeoutThread == null) {
			timeoutThread = new Thread(() -> {
				final long time = TimeoutTransition.this.timeoutDuration.getAsLong();

				if(time > 0L) {
					try {
						// Sleeping the thread.
						Thread.sleep(time);
						// There is a timeoutDuration and the interaction must be notified of that.
						// Notifying the interaction of the timeoutDuration.
						timeouted = true;
						TimeoutTransition.this.src.getFSM().onTimeout();
					}catch(final InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}, TIMEOUT_THREAD_NAME_BASE + System.currentTimeMillis());
			timeoutThread.start();
		}
	}

	/**
	 * Stops the timer.
	 */
	public void stopTimeout() {
		if(timeoutThread != null) {
			timeoutThread.interrupt();
			timeoutThread = null;
		}
	}

	@Override
	protected boolean accept(final E event) {
		return timeouted;
	}

	@Override
	protected boolean isGuardOK(final E event) {
		return timeouted;
	}

	@Override
	public Optional<InputState<E>> execute(final E event) throws CancelFSMException {
		try {
			if(accept(event) && isGuardOK(event)) {
				src.exit();
				action(event);
				tgt.enter();
				timeouted = false;
				return Optional.of(tgt);
			}
			return Optional.empty();
		}catch(final CancelFSMException ex) {
			timeouted = false;
			throw ex;
		}
	}

	@Override
	public Set<Object> getAcceptedEvents() {
		return Collections.emptySet();
	}
}
