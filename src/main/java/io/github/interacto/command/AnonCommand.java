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
 * An anonymous command that takes an anonymous function as a parameter corresponding to the command to execute.
 * The goal of this command is to avoid the creation of a command class for a small cmd.
 * @author Arnaud Blouin
 */
public class AnonCommand extends CommandImpl {
	private final Runnable exec;

	public AnonCommand(final Runnable function) {
		super();
		exec = function;
	}

	@Override
	public boolean canDo() {
		return exec != null;
	}

	@Override
	protected void doCmdBody() {
		exec.run();
	}
}
