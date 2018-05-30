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


import {StubFSMHandler} from "../fsm/StubFSMHandler";
import {EventRegistrationToken} from "../../src/interaction/Events";
import {createKeyEvent} from "./StubEvents";
import {FSMHandler} from "../../src-core/fsm/FSMHandler";
import {KeyPressed} from "../../src/interaction/library/KeyPressed";

jest.mock("../fsm/StubFSMHandler");

let interaction: KeyPressed;
let text: HTMLElement;
let handler: FSMHandler;

beforeEach(() => {
    jest.clearAllMocks();
    handler = new StubFSMHandler();
    interaction = new KeyPressed(false);
    interaction.log(true);
    interaction.getFsm().log(true);
    interaction.getFsm().addHandler(handler);
    document.documentElement.innerHTML = "<html><div><textarea id='text1'></textarea></div></html>";
    const elt = document.getElementById("text1");
    if (elt !== null) {
        text = elt;
    }
});

test("Appuyer sur A dans la textarea debute et fini l'interaction", () => {
    interaction.registerToNodes([text]);
    text.dispatchEvent(createKeyEvent(EventRegistrationToken.KeyDown, "a"));
    expect(handler.fsmStarts).toHaveBeenCalledTimes(1);
    expect(handler.fsmStops).toHaveBeenCalledTimes(1);
});

test("La touche utiliser est bien la touche enregister", () => {
    interaction.registerToNodes([text]);
    text.dispatchEvent(createKeyEvent(EventRegistrationToken.KeyDown, "a"));
    expect(interaction.getData().getKey()).toEqual("a");
});
