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

import io.github.interacto.error.ErrorCatcher;
import io.github.interacto.interaction.ThreadService;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.LongSupplier;

/**
 * A timeout transition is an FSM transition that is not executed by an event:
 * the FSM goes through such a transition on a timeout.
 * @param <E> The type of events the FSM processes.
 */
public class TimeoutTransition<E> extends Transition<E, E> {
	/** The base name (starts with) of the threads created for the timeout. */
	public static final String TIMEOUT_THREAD_NAME_BASE = "interacto-timeout-transition-";
	/** The timeoutDuration in ms. */
	private final LongSupplier timeoutDuration;

	/** The current thread in progress. */
	private Thread timeoutThread;

	private boolean timeouted;

	/**
	 * Creates the timeout transition.
	 * @param srcState The source state of the transition.
	 * @param tgtState The output state of the transition.
	 * @param timeout The function that returns the timeout value in ms.
	 * @throws IllegalArgumentException If one of the states is null.
	 */
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
		final long time = TimeoutTransition.this.timeoutDuration.getAsLong();
		// If incorrect duration value, no thread created
		if(timeoutThread == null) {
			if(time <= 0L) {
				TimeoutTransition.this.src.getFSM().onTimeout();
				return;
			}

			timeoutThread = new Thread(() -> {
				try {
					// Sleeping the thread.
					ThreadService.getInstance().sleep(time);
					// There is a timeoutDuration and the interaction must be notified of that.
					// Notifying the interaction of the timeoutDuration.
					timeouted = true;
					TimeoutTransition.this.src.getFSM().onTimeout();
				}catch(final InterruptedException ex) {
					ThreadService.getInstance().currentThread().interrupt();
				}
			}, TIMEOUT_THREAD_NAME_BASE + System.currentTimeMillis());
			timeoutThread.setUncaughtExceptionHandler((th, ex) -> ErrorCatcher.getInstance().reportError(ex));
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
	protected E accept(final E event) {
		return timeouted ? event : null;
	}

	@Override
	protected boolean isGuardOK(final E event) {
		return timeouted;
	}

	@Override
	public Optional<InputState<E>> execute(final E event) throws CancelFSMException {
		try {
			if(timeouted && isGuardOK(event)) {
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
