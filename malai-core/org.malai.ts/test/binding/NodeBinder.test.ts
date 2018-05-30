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

import {Click} from "../../src/interaction/library/Click";
import {NodeBinder} from "../../src/binding/NodeBinder";
import {DoubleClick} from "../../src/interaction/library/DoubleClick";
import {StubCmd} from "../command/StubCmd";
import {PointData} from "../../src/interaction/library/PointData";
import {EventRegistrationToken} from "../../src/interaction/Events";
import {createMouseEvent} from "../interaction/StubEvents";

jest.mock("../command/StubCmd");

let widget: HTMLElement;

beforeEach(() => {
    jest.clearAllMocks();
    document.documentElement.innerHTML = "<html><div><canvas id='canvas1' /></div></html>";
    const elt = document.getElementById("canvas1");
    if (elt !== null) {
        widget = elt;
    }
});

test("Node binder ok with click", () => {
    new NodeBinder<StubCmd, Click, PointData>(new Click(), () => new StubCmd()).on(widget).bind();
    widget.dispatchEvent(createMouseEvent(EventRegistrationToken.MouseDown, widget));
    widget.dispatchEvent(createMouseEvent(EventRegistrationToken.MouseUp, widget));
    expect(StubCmd.prototype.doIt).toHaveBeenCalledTimes(1);
});

test("Node binder ok with double-click", () => {
    new NodeBinder<StubCmd, DoubleClick, PointData>(new DoubleClick(), () => new StubCmd()).on(widget).bind();
    widget.dispatchEvent(createMouseEvent(EventRegistrationToken.MouseDown, widget));
    widget.dispatchEvent(createMouseEvent(EventRegistrationToken.MouseUp, widget));
    widget.dispatchEvent(createMouseEvent(EventRegistrationToken.MouseDown, widget));
    widget.dispatchEvent(createMouseEvent(EventRegistrationToken.MouseUp, widget));
    expect(StubCmd.prototype.doIt).toHaveBeenCalledTimes(1);
});
