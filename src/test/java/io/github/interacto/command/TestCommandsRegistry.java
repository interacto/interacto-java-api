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
	public void testCancelsCommandNotNullDoNotCancel() {
		final Command cmd = new CommandImplStub();
		instance.addCommand(cmd);
		instance.unregisterCommand(new CommandImplStub2());
		assertEquals(1, instance.getCommands().size());
		assertNotSame(CmdStatus.FLUSHED, cmd.getStatus());
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
		final Command command = new io.github.interacto.command.CommandImplStub();
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
