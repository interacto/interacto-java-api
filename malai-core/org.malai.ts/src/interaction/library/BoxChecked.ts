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
import {BoxCheckPressedTransition} from "../BoxCheckPressedTransition";
import {isCheckBox} from "../Events";
import {FSMDataHandler} from "../FSMDataHandler";
import {TSInteraction} from "../TSInteraction";
import {WidgetData} from "../../../src-core/interaction/WidgetData";

class BoxCheckedFSM extends TSFSM<BoxCheckedHandler> {
    public constructor() {
        super();
    }

    public buildFSM(dataHandler?: BoxCheckedHandler): void {
        if (this.states.length > 1) {
            return ;
        }

        super.buildFSM(dataHandler);
        const checked: TerminalState<Event> = new TerminalState<Event>(this, "checked");
        this.addState(checked);

        new class extends BoxCheckPressedTransition {
            public action(event: Event): void {
                if (event.target !== null && isCheckBox(event.target) && dataHandler !== undefined) {
                    dataHandler.initToCheckHandler(event);
                }
            }
        }(this.initState, checked);
    }
}


interface BoxCheckedHandler  extends FSMDataHandler {
    initToCheckHandler(event: Event): void;
}

/**
 * A user interaction for CheckBox
 * @author Gwendal DIDOT
 */

export class BoxChecked extends TSInteraction<WidgetData<Element>, BoxCheckedFSM, Element> {
        private readonly handler: BoxCheckedHandler;

        public constructor() {
            super(new BoxCheckedFSM());

            this.handler = new class implements BoxCheckedHandler {
                private readonly _parent: BoxChecked;

                constructor(parent: BoxChecked) {
                    this._parent = parent;
                }

                public initToCheckHandler(event: Event): void {
                    if (event.target !== null && isCheckBox(event.target)) {
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
            if (isCheckBox(node)) {
                this.registerActionHandler(node);
            }
        }

        public onNodeUnregistered(node: EventTarget): void {
            if (isCheckBox(node)) {
                this.unregisterActionHandler(node);
            }
        }

        public getData(): WidgetData<Element> {
            return this;
        }
}
