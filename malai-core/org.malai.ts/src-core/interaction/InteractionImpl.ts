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

import {FSM} from "../fsm/FSM";
import {OutputState} from "../fsm/OutputState";
import {InitState} from "../fsm/InitState";
import {Logger} from "typescript-logging";
import {factory} from "../logging/ConfigLog";

export abstract class InteractionImpl<E, F extends FSM<E>> {
    protected logger: Logger | undefined;

    protected readonly fsm: F;

    /**
     * Defines if the interaction is activated or not. If not, the interaction will not
     * change on events.
     */
    protected activated: boolean;

    protected constructor(fsm: F) {
        this.activated = false;
        this.fsm = fsm;
        fsm.currentStateProp().obs((oldValue, newValue) => this.updateEventsRegistered(newValue, oldValue));
        this.activated = true;
    }

    protected abstract updateEventsRegistered(newState: OutputState<E>, oldState: OutputState<E>): void;

    public isRunning(): boolean {
        return this.activated && !(this.fsm.currentState instanceof InitState);
    }

    public fullReinit(): void {
        this.fsm.fullReinit();
    }

    public processEvent(event: E): void {
        if (this.isActivated()) {
            this.fsm.process(event);
        }
    }

    public log(log: boolean): void {
        if (log) {
            if (this.logger === undefined) {
                this.logger = factory.getLogger("Interaction");
            }
        } else {
            this.logger = undefined;
        }
        this.fsm.log(log);
    }

    public isActivated(): boolean {
        return this.activated;
    }

    public setActivated(activated: boolean): void {
        if (this.logger !== undefined) {
            this.logger.info("Interaction activation: " + activated);
        }
        this.activated = activated;
        if (!activated) {
            this.fsm.fullReinit();
        }
    }

    public getFsm(): F {
        return this.fsm;
    }

    protected reinit(): void {
        this.fsm.reinit();
        this.reinitData();
    }

    protected abstract reinitData(): void;
}
