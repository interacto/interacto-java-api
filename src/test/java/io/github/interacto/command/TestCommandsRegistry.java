package io.github.interacto.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.interacto.command.Command.CmdStatus;
import io.github.interacto.undo.UndoCollector;
import io.github.interacto.undo.Undoable;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCommandsRegistry {
	@BeforeEach
	public void setUp() {
		CommandsRegistry.INSTANCE.getCommands().clear();
		CommandsRegistry.INSTANCE.setSizeMax(30);
		UndoCollector.INSTANCE.clear();
	}

	@Test
	public void testGetSetSizeMaxOK() {
		CommandsRegistry.INSTANCE.setSizeMax(55);
		assertEquals(55, CommandsRegistry.INSTANCE.getSizeMax());
	}

	@Test
	public void testGetSetSizeMaxNeg() {
		CommandsRegistry.INSTANCE.setSizeMax(55);
		CommandsRegistry.INSTANCE.setSizeMax(-1);
		assertEquals(55, CommandsRegistry.INSTANCE.getSizeMax());
	}

	@Test
	public void testGetSetSizeMaxZero() {
		CommandsRegistry.INSTANCE.setSizeMax(0);
		assertEquals(0, CommandsRegistry.INSTANCE.getSizeMax());
	}

	@Test
	public void testSetSizeMaxRemovesCmd() {
		final List<Command> cmds = CommandsRegistry.INSTANCE.getCommands();
		final Command command1 = new CommandImplStub();
		final Command command2 = new CommandImplStub();
		CommandsRegistry.INSTANCE.setSizeMax(10);
		CommandsRegistry.INSTANCE.addCommand(command1);
		CommandsRegistry.INSTANCE.addCommand(command2);
		CommandsRegistry.INSTANCE.setSizeMax(1);

		assertEquals(CmdStatus.FLUSHED, command1.getStatus());
		assertEquals(CmdStatus.CREATED, command2.getStatus());
		assertEquals(1, cmds.size());
		assertEquals(command2, cmds.get(0));
	}


	@Test
	public void testCancelCommandNull() {
		CommandsRegistry.INSTANCE.cancelCmd(null);
	}


	@Test
	public void testCancelCommandFlush() {
		final Command command = new CommandImplStub();
		CommandsRegistry.INSTANCE.cancelCmd(command);
		assertEquals(CmdStatus.FLUSHED, command.getStatus());
	}


	@Test
	public void testCancelCommandRemoved() {
		final Command command = Mockito.mock(Command.class);
		CommandsRegistry.INSTANCE.addCommand(command);
		CommandsRegistry.INSTANCE.cancelCmd(command);
		assertTrue(CommandsRegistry.INSTANCE.getCommands().isEmpty());
	}


	@Test
	public void testRemoveCommandNull() {
		CommandsRegistry.INSTANCE.addCommand(Mockito.mock(Command.class));
		CommandsRegistry.INSTANCE.removeCommand(null);
		assertEquals(1, CommandsRegistry.INSTANCE.getCommands().size());
	}


	@Test
	public void testRemoveCommandNotNull() {
		final Command command = new CommandImplStub();
		CommandsRegistry.INSTANCE.addCommand(command);
		CommandsRegistry.INSTANCE.removeCommand(command);
		assertTrue(CommandsRegistry.INSTANCE.getCommands().isEmpty());
		assertEquals(CmdStatus.FLUSHED, command.getStatus());
	}


	@Test
	public void testGetCommandsNotNull() {
		assertNotNull(CommandsRegistry.INSTANCE.getCommands());
	}


	@Test
	public void testCancelsCommandNull() {
		CommandsRegistry.INSTANCE.unregisterCommand(null);
		assertTrue(CommandsRegistry.INSTANCE.getCommands().isEmpty());
	}


	@Test
	public void testCancelsCommandNotNullDoNotCancel() {
		final Command cmd = new CommandImplStub();
		CommandsRegistry.INSTANCE.addCommand(cmd);
		CommandsRegistry.INSTANCE.unregisterCommand(new CommandImplStub2());
		assertEquals(1, CommandsRegistry.INSTANCE.getCommands().size());
		assertNotSame(CmdStatus.FLUSHED, cmd.getStatus());
	}

	@Test
	public void testAddCommandCannotAddBecauseNull() {
		final Command command = new CommandImplStub();
		CommandsRegistry.INSTANCE.getCommands().add(command);
		CommandsRegistry.INSTANCE.addCommand(null);
		assertEquals(1, CommandsRegistry.INSTANCE.getCommands().size());
	}

	@Test
	public void testAddCommandCannotAddBecauseExist() {
		final Command command = new io.github.interacto.command.CommandImplStub();
		CommandsRegistry.INSTANCE.getCommands().add(command);
		CommandsRegistry.INSTANCE.addCommand(command);
		assertEquals(1, CommandsRegistry.INSTANCE.getCommands().size());
	}


	@Test
	public void testAddCommandRemovesCommandWhenMaxCapacity() {
		final Command command = Mockito.mock(Command.class);
		final Command command2 = new CommandImplStub();
		CommandsRegistry.INSTANCE.setSizeMax(1);
		CommandsRegistry.INSTANCE.getCommands().add(command2);
		CommandsRegistry.INSTANCE.addCommand(command);
		assertEquals(1, CommandsRegistry.INSTANCE.getCommands().size());
		assertEquals(command, CommandsRegistry.INSTANCE.getCommands().get(0));
		assertEquals(CmdStatus.FLUSHED, command2.getStatus());
	}


	@Test
	public void testAddCommandMaxCapacityIs0() {
		CommandsRegistry.INSTANCE.setSizeMax(0);
		CommandsRegistry.INSTANCE.addCommand(Mockito.mock(Command.class));
		assertTrue(CommandsRegistry.INSTANCE.getCommands().isEmpty());
	}


	@Test
	public void testAddCommandAddsUndoableCollector() {
		final Command command = new CommandImplUndoableStub();
		CommandsRegistry.INSTANCE.addCommand(command);
		assertEquals(command, UndoCollector.INSTANCE.getLastUndo().get());
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
				CommandsRegistry.INSTANCE.addCommand(command);
			}else {
				Command command = null;
				synchronized(addedCommands) {
					if(!addedCommands.isEmpty()) {
						command = addedCommands.remove(new Random().nextInt(addedCommands.size()));
					}
				}
				CommandsRegistry.INSTANCE.removeCommand(command);
			}
		});
	}


	private static class CommandImplUndoableStub extends CommandImpl implements Undoable {
		public CommandImplUndoableStub() {
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
		public CommandImplStub() {
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
		public CommandImplStub2() {
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
