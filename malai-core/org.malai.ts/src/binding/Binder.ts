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

import {TSInteraction} from "../interaction/TSInteraction";
import {MArray} from "../util/ArrayUtil";
import {LogLevel} from "../../src-core/logging/LogLevel";
import {TSWidgetBinding} from "./TSWidgetBinding";
import {AnonNodeBinding} from "./AnonNodeBinding";
import {FSM} from "../../src-core/fsm/FSM";
import {CommandImpl} from "../../src-core/command/CommandImpl";

/**
 * The base class that defines the concept of binding builder (called binder).
 * @param <W> The type of the widgets.
 * @param <A> The type of the command to produce.
 * @param <I> The type of the user interaction to bind.
 * @author Arnaud Blouin
 */
export abstract class Binder<C extends CommandImpl, I extends TSInteraction<FSM<Event>, {}>, B extends Binder<C, I, B>> {
    protected initCmd: (i: I, c: C | undefined) => void;
    protected checkConditions: (i: I) => boolean;
    protected readonly widgets: MArray<EventTarget>;
    protected readonly cmdClass: () => C;
    protected readonly interaction: I;
    protected _async: boolean;
    protected onEnd: (i: I, c: C | undefined) => void;
// protected List<ObservableList<? extends Node>> additionalWidgets;
    protected readonly logLevels: Set<LogLevel>;

    protected constructor(interaction: I, cmdProducer: () => C) {
        this.cmdClass = cmdProducer;
        this.interaction = interaction;
        this.widgets = new MArray();
        this._async = false;
        this.checkConditions = () => true;
        this.initCmd = () => {};
        this.onEnd = () => {};
        this.logLevels = new Set<LogLevel>();
    }

    /**
     * Specifies the widgets on which the binding must operate.
     * @param widget The widgets involve in the bindings.
     * @return The builder to chain the building configuration.
     */
    public on(widget: EventTarget): B {
        this.widgets.push(widget);
        return this as {} as B;
    }


// /**
//  * Specifies the observable list that will contain the widgets on which the binding must operate.
//  * When a widget is added to this list, the added widget is binded to this binding.
//  * When widget is removed from this list, this widget is unbinded from this binding.
//  * @param widgets The observable list of the widgets involved in the bindings.
//  * @return The builder to chain the building configuration.
//  */
// public on(final ObservableList<? extends Node> widgets): B {
//     if(additionalWidgets == null) {
//         additionalWidgets = new ArrayList<>();
//     }
//     additionalWidgets.add(widgets);
//     return this as {} as B;
// }


    /**
     * Specifies the initialisation of the command when the interaction starts.
     * Each time the interaction starts, an instance of the command is created and configured by the given callback.
     * @param initCmdFct The callback method that initialises the command.
     * This callback takes as arguments both the command and interaction involved in the binding.
     * @return The builder to chain the building configuration.
     */
    public first(initCmdFct: (i: I, c: C) => void): B {
        this.initCmd = initCmdFct;
        return this as {} as B;
    }

    /**
     * Specifies the conditions to fulfill to initialise, update, or execute the command while the interaction is running.
     * @param checkCmd The predicate that checks whether the command can be initialised, updated, or executed.
     * This predicate takes as arguments the ongoing user interaction involved in the binding.
     * @return The builder to chain the building configuration.
     */
    public when(checkCmd: (i: I) => boolean): B {
        this.checkConditions = checkCmd;
        return this as {} as B;
    }


    /**
     * Specifies that the command will be executed in a separated threads.
     * Beware of UI modifications: UI changes must be done in the JFX UI thread.
     * @return The builder to chain the building configuration.
     */
    public async(): B {
        this._async = true;
        return this as {} as B;
    }

    /**
     * Specifies what to do end when an interaction ends (when the last event of the interaction has occurred, but just after
     * the interaction is reinitialised and the command finally executed and discarded / saved).
     * @param onEndFct The callback method to specify what to do when an interaction ends.
     * @return The builder to chain the building configuration.
     */
    public end(onEndFct: (i: I, c: C) => void): B {
        this.onEnd = onEndFct;
        return this as {} as B;
    }

    /**
     * Specifies the loggings to use.
     * Several call to 'log' can be done to log different parts:
     * log(LogLevel.INTERACTION).log(LogLevel.COMMAND)
     * @param level The logging level to use.
     * @return The builder to chain the building configuration.
     */
    public log(level: LogLevel): B {
        this.logLevels.add(level);
        return this as {} as B;
    }

    /**
     * Executes the builder to create and install the binding on the instrument.
     * @throws IllegalArgumentException On issues while creating the commands.
     * @throws InstantiationException On issues while creating the commands.
     */
    public bind(): TSWidgetBinding<C, I> {
        return new AnonNodeBinding<C, I>(false, this.interaction, this.cmdClass, this.initCmd, () => {},
            this.checkConditions, this.onEnd, () => {}, () => {}, () => {},
            this.widgets, this._async, false, new Array(...this.logLevels));
    }
}
