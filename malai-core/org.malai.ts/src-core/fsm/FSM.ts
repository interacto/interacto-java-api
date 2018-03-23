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

import {State} from "./State";
import {InitState} from "./InitState";
import {ObsValue} from "../utils/ObsValue";
import {OutputState} from "./OutputState";
import {FSMHandler} from "./FSMHandler";
import {TimeoutTransition} from "./TimeoutTransition";
import {StdState} from "./StdState";
import {InputState} from "./InputState";
import {Logger} from "typescript-logging";
import {factory} from "../logging/ConfigLog";
import {MArray} from "../../src/util/ArrayUtil";
import {OutputStateImpl} from "./OutputStateImpl";

export class FSM<E> {
    protected logger: Logger | undefined;

    protected _inner: boolean;

    /**
     * By default an FSM triggers its 'start' event when it leaves its initial state.
     * In some cases, this is not the case. For example, a double-click interaction is an FSM that must trigger
     * its start event when the FSM reaches... its terminal state. Similarly, a DnD must trigger its start event
     * on the first move, not on the first press.
     * The goal of this attribute is to identify the state of the FSM that must trigger the start event.
     * By default, this attribute is set with the initial state of the FSM.
     */
    protected _startingState: State<E>;

    /**
     * Goes with 'startingState'. It permits to know whether the FSM has started, ie whether the 'starting state'
     * has been reached.
     */
    protected started: boolean;

    public readonly initState: InitState<E>;

    protected readonly _currentState: ObsValue<OutputState<E>>;

    /**
     * The states that compose the finite state machine.
     */
    protected readonly states: MArray<State<E>>;

    /**
     * The handler that want to be notified when the state machine of the interaction changed.
     */
    protected readonly handlers: MArray<FSMHandler>;

    /**
     * The events still in process. For example when the user press key ctrl and scroll one time using
     * the wheel of the mouse, the interaction scrolling is
     * finished but the event keyPressed 'ctrl' is still in process. At the end of the interaction, these events
     * are re-introduced into the
     * state machine of the interaction for processing.
     */
    protected readonly eventsToProcess: MArray<E>;

    /**
     * The current timeout in progress.
     */
    protected currentTimeout: TimeoutTransition<E> | undefined;

    protected currentSubFSM: FSM<E> | undefined;

    public constructor() {
        this._inner = false;
        this.started = false;
        this.started = false;
        this.initState = new InitState<E>(this, "init");
        this.states = new MArray<State<E>>(this.initState);
        this._startingState = this.initState;
        this._currentState = new ObsValue<OutputState<E>>(this.initState);
        this.handlers = new MArray();
        this.eventsToProcess = new MArray();
    }

    public setCurrentSubFSM(subFSM?: FSM<E>): void {
        this.currentSubFSM = subFSM;
    }

    public get currentState(): OutputState<E> {
        return this._currentState.get();
    }

    public set inner(inner: boolean) {
        this._inner = inner;
    }

    public get inner(): boolean {
        return this._inner;
    }

    public process(event: E): boolean {
        if (event === undefined) {
            return false;
        }
        if (this.currentSubFSM !== undefined) {
            return this.currentSubFSM.process(event);
        }
        return this._currentState.get().process(event);
    }

    public enterStdState(state: StdState<E>): void {
        this.currentState = state;
        this.checkTimeoutTransition();
        if (this.started) {
            this.onUpdating();
        }
    }

    public isStarted(): boolean {
        return this.started;
    }

    public set currentState(state: OutputState<E>) {
        this._currentState.set(state);
    }

    /**
     * At the end of the FSM execution, the events still (eg keyPress) in process must be recycled to be reused in the FSM.
     */
    protected processRemainingEvents(): void {
        if (this.eventsToProcess !== undefined) {
            const list: MArray<E> = new MArray(...this.eventsToProcess);
            while (list.length > 0) {
                const event = list.removeAt(0);

                if (event) {
                    this.eventsToProcess.removeAt(0);
                    if (this.logger !== undefined) {
                        this.logger.info("Recycling event: " + String(event));
                    }
                    this.process(event);
                }
            }
        }
    }

    public addRemaningEventsToProcess(event: E): void {
        if (event !== undefined) {
            this.eventsToProcess.push(event);
        }
    }

    /**
     * Terminates the state machine.
     */
    public onTerminating(): void {
        if (this.logger !== undefined) {
            this.logger.info("FSM ended");
        }
        if (this.started) {
            this.notifyHandlerOnStop();
        }
        this.reinit();
        this.processRemainingEvents();
    }

    /**
     * Cancels the state machine.
     */
    public onCancelling(): void {
        if (this.logger !== undefined) {
            this.logger.info("FSM cancelled");
        }
        if (this.started) {
            this.notifyHandlerOnCancel();
        }
        this.fullReinit();
    }

    /**
     * Starts the state machine.
     */
    public onStarting(): void {
        if (this.logger !== undefined) {
            this.logger.info("FSM started");
        }
        this.started = true;
        this.notifyHandlerOnStart();
    }

    /**
     * Updates the state machine.
     */
    public onUpdating(): void {
        if (this.started) {
            if (this.logger !== undefined) {
                this.logger.info("FSM updated");
            }
            this.notifyHandlerOnUpdate();
        }
    }

    /**
     * Adds a state to the state machine.
     * @param {*} state The state to add. Must not be null.
     */
    public addState(state: InputState<E>): void {
        if (state !== undefined) {
            this.states.push(state);
        }
    }

    public log(log: boolean): void {
        if (log) {
            if (this.logger === undefined) {
                this.logger = factory.getLogger("FSM");
            }
        } else {
            this.logger = undefined;
        }
    }

    public reinit(): void {
        if (this.logger !== undefined) {
            this.logger.info("FSM reinitialised");
        }
        if (this.currentTimeout !== undefined) {
            this.currentTimeout.stopTimeout();
        }
        this.started = false;
        this._currentState.set(this.initState);
        this.currentTimeout = undefined;
        if (this.currentSubFSM !== undefined) {
            this.currentSubFSM.reinit();
        }
    }

    public fullReinit(): void {
        if (this.eventsToProcess !== undefined) {
            this.eventsToProcess.clear();
        }
        this.reinit();
        if (this.currentSubFSM !== undefined) {
            this.currentSubFSM.fullReinit();
        }
    }

    public onTimeout() {
        if (this.currentTimeout !== undefined) {
            if (this.logger !== undefined) {
                this.logger.info("Timeout");
            }
            const state = this.currentTimeout.execute().get();
            if (state instanceof OutputStateImpl) {
                this._currentState.set(state);
                this.checkTimeoutTransition();
            }
        }
    }

    /**
     * Stops the current timeout transition.
     */
    public stopCurrentTimeout(): void {
        if (this.currentTimeout !== undefined) {
            if (this.logger !== undefined) {
                this.logger.info("Timeout stopped");
            }
            this.currentTimeout.stopTimeout();
            this.currentTimeout = undefined;
        }
    }

    /**
     * Checks whether the current state has a timeout transition.
     * If it is the case, the timeout transition is launched.
     */
    protected checkTimeoutTransition(): void {
        const tr = this._currentState.get().getTransitions().find(t => t instanceof TimeoutTransition) as TimeoutTransition<E> | undefined;

        if (tr) {
            if (this.logger !== undefined) {
                this.logger.info("Timeout starting");
            }
            this.currentTimeout = tr;
            this.currentTimeout.startTimeout();
        }
    }

    public addHandler(handler: FSMHandler): void {
        if (handler !== undefined) {
            this.handlers.push(handler);
        }
    }

    public removeHandler(handler: FSMHandler): void {
        if (handler !== undefined) {
            this.handlers.remove(handler);
        }
    }

    /**
     * Notifies handler that the interaction starts.
     */
    protected notifyHandlerOnStart(): void {
        try {
            this.handlers.forEach(handler => handler.fsmStarts());
        } catch (ex) {
            this.onCancelling();
            throw ex;
        }
    }

    /**
     * Notifies handler that the interaction updates.
     */
    protected notifyHandlerOnUpdate(): void {
        try {
            this.handlers.forEach(handler => handler.fsmUpdates());
        } catch (ex) {
            this.onCancelling();
            throw ex;
        }
    }

    /**
     * Notifies handler that the interaction stops.
     */
    public notifyHandlerOnStop() {
        try {
            this.handlers.forEach(handler => handler.fsmStops());
        } catch (ex) {
            this.onCancelling();
            throw ex;
        }
    }

    /**
     * Notifies handler that the interaction is cancelled.
     */
    protected notifyHandlerOnCancel(): void {
        this.handlers.forEach(handler => handler.fsmCancels());
    }

    public getStates(): Array<State<E>> {
        return [...this.states];
    }

    public currentStateProp(): ObsValue<OutputState<E>> {
        return this._currentState;
    }

    public get startingState(): State<E> {
        return this._startingState;
    }

    public set startingState(state: State<E>) {
        if (state !== undefined) {
            this._startingState = state;
        }
    }

    public getEventsToProcess(): MArray<E> {
        return new MArray(...this.eventsToProcess);
    }
}
