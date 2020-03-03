/*
 * Interacto
 * Copyright (C) 2020 Arnaud Blouin
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
package io.github.interacto.command;

/**
 * Base implementation of the Command interface.
 * @author Arnaud BLOUIN
 */
public abstract class CommandImpl implements Command {
	/** The state of the command. */
	protected CmdStatus status;

	/**
	 * The default constructor.
	 * Initialises the current status to created.
	 */
	public CommandImpl() {
		super();
		status = CmdStatus.CREATED;
	}


	@Override
	public void flush() {
		status = CmdStatus.FLUSHED;
	}

	/**
	 * Commands may need to create a memento before their first execution.
	 * This is the goal of the operation that should be overriden.
	 * This operator is called a single time before the first execution of the command.
	 */
	protected void createMemento() {
		// To Override.
	}

	@Override
	public boolean doIt() {
		final boolean ok;

		if((status == CmdStatus.CREATED || status == CmdStatus.EXECUTED) && canDo()) {
			if(status == CmdStatus.CREATED) {
				createMemento();
			}

			ok = true;
			doCmdBody();
			status = CmdStatus.EXECUTED;
		}else {
			ok = false;
		}

		return ok;
	}

	@Override
	public boolean canDo() {
		return true;
	}

	/**
	 * This method contains the statements to execute the command.
	 * This method is automatically called by DoIt and must not be called explicitly.
	 * @since 0.1
	 */
	protected abstract void doCmdBody();

	@Override
	public RegistrationPolicy getRegistrationPolicy() {
		return hadEffect() ? RegistrationPolicy.LIMITED : RegistrationPolicy.NONE;
	}

	@Override
	public boolean hadEffect() {
		return isDone();
	}


	@Override
	public boolean unregisteredBy(final Command cmd) {
		return false;
	}


	@Override
	public void done() {
		if(status == CmdStatus.CREATED || status == CmdStatus.EXECUTED) {
			status = CmdStatus.DONE;
		}
	}


	@Override
	public boolean isDone() {
		return status == CmdStatus.DONE;
	}


	@Override
	public String toString() {
		return getClass().getSimpleName();
	}


	@Override
	public void cancel() {
		status = CmdStatus.CANCELLED;
	}


	@Override
	public CmdStatus getStatus() {
		return status;
	}
}
