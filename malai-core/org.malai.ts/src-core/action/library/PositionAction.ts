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

/**
 * Creates the action.
 * @class
 * @extends ActionImpl
 * @author Arnaud BLOUIN
 */
export abstract class PositionAction extends ActionImpl {
    /**
     * The X-coordinate of the location to zoom.
     */
    protected px: number;

    /**
     * The Y-coordinate of the location to zoom.
     */
    protected py: number;

    public constructor() {
        super();
        this.px = NaN;
        this.py = NaN;
    }

    /**
     *
     * @return {boolean}
     */
    public canDo(): boolean {
        return !isNaN(this.px) && !isNaN(this.py);
    }

    /**
     * @param {number} px The x-coordinate to set.
     * @since 0.2
     */
    public setPx(px: number): void {
        this.px = px;
    }

    /**
     * @param {number} py The y-coordinate to set.
     * @since 0.2
     */
    public setPy(py: number): void {
        this.py = py;
    }
}
