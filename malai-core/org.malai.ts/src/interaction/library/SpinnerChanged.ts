/*
 * This file is part of Malai.
 * Copyright (c) 2009-2018 Arnaud BLOUIN Gwendal DIDOT
 * Malai is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * Malai is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */

import {TSFSM} from "../TSFSM";
import {TerminalState} from "../../../src-core/fsm/TerminalState";
import {isSpinner} from "../Events";
import {FSMDataHandler} from "../FSMDataHandler";
import {TSInteraction} from "../TSInteraction";
import {WidgetData} from "../../../src-core/interaction/WidgetData";
import {SpinnerChangedTransition} from "../SpinnerChangedTransition";

class SpinnerChangedFSM extends TSFSM<SpinnerChangedHandler> {
    public constructor() {
        super();
    }

    public buildFSM(dataHandler?: SpinnerChangedHandler): void {
        if (this.states.length > 1) {
            return ;
        }

        super.buildFSM(dataHandler);
        const picked: TerminalState<Event> = new TerminalState<Event>(this, "picked");
        this.addState(picked);

        new class extends SpinnerChangedTransition {
            public action(event: Event): void {
                if (event.target !== null && isSpinner(event.target) && dataHandler !== undefined) {
                    dataHandler.initToChangedHandler(event);
                }
            }
        }(this.initState, picked);
    }
}


interface SpinnerChangedHandler  extends FSMDataHandler {
    initToChangedHandler(event: Event): void;
}

/**
 * A user interaction for Number input.
 * @author Gwendal DIDOT
 */
export class SpinnerChanged extends TSInteraction<WidgetData<Element>, SpinnerChangedFSM, Element> {
    private readonly handler: SpinnerChangedHandler;

    /**
     * Creates the interaction.
     */
    public constructor() {
        super(new SpinnerChangedFSM());

        this.handler = new class implements SpinnerChangedHandler {
            private readonly _parent: SpinnerChanged;

            constructor(parent: SpinnerChanged) {
                this._parent = parent;
            }

            public initToChangedHandler(event: Event): void {
                if (event.target !== null && isSpinner(event.target)) {
                    this._parent._widget = event.currentTarget as Element;
                }
            }

            public reinitData(): void {
                this._parent.reinitData();
            }

        }(this);

        this.fsm.buildFSM(this.handler);
    }

    public onNewNodeRegistered(node: EventTarget): void {
        if (isSpinner(node)) {
            this.registerActionHandler(node);
        }
    }

    public onNodeUnregistered(node: EventTarget): void {
        if (isSpinner(node)) {
            this.unregisterActionHandler(node);
        }
    }

    public getData(): WidgetData<Element> {
        return this;
    }
}
