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
package io.github.interacto.interaction;

import io.github.interacto.fsm.FSM;
import io.github.interacto.fsm.InitState;
import io.github.interacto.fsm.OutputState;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The base implementation of a user interaction.
 * @param <D> The type of the interaction data.
 * @param <E> The type of the events that the interaction will process.
 * @param <F> The type of the FSM.
 */
public abstract class InteractionImpl<D extends InteractionData, E, F extends FSM<E>> {
	static Logger defaultLogger = Logger.getLogger(InteractionImpl.class.getName());

	/**
	 * Sets the logger to use. Cannot be null.
	 * Does not change the loggers of existing user interactions.
	 * @param logger The new logger to use.
	 */
	public static void setLogger(final Logger logger) {
		if(logger != null) {
			defaultLogger = logger;
		}
	}

	protected final F fsm;
	/** Defines whether the interaction is activated. If not, the interaction will not change on events. */
	protected boolean activated;
	protected Logger logger;
	protected long throttleTimeout;
	protected final AtomicLong throttleCounter;
	protected E currentThrottledEvent;
	/** The current throttle thread in progress. */
	protected Future<?> currThrottleTimeoutFuture;
	protected ExecutorService executor;
	protected final Disposable disposable;
	private boolean consumeEvents;

	protected InteractionImpl(final F fsm) {
		super();

		if(fsm == null) {
			throw new IllegalArgumentException("null fsm");
		}

		executor = null;
		currThrottleTimeoutFuture = null;
		throttleTimeout = 0L;
		this.fsm = fsm;
		disposable = fsm.currentState().subscribe(current -> updateEventsRegistered(current.getValue(), current.getKey()));
		activated = true;
		throttleCounter = new AtomicLong();
		currentThrottledEvent = null;
		consumeEvents = false;
	}

	/**
	 * @return The interaction data of the user interaction. Cannot be null.
	 */
	public abstract D getData();

	/**
	 * Sets the throttle timeout the interaction will use.
	 * @param timeout The throttle value.
	 */
	public void setThrottleTimeout(final long timeout) {
		throttleTimeout = timeout;
	}

	protected abstract void updateEventsRegistered(final OutputState<E> newState, final OutputState<E> oldState);

	/**
	 * @return Whether the user interaction is running.
	 */
	public boolean isRunning() {
		return activated && !(fsm.getCurrentState() instanceof InitState<?>);
	}

	/**
	 * Reinitialises the user interaction
	 */
	public void fullReinit() {
		fsm.fullReinit();
	}

	private void directEventProcess(final E event) {
		fsm.process(event);
		if(consumeEvents) {
			consumeEvent(event);
		}
	}

	protected abstract void consumeEvent(final E event);

	/**
	 * Sets whether the user interaction will consumes the processed UI events.
	 * 				This is related to the event.consume() method.
	 * @param consumeEvents True: the processed events will be consumed.
	 */
	public void setConsumeEvents(final boolean consumeEvents) {
		this.consumeEvents = consumeEvents;
	}

	/**
	 * Defines whether the two given events are of the same type.
	 * For example, whether they are both mouse move events.
	 * This check is platform specific.
	 * @param evt1 The first event to check.
	 * @param evt2 The second event to check.
	 * @return True: the two events are of the same type.
	 */
	protected abstract boolean isEventsOfSameType(final E evt1, final E evt2);

	/**
	 * Throttling: sleeping between events of the same type.
	 */
	private void createThrottleTimeout() {
		if(executor == null) {
			executor = Executors.newWorkStealingPool();
		}

		// Cancelling the current task.
		if(currThrottleTimeoutFuture != null && !currThrottleTimeoutFuture.isDone()) {
			currThrottleTimeoutFuture.cancel(true);
		}

		// Executing a new timeout for the throttling operation.
		currThrottleTimeoutFuture = executor.submit(() -> {
			try {
				ThreadService.getInstance().sleep(throttleTimeout);
				E evt = null;
				if(throttleCounter.getAndSet(0L) > 0L) {
					evt = currentThrottledEvent;
				}
				currentThrottledEvent = null;
				if(evt != null) {
					final E evtToProcess = evt;
					runInUIThread(() -> directEventProcess(evtToProcess));
				}
			}catch(final InterruptedException ex) {
				ThreadService.getInstance().currentThread().interrupt();
			}
		});
	}

	/**
	 * Runs the given command in the UI thread.
	 * This is necessary since some created threads (e.g. throttling, timeout transition)
	 * exit the UI thread but may require some job to be executed in the UI thread.
	 * @param cmd The job to execute in the UI thread.
	 */
	protected abstract void runInUIThread(final Runnable cmd);

	/**
	 * Throttling processing: the given event is checked to be throttled or not.
	 * @param event The event to check.
	 * @return True: the event must be processed by the interaction.
	 */
	private boolean checkThrottlingEvent(final E event) {
		if(currentThrottledEvent == null || !isEventsOfSameType(currentThrottledEvent, event)) {
			if(throttleCounter.getAndSet(0L) > 0L) {
				directEventProcess(event);
			}
			currentThrottledEvent = event;
			createThrottleTimeout();
			return true;
		}else {
			// The previous throttled event is ignored
			throttleCounter.incrementAndGet();
			currentThrottledEvent = event;
			return false;
		}
	}

	/**
	 * Processes the given UI event.
	 * @param event The event to process.
	 */
	public void processEvent(final E event) {
		if(isActivated()) {
			if(throttleTimeout <= 0L || checkThrottlingEvent(event)) {
				directEventProcess(event);
			}
		}
	}

	/**
	 * Sets the logging of the user interaction.
	 * @param log True: the user interaction will log information.
	 */
	public void log(final boolean log) {
		if(log) {
			if(logger == null) {
				logger = defaultLogger;
			}
		}else {
			logger = null;
		}

		fsm.log(log);
	}

	/**
	 * @return True if the user interaction is activated.
	 */
	public boolean isActivated() {
		return activated;
	}

	/**
	 * Sets whether the user interaction is activated.
	 * 				When not activated, a user interaction does not process
	 * 				input events any more.
	 * @param activated True: the user interaction will be activated.
	 */
	public void setActivated(final boolean activated) {
		if(logger != null) {
			logger.log(Level.INFO, "Interaction activation: " + activated);
		}

		this.activated = activated;

		if(!activated) {
			fsm.fullReinit();
		}
	}

	/**
	 * @return The FSM of the user interaction.
	 */
	public F getFsm() {
		return fsm;
	}

	protected void reinit() {
		fsm.reinit();
		reinitData();
	}

	protected abstract void reinitData();

	/**
	 * Uninstall the user interaction. Used to free memory.
	 * 				Then, user interaction can be used any more.
	 */
	public void uninstall() {
		disposable.dispose();
		setActivated(false);
		logger = null;
		if(executor != null) {
			executor.shutdownNow();
			try {
				executor.awaitTermination(5, TimeUnit.SECONDS);
			}catch(final InterruptedException ex) {
				ThreadService.getInstance().currentThread().interrupt();
			}
		}
	}
}
