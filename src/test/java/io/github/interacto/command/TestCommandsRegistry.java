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
package io.github.interacto.command;

import io.github.interacto.command.Command.CmdStatus;
import io.github.interacto.undo.UndoCollector;
import io.github.interacto.undo.Undoable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCommandsRegistry {
	CommandsRegistry instance;

	@BeforeEach
	public void setUp() {
		instance = CommandsRegistry.getInstance();
		instance.getCommands().clear();
		instance.setSizeMax(30);
		UndoCollector.getInstance().clear();
	}

	@Test
	void testGetSetInstanceKO() {
		CommandsRegistry.setInstance(null);
		assertEquals(instance, CommandsRegistry.getInstance());
	}

	@Test
	void testGetSetInstanceOK() {
		final var newinstance = new CommandsRegistry();
		CommandsRegistry.setInstance(newinstance);
		assertEquals(newinstance, CommandsRegistry.getInstance());
	}

	@Test
	public void testGetSetSizeMaxOK() {
		instance.setSizeMax(55);
		assertEquals(55, instance.getSizeMax());
	}

	@Test
	public void testGetSetSizeMaxNeg() {
		instance.setSizeMax(55);
		instance.setSizeMax(-1);
		assertEquals(55, instance.getSizeMax());
	}

	@Test
	public void testGetSetSizeMaxZero() {
		instance.setSizeMax(0);
		assertEquals(0, instance.getSizeMax());
	}

	@Test
	public void testSetSizeMaxRemovesCmd() {
		final List<Command> cmds = instance.getCommands();
		final Command command1 = new CommandImplStub();
		final Command command2 = new CommandImplStub();
		instance.setSizeMax(10);
		instance.addCommand(command1);
		instance.addCommand(command2);
		instance.setSizeMax(1);

		assertEquals(CmdStatus.FLUSHED, command1.getStatus());
		assertEquals(CmdStatus.CREATED, command2.getStatus());
		assertEquals(1, cmds.size());
		assertEquals(command2, cmds.get(0));
	}

	@Test
	void testSetSiezMaxWithUnlimited() {
		final var cmd1 = Mockito.mock(Command.class);
		Mockito.when(cmd1.getRegistrationPolicy()).thenReturn(Command.RegistrationPolicy.UNLIMITED);
		final var cmd2 = Mockito.mock(Command.class);
		Mockito.when(cmd2.getRegistrationPolicy()).thenReturn(Command.RegistrationPolicy.LIMITED);
		final var cmd3 = Mockito.mock(Command.class);
		Mockito.when(cmd3.getRegistrationPolicy()).thenReturn(Command.RegistrationPolicy.LIMITED);
		instance.addCommand(cmd2);
		instance.addCommand(cmd1);
		instance.addCommand(cmd3);
		instance.setSizeMax(0);
		assertEquals(List.of(cmd1), instance.getCommands());
	}

	@Test
	void testCommandsNotNull() {
		assertNotNull(instance.commands());
	}

	@Test
	void testCommandsObservedOnAdded() {
		final List<Command> cmds = new ArrayList<>();
		instance.commands().subscribe(cmds::add);
		final var cmd = Mockito.mock(Command.class);
		instance.addCommand(cmd);
		assertEquals(List.of(cmd), cmds);
	}

	@Test
	public void testCancelCommandNull() {
		instance.cancelCmd(null);
	}


	@Test
	public void testCancelCommandFlush() {
		final Command command = new CommandImplStub();
		instance.cancelCmd(command);
		assertEquals(CmdStatus.FLUSHED, command.getStatus());
	}


	@Test
	public void testCancelCommandRemoved() {
		final Command command = Mockito.mock(Command.class);
		instance.addCommand(command);
		instance.cancelCmd(command);
		assertTrue(instance.getCommands().isEmpty());
	}


	@Test
	public void testRemoveCommandNull() {
		instance.addCommand(Mockito.mock(Command.class));
		instance.removeCommand(null);
		assertEquals(1, instance.getCommands().size());
	}


	@Test
	public void testRemoveCommandNotNull() {
		final Command command = new CommandImplStub();
		instance.addCommand(command);
		instance.removeCommand(command);
		assertTrue(instance.getCommands().isEmpty());
		assertEquals(CmdStatus.FLUSHED, command.getStatus());
	}


	@Test
	public void testGetCommandsNotNull() {
		assertNotNull(instance.getCommands());
	}


	@Test
	public void testCancelsCommandNull() {
		instance.unregisterCommand(null);
		assertTrue(instance.getCommands().isEmpty());
	}


	@Test
	public void testUnregisterDoNothing() {
		final Command cmd = new CommandImplStub();
		instance.addCommand(cmd);
		instance.unregisterCommand(new CommandImplStub2());
		assertEquals(1, instance.getCommands().size());
		assertNotSame(CmdStatus.FLUSHED, cmd.getStatus());
	}

	@Test
	public void testUnregisterOK() {
		final Command cmd = new CommandImplStub2();
		instance.addCommand(cmd);
		instance.unregisterCommand(new CommandImplStub());
		assertTrue(instance.getCommands().isEmpty());
		assertEquals(CmdStatus.FLUSHED, cmd.getStatus());
	}

	@Test
	public void testAddCommandCannotAddBecauseNull() {
		final Command command = new CommandImplStub();
		instance.getCommands().add(command);
		instance.addCommand(null);
		assertEquals(1, instance.getCommands().size());
	}

	@Test
	public void testAddCommandCannotAddBecauseExist() {
		final Command command = new CommandImplStub();
		instance.getCommands().add(command);
		instance.addCommand(command);
		assertEquals(1, instance.getCommands().size());
	}


	@Test
	public void testAddCommandRemovesCommandWhenMaxCapacity() {
		final Command command = Mockito.mock(Command.class);
		final Command command2 = new CommandImplStub();
		instance.setSizeMax(1);
		instance.getCommands().add(command2);
		instance.addCommand(command);
		assertEquals(1, instance.getCommands().size());
		assertEquals(command, instance.getCommands().get(0));
		assertEquals(CmdStatus.FLUSHED, command2.getStatus());
	}


	@Test
	public void testAddCommandMaxCapacityIs0() {
		instance.setSizeMax(0);
		instance.addCommand(Mockito.mock(Command.class));
		assertTrue(instance.getCommands().isEmpty());
	}

	@Test
	void testAddCommandMaxCapacityButUnlimitedInHistory() {
		final var cmd1 = Mockito.mock(Command.class);
		Mockito.when(cmd1.getRegistrationPolicy()).thenReturn(Command.RegistrationPolicy.UNLIMITED);
		final var cmd2 = Mockito.mock(Command.class);
		Mockito.when(cmd2.getRegistrationPolicy()).thenReturn(Command.RegistrationPolicy.LIMITED);
		final var cmd3 = Mockito.mock(Command.class);
		Mockito.when(cmd3.getRegistrationPolicy()).thenReturn(Command.RegistrationPolicy.LIMITED);
		instance.setSizeMax(2);
		instance.addCommand(cmd1);
		instance.addCommand(cmd2);
		instance.addCommand(cmd3);
		assertEquals(List.of(cmd1, cmd3), instance.getCommands());
	}

	@Test
	void testAddCommandMaxCapacityButUnlimitedAdded() {
		final var cmd1 = Mockito.mock(Command.class);
		Mockito.when(cmd1.getRegistrationPolicy()).thenReturn(Command.RegistrationPolicy.UNLIMITED);
		instance.setSizeMax(0);
		instance.addCommand(cmd1);
		assertEquals(List.of(cmd1), instance.getCommands());
	}

	@Test
	public void testAddCommandAddsUndoableCollector() {
		final Command command = new CommandImplUndoableStub();
		instance.addCommand(command);
		assertEquals(command, UndoCollector.getInstance().getLastUndo().get());
	}


	@Test
	public void testRegistryConcurrentAccess() {
		final List<Command> addedCommands = new ArrayList<>();

		IntStream.range(0, 100000).parallel().forEach(i -> {
			if(i % 2 == 0) {
				final Command command = Mockito.mock(Command.class);
				synchronized(addedCommands) {
					addedCommands.add(command);
				}
				instance.addCommand(command);
			}else {
				Command command = null;
				synchronized(addedCommands) {
					if(!addedCommands.isEmpty()) {
						command = addedCommands.remove(new Random().nextInt(addedCommands.size()));
					}
				}
				instance.removeCommand(command);
			}
		});
	}


	@Test
	void testClear() {
		final var c1 = Mockito.mock(Command.class);
		final var c2 = Mockito.mock(Command.class);
		instance.addCommand(c1);
		instance.addCommand(c2);
		instance.clear();
		assertTrue(instance.getCommands().isEmpty());
		Mockito.verify(c1, Mockito.times(1)).flush();
		Mockito.verify(c2, Mockito.times(1)).flush();
	}

	private static class CommandImplUndoableStub extends CommandImpl implements Undoable {
		CommandImplUndoableStub() {
			super();
		}

		@Override
		protected void doCmdBody() {
			//
		}

		@Override
		public boolean canDo() {
			return false;
		}

		@Override
		public void undo() {
			//
		}

		@Override
		public void redo() {
			//
		}

		@Override
		public String getUndoName(final ResourceBundle bundle) {
			return null;
		}
	}


	private static class CommandImplStub extends CommandImpl {
		CommandImplStub() {
			super();
		}

		@Override
		protected void doCmdBody() {
			//
		}

		@Override
		public boolean canDo() {
			return false;
		}
	}


	private static class CommandImplStub2 extends CommandImpl {
		CommandImplStub2() {
			super();
		}

		@Override
		public boolean unregisteredBy(final Command cmd) {
			return cmd instanceof CommandImplStub;
		}

		@Override
		protected void doCmdBody() {
			//
		}

		@Override
		public boolean canDo() {
			return false;
		}
	}
}
