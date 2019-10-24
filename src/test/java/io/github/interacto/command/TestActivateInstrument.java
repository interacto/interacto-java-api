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
import io.github.interacto.command.library.ActivateInstrument;
import io.github.interacto.instrument.Instrument;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestActivateInstrument extends TestInstrumentCommand<ActivateInstrument> {
	@Override
	protected ActivateInstrument createCommand() {
		return new ActivateInstrument();
	}

	@Override
	@Test
	public void testDo() {
		final Instrument<?> ins = Mockito.mock(Instrument.class);
		cmd.setInstrument(ins);
		cmd.doIt();
		Mockito.verify(ins, Mockito.times(1)).setActivated(true);
	}

	@Override
	@Test
	public void testIsRegisterable() {
		assertEquals(Command.RegistrationPolicy.NONE, cmd.getRegistrationPolicy());
	}

	@Override
	@Test
	public void testHadEffect() {
		cmd.done();
		assertTrue(cmd.hadEffect());
	}

	@Test
	public void testHadNoEffectBeforeDone() {
		assertFalse(cmd.hadEffect());
	}
}
