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

import {Optional} from "../../util/Optional";
import {PointData} from "./PointData";

export class PointDataImpl implements PointData {
    /** The pressed X-local position. */
    protected srcClientX: number | undefined;
    /** The pressed Y-local position. */
    protected srcClientY: number | undefined;
    /** The pressed X-screen position. */
    protected srcScreenX: number | undefined;
    /** The pressed Y-screen position. */
    protected srcScreenY: number | undefined;

    /** The button used for the pressure. */
    protected button: number | undefined;

    protected altPressed: boolean;

    protected ctrlPressed: boolean;

    protected shiftPressed: boolean;

    protected metaPressed: boolean;

    /** The object picked at the pressed position. */
    protected srcObject: EventTarget | undefined;

    public constructor() {
    }

    public reinitData(): void {
        this.button = undefined;
        this.altPressed = false;
        this.ctrlPressed = false;
        this.shiftPressed = false;
        this.metaPressed = false;
    }

    public isAltPressed(): boolean {
        return this.altPressed;
    }

    public isCtrlPressed(): boolean {
        return this.ctrlPressed;
    }

    public isShiftPressed(): boolean {
        return this.shiftPressed;
    }

    public isMetaPressed(): boolean {
        return this.metaPressed;
    }

    public getButton(): number | undefined {
        return this.button;
    }

    public getSrcObject(): Optional<EventTarget> {
        return Optional.ofNullable(this.srcObject);
    }

    public getSrcScreenY(): number {
        return this.srcScreenY === undefined ? 0 : this.srcScreenY;
    }

    public getSrcScreenX(): number {
        return this.srcScreenX === undefined ? 0 : this.srcScreenX;
    }

    public getSrcClientY(): number {
        return this.srcClientX === undefined ? 0 : this.srcClientX;
    }

    public getSrcClientX(): number {
        return this.srcClientY === undefined ? 0 : this.srcClientY;
    }

    public setModifiersData(event: MouseEvent): void {
        this.altPressed = event.altKey;
        this.shiftPressed = event.shiftKey;
        this.ctrlPressed = event.ctrlKey;
        this.metaPressed = event.metaKey;
    }

    public setPointData(event: MouseEvent): void {
        this.srcScreenY = event.screenY;
        this.srcScreenX = event.screenX;
        this.srcClientX = event.clientX;
        this.srcClientY = event.clientY;
        this.button = event.button;
        this.srcObject = event.target === null ? undefined : event.target;
        this.setModifiersData(event);
    }
}
