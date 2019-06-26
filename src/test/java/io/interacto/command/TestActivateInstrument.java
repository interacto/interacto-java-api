package io.interacto.command;

import org.junit.jupiter.api.Test;
import io.interacto.command.library.ActivateInstrument;
import io.interacto.instrument.Instrument;
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
