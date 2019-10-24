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
package io.github.interacto.command;

import org.junit.jupiter.api.Test;
import io.github.interacto.command.library.InstrumentCommand;
import io.github.interacto.instrument.Instrument;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TestInstrumentCommand<T extends InstrumentCommand> extends BaseCommandTest<InstrumentCommand> {
	@Test
	@Override
	public void testCanDo() throws SecurityException, IllegalArgumentException {
		cmd.setInstrument(Mockito.mock(Instrument.class));
		assertTrue(cmd.canDo());
	}

	@Override
	@Test
	public void testFlush() {
		cmd.setInstrument(Mockito.mock(Instrument.class));
		cmd.flush();
		assertNull(cmd.getInstrument());
	}

	@Test
	public void testGetSetInstrument() {
		final Instrument<?> ins = Mockito.mock(Instrument.class);
		cmd.setInstrument(ins);
		assertEquals(ins, cmd.getInstrument());
	}
}
