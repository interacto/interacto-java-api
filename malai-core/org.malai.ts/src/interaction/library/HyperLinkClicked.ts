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
import {isHyperLink} from "../Events";
import {FSMDataHandler} from "../FSMDataHandler";
import {TSInteraction} from "../TSInteraction";
import {WidgetData} from "../../../src-core/interaction/WidgetData";
import {HyperLinkTransition} from "../HyperLinkTransition";

class HyperLinkClickedFSM extends TSFSM<ColorPickedHandler> {
    public constructor() {
        super();
    }

    public buildFSM(dataHandler?: ColorPickedHandler): void {
        if (this.states.length > 1) {
            return ;
        }

        super.buildFSM(dataHandler);
        const clicked: TerminalState<Event> = new TerminalState<Event>(this, "clicked");
        this.addState(clicked);

        new class extends HyperLinkTransition {
            public action(event: Event): void {
                if (event.target !== null && isHyperLink(event.target) && dataHandler !== undefined) {
                    dataHandler.initToClickedHandler(event);
                }
            }
        }(this.initState, clicked);
    }
}


interface ColorPickedHandler extends FSMDataHandler {
    initToClickedHandler(event: Event): void;
}

/**
 * A user interaction for CheckBox
 * @author Gwendal DIDOT
 */

export class HyperLinkClicked extends TSInteraction<WidgetData<Element>, HyperLinkClickedFSM, Element> {
    private readonly handler: ColorPickedHandler;

    /**
     * Creates the interaction.
     */
    public constructor() {
        super(new HyperLinkClickedFSM());

        this.handler = new class implements ColorPickedHandler {
            private readonly _parent: HyperLinkClicked;

            constructor(parent: HyperLinkClicked) {
                this._parent = parent;
            }

            public initToClickedHandler(event: Event): void {
                if (event.target !== null && isHyperLink(event.target)) {
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
        if (isHyperLink(node)) {
            this.registerActionHandler(node);
        }
    }

    public onNodeUnregistered(node: EventTarget): void {
        if (isHyperLink(node)) {
            this.unregisterActionHandler(node);
        }
    }

    public getData(): WidgetData<Element> {
        return this;
    }
}
