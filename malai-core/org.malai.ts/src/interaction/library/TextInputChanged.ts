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
import {isTextInput} from "../Events";
import {FSMDataHandler} from "../FSMDataHandler";
import {TSInteraction} from "../TSInteraction";
import {WidgetData} from "../../../src-core/interaction/WidgetData";
import {StdState} from "../../../src-core/fsm/StdState";
import {TextInputChangedTransition} from "../TextInputChangedTransition";
import {TimeoutTransition} from "../../../src-core/fsm/TimeoutTransition";
import {OutputState} from "../../../src-core/fsm/OutputState";
import {InputState} from "../../../src-core/fsm/InputState";

class TextInputChangedFSM extends TSFSM<TextInputChangedHandler> {
    /** The time gap between the two spinner events. */
    private static readonly timeGap = 1000;
    /** The supplier that provides the time gap. */
    private static readonly SUPPLY_TIME_GAP = () => TextInputChangedFSM.getTimeGap();

    /**
     * @return The time gap between the two spinner events.
     */
    public static getTimeGap(): number {
        return TextInputChangedFSM.timeGap;
    }

    public constructor() {
        super();
    }

    public buildFSM(dataHandler?: TextInputChangedHandler): void {
        if (this.states.length > 1) {
            return ;
        }

        super.buildFSM(dataHandler);
        const changed: StdState<Event> = new StdState<Event>(this, "changed");
        const ended: TerminalState<Event> = new TerminalState<Event>(this, "ended");
        this.addState(changed);
        this.addState(ended);

        new class extends TextInputChangedTransition {

            public constructor(srcState: OutputState<Event>, tgtState: InputState<Event>) {
                super(srcState, tgtState);
            }

            public action(event: Event): void {
                if (event.target !== null && isTextInput(event.target) && dataHandler !== undefined) {
                    dataHandler.initToChangedHandler(event);
                }
            }
        }(this.initState, changed);

        new class extends TextInputChangedTransition {

            public constructor(srcState: OutputState<Event>, tgtState: InputState<Event>) {
                super(srcState, tgtState);
            }

            public action(event: Event): void {
                if (event.target !== null && isTextInput(event.target) && dataHandler !== undefined) {
                    dataHandler.initToChangedHandler(event);
                }
            }
        }(changed, changed);

        new TimeoutTransition(changed, ended, TextInputChangedFSM.SUPPLY_TIME_GAP);
    }
}


interface TextInputChangedHandler  extends FSMDataHandler {
    initToChangedHandler(event: Event): void;
}

/**
 * A user interaction for Number input.
 * @author Gwendal DIDOT
 */

export class TextInputChanged extends TSInteraction<WidgetData<Element>, TextInputChangedFSM, Element> {
    private readonly handler: TextInputChangedHandler;

    public constructor() {
        super(new TextInputChangedFSM());

        this.handler = new class implements TextInputChangedHandler {
            private readonly _parent: TextInputChanged;

            constructor(parent: TextInputChanged) {
                this._parent = parent;
            }

            public initToChangedHandler(event: Event): void {
                if (event.target !== null && isTextInput(event.target)) {
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
        if (isTextInput(node)) {
            this.registerActionHandlerInput(node);
        }
    }

    public onNodeUnregistered(node: EventTarget): void {
        if (isTextInput(node)) {
            this.unregisterActionHandlerInput(node);
        }
    }

    public getData(): WidgetData<Element> {
        return this;
    }
}
