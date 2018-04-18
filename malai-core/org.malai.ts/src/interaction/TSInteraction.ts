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
import {InteractionImpl} from "../../src-core/interaction/InteractionImpl";
import {OutputState} from "../../src-core/fsm/OutputState";
import {EventRegistrationToken} from "./Events";

export abstract class TSInteraction<F extends FSM<Event>, T> extends InteractionImpl<Event, F> {
    protected readonly _registeredNodes: Set<EventTarget>;
    /** The widget used during the interaction. */
    protected _widget: T | undefined;
    private mouseHandler: ((e: MouseEvent) => void) | undefined;
    private keyHandler: ((e: KeyboardEvent) => void) | undefined;
    private actionHandler: EventListener | undefined;

    protected constructor(fsm: F) {
        super(fsm);
        this._registeredNodes = new Set<EventTarget>();
    }

    /**
     * @return The widget used during the interaction.
     */
    public get widget(): T | undefined {
        return this._widget;
    }

    protected updateEventsRegistered(newState: OutputState<Event>, oldState: OutputState<Event>): void {
        // Do nothing when the interaction has only two nodes: init node and terminal node (this is a single-event interaction).
        if (newState === oldState || this.fsm.getStates().length === 2) {
            return;
        }

        const currEvents: Array<string> = [...this.getEventTypesOf(newState)];
        const events: Array<string> = [...this.getEventTypesOf(oldState)];
        const eventsToRemove: Array<string> = events.filter(e => currEvents.indexOf(e) < 0);
        const eventsToAdd: Array<string> = currEvents.filter(e => events.indexOf(e) < 0);
        this._registeredNodes.forEach(n => {
            eventsToRemove.forEach(type => this.unregisterEventToNode(type, n));
            eventsToAdd.forEach(type => this.registerEventToNode(type, n));
        });
        // additionalNodes.forEach(nodes -> nodes.forEach(n -> {
        //     eventsToRemove.forEach(type -> unregisterEventToNode(type, n));
        //     eventsToAdd.forEach(type -> registerEventToNode(type, n));
        // }));
    }

    private getEventTypesOf(state: OutputState<Event>): Set<string> {
        return state.getTransitions().map(t => t.getAcceptedEvents()).reduce((a, b) => new Set([...a, ...b]));
    }

    public registerToNodes(widgets: Array<EventTarget>): void {
        widgets.forEach(w => {
            this._registeredNodes.add(w);
            this.onNewNodeRegistered(w);
        });
    }

    public unregisterFromNodes(widgets: Array<EventTarget>): void {
        widgets.forEach(w => {
            this._registeredNodes.delete(w);
            this.onNodeUnregistered(w);
        });
    }

    public onNodeUnregistered(node: EventTarget): void {
        this.getEventTypesOf(this.fsm.currentState).forEach(type => this.unregisterEventToNode(type, node));
    }

    public onNewNodeRegistered(node: EventTarget): void {
        this.getEventTypesOf(this.fsm.currentState).forEach(type => this.registerEventToNode(type, node));
    }

    private registerEventToNode(eventType: string, node: EventTarget): void {
        if (EventRegistrationToken.MouseDown === eventType) {
            node.addEventListener(EventRegistrationToken.MouseDown, this.getMouseHandler());
            return;
        }
        if (EventRegistrationToken.MouseUp === eventType) {
            node.addEventListener(EventRegistrationToken.MouseUp, this.getMouseHandler());
            return;
        }
        if (EventRegistrationToken.Click === eventType) {
            node.addEventListener(EventRegistrationToken.Click, this.getMouseHandler());
            return;
        }
        if (EventRegistrationToken.MouseMove === eventType) {
            node.addEventListener(EventRegistrationToken.MouseMove, this.getMouseHandler());
            return;
        }
        if (EventRegistrationToken.KeyDown === eventType) {
            node.addEventListener(EventRegistrationToken.KeyDown, this.getKeyHandler());
            return;
        }
        if (EventRegistrationToken.KeyUp === eventType) {
            node.addEventListener(EventRegistrationToken.KeyUp, this.getKeyHandler());
            return;
        }
    }

    protected registerActionHandler(node: EventTarget): void {
        node.addEventListener(EventRegistrationToken.Click, this.getActionHandler());
    }

    protected unregisterActionHandler(node: EventTarget): void {
        node.removeEventListener(EventRegistrationToken.Click, this.getActionHandler());
    }

    protected getActionHandler(): EventListener {
        if (this.actionHandler === undefined) {
            this.actionHandler = evt => this.processEvent(evt);
        }
        return this.actionHandler;
    }

    public reinitData(): void {
        this._widget = undefined;
    }

    private unregisterEventToNode(eventType: string, node: EventTarget): void {
        if (EventRegistrationToken.MouseDown === eventType) {
            node.removeEventListener(EventRegistrationToken.MouseDown, this.getMouseHandler());
            return;
        }
        if (EventRegistrationToken.MouseUp === eventType) {
            node.removeEventListener(EventRegistrationToken.MouseUp, this.getMouseHandler());
            return;
        }
        if (EventRegistrationToken.Click === eventType) {
            node.removeEventListener(EventRegistrationToken.Click, this.getMouseHandler());
            return;
        }
        if (EventRegistrationToken.MouseMove === eventType) {
            node.removeEventListener(EventRegistrationToken.MouseMove, this.getMouseHandler());
            return;
        }
        if (EventRegistrationToken.KeyDown === eventType) {
            node.removeEventListener(EventRegistrationToken.KeyDown, this.getKeyHandler());
            return;
        }
        if (EventRegistrationToken.KeyUp === eventType) {
            node.removeEventListener(EventRegistrationToken.KeyUp, this.getKeyHandler());
            return;
        }
    }

    protected getMouseHandler(): (e: MouseEvent) => void {
        if (this.mouseHandler === undefined) {
            this.mouseHandler = evt => this.processEvent(evt);
        }
        return this.mouseHandler;
    }

    protected getKeyHandler(): (e: KeyboardEvent) => void {
        if (this.keyHandler === undefined) {
            this.keyHandler = evt => this.processEvent(evt);
        }
        return this.keyHandler;
    }

    public uninstall(): void {
        this._widget = undefined;
        this._registeredNodes.clear();
        super.uninstall();
    }
}
