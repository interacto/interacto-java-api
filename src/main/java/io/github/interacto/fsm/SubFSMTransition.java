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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A transition that refers to another FSM.
 * Entering this transition starts the underlying sub-FSM.
 * To leave the transition, the sub-FSM must end.
 * @param <E0> The type of events the FSM processes.
 */
public class SubFSMTransition<E0> extends Transition<E0, E0> {
	private final FSM<E0> subFSM;
	private final FSMHandler subFSMHandler;

	/**
	 * Creates the transition.
	 * @param srcState The source state of the transition.
	 * @param tgtState The output state of the transition.
	 * @param fsm The inner FSM that composes the transition.
	 * @throws IllegalArgumentException If one of the states is null.
	 */
	public SubFSMTransition(final OutputState<E0> srcState, final InputState<E0> tgtState, final FSM<E0> fsm) {
		super(srcState, tgtState);

		if(fsm == null) {
			throw new IllegalArgumentException("sub fsm cannot be null");
		}

		subFSM = fsm;
		subFSM.setInner(true);
		subFSMHandler = new FSMHandler() {
			@Override
			public void fsmStarts() throws CancelFSMException {
				src.exit();
			}

			@Override
			public void fsmUpdates() throws CancelFSMException {
				src.getFSM().setCurrentState(subFSM.getCurrentState());
				src.getFSM().onUpdating();
			}

			@Override
			public void fsmStops() throws CancelFSMException {
				action(null);
				subFSM.removeHandler(subFSMHandler);
				src.getFSM().currentSubFSM = null;
				if(tgt instanceof TerminalState) {
					tgt.enter();
					return;
				}
				if(tgt instanceof CancellingState) {
					fsmCancels();
					return;
				}
				if(tgt instanceof OutputState) {
					src.getFSM().setCurrentState((OutputState<E0>) tgt);
					tgt.enter();
				}
			}

			@Override
			public void fsmCancels() {
				subFSM.removeHandler(subFSMHandler);
				src.getFSM().currentSubFSM = null;
				src.getFSM().onCancelling();
			}
		};
	}

	@Override
	public Optional<InputState<E0>> execute(final E0 event) {
		final Optional<Transition<E0, E0>> transition = findTransition(event);

		if(transition.isPresent()) {
			src.getFSM().stopCurrentTimeout();
			subFSM.addHandler(subFSMHandler);
			src.getFSM().currentSubFSM = subFSM;
			subFSM.process(event);
			return Optional.of(transition.get().tgt);
		}

		return Optional.empty();
	}

	@Override
	protected E0 accept(final E0 event) {
		return findTransition(event).isEmpty() ? null : event;
	}

	@Override
	protected boolean isGuardOK(final E0 event) {
		return findTransition(event)
			.filter(tr -> tr.isGuardOK(event))
			.isPresent();
	}

	private Optional<Transition<E0, E0>> findTransition(final E0 event) {
		return subFSM.initState.transitions
			.stream()
			.filter(tr -> tr.accept(event) != null)
			.map(tr -> (Transition<E0, E0>) tr)
			.findFirst();
	}

	@Override
	public Set<Object> getAcceptedEvents() {
		return subFSM.initState
			.getTransitions()
			.stream()
			.map(tr -> tr.getAcceptedEvents())
			.flatMap(s -> s.stream())
			.collect(Collectors.toSet());
	}

	@Override
	public void uninstall() {
		subFSM.uninstall();
	}
}
