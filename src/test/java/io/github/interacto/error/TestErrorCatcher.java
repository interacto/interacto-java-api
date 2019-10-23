package io.github.interacto.error;

import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestErrorCatcher {
	ErrorCatcher instance;

	@BeforeEach
	void setUp() {
		instance = ErrorCatcher.getInstance();
	}

	@Test
	void testNotNull() {
		assertNotNull(instance);
	}

	@Test
	void testGetSetInstanceKO() {
		ErrorCatcher.setInstance(null);
		assertEquals(instance, ErrorCatcher.getInstance());
	}

	@Test
	void testGetSetInstanceOK() {
		final var newinstance = new ErrorCatcher();
		ErrorCatcher.setInstance(newinstance);
		assertEquals(newinstance, ErrorCatcher.getInstance());
	}

	@Test
	void testErrorsOK() {
		assertNotNull(instance.getErrors());
	}

	@Test
	void testErrors() {
		final List<Throwable> errors = new ArrayList<>();
		final Disposable errorStream = instance.getErrors().subscribe(errors::add);
		final var ex = new NullPointerException();
		instance.reportError(ex);
		errorStream.dispose();

		assertEquals(1, errors.size());
		assertEquals(ex, errors.get(0));
	}

	@Test
	void testErrorsKO() {
		final List<Throwable> errors = new ArrayList<>();
		final Disposable errorStream = instance.getErrors().subscribe(errors::add);
		instance.reportError(null);
		errorStream.dispose();

		assertTrue(errors.isEmpty());
	}
}
