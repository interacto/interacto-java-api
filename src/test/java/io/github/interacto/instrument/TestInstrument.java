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
package io.github.interacto.instrument;

import io.github.interacto.binding.WidgetBinding;
import io.github.interacto.command.CmdStub;
import io.github.interacto.error.ErrorCatcher;
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestInstrument {
	InstrumentImpl<WidgetBinding<CmdStub>> instrument;
	WidgetBinding<CmdStub> binding;
	Disposable errorStream;
	List<Throwable> errors;

	@BeforeEach
	void setUp() {
		errors = new ArrayList<>();
		errorStream = ErrorCatcher.getInstance().getErrors().subscribe(errors::add);
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
		assertTrue(errors.isEmpty());
		errorStream.dispose();
	}

	@Test
	void testDefaultValue() {
		assertFalse(instrument.isActivated());
		assertFalse(instrument.isModified());
		assertFalse(instrument.hasWidgetBindings());
		assertTrue(instrument.getWidgetBindings().isEmpty());
		assertTrue(instrument.disposables.isEmpty());
		assertTrue(errors.isEmpty());
	}

	@Test
	void testActivated() {
		instrument.setActivated(true);
		assertTrue(instrument.isActivated());
		assertEquals(1, instrument.getNbWidgetBindings());
		assertFalse(instrument.getWidgetBindings().isEmpty());
		assertEquals(binding, instrument.getWidgetBindings().get(0));
		Mockito.verify(binding, Mockito.times(1)).setActivated(true);
		assertTrue(errors.isEmpty());
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

	@Test
	void testHasWidgetBindings() {
		instrument.addBinding(Mockito.mock(WidgetBinding.class));
		assertTrue(instrument.hasWidgetBindings());
	}

	@Test
	void testAddBindingNull() {
		instrument = new InstrumentImpl<>() {
			@Override
			protected void configureBindings() {
			}
		};
		instrument.setActivated(true);
		instrument.addBinding(null);
		assertFalse(instrument.hasWidgetBindings());
	}

	@Test
	void testSetActivatedWithNoBinding() {
		instrument = new InstrumentImpl<>() {
			@Override
			protected void configureBindings() {
			}
		};
		instrument.setActivated(true);
		assertTrue(instrument.isActivated());
		assertFalse(instrument.hasWidgetBindings());
	}

	@Test
	void testSetActivatedWithBinding() {
		instrument.setActivated(true);
		assertTrue(instrument.isActivated());
		assertTrue(instrument.hasWidgetBindings());
	}

	@Test
	void testSetActivatedWithBindingAlreadyAdded() {
		instrument.addBinding(Mockito.mock(WidgetBinding.class));
		instrument.setActivated(true);
		assertTrue(instrument.isActivated());
		assertEquals(2, instrument.getNbWidgetBindings());
	}

	@Test
	void testSetActivatedTwoTimesWithBindingAlreadyAdded() {
		instrument.addBinding(Mockito.mock(WidgetBinding.class));
		instrument.setActivated(true);
		instrument.setActivated(true);
		assertTrue(instrument.isActivated());
		assertEquals(2, instrument.getNbWidgetBindings());
	}

	@Test
	void testSave() {
		instrument.save(false, null, null, null);
	}

	@Test
	void testLoad() {
		instrument.load(false, null, null);
	}

	@Test
	void testReinit() {
		instrument.reinit();
	}

	@Test
	void testDisposeBinding() {
		final var disposable1 = Mockito.mock(Disposable.class);
		final var disposable2 = Mockito.mock(Disposable.class);
		final var binding = Mockito.mock(WidgetBinding.class);
		instrument.addBinding(binding);
		instrument.addDisposable(disposable2);
		instrument.addDisposable(disposable1);
		Mockito.when(disposable1.isDisposed()).thenReturn(true);

		instrument.uninstallBindings();

		Mockito.verify(binding, Mockito.times(1)).uninstallBinding();
		assertFalse(instrument.hasWidgetBindings());
		Mockito.verify(disposable1, Mockito.times(1)).isDisposed();
		Mockito.verify(disposable2, Mockito.times(1)).isDisposed();
		Mockito.verify(disposable2, Mockito.times(1)).dispose();

	}
}
