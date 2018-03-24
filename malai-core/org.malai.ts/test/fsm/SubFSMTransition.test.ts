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

import {StubEvent, StubSubEvent1, StubSubEvent2} from "./StubEvent";
import {TerminalState} from "../../src-core/fsm/TerminalState";
import {StdState} from "../../src-core/fsm/StdState";
import {FSM} from "../../src-core/fsm/FSM";
import {SubFSMTransition} from "../../src-core/fsm/SubFSMTransition";
import {SubStubTransition1} from "./StubTransitionOK";
import {InputState} from "../../src-core/fsm/InputState";
import {Optional} from "../../src/util/Optional";

jest.mock("../../src-core/fsm/StdState");

let tr: SubFSMTransition<StubEvent>;
let fsm: FSM<StubEvent>;
let mainfsm: FSM<StubEvent>;
let s1: StdState<StubEvent>;
let s2: StdState<StubEvent>;
let subS: TerminalState<StubEvent>;

beforeEach(() => {
    fsm = new FSM();
    mainfsm = new FSM();
    s1 = new StdState<StubEvent>(mainfsm, "s1");
    s2 = new StdState<StubEvent>(mainfsm, "s2");
    s1.getFSM = jest.fn().mockReturnValue(mainfsm);
    s2.getFSM = jest.fn().mockReturnValue(mainfsm);
    mainfsm.addState(s1);
    mainfsm.addState(s2);
    tr = new SubFSMTransition(s1, s2, fsm);

    subS = new TerminalState(fsm, "sub1");
    new SubStubTransition1(fsm.initState, subS, true);
    fsm.addState(subS);
});

test("testInner", () => {
    expect(fsm.inner).toBeTruthy();
    expect(mainfsm.inner).toBeFalsy();
});

test("testAcceptFirstEvent", () => {
    expect(tr.accept(new StubSubEvent1())).toBeTruthy();
});

test("testNotAcceptFirstEvent", () => {
    expect(tr.accept(new StubSubEvent2())).toBeFalsy();
});

test("testGuardOKFirstEvent", () => {
    expect(tr.isGuardOK(new StubSubEvent1())).toBeTruthy();
});

test("testGuardKOFirstEvent", () => {
    expect(tr.isGuardOK(new StubSubEvent2())).toBeFalsy();
});

test("testExecuteFirstEventReturnsSubState", () => {
    const state : Optional<InputState<StubEvent>> = tr.execute(new StubSubEvent1());
    expect(state.isPresent()).toBeTruthy();
    expect(state.get()).toEqual(subS);
});

test("testExecuteFirstEventKO", () => {
    const state : Optional<InputState<StubEvent>> = tr.execute(new StubSubEvent2());
    expect(state.isPresent()).toBeFalsy();
});

test("testExecuteExitSrcState", () => {
    tr.execute(new StubSubEvent1());
    expect(s1.exit).toHaveBeenCalledTimes(1);
});

test("testExecuteEnterTgtState", () => {
    tr.execute(new StubSubEvent1());
    expect(s2.enter).toHaveBeenCalledTimes(1);
});

