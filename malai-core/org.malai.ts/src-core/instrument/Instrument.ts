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

import {WidgetBinding} from "../binding/WidgetBinding";
import {Modifiable} from "../properties/Modifiable";
import {Reinitialisable} from "../properties/Reinitialisable";
import {MArray} from "../../src/util/ArrayUtil";
import {CommandHandler} from "../command/CommandHandler";

/**
 * The concept of instrument and its related services.
 * @author Arnaud BLOUIN
 * @class
 */
export interface Instrument<T extends WidgetBinding> extends Modifiable, Reinitialisable, CommandHandler {
    /**
     * @return {number} The number of widget bindings that compose the instrument.
     */
    getNbWidgetBindings(): number;

    /**
     * @return {boolean} True: the instrument has at least one widget binding. False otherwise.
     */
    hasWidgetBindings(): boolean;

    /**
     * @return {*[]} The widget bindings that compose the instrument. Cannot be null.
     */
    getWidgetBindings(): MArray<T>;

    /**
     * Stops the interactions of the instrument and clears all its events waiting for a process.
     */
    clearEvents(): void;

    /**
     * @return {boolean} True if the instrument is activated.
     */
    isActivated(): boolean;

    /**
     * Activates or deactivates the instrument.
     * @param {boolean} activated True = activation.
     */
    setActivated(activated: boolean): void;

    /**
     * Reinitialises the interim feedback of the instrument.
     * Must be overridden.
     */
    interimFeedback(): void;
}
