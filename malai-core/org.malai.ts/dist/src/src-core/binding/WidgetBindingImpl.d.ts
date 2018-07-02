import { InteractionImpl } from "../interaction/InteractionImpl";
import { WidgetBinding } from "./WidgetBinding";
import { Logger } from "typescript-logging";
import { FSM } from "../fsm/FSM";
import { CommandImpl } from "../command/CommandImpl";
import { Command } from "../command/Command";
import { InteractionData } from "../interaction/InteractionData";
export declare abstract class WidgetBindingImpl<C extends CommandImpl, I extends InteractionImpl<D, {}, FSM<{}>>, D extends InteractionData> implements WidgetBinding {
    protected loggerBinding: Logger | undefined;
    protected loggerCmd: Logger | undefined;
    protected readonly interaction: I;
    protected cmd: C | undefined;
    protected execute: boolean;
    protected async: boolean;
    private readonly cmdProducer;
    protected constructor(exec: boolean, interaction: I, cmdProducer: (i?: D) => C);
    logBinding(log: boolean): void;
    logCmd(log: boolean): void;
    logInteraction(log: boolean): void;
    isAsync(): boolean;
    setAsync(asyncCmd: boolean): void;
    clearEvents(): void;
    protected map(): C;
    abstract first(): void;
    then(): void;
    abstract when(): boolean;
    getInteraction(): I;
    getCommand(): C | undefined;
    isActivated(): boolean;
    isRunning(): boolean;
    isStrictStart(): boolean;
    protected unbindCmdAttributes(): void;
    private unbindCmdAttributesClass(clazz);
    fsmCancels(): void;
    fsmStarts(): void;
    fsmStops(): void;
    private executeCmd(cmd, async);
    protected abstract executeCmdAsync(cmd: Command): void;
    protected afterCmdExecuted(cmd: Command, ok: boolean): void;
    fsmUpdates(): void;
    uninstallBinding(): void;
    isExecute(): boolean;
    feedback(): void;
    setActivated(activ: boolean): void;
}
