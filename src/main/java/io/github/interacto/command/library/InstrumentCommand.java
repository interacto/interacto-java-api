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
package io.github.interacto.command.library;

import io.github.interacto.instrument.Instrument;
import io.github.interacto.command.CommandImpl;

/**
 * This command manipulates an instrument.
 * @author Arnaud Blouin
 */
public abstract class InstrumentCommand extends CommandImpl {
	/** The manipulated instrument. */
	protected final Instrument<?> instrument;

	public InstrumentCommand(final Instrument<?> instrument) {
		super();
		this.instrument = instrument;
	}

	@Override
	public boolean canDo() {
		return instrument != null;
	}
}
