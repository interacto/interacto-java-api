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

import {Transition} from "./Transition";
import {OutputState} from "./OutputState";
import {InputState} from "./InputState";

export class EpsilonTransition<E> extends Transition<E> {
    public constructor(srcState: OutputState<E>, tgtState: InputState<E>) {
        super(srcState, tgtState);
    }

    /**
     *
     * @param {*} event
     * @return {boolean}
     */
    accept(event: E): boolean {
        return true;
    }

    /**
     *
     * @param {*} event
     * @return {boolean}
     */
    isGuardOK(event: E): boolean {
        return true;
    }

    /**
     *
     * @return {*[]}
     */
    public getAcceptedEvents(): Set<String> {
        return new Set([]);
    }
}

