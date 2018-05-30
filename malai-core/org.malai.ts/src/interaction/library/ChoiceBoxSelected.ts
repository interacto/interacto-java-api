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
import {isChoiceBox} from "../Events";
import {FSMDataHandler} from "../FSMDataHandler";
import {TSInteraction} from "../TSInteraction";
import {WidgetData} from "../../../src-core/interaction/WidgetData";
import {ChoiceBoxTransition} from "../ChoiceBoxTransition";

class ChoiceBoxSelectedSFM extends TSFSM<ChoiceBoxSelectedHandler> {
    public constructor() {
        super();
    }

    public buildFSM(dataHandler?: ChoiceBoxSelectedHandler): void {
        if (this.states.length > 1) {
            return ;
        }

        super.buildFSM(dataHandler);
        const selected: TerminalState<Event> = new TerminalState<Event>(this, "selected");
        this.addState(selected);

        new class extends ChoiceBoxTransition {
            public action(event: Event): void {
                if (event.target !== null && isChoiceBox(event.target) && dataHandler !== undefined) {
                    dataHandler.initToSelectedHandler(event);
                }
            }
        }(this.initState, selected);
    }
}


interface ChoiceBoxSelectedHandler  extends FSMDataHandler {
    initToSelectedHandler(event: Event): void;
}

/**
 * A user interaction for CheckBox
 * @author Gwendal DIDOT
 */
export class ChoiceBoxSelected extends TSInteraction<WidgetData<Element>, ChoiceBoxSelectedSFM, Element> {
    private readonly handler: ChoiceBoxSelectedHandler;

    /**
     * Creates the interaction.
     */
    public constructor() {
        super(new ChoiceBoxSelectedSFM());

        this.handler = new class implements ChoiceBoxSelectedHandler {
            private readonly _parent: ChoiceBoxSelected;

            constructor(parent: ChoiceBoxSelected) {
                this._parent = parent;
            }

            public initToSelectedHandler(event: Event): void {
                if (event.target !== null && isChoiceBox(event.target)) {
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
        if (isChoiceBox(node)) {
            this.registerActionHandler(node);
        }
    }

    public onNodeUnregistered(node: EventTarget): void {
        if (isChoiceBox(node)) {
            this.unregisterActionHandler(node);
        }
    }

    public getData(): WidgetData<Element> {
        return this;
    }
}
