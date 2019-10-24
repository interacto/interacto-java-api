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

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import io.github.interacto.command.library.OpenWebPage;
import io.github.interacto.HelperTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class TestOpenWebPage extends BaseCommandTest<OpenWebPage> {
	@Override
	protected OpenWebPage createCommand() {
		return new OpenWebPage();
	}

	@Override
	@Test
	public void testFlush() throws URISyntaxException {
		cmd.setUri(new URI("foo"));
		cmd.flush();
	}

	@Override
	@Test
	public void testDo() throws URISyntaxException {
		cmd.setUri(new URI("http://www.google.com"));
		cmd.doIt();
	}


	@Test
	public void testDoBadURI() throws URISyntaxException {
		cmd.setUri(new URI("foo"));
		cmd.doIt();
	}


	@Override
	@Test
	public void testCanDo() throws URISyntaxException {
		assumeTrue(HelperTest.isX11Set());
		cmd.setUri(new URI("foo"));
		assertEquals(cmd.canDo(), Desktop.getDesktop().isSupported(Desktop.Action.BROWSE));
	}

	@Override
	@Test
	public void testIsRegisterable() {
		assertEquals(Command.RegistrationPolicy.NONE, cmd.getRegistrationPolicy());
	}

	@Override
	@Test
	public void testHadEffect() throws URISyntaxException {
		assumeTrue(HelperTest.isX11Set());
		cmd.setUri(new URI("http://localhost"));
		cmd.doIt();
		cmd.done();
		assertTrue(cmd.hadEffect());
	}

	@Test
	public void testHadEffectWhenNotDone() throws URISyntaxException {
		assumeTrue(HelperTest.isX11Set());
		cmd.setUri(new URI("http://localhost"));
		assertFalse(cmd.hadEffect());
	}

	@Test
	public void testHadEffectBadURI() throws URISyntaxException {
		assumeTrue(HelperTest.isX11Set());
		cmd.setUri(new URI("foo"));
		cmd.doIt();
		cmd.done();
		assertFalse(cmd.hadEffect());
	}
}
