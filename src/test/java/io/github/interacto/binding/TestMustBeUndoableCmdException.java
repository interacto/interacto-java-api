package io.github.interacto.binding;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestMustBeUndoableCmdException {
	@Test
	public void testToStringNotNullOnNull() {
		final MustBeUndoableCmdException ex = new MustBeUndoableCmdException(null);
		assertNotNull(ex.toString());
	}

	@Test
	public void testToStringNotNullOnClass() {
		final MustBeUndoableCmdException ex = new MustBeUndoableCmdException(Class.class);
		assertNotNull(ex.toString());
	}
}
