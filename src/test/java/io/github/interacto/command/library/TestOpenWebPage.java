/*
 * Interacto
 * Copyright (C) 2020 Arnaud Blouin
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

import io.github.interacto.command.Command;
import io.github.interacto.error.ErrorCatcher;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestOpenWebPage {
	OpenWebPage cmd;
	ErrorCatcher catcher;
	ErrorCatcher mementoCatcher;

	@BeforeEach
	void setUp() {
		mementoCatcher = ErrorCatcher.getInstance();
		catcher = Mockito.mock(ErrorCatcher.class);
		ErrorCatcher.setInstance(catcher);
	}

	@AfterEach
	void tearDown() {
		ErrorCatcher.setInstance(mementoCatcher);
	}


	@Nested
	class CannotDo {

		@Test
		void testNullDesktop() {
			cmd = new OpenWebPage(null, Mockito.mock(URI.class));
			assertFalse(cmd.canDo());
		}

		@Test
		void testNullURI() {
			cmd = new OpenWebPage(Mockito.mock(Desktop.class), null);
			assertFalse(cmd.canDo());
		}

		@Test
		void testDoesNotSupportBrowse() {
			cmd = new OpenWebPage(Mockito.mock(Desktop.class), Mockito.mock(URI.class));
			assertFalse(cmd.canDo());
		}
	}

	@Nested
	class DoCmd {
		Desktop desktop;
		URI uri;

		@BeforeEach
		void setUp() {
			desktop = Mockito.mock(Desktop.class);
			uri = Mockito.mock(URI.class);
			Mockito.when(desktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);
			cmd = new OpenWebPage(desktop, uri);
		}

		@Test
		public void testFlush() {
			cmd.flush();
			assertEquals(Command.CmdStatus.FLUSHED, cmd.getStatus());
		}

		@Test
		public void testDo() throws IOException {
			cmd.doIt();
			Mockito.verify(desktop, Mockito.times(1)).browse(uri);
		}

		@Test
		public void testHadEffects() {
			cmd.doIt();
			assertTrue(cmd.hadEffect());
		}

		@Test
		public void testDoBadURI() throws IOException {
			Mockito.doThrow(IOException.class).when(desktop).browse(uri);
			cmd.doIt();
			assertFalse(cmd.hadEffect());
			Mockito.verify(catcher, Mockito.times(1)).reportError(Mockito.any());
		}

		@Test
		public void testIsRegisterable() {
			assertEquals(Command.RegistrationPolicy.NONE, cmd.getRegistrationPolicy());
		}
	}
}
