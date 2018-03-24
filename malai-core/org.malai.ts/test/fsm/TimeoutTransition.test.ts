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

import {StubEvent} from "./StubEvent";
import {FSM} from "../../src-core/fsm/FSM";
import {InputState} from "../../src-core/fsm/InputState";
import {OutputState} from "../../src-core/fsm/OutputState";
import {TimeoutTransition} from "../../src-core/fsm/TimeoutTransition";
import {StdState} from "../../src-core/fsm/StdState";

jest.mock("../../src-core/fsm/FSM");
jest.mock("../../src-core/fsm/StdState");

let evt: TimeoutTransition<StubEvent>;
let src: OutputState<StubEvent>;
let tgt: InputState<StubEvent>;
let fsm: FSM<StubEvent>;

beforeEach(() => {
    jest.useFakeTimers();
    fsm = new FSM<StubEvent>();
    src = new StdState<StubEvent>(fsm, "src");
    tgt = new StdState<StubEvent>(fsm, "tgt");
    src.getFSM = jest.fn().mockReturnValue(fsm);
    tgt.getFSM = jest.fn().mockReturnValue(fsm);
    evt = new TimeoutTransition<StubEvent>(src, tgt, () => 500);
});

test("testIsGuardOKAfterTimeout", () => {
    evt.startTimeout();
    jest.runOnlyPendingTimers();
    expect(evt.isGuardOK(undefined)).toBeTruthy();
    expect(setTimeout).toHaveBeenCalledTimes(1);
    expect(setTimeout).toHaveBeenLastCalledWith(expect.any(Function), 500);
});

test("testIsGuardKOBeforeTimeout", () => {
    evt.startTimeout();
    expect(evt.isGuardOK(undefined)).toBeFalsy();
});

test("testacceptOKAfterTimeout", () => {
    evt.startTimeout();
    expect(setTimeout).toHaveBeenCalledTimes(1);
    expect(setTimeout).toHaveBeenLastCalledWith(expect.any(Function), 500);
    setTimeout(() => {
    }, 100);
    jest.runOnlyPendingTimers();
    expect(evt.accept(undefined)).toBeTruthy();
});

test("testacceptKOBeforeTimeout", () => {
    evt.startTimeout();
    expect(evt.accept(undefined)).toBeFalsy();
});

test("testStopTimeout", () => {
    evt.startTimeout();
    evt.stopTimeout();
    setTimeout(() => {
    }, 100);
    jest.runOnlyPendingTimers();
    expect(evt.isGuardOK(undefined)).toBeFalsy();
});

test("testGetAcceptEventsEmpty", () => {
    expect(evt.getAcceptedEvents().size).toEqual(0);
});

test("testExecuteWithoutTimeout", () => {
    expect(evt.execute(undefined).isPresent()).toBeFalsy();
});

test("testExecuteWithTimeout", () => {
    evt.startTimeout();
    setTimeout(() => {
    }, 100);
    jest.runOnlyPendingTimers();
    expect(evt.execute(undefined).get()).toEqual(tgt);
});

test("testExecuteCallFSMTimeout", () => {
    evt.startTimeout();
    setTimeout(() => {
    }, 100);
    jest.runOnlyPendingTimers();
    expect(fsm.onTimeout).toHaveBeenCalledTimes(1);
});

test("testExecuteCallsStatesMethods", () => {
    evt.startTimeout();
    setTimeout(() => {
    }, 100);
    jest.runOnlyPendingTimers();
    evt.execute(undefined);
    expect(src.exit).toHaveBeenCalledTimes(1);
    expect(tgt.enter).toHaveBeenCalledTimes(1);
});
