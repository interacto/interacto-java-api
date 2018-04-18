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

import {FSM} from "../../src-core/fsm/FSM";
import {InitState} from "../../src-core/fsm/InitState";
import {FSMDataHandler} from "./FSMDataHandler";
import {isKeyPressEvent} from "./Events";

export abstract class TSFSM<H extends FSMDataHandler> extends FSM<Event> {
    protected dataHandler: H | undefined;

    protected buildFSM(dataHandler?: H): void {
        if (this.states.length > 1) {
            return;
        }
        this.dataHandler = dataHandler;
    }

    public reinit(): void {
        super.reinit();
        if (this.dataHandler !== undefined && !this._inner) {
            this.dataHandler.reinitData();
        }
    }

    public process(event: Event): boolean {
        // Removing the possible corresponding and pending key pressed event
        if (isKeyPressEvent(event)) {
            this.removeKeyEvent(event.keyCode);
        }

        // Processing the event
        const processed: boolean = super.process(event);

        // Recycling events
        if (processed && isKeyPressEvent(event) && !(this.currentState instanceof InitState) &&
            this.eventsToProcess.find(evt => isKeyPressEvent(evt) && evt.keyCode === event.keyCode) === undefined) {
            // this.addRemaningEventsToProcess((Event) event.clone()); //TODO
        }

        return processed;
    }

    /**
     * Removes the given KeyPress event from the events 'still in process' list.
     * @param key The key code of the event to remove.
     */
    private removeKeyEvent(key: number): void {
        let removed = false;

        for (let i = 0, size = this.eventsToProcess.length; i < size && !removed; i++) {
            const event = this.eventsToProcess[i];

            if (event instanceof KeyboardEvent && event.keyCode === key) {
                removed = true;
                this.eventsToProcess.removeAt(i);
            }
        }
    }

    public getDataHandler(): H | undefined {
        return this.dataHandler;
    }

    public uninstall(): void {
        super.uninstall();
        this.dataHandler = undefined;
    }
}
