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
package io.github.interacto.binding;

import io.github.interacto.command.Command;
import io.github.interacto.command.CommandImpl;
import io.github.interacto.command.CommandsRegistry;
import io.github.interacto.fsm.FSM;
import io.github.interacto.fsm.TerminalState;
import io.github.interacto.fsm.Transition;
import io.github.interacto.interaction.InteractionData;
import io.github.interacto.interaction.InteractionStub;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestWidgetBindingIntegration {
	InteractionStub interaction;
	WidgetBindingImpl<CommandImplStub, InteractionStub, InteractionData> binding;
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
		fsm = new OneTrFSM();
		interaction = new InteractionStub(fsm);
	}

	@AfterEach
	void tearDown() {
		CommandsRegistry.getInstance().clear();
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
					super.ifCmdHadNoEffect();
					noEffect++;
				}
				@Override
				public void ifCmdHadEffects() {
					super.ifCmdHadEffects();
					effects++;
				}
				@Override
				public void ifCannotExecuteCmd() {
					super.ifCannotExecuteCmd();
					cannotExec++;
				}
				@Override
				protected void unbindCmdAttributes() {
				}
				@Override
				protected void executeCmdAsync(final Command cmd) {
				}
			};
			binding.setActivated(true);
			binding.logBinding(true);
			binding.logCmd(true);
			binding.logInteraction(true);
		}

		@Test
		void testNothingDoneIsDeactivated() {
			binding.setActivated(false);
			fsm.process(new EventStub1());
			assertEquals(0, cmd.executed);
			assertEquals(0, effects);
			assertEquals(0, noEffect);
			assertEquals(0, cannotExec);
			assertEquals(Command.CmdStatus.CREATED, cmd.getStatus());
		}

		@Test
		void testCmdCreatedExecSavedWhenActivated() {
			fsm.process(new EventStub1());
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
			assertEquals(1, cmd.executed);
			assertEquals(0, effects);
			assertEquals(1, noEffect);
			assertEquals(0, cannotExec);
		}

		@Test
		void testProducedNone() {
			cmd.can = false;
			final List<CommandImplStub> cmds = new ArrayList<>();
			binding.produces().subscribe(cmds::add);
			fsm.process(new EventStub1());
			assertTrue(cmds.isEmpty());
		}

		@Test
		void testProducedOne() {
			final List<CommandImplStub> cmds = new ArrayList<>();
			binding.produces().subscribe(cmds::add);
			fsm.process(new EventStub1());
			assertEquals(1, cmds.size());
		}

		@Test
		void testProducedTwo() {
			final List<CommandImplStub> cmds = new ArrayList<>();
			binding.produces().subscribe(cmds::add);
			fsm.process(new EventStub1());
			cmd = new CommandImplStub();
			fsm.process(new EventStub1());
			assertEquals(2, cmds.size());
			assertNotSame(cmds.get(0), cmds.get(1));
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
				public void ifCmdHadNoEffect() {
					super.ifCmdHadNoEffect();
					noEffect++;
				}
				@Override
				public void ifCmdHadEffects() {
					super.ifCmdHadEffects();
					effects++;
				}
				@Override
				public void ifCannotExecuteCmd() {
					super.ifCannotExecuteCmd();
					cannotExec++;
				}
				@Override
				protected void unbindCmdAttributes() {
				}
				@Override
				protected void executeCmdAsync(final Command cmd) {
				}
			};
			binding.setActivated(true);
		}

		@Test
		void testNothingDoneIsDeactivated() {
			binding.setActivated(false);
			fsm.process(new EventStub1());
			assertEquals(0, effects);
			assertEquals(0, noEffect);
			assertEquals(0, cannotExec);
			assertEquals(0, cmd.executed);
		}

		@Test
		void testCmdCreatedExecSavedWhenActivated() {
			fsm.process(new EventStub1());
			assertEquals(1, effects);
			assertEquals(0, noEffect);
			assertEquals(0, cannotExec);
			assertEquals(1, cmd.executed);
		}

		@Test
		void testCmdKOWhenNotWhenOK() {
			whenValue.set(false);
			fsm.process(new EventStub1());
			assertEquals(0, cmd.executed);
			assertEquals(0, effects);
			assertEquals(0, noEffect);
			assertEquals(0, cannotExec);
		}
	}



	private static class EventStub1 {
	}

	private static class OneTrFSM extends FSM<Object> {
		OneTrFSM() {
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
