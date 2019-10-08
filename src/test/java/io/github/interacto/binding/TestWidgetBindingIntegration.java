package io.github.interacto.binding;

import io.github.interacto.command.CmdHandler;
import io.github.interacto.command.Command;
import io.github.interacto.command.CommandImpl;
import io.github.interacto.command.CommandsRegistry;
import io.github.interacto.error.ErrorCatcher;
import io.github.interacto.fsm.FSM;
import io.github.interacto.fsm.TerminalState;
import io.github.interacto.fsm.Transition;
import io.github.interacto.interaction.InteractionData;
import io.github.interacto.interaction.InteractionStub;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestWidgetBindingIntegration {
	InteractionStub interaction;
	WidgetBindingImpl<CommandImplStub, InteractionStub, InteractionData> binding;
	CmdHandler cmdHandler;
	FSM<Object> fsm;
	CommandImplStub cmd;
	AtomicBoolean whenValue;
	int noEffect;
	int effects;
	int cannotExec;

	@BeforeEach
	void setUp() {
		noEffect = 0;
		effects = 0;
		cannotExec = 0;
		whenValue = new AtomicBoolean(true);
		cmd = new CommandImplStub();
		cmdHandler = Mockito.mock(CmdHandler.class);
		fsm = new OneTrFSM();
		interaction = new InteractionStub(fsm);
	}

	@AfterEach
	void tearDown() {
		CommandsRegistry.INSTANCE.clear();
		ErrorCatcher.INSTANCE.setNotifier(null);
		WidgetBindingImpl.setLogger(null);
	}

	@Nested
	class NonExecWithLog {
		@BeforeEach
		void setUp() {
			binding = new WidgetBindingImpl<>(false, i -> cmd, interaction) {
				@Override
				public boolean when() {
					return whenValue.get();
				}
				@Override
				public void ifCmdHadNoEffect() {
					noEffect++;
				}
				@Override
				public void ifCmdHadEffects() {
					effects++;
				}
				@Override
				public void ifCannotExecuteCmd() {
					cannotExec++;
				}
				@Override
				protected void unbindCmdAttributes() {
				}
				@Override
				protected void executeCmdAsync(final Command cmd) {
				}
			};
			binding.setCmdHandler(cmdHandler);
			binding.setActivated(true);
			binding.logBinding(true);
			binding.logCmd(true);
			binding.logInteraction(true);
		}

		@Test
		void testNothingDoneIsDeactivated() {
			binding.setActivated(false);
			fsm.process(new EventStub1());
			Mockito.verify(cmdHandler, Mockito.never()).onCmdAdded(cmd);
			Mockito.verify(cmdHandler, Mockito.never()).onCmdExecuted(cmd);
			Mockito.verify(cmdHandler, Mockito.never()).onCmdDone(cmd);
			assertEquals(0, cmd.executed);
			assertEquals(0, effects);
			assertEquals(0, noEffect);
			assertEquals(0, cannotExec);
			assertEquals(Command.CmdStatus.CREATED, cmd.getStatus());
		}

		@Test
		void testCmdCreatedExecSavedWhenActivated() {
			fsm.process(new EventStub1());
			Mockito.verify(cmdHandler, Mockito.times(1)).onCmdAdded(cmd);
			Mockito.verify(cmdHandler, Mockito.times(1)).onCmdExecuted(cmd);
			Mockito.verify(cmdHandler, Mockito.times(1)).onCmdDone(cmd);
			assertEquals(1, cmd.executed);
			assertEquals(1, effects);
			assertEquals(0, noEffect);
			assertEquals(0, cannotExec);
			assertEquals(Command.CmdStatus.DONE, cmd.getStatus());
		}

		@Test
		void testCmdKOWhenNotWhenOK() {
			whenValue.set(false);
			fsm.process(new EventStub1());
			Mockito.verify(cmdHandler, Mockito.never()).onCmdAdded(cmd);
			Mockito.verify(cmdHandler, Mockito.never()).onCmdExecuted(cmd);
			Mockito.verify(cmdHandler, Mockito.never()).onCmdDone(cmd);
			assertEquals(0, cmd.executed);
			assertEquals(0, effects);
			assertEquals(0, noEffect);
			assertEquals(0, cannotExec);
			assertEquals(Command.CmdStatus.CREATED, cmd.getStatus());
		}

		@Test
		void testCmdKOWhenCannotDoCmd() {
			cmd.can = false;
			fsm.process(new EventStub1());
			Mockito.verify(cmdHandler, Mockito.never()).onCmdAdded(cmd);
			Mockito.verify(cmdHandler, Mockito.never()).onCmdExecuted(cmd);
			Mockito.verify(cmdHandler, Mockito.never()).onCmdDone(cmd);
			assertEquals(0, cmd.executed);
			assertEquals(0, effects);
			assertEquals(0, noEffect);
			assertEquals(1, cannotExec);
		}

		@Test
		void testWhenOKCanDoButNoEffect() {
			cmd.can = true;
			cmd.effects = false;
			fsm.process(new EventStub1());
			Mockito.verify(cmdHandler, Mockito.never()).onCmdAdded(cmd);
			Mockito.verify(cmdHandler, Mockito.times(1)).onCmdExecuted(cmd);
			Mockito.verify(cmdHandler, Mockito.times(1)).onCmdDone(cmd);
			assertEquals(1, cmd.executed);
			assertEquals(0, effects);
			assertEquals(1, noEffect);
			assertEquals(0, cannotExec);
		}
	}


	@Nested
	class ExecWithNoLog {
		@BeforeEach
		void setUp() {
			binding = new WidgetBindingImpl<>(true, i -> cmd, interaction) {
				@Override
				public void first() {
				}
				@Override
				public boolean when() {
					return whenValue.get();
				}
				@Override
				protected void unbindCmdAttributes() {
				}
				@Override
				protected void executeCmdAsync(final Command cmd) {
				}
			};
			binding.setCmdHandler(cmdHandler);
			binding.setActivated(true);
		}

		@Test
		void testNothingDoneIsDeactivated() {
			binding.setActivated(false);
			fsm.process(new EventStub1());
			Mockito.verify(cmdHandler, Mockito.never()).onCmdAdded(cmd);
			Mockito.verify(cmdHandler, Mockito.never()).onCmdExecuted(cmd);
			Mockito.verify(cmdHandler, Mockito.never()).onCmdDone(cmd);
			assertEquals(0, cmd.executed);
		}

		@Test
		void testCmdCreatedExecSavedWhenActivated() {
			fsm.process(new EventStub1());
			Mockito.verify(cmdHandler, Mockito.times(1)).onCmdAdded(cmd);
			Mockito.verify(cmdHandler, Mockito.times(1)).onCmdExecuted(cmd);
			Mockito.verify(cmdHandler, Mockito.times(1)).onCmdDone(cmd);
			assertEquals(1, cmd.executed);
		}

		@Test
		void testCmdKOWhenNotWhenOK() {
			whenValue.set(false);
			fsm.process(new EventStub1());
			Mockito.verify(cmdHandler, Mockito.never()).onCmdAdded(cmd);
			Mockito.verify(cmdHandler, Mockito.never()).onCmdExecuted(cmd);
			Mockito.verify(cmdHandler, Mockito.never()).onCmdDone(cmd);
			assertEquals(0, cmd.executed);
		}
	}




	private static class EventStub1 {
	}

	private static class OneTrFSM extends FSM<Object> {
		public OneTrFSM() {
			super();
			final TerminalState<Object> s1 = new TerminalState<>(this, "s1");
			addState(s1);
			new Transition<>(initState, s1) {
				@Override
				protected boolean accept(final Object event) {
					return event instanceof EventStub1;
				}

				@Override
				protected boolean isGuardOK(final Object event) {
					return true;
				}

				@Override
				public Set<Object> getAcceptedEvents() {
					return Set.of(EventStub1.class);
				}
			};
		}
	}

	private static class CommandImplStub extends CommandImpl {
		int executed = 0;
		boolean can = true;
		boolean effects = true;

		@Override
		protected void doCmdBody() {
			executed++;
		}

		@Override
		public boolean canDo() {
			return can;
		}

		@Override
		public boolean hadEffect() {
			return effects;
		}
	}
}
