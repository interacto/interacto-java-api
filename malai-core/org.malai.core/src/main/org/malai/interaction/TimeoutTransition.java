/*
 * This file is part of Malai.
 * Copyright (c) 2005-2017 Arnaud BLOUIN
 * Malai is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * Malai is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */
package org.malai.interaction;

import java.util.function.LongSupplier;
import org.malai.stateMachine.SourceableState;
import org.malai.stateMachine.StateMachine;
import org.malai.stateMachine.TargetableState;

/**
 * This transition defines a timeout: when the user does nothing during a given duration, the timeout transition is executed.
 * @author Arnaud BLOUIN
 * @since 0.2
 */
public class TimeoutTransition extends TransitionImpl {
	/** The timeout in ms. */
	protected final LongSupplier timeout;

	/** The current thread in progress. */
	private Thread timeoutThread;


	/**
	 * Creates the transition.
	 * @param inputState The source state of the transition.
	 * @param outputState The target state of the transition.
	 * @param timeout The timeout in ms. Must be greater than 0.
	 * @throws IllegalArgumentException If one of the given parameters is null or not valid.
	 * @since 0.2
	 */
	public TimeoutTransition(final SourceableState inputState, final TargetableState outputState, final LongSupplier timeout) {
		super(inputState, outputState);

		if(timeout == null) {
			throw new IllegalArgumentException();
		}

		this.timeout = timeout;
	}


	/**
	 * Launches the timer.
	 * @since 0.2
	 */
	public void startTimeout() {
		if(timeoutThread == null) {
			timeoutThread = new Thread(() -> {
				final long time = TimeoutTransition.this.timeout.getAsLong();

				if(time > 0L) {
					try {
						// Sleeping the thread.
						Thread.sleep(time);
						// There is a timeout and the interaction must be notified of that.
						final StateMachine sm = getInputState().getStateMachine();
						// Notifying the interaction of the timeout.
						if(sm instanceof Interaction) {
							((Interaction) sm).onTimeout(this);
						}
					}catch(final InterruptedException ex) {
						// OK, thread stopped.
					}
				}
			});
			timeoutThread.start();
		}
	}


	/**
	 * Stops the timer.
	 * @since 0.2
	 */
	public void stopTimeout() {
		if(timeoutThread != null) {
			timeoutThread.interrupt();
			timeoutThread = null;
		}
	}

	@Override
	public <T> T getEventType() {
		return null;
	}
}
