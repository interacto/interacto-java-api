/*
 * Interacto
 * Copyright (C) 2019 Arnaud Blouin
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.interacto.binding;

import io.github.interacto.command.CmdHandler;
import io.github.interacto.command.Command;
import io.github.interacto.command.CommandsRegistry;
import io.github.interacto.fsm.CancelFSMException;
import io.github.interacto.interaction.InteractionData;
import io.github.interacto.interaction.InteractionImpl;
import io.github.interacto.undo.Undoable;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The base class to do widget bindings, i.e. bindings between user interactions and (undoable) commands.
 * @param <C> The type of the command that will produce this widget binding.
 * @param <I> The type of the interaction that will use this widget binding.
 * @author Arnaud BLOUIN
 */
public abstract class WidgetBindingImpl<C extends Command, I extends InteractionImpl<D, ?, ?>, D extends InteractionData> implements WidgetBinding {
	private static Logger logger = Logger.getLogger(WidgetBinding.class.getName());

	/**
	 * Sets the logger to use. Cannot be null.
	 * Does not change the loggers of existing widget bindings.
	 * This also sets this logger as the default logger for user interactions.
	 * @param logger The new logger to use.
	 */
	public static void setLogger(final Logger logger) {
		if(logger != null) {
			WidgetBindingImpl.logger = logger;
		}
	}

	protected Logger loggerBinding;

	protected Logger loggerCmd;

	protected boolean activated;

	/** The source interaction. */
	protected final I interaction;

	/** The current command in progress. */
	protected C cmd;

	/** The possible command handler. May be null */
	protected CmdHandler cmdHandler;

	/** Specifies whether the command must be executed on each step of the interaction. */
	protected final boolean continuousCmdExec;

	/** Defines whether the command must be executed in a specific thread. */
	protected boolean async;

	/** A function that produces commands. */
	protected final Function<D, C> cmdProducer;


	/**
	 * Creates a widget binding.
	 * @param continuousExecution Specifies whether the command must be executed on each step of the interaction.
	 * @param cmdCreation The type of the command that will be created. Used to instantiate the cmd by reflexivity.
	 * The class must be public and must have a constructor with no parameter.
	 * @param interaction The user interaction of the binding.
	 * @throws IllegalArgumentException If the given interaction or instrument is null.
	 */
	public WidgetBindingImpl(final boolean continuousExecution, final Function<D, C> cmdCreation, final I interaction) {
		super();

		if(cmdCreation == null || interaction == null) {
			throw new IllegalArgumentException();
		}

		cmdProducer = cmdCreation;
		this.interaction = interaction;
		cmd = null;
		cmdHandler = null;
		continuousCmdExec = continuousExecution;
		activated = true;
		this.interaction.getFsm().addHandler(this);
		async = false;
	}

	public void logBinding(final boolean log) {
		if(log) {
			if(loggerBinding == null) {
				loggerBinding = logger;
			}
		}else {
			loggerBinding = null;
		}
	}

	public void logCmd(final boolean log) {
		if(log) {
			if(loggerCmd == null) {
				loggerCmd = logger;
			}
		}else {
			loggerCmd = null;
		}
	}

	public void logInteraction(final boolean log) {
		interaction.log(log);
	}

	/**
	 * Whether the command must be executed in a specific thread.
	 * @return True: the command will be executed asynchronously.
	 */
	public boolean isAsync() {
		return async;
	}

	/**
	 * Sets whether the command must be executed in a specific thread.
	 * @param asyncCmd True: the command will be executed asynchronously.
	 */
	public void setAsync(final boolean asyncCmd) {
		async = asyncCmd;
	}

	@Override
	public void clearEvents() {
		interaction.fullReinit();
	}


	/**
	 * creates the command of the widget binding. If the attribute 'cmd' is not null, nothing will be done.
	 * @return The created command or null if problems occurred.
	 */
	protected C createCommand() {
		return cmdProducer.apply(interaction.getData());
	}


	@Override
	public void first() {
		// to override.
	}


	@Override
	public void then() {
		// to override.
	}

	@Override
	public void end() {
		// to override.
	}

	@Override
	public void cancel() {
		// to override.
	}

	@Override
	public void endOrCancel() {
		// to override.
	}

	@Override
	public abstract boolean when();


	@Override
	public I getInteraction() {
		return interaction;
	}


	@Override
	public C getCommand() {
		return cmd;
	}


	@Override
	public boolean isActivated() {
		return activated;
	}

	@Override
	public boolean isRunning() {
		return interaction.isRunning();
	}


	@Override
	public boolean isStrictStart() {
		return false;
	}

	/**
	 * Manages to automatically unbind commands' attributes tagged with AutoUnbind
	 */
	protected abstract void unbindCmdAttributes();

	@Override
	public void fsmCancels() {
		if(cmd != null) {
			if(loggerBinding != null) {
				loggerBinding.log(Level.INFO, "Binding cancelled");
			}

			cmd.cancel();
			if(loggerCmd != null) {
				loggerCmd.log(Level.INFO, "Command cancelled");
			}
			unbindCmdAttributes();

			// The instrument is notified about the cancel of the command.
			if(cmdHandler != null) {
				cmdHandler.onCmdCancelled(cmd);
			}

			if(isContinuousCmdExec() && cmd.hadEffect()) {
				if(cmd instanceof Undoable) {
					((Undoable) cmd).undo();
					if(loggerCmd != null) {
						loggerCmd.log(Level.INFO, "Command undone");
					}
				}else {
					throw new MustBeUndoableCmdException(cmd.getClass());
				}
			}

			cmd = null;
			cancel();
			endOrCancel();
		}
	}


	@Override
	public void fsmStarts() throws CancelFSMException {
		if(!isActivated()) {
			return;
		}

		final boolean ok = when();

		if(loggerBinding != null) {
			loggerBinding.log(Level.INFO, "Starting binding: " + ok);
		}

		if(ok) {
			cmd = createCommand();
			first();
			if(loggerCmd != null) {
				loggerCmd.log(Level.INFO, "Command created and init: " + cmd);
			}
		}else {
			if(isStrictStart()) {
				if(loggerBinding != null) {
					loggerBinding.log(Level.INFO, "Cancelling starting interaction: " + interaction);
				}
				throw new CancelFSMException();
			}
		}
	}


	@Override
	public void fsmStops() {
		if(!isActivated()) {
			return;
		}

		final boolean ok = when();
		if(loggerBinding != null) {
			loggerBinding.log(Level.INFO, "Binding stops with condition: " + ok);
		}
		if(ok) {
			if(cmd == null) {
				cmd = createCommand();
				first();
				if(loggerCmd != null) {
					loggerCmd.log(Level.INFO, "Command created and init: " + cmd);
				}
			}

			if(!continuousCmdExec) {
				then();
				if(loggerCmd != null) {
					loggerCmd.log(Level.INFO, "Command updated: " + cmd);
				}
			}

			executeCmd(cmd, async);
			unbindCmdAttributes();
			cmd = null;
		}else {
			if(cmd != null) {
				if(loggerCmd != null) {
					loggerCmd.log(Level.INFO, "Cancelling the command: " + cmd);
				}
				cmd.cancel();
				unbindCmdAttributes();
				if(cmdHandler != null) {
					cmdHandler.onCmdCancelled(cmd);
				}
				cmd = null;
			}
		}
	}


	private void executeCmd(final Command cmd, final boolean async) {
		if(async) {
			executeCmdAsync(cmd);
		}else {
			afterCmdExecuted(cmd, cmd.doIt());
		}
	}

	protected abstract void executeCmdAsync(final Command cmd);

	protected void afterCmdExecuted(final Command cmd, final boolean ok) {
		if(loggerCmd != null) {
			loggerCmd.log(Level.INFO, "Command execution did it: " + ok);
		}

		if(ok) {
			if(cmdHandler != null) {
				cmdHandler.onCmdExecuted(cmd);
			}
			cmd.done();
			end();
			endOrCancel();
			if(cmdHandler != null) {
				cmdHandler.onCmdDone(cmd);
			}
		}

		final boolean hadEffect = cmd.hadEffect();

		if(loggerCmd != null) {
			loggerCmd.log(Level.INFO, "Command execution had effect: " + hadEffect);
		}
		if(hadEffect) {
			if(cmd.getRegistrationPolicy() != Command.RegistrationPolicy.NONE) {
				CommandsRegistry.INSTANCE.addCommand(cmd, cmdHandler);
				if(cmdHandler != null) {
					cmdHandler.onCmdAdded(cmd);
				}
			}else {
				CommandsRegistry.INSTANCE.unregisterCommand(cmd);
			}
			cmd.followingCmds().forEach(cmdFollow -> executeCmd(cmdFollow, false));
		}
	}


	@Override
	public void fsmUpdates() {
		if(!isActivated()) {
			return;
		}

		final boolean ok = when();

		if(loggerBinding != null) {
			loggerBinding.log(Level.INFO, "Binding updates with condition: " + ok);
		}

		if(ok) {
			if(cmd == null) {
				if(loggerCmd != null) {
					loggerCmd.log(Level.INFO, "Command creation");
				}
				cmd = createCommand();
				first();
			}

			if(loggerCmd != null) {
				loggerCmd.log(Level.INFO, "Command update");
			}

			then();

			if(continuousCmdExec && cmd.canDo()) {
				if(loggerCmd != null) {
					loggerCmd.log(Level.INFO, "Command execution");
				}
				cmd.doIt();
				if(cmdHandler != null) {
					cmdHandler.onCmdExecuted(cmd);
				}
			}
		}
	}

	@Override
	public void uninstallBinding() {
		setActivated(false);
		loggerCmd = null;
		loggerBinding = null;
	}

	@Override
	public boolean isContinuousCmdExec() {
		return continuousCmdExec;
	}


	@Override
	public void setActivated(final boolean activated) {
		this.activated = activated;

		if(loggerBinding != null) {
			loggerBinding.log(Level.INFO, "Binding Activated: " + activated);
		}

		interaction.setActivated(activated);
		if(!activated && cmd != null) {
			unbindCmdAttributes();
			cmd.flush();
			cmd = null;
		}
	}

	@Override
	public void setCmdHandler(final CmdHandler cmdHandler) {
		this.cmdHandler = cmdHandler;
	}
}
