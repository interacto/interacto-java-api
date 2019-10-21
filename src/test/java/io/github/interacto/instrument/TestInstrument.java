package io.github.interacto.instrument;

import io.github.interacto.binding.WidgetBinding;
import io.github.interacto.command.CommandImplStub;
import io.github.interacto.error.ErrorCatcher;
import io.github.interacto.error.ErrorNotifier;
import io.reactivex.disposables.Disposable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestInstrument {
	InstrumentImpl<WidgetBinding<CommandImplStub>> instrument;
	WidgetBinding<CommandImplStub> binding;
	ErrorNotifier error;

	@BeforeEach
	void setUp() {
		error = Mockito.mock(ErrorNotifier.class);
		ErrorCatcher.INSTANCE.setNotifier(error);
		binding = Mockito.mock(WidgetBinding.class);
		instrument = new InstrumentImpl<>() {
			@Override
			protected void configureBindings() {
				addBinding(binding);
			}
		};
	}

	@AfterEach
	void tearDown() {
		ErrorCatcher.INSTANCE.setNotifier(null);
	}

	@Test
	void testDefaultValue() {
		assertFalse(instrument.isActivated());
		assertFalse(instrument.isModified());
		assertFalse(instrument.hasWidgetBindings());
		assertTrue(instrument.getWidgetBindings().isEmpty());
		assertTrue(instrument.disposables.isEmpty());
		Mockito.verify(error, Mockito.never()).onException(Mockito.any());
	}

	@Test
	void testActivated() {
		instrument.setActivated(true);
		assertTrue(instrument.isActivated());
		assertEquals(1, instrument.getNbWidgetBindings());
		assertFalse(instrument.getWidgetBindings().isEmpty());
		assertEquals(binding, instrument.getWidgetBindings().get(0));
		Mockito.verify(binding, Mockito.times(1)).setActivated(true);
		Mockito.verify(error, Mockito.never()).onException(Mockito.any());
	}

	@Test
	void testSetDeactivated() {
		instrument.setActivated(true);
		instrument.setActivated(false);

		Mockito.verify(binding, Mockito.times(1)).setActivated(false);
	}

	@Test
	void testModified() {
		instrument.setModified(true);
		assertTrue(instrument.isModified());
	}

	@Test
	void testAddDisposable() {
		final Disposable d = Mockito.mock(Disposable.class);
		instrument.addDisposable(d);

		assertFalse(instrument.disposables.isEmpty());
		assertEquals(d, instrument.disposables.iterator().next());
	}

	@Test
	void testAddDisposableKO() {
		instrument.addDisposable(null);
		assertTrue(instrument.disposables.isEmpty());
	}

	@Test
	void testUninstall() {
		final Disposable d = Mockito.mock(Disposable.class);

		instrument.setActivated(true);
		instrument.addDisposable(d);
		instrument.uninstallBindings();

		assertFalse(instrument.hasWidgetBindings());
		assertTrue(instrument.disposables.isEmpty());
		Mockito.verify(d, Mockito.times(1)).isDisposed();
		Mockito.verify(d, Mockito.times(1)).dispose();
		Mockito.verify(binding, Mockito.times(1)).uninstallBinding();
	}

	@Test
	void testClearEvents() {
		instrument.setActivated(true);
		instrument.clearEvents();
		Mockito.verify(binding, Mockito.times(1)).clearEvents();
	}
}
