/*
 * This file is part of Malai.
 * Copyright (c) 2009-2018 Arnaud BLOUIN
 * Malai is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * Malai is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */

import {Transition} from "./Transition";
import {FSM} from "./FSM";
import {FSMHandler} from "./FSMHandler";
import {OutputState} from "./OutputState";
import {InputState} from "./InputState";
import {Optional} from "../../src/util/Optional";
import {TerminalState} from "./TerminalState";
import {CancellingState} from "./CancellingState";
import {OutputStateImpl} from "./OutputStateImpl";

export class SubFSMTransition<E> extends Transition<E> {
    private readonly subFSM: FSM<E>;

    private readonly subFSMHandler: FSMHandler;

    public constructor(srcState: OutputState<E>, tgtState: InputState<E>, fsm: FSM<E>) {
        super(srcState, tgtState);
        this.subFSM = fsm;
        this.subFSM.setInner(true);
        this.subFSMHandler = new class implements FSMHandler {
            protected _parent: SubFSMTransition<E>;

            constructor(parentFSM: SubFSMTransition<E>) {
                this._parent = parentFSM;
            }

            public fsmStarts(): void {
                this._parent.src.exit();
            }

            public fsmUpdates(): void {
                this._parent.src.getFSM().setCurrentState(this._parent.subFSM.getCurrentState());
                this._parent.src.getFSM().onUpdating();
            }

            public fsmStops(): void {
                this._parent.action(undefined);
                this._parent.subFSM.removeHandler(this._parent.subFSMHandler);
                this._parent.src.getFSM().setCurrentSubFSM(undefined);
                if (this._parent.tgt instanceof  TerminalState) {
                    this._parent.tgt.enter();
                    return;
                }
                if (this._parent.tgt instanceof CancellingState) {
                    this.fsmCancels();
                    return;
                }
                if (this._parent.tgt instanceof OutputStateImpl) {
                    this._parent.src.getFSM().setCurrentState(this._parent.tgt);
                    this._parent.tgt.enter();
                }
            }

            public fsmCancels(): void {
                this._parent.subFSM.removeHandler(this._parent.subFSMHandler);
                this._parent.src.getFSM().setCurrentSubFSM(undefined);
                this._parent.src.getFSM().onCancelling();
            }
        }(this);
    }

    /**
     * @param {*} event
     */
    public execute(event: E): Optional<InputState<E>> {
        if (this.isGuardOK(event)) {
            this.src.getFSM().stopCurrentTimeout();
            const transition: Optional<Transition<E>> = this.findTransition(event);
            if (transition.isPresent()) {
                this.subFSM.addHandler(this.subFSMHandler);
                this.src.getFSM().setCurrentSubFSM(this.subFSM);
                this.subFSM.process(event);
                return Optional.ofNullable(transition.get()).map(t => t.tgt);
            }
        }
        return Optional.empty<InputState<E>>();
    }

    /**
     *
     * @param {*} event
     * @return {boolean}
     */
    public accept(event: E): boolean {
        return this.findTransition(event).isPresent();
    }

    /**
     *
     * @param {*} event
     * @return {boolean}
     */
    public isGuardOK(event: E): boolean {
        return this.findTransition(event).filter(tr => tr.isGuardOK(event)).isPresent();
    }

    private findTransition(event: E): Optional<Transition<E>> {
        return Optional.ofNullable(this.subFSM.initState.getTransitions().find(tr => tr.accept(event)));
    }

    /**
     *
     * @return {*[]}
     */
    public getAcceptedEvents(): Set<String> {
        return this.subFSM.initState.getTransitions().map(tr => tr.getAcceptedEvents()).reduce((a, b) => new Set([...a, ...b]));
    }
}
