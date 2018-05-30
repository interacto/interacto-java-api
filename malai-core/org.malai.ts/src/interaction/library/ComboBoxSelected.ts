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
import {isComboBox} from "../Events";
import {FSMDataHandler} from "../FSMDataHandler";
import {TSInteraction} from "../TSInteraction";
import {WidgetData} from "../../../src-core/interaction/WidgetData";
import {ComboBoxTransition} from "../ComboBoxTransition";

class ComboBoxSelectedFSM extends TSFSM<ComboBoxSelectedHandler> {
    public constructor() {
        super();
    }

    public buildFSM(dataHandler?: ComboBoxSelectedHandler): void {
        if (this.states.length > 1) {
            return ;
        }

        super.buildFSM(dataHandler);
        const selected: TerminalState<Event> = new TerminalState<Event>(this, "selected");
        this.addState(selected);

        new class extends ComboBoxTransition {
            public action(event: Event): void {
                if (event.target !== null && isComboBox(event.target) && dataHandler !== undefined) {
                    dataHandler.initToSelectedHandler(event);
                }
            }
        }(this.initState, selected);
    }
}


interface ComboBoxSelectedHandler  extends FSMDataHandler {
    initToSelectedHandler(event: Event): void;
}

/**
 * A user interaction for CheckBox
 * @author Gwendal DIDOT
 */

export class ComboBoxSelected extends TSInteraction<WidgetData<Element>, ComboBoxSelectedFSM, Element> {
    private readonly handler: ComboBoxSelectedHandler;

    /**
     * Creates the interaction.
     */
    public constructor() {
        super(new ComboBoxSelectedFSM());

        this.handler = new class implements ComboBoxSelectedHandler {
            private readonly _parent: ComboBoxSelected;

            constructor(parent: ComboBoxSelected) {
                this._parent = parent;
            }

            public initToSelectedHandler(event: Event): void {
                if (event.target !== null && isComboBox(event.target)) {
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
        if (isComboBox(node)) {
            this.registerActionHandler(node);
        }
    }

    public onNodeUnregistered(node: EventTarget): void {
        if (isComboBox(node)) {
            this.unregisterActionHandler(node);
        }
    }

    public getData(): WidgetData<Element> {
        return this;
    }
}
