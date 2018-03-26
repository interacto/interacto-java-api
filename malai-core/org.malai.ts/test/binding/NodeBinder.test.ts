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

import {StubAction} from "../action/StubAction";
import {Click} from "../../src/interaction/library/Click";
import {NodeBinder} from "../../src/binding/NodeBinder";
import {DoubleClick} from "../../src/interaction/library/DoubleClick";

jest.mock("../action/StubAction");

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
    new NodeBinder(() => new StubAction(), new Click()).on(widget).bind();
    // nodeBinder(() => new StubAction(), new Click()).on(widget).bind();
    widget.click();
    expect(StubAction.prototype.doIt).toHaveBeenCalledTimes(1);
});

test("Node binder ok with double-click", () => {
    new NodeBinder(() => new StubAction(), new DoubleClick()).on(widget).bind();
    widget.click();
    widget.click();
    expect(StubAction.prototype.doIt).toHaveBeenCalledTimes(1);
});
