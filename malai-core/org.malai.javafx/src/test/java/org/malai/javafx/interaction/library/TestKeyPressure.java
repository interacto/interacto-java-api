package org.malai.javafx.interaction.library;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.malai.javafx.MockitoExtension;
import org.malai.stateMachine.MustCancelStateMachineException;
import org.mockito.Mockito;

@ExtendWith(MockitoExtension.class)
public class TestKeyPressure extends BaseJfXInteractionTest<KeyPressure> {
	@Override
	protected KeyPressure createInteraction() {
		return new KeyPressure();
	}

	@Test
	public void testOneKeyPressEndsAndReinit() throws MustCancelStateMachineException {
		interaction.onKeyPressure(createKeyPressEvent("A", KeyCode.A), 0);
		Mockito.verify(handler, Mockito.times(1)).interactionStops();
		Mockito.verify(handler, Mockito.times(1)).interactionStarts();
	}

	@Test
	public void testTwoKeyPressEndsAndReinit() throws MustCancelStateMachineException {
		interaction.onKeyPressure(createKeyPressEvent("A", KeyCode.A), 0);
		interaction.onKeyPressure(createKeyPressEvent("B", KeyCode.B), 0);
		Mockito.verify(handler, Mockito.times(2)).interactionStops();
		Mockito.verify(handler, Mockito.times(2)).interactionStarts();
	}
}
