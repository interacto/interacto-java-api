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

import {ButtonBinder} from "./ButtonBinder";
import {TSInteraction} from "../interaction/TSInteraction";
import {FSM} from "../../src-core/fsm/FSM";
import {NodeBinder} from "./NodeBinder";
import {CommandImpl} from "../../src-core/command/CommandImpl";
import {AnonCmdBinder} from "./AnonCmdBinder";

/**
 * Creates binding builder to build a binding between a given interaction and the given command type.
 * This builder is dedicated to bind node interactions to commands.
 * Do not forget to call bind() at the end of the build to execute the builder.
 * @param cmdProducer The command to produce.
 * @param interaction The user interaction to perform on nodes
 * @return The binding builder. Cannot be null.
 * @throws NullPointerException If the given class is null.
 */
export function nodeBinder<C extends CommandImpl, I extends TSInteraction<FSM<Event>, {}>>(cmdProducer: () => C,
                                                                                           interaction: I): NodeBinder<C, I> {
    return new NodeBinder(cmdProducer, interaction);
}

/**
 * Creates binding builder to build a binding between a button interaction and the given command type.
 * Do not forget to call bind() at the end of the build to execute the builder.
 * @param cmdProducer The command to produce.
 * @return The binding builder. Cannot be null.
 * @throws NullPointerException If the given class is null.
 */
export function buttonBinder<C extends CommandImpl>(cmdProducer: () => C): ButtonBinder<C> {
    return new ButtonBinder<C>(cmdProducer);
}

/**
 * Creates binding builder to build a binding between a KeysPressure interaction (done on a Node) and the given command type.
 * Do not forget to call bind() at the end of the build to execute the builder.
 * @param cmd The anonymous command to produce.
 * @return The binding builder. Cannot be null.
 * @throws NullPointerException If the given class is null.
 */
export function anonCmdBinder<I extends TSInteraction<FSM<Event>, {}>>(cmd: () => void, interaction: I): AnonCmdBinder<I> {
    return new AnonCmdBinder(cmd, interaction);
}
