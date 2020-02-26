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

import io.github.interacto.undo.UndoCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCommand {
	CmdStub cmd;

	@BeforeEach
	void setUp() {
		cmd = new CmdStub();
		cmd.candoValue = true;
		CommandsRegistry.getInstance().clear();
		UndoCollector.getInstance().clear();
	}

	@Test
	void testExecuteAndFlushNull() {
		Command.executeAndFlush(null);
		assertTrue(CommandsRegistry.getInstance().getCommands().isEmpty());
	}

	@Test
	void testExecuteAndFlushCannotDo() {
		cmd.candoValue = false;
		Command.executeAndFlush(cmd);
		Mockito.verify(cmd, Mockito.never()).doIt();
		Mockito.verify(cmd, Mockito.times(1)).flush();
	}

	@Test
	void testExecuteAndFlushCanDo() {
		Command.executeAndFlush(cmd);
		Mockito.verify(cmd, Mockito.times(1)).doIt();
		Mockito.verify(cmd, Mockito.times(1)).flush();
	}

	@Test
	void testCommandStatusAfterCreation() {
		assertEquals(Command.CmdStatus.CREATED, cmd.getStatus());
	}

	@Test
	void testCommandStatusAfterFlush() {
		cmd.flush();
		assertEquals(Command.CmdStatus.FLUSHED, cmd.getStatus());
	}

	@Test
	void testExecutedTwoTimes() {
		cmd.doIt();
		cmd.doIt();
		assertEquals(2, cmd.cptDoCmdBody.get());
	}

	@Test
	void testCommandCannotDoItWhenFlushed() {
		cmd.flush();
		assertFalse(cmd.doIt());
	}

	@Test
	void testCommandCannotDoItWhenDone() {
		cmd.done();
		assertFalse(cmd.doIt());
	}

	@Test
	void testCommandCannotDoItWhenCancelled() {
		cmd.cancel();
		assertFalse(cmd.doIt());
	}

	@Test
	void testCommandCannotDoItWhenCannotDoAndCreated() {
		final Command cmd = new CmdStub();
		assertFalse(cmd.doIt());
	}

	@Test
	void testCommandCanDoItWhenCanDo() {
		assertTrue(cmd.doIt());
	}

	@Test
	void testCommandIsExecutedWhenDoIt() {
		cmd.doIt();
		assertEquals(Command.CmdStatus.EXECUTED, cmd.getStatus());
	}

	@Test
	void testCommandHadEffectWhenDone() {
		cmd.done();
		assertTrue(cmd.hadEffect());
	}

	@Test
	void testCommandHadEffectWhenNotDoneAndCreated() {
		assertFalse(cmd.hadEffect());
	}

	@Test
	void testCommandHadEffectWhenNotDoneAndCancelled() {
		cmd.cancel();
		assertFalse(cmd.hadEffect());
	}

	@Test
	void testCommandHadEffectWhenNotDoneAndFlushed() {
		cmd.flush();
		assertFalse(cmd.hadEffect());
	}

	@Test
	void testCommandHadEffectWhenNotDoneAndExecuted() {
		cmd.doIt();
		assertFalse(cmd.hadEffect());
	}

	@Test
	void testGetRegistrationPolicyNotExecuted() {
		assertEquals(Command.RegistrationPolicy.NONE, cmd.getRegistrationPolicy());
	}

	@Test
	void testGetRegistrationPolicyDone() {
		cmd.done();
		assertEquals(Command.RegistrationPolicy.LIMITED, cmd.getRegistrationPolicy());
	}

	@Test
	void testCommandNotUnregisterByByDefault() {
		assertFalse(cmd.unregisteredBy(null));
		assertFalse(cmd.unregisteredBy(new CmdStub()));
	}

	@Test
	void testCommandNotDoneWhenFlushed() {
		cmd.flush();
		cmd.done();
		assertEquals(Command.CmdStatus.FLUSHED, cmd.getStatus());
	}

	@Test
	void testCommandNotDoneWhenCancelled() {
		cmd.cancel();
		cmd.done();
		assertEquals(Command.CmdStatus.CANCELLED, cmd.getStatus());
	}

	@Test
	void testHadEffectKOByDefault() {
		assertFalse(cmd.hadEffect());
	}

	@Test
	void testCommandDoneWhenCreated() {
		cmd.done();
		assertEquals(Command.CmdStatus.DONE, cmd.getStatus());
	}


	@Test
	void testCommandDoneWhenExecuted() {
		cmd.doIt();
		cmd.done();
		assertEquals(Command.CmdStatus.DONE, cmd.getStatus());
	}


	@Test
	void testToStringNotNull() {
		assertNotNull(cmd.toString());
	}

	@Test
	void testIsDoneWhenCreated() {
		assertFalse(cmd.isDone());
	}

	@Test
	void testIsDoneWhenCancelled() {
		cmd.cancel();
		assertFalse(cmd.isDone());
	}

	@Test
	void testIsDoneWhenFlushed() {
		cmd.flush();
		assertFalse(cmd.isDone());
	}


	@Test
	void testIsDoneWhenDone() {
		cmd.done();
		assertTrue(cmd.isDone());
	}

	@Test
	void testIsDoneWhenExecuted() {
		cmd.doIt();
		assertFalse(this.cmd.isDone());
	}

	@Test
	void testNotCancelAtStart() {
		assertNotSame(Command.CmdStatus.CANCELLED, cmd.getStatus());
	}

	@Test
	void testCancel() {
		cmd.cancel();
		assertEquals(Command.CmdStatus.CANCELLED, cmd.getStatus());
	}
}
