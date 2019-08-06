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
