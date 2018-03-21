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

import {ActionImpl} from "../ActionImpl";
import {Instrument} from "../../instrument/Instrument";
import {WidgetBinding} from "../../binding/WidgetBinding";

/**
 * This action manipulates an instrument.
 * @author Arnaud Blouin
 * @param {*} instrument
 * @class
 * @extends ActionImpl
 */
export abstract class InstrumentAction extends ActionImpl {
    /**
     * The manipulated instrument.
     */
    protected instrument: Instrument<WidgetBinding> | undefined;

    public constructor(instrument?: Instrument<WidgetBinding>) {
        super();
        this.instrument = instrument;
    }

    /**
     *
     */
    public flush(): void {
        super.flush();
        this.instrument = undefined;
    }

    /**
     *
     * @return {boolean}
     */
    public canDo(): boolean {
        return this.instrument !== undefined;
    }

    /**
     * @return {*} The manipulated instrument.
     */
    public getInstrument(): Instrument<WidgetBinding> | undefined {
        return this.instrument;
    }

    /**
     * Sets the manipulated instrument.
     * @param {*} newInstrument The manipulated instrument.
     */
    public setInstrument(newInstrument: Instrument<WidgetBinding>): void {
        this.instrument = newInstrument;
    }
}
