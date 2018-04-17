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

import {FSMDataHandler} from "../FSMDataHandler";
import {TSFSM} from "../TSFSM";
import {ButtonPressedTransition} from "../ButtonPressedTransition";
import {TerminalState} from "../../../src-core/fsm/TerminalState";
import {isButton} from "../Events";
import {TSInteraction} from "../TSInteraction";


class ButtonPressedFSM extends TSFSM<ButtonPressedFSMHandler> {
    public constructor() {
        super();
    }

    public buildFSM(dataHandler?: ButtonPressedFSMHandler): void {
        if (this.states.length > 1) {
            return;
        }
        super.buildFSM(dataHandler);
        const pressed: TerminalState<Event> = new TerminalState<Event>(this, "pressed");
        this.addState(pressed);

        new class extends ButtonPressedTransition {
            public action(event: Event): void {
                if (event.target !== null && isButton(event.target) && dataHandler !== undefined) {
                    dataHandler.initToPressedHandler(event);
                }
            }
        }(this.initState, pressed);
    }
}

interface ButtonPressedFSMHandler extends FSMDataHandler {
    initToPressedHandler(event: Event): void;
}

/**
 * A user interaction for buttons.
 * @author Arnaud BLOUIN
 */
export class ButtonPressed extends TSInteraction<ButtonPressedFSM, Element> {
    private readonly handler: ButtonPressedFSMHandler;

    /**
     * Creates the interaction.
     */
    public constructor() {
        super(new ButtonPressedFSM());

        this.handler = new class implements ButtonPressedFSMHandler {
            private readonly _parent: ButtonPressed;

            constructor(parent: ButtonPressed) {
                this._parent = parent;
            }

            public initToPressedHandler(event: Event): void {
                if (event.target !== null && isButton(event.target)) {
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
        if (isButton(node)) {
            this.registerActionHandler(node);
        }
    }

    public onNodeUnregistered(node: EventTarget): void {
        if (isButton(node)) {
            this.unregisterActionHandler(node);
        }
    }
}
