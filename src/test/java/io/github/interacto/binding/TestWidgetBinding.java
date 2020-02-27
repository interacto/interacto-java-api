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

import io.github.interacto.command.CmdStub;
import io.github.interacto.command.Command;
import io.github.interacto.command.CommandsRegistry;
import io.github.interacto.error.ErrorCatcher;
import io.github.interacto.fsm.CancelFSMException;
import io.github.interacto.fsm.FSM;
import io.github.interacto.interaction.InteractionData;
import io.github.interacto.interaction.InteractionStub;
import io.github.interacto.undo.Undoable;
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestWidgetBinding {
	protected WidgetBindingStub binding;
	Disposable errorStream;
	Logger mementoLogger;
	List<Throwable> errors;

	@BeforeEach
	public void setUp() {
		mementoLogger = WidgetBindingImpl.getLogger();
		binding = new WidgetBindingStub(false, CmdStub::new, new InteractionStub());
		binding.setActivated(true);
		errors = new ArrayList<>();
		errorStream = ErrorCatcher.getInstance().getErrors().subscribe(errors::add);
	}

	@AfterEach
	void tearDown() {
		WidgetBindingImpl.setLogger(mementoLogger);
		CommandsRegistry.getInstance().clear();
		errorStream.dispose();
		assertEquals(List.of(), errors);
	}

	@Test
	void testConstructorInteractionNull() {
		assertThrows(IllegalArgumentException.class, () -> new WidgetBindingStub(false, CmdStub::new, null));
	}

	@Test
	void testConstructorCmdCreationNull() {
		assertThrows(IllegalArgumentException.class,
			() -> new WidgetBindingStub(false, (Function<InteractionData, CmdStub>) null, new InteractionStub()));
	}

	@Test
	void testConstructorCreatedInteractionNotNull() {
		assertNotNull(binding.getInteraction());
	}

	@Test
	void testConstructorCreatedActionIsNull() {
		assertNull(binding.getCommand());
	}

	@Test
	void testLinkDeActivation() {
		binding.setActivated(true);
		binding.setActivated(false);
		Assertions.assertFalse(binding.isActivated());
	}

	@Test
	void testLinkActivation() {
		binding.setActivated(false);
		binding.setActivated(true);
		assertTrue(binding.isActivated());
	}

	@Test
	void testExecuteNope() {
		Assertions.assertFalse(binding.isContinuousCmdExec());
	}

	@Test
	void testExecuteOK() {
		binding = new WidgetBindingStub(true, CmdStub::new, new InteractionStub());
		assertTrue(binding.isContinuousCmdExec());
	}

	@Test
	void testExecuteCrash() {
		errorStream.dispose();
		final List<Throwable> errors = new ArrayList<>();
		final IllegalArgumentException ex = new IllegalArgumentException();
		errorStream = ErrorCatcher.getInstance().getErrors().subscribe(errors::add);
		final Supplier<CmdStub> supplier = () -> {
			throw ex;
		};

		binding = new WidgetBindingStub(true, supplier, new InteractionStub());
		assertNull(binding.createCommand());
		assertEquals(1, errors.size());
		assertSame(ex, errors.get(0));
	}

	@Test
	void testExecuteCrashAndInteractionStops() throws CancelFSMException {
		errorStream.dispose();
		final IllegalArgumentException ex = new IllegalArgumentException();
		final Supplier<CmdStub> supplier = () -> {
			throw ex;
		};

		binding = new WidgetBindingStub(true, supplier, new InteractionStub());
		binding.conditionRespected = true;
		binding.fsmStops();
	}

	@Test
	void testIsInteractionMustBeCancelled() {
		assertFalse(binding.isStrictStart());
	}

	@Test
	void testNotRunning() {
		Assertions.assertFalse(binding.isRunning());
	}

	@Test
	void testInteractionCancelsWhenNotStarted() {
		binding.fsmCancels();
	}

	@Test
	void testInteractionUpdatesWhenNotStarted() {
		binding.fsmUpdates();
	}

	@Test
	void testInteractionStopsWhenNotStarted() {
		binding.fsmStops();
	}

	@Test
	void testInteractionStartsWhenNoCorrectInteractionNotActivated() throws CancelFSMException {
		binding.mustCancel = false;
		binding.setActivated(false);
		binding.fsmStarts();
		assertNull(binding.getCommand());
	}

	@Test
	void testInteractionStartsWhenNoCorrectInteractionActivated() throws CancelFSMException {
		binding.mustCancel = false;
		binding.conditionRespected = false;
		binding.fsmStarts();
		assertNull(binding.getCommand());
	}

	@Test
	void testInteractionStartsThrowMustCancelStateMachineException() {
		binding.mustCancel = true;
		assertThrows(CancelFSMException.class, () -> binding.fsmStarts());
	}

	@Test
	void testInteractionStartsThrowMustCancelStateMachineExceptionWithLog() {
		binding.mustCancel = true;
		binding.logBinding(true);
		assertThrows(CancelFSMException.class, () -> binding.fsmStarts());
	}

	@Test
	void testInteractionStartsOk() throws CancelFSMException {
		binding.conditionRespected = true;
		binding.fsmStarts();
		assertNotNull(binding.getCommand());
	}

	@Test
	void testCounters() {
		assertEquals(0, binding.getTimesEnded());
		assertEquals(0, binding.getTimesCancelled());
	}

	@Test
	void testCounterEndedOnce() throws CancelFSMException {
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.fsmStops();
		assertEquals(1, binding.getTimesEnded());
		assertEquals(0, binding.getTimesCancelled());
	}

	@Test
	void testCounterEndedTwice() throws CancelFSMException {
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.fsmStops();
		binding.fsmStarts();
		binding.fsmStops();
		assertEquals(2, binding.getTimesEnded());
		assertEquals(0, binding.getTimesCancelled());
	}

	@Test
	void testCounterCancelledOnce() throws CancelFSMException {
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.fsmCancels();
		assertEquals(1, binding.getTimesCancelled());
		assertEquals(0, binding.getTimesEnded());
	}

	@Test
	void testCounterCancelledTwice() throws CancelFSMException {
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.fsmCancels();
		binding.fsmStarts();
		binding.fsmCancels();
		assertEquals(2, binding.getTimesCancelled());
		assertEquals(0, binding.getTimesEnded());
	}

	@Test
	void testSetGetLogger() {
		final Logger log = Mockito.mock(Logger.class);
		WidgetBindingImpl.setLogger(log);
		assertEquals(log, WidgetBindingImpl.getLogger());
	}

	@Test
	void testSetLoggerNull() {
		WidgetBindingImpl.setLogger(null);
		assertEquals(mementoLogger, WidgetBindingImpl.getLogger());
	}

	@Test
	void testLogBinding() {
		binding.logBinding(true);
		assertNotNull(binding.loggerBinding);
	}

	@Test
	void testLogAgainBinding() {
		binding.logBinding(true);
		binding.logBinding(true);
		assertNotNull(binding.loggerBinding);
	}

	@Test
	void testNoLogBinding() {
		binding.logBinding(true);
		binding.logBinding(false);
		assertNull(binding.loggerBinding);
	}

	@Test
	void testLogCmd() {
		binding.logCmd(true);
		assertNotNull(binding.loggerCmd);
	}

	@Test
	void testLogAgainCmd() {
		binding.logCmd(true);
		binding.logCmd(true);
		assertNotNull(binding.loggerCmd);
	}

	@Test
	void testNoLogCmd() {
		binding.logCmd(true);
		binding.logCmd(false);
		assertNull(binding.loggerCmd);
	}

	@Test
	void testLogInteraction() {
		final var interaction = Mockito.mock(InteractionStub.class);
		Mockito.when(interaction.getFsm()).thenReturn(Mockito.mock(FSM.class));
		binding = new WidgetBindingStub(false, CmdStub::new, interaction);
		binding.logInteraction(true);
		Mockito.verify(interaction, Mockito.times(1)).log(true);
		Mockito.verify(interaction, Mockito.never()).log(false);
	}

	@Test
	void testNoLogInteraction() {
		final var interaction = Mockito.mock(InteractionStub.class);
		Mockito.when(interaction.getFsm()).thenReturn(Mockito.mock(FSM.class));
		binding = new WidgetBindingStub(false, CmdStub::new, interaction);
		binding.logInteraction(false);
		Mockito.verify(interaction, Mockito.times(1)).log(false);
		Mockito.verify(interaction, Mockito.never()).log(true);
	}

	@Test
	void testClearEvents() {
		final var interaction = Mockito.mock(InteractionStub.class);
		Mockito.when(interaction.getFsm()).thenReturn(Mockito.mock(FSM.class));
		binding = new WidgetBindingStub(false, CmdStub::new, interaction);
		binding.clearEvents();
		Mockito.verify(interaction, Mockito.times(1)).fullReinit();
	}

	@Test
	void testSetGetAsync() {
		binding.setAsync(true);
		assertTrue(binding.isAsync());
	}

	@Test
	void testSetGetAsyncFalse() {
		binding.setAsync(true);
		binding.setAsync(false);
		assertFalse(binding.isAsync());
	}

	@Test
	void testCancelInteraction() throws CancelFSMException {
		binding.conditionRespected = true;
		binding.logBinding(true);
		binding.logCmd(true);
		binding.fsmStarts();
		final var cmd = binding.cmd;
		binding.fsmCancels();
		assertEquals(Command.CmdStatus.CANCELLED, cmd.getStatus());
		assertTrue(binding.unbind);
		assertTrue(binding.cancel);
		assertTrue(binding.endOrcancel);
		assertEquals(1, binding.timeCancelled);
		assertNull(binding.cmd);
	}

	@Test
	void testCancelInteractionTwoTimes() throws CancelFSMException {
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.fsmCancels();
		binding.fsmStarts();
		binding.fsmCancels();
		assertEquals(2, binding.timeCancelled);
	}

	@Test
	void testCancelInteractionContinuous() throws CancelFSMException {
		binding = new WidgetBindingStub(true, CmdStub::new, new InteractionStub());
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.cmd.done();
		assertThrows(MustBeUndoableCmdException.class, () -> binding.fsmCancels());
	}

	@Test
	void testCancelInteractionContinuousNoEffect() throws CancelFSMException {
		binding = new WidgetBindingStub(true, CmdStub::new, new InteractionStub());
		binding.conditionRespected = true;
		binding.fsmStarts();
		final var cmd = binding.cmd;
		binding.fsmCancels();
		assertEquals(Command.CmdStatus.CANCELLED, cmd.getStatus());
	}

	@Test
	void testCancelInteractionContinuousUndoable() throws CancelFSMException {
		final var cmd = Mockito.spy(new CmdStubUndoable());
		binding = new WidgetBindingStub(true, () -> cmd, new InteractionStub());
		binding.conditionRespected = true;
		binding.logCmd(true);
		binding.fsmStarts();
		binding.fsmCancels();
		Mockito.verify(cmd, Mockito.times(1)).undo();
	}

	@Test
	void testCancelInteractionContinuousUndoableNoLog() throws CancelFSMException {
		final var cmd = Mockito.spy(new CmdStubUndoable());
		binding = new WidgetBindingStub(true, () -> cmd, new InteractionStub());
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.fsmCancels();
		Mockito.verify(cmd, Mockito.times(1)).undo();
	}

	@Test
	void testUpdateActivatedWithLogCmdNotOk() throws CancelFSMException {
		binding.conditionRespected = false;
		binding.logBinding(true);
		binding.fsmStarts();
		binding.fsmUpdates();
		assertFalse(binding.then);
	}

	@Test
	void testUpdateActivatedNoLogCmdOk() throws CancelFSMException {
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.fsmUpdates();
		assertTrue(binding.then);
	}

	@Test
	void testUpdateActivatedWithLogCmdOk() throws CancelFSMException {
		binding.conditionRespected = true;
		binding.logCmd(true);
		binding.fsmStarts();
		binding.fsmUpdates();
		assertTrue(binding.then);
	}

	@Test
	void testUpdateNotActivated() throws CancelFSMException {
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.first = false;
		binding.setActivated(false);
		binding.fsmUpdates();
		assertFalse(binding.then);
		assertFalse(binding.first);
	}

	@Test
	void testUpdateWhenCmdNotCreated() throws CancelFSMException {
		binding.conditionRespected = false;
		binding.fsmStarts();
		binding.logCmd(true);
		binding.conditionRespected = true;
		binding.fsmUpdates();
		assertTrue(binding.first);
		assertNotNull(binding.cmd);
	}

	@Test
	void testUpdateWithCmdCrash() throws CancelFSMException {
		final var ex = new IllegalArgumentException();
		final Supplier<CmdStub> supplier = () -> {
			throw ex;
		};
		binding = new WidgetBindingStub(true, supplier, new InteractionStub());

		binding.conditionRespected = false;
		binding.fsmStarts();
		binding.conditionRespected = true;
		binding.fsmUpdates();
		errors.remove(ex);
		assertFalse(binding.first);
		assertNull(binding.cmd);
	}

	@Test
	void testUpdateContinuousWithLogCannotDo() throws CancelFSMException {
		binding = new WidgetBindingStub(true, CmdStub::new, new InteractionStub());
		binding.conditionRespected = true;
		binding.logCmd(true);
		binding.fsmStarts();
		binding.cmd.candoValue = false;
		binding.fsmUpdates();
		assertTrue(binding.ifcannotdo);
		assertEquals(0, binding.cmd.cptDoCmdBody.get());
	}

	@Test
	void testUpdateContinuousNotLogCanDo() throws CancelFSMException {
		binding = new WidgetBindingStub(true, CmdStub::new, new InteractionStub());
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.cmd.candoValue = true;
		binding.fsmUpdates();
		assertFalse(binding.ifcannotdo);
		assertEquals(1, binding.cmd.cptDoCmdBody.get());
	}

	@Test
	void testStopNoLogCmdCreated() throws CancelFSMException {
		binding.conditionRespected = true;
		binding.fsmStarts();
		final var cmd = binding.cmd;
		binding.conditionRespected = false;
		binding.fsmStops();
		assertEquals(Command.CmdStatus.CANCELLED, cmd.getStatus());
		assertTrue(binding.unbind);
		assertNull(binding.cmd);
		assertEquals(1, binding.getTimesCancelled());
	}

	@Test
	void testStopNoCmdCreated() throws CancelFSMException {
		binding.conditionRespected = false;
		binding.fsmStarts();
		binding.fsmStops();
		assertFalse(binding.unbind);
		assertNull(binding.cmd);
		assertEquals(0, binding.getTimesCancelled());
	}

	@Test
	void testStopWithLogCmdCreatedCancelledTwoTimes() throws CancelFSMException {
		binding.conditionRespected = true;
		binding.logCmd(true);
		binding.fsmStarts();
		binding.conditionRespected = false;
		binding.fsmStops();
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.conditionRespected = false;
		binding.fsmStops();
		assertEquals(2, binding.getTimesCancelled());
	}

	@Test
	void testUninstallBinding() {
		binding.uninstallBinding();
		assertFalse(binding.isActivated());
		assertTrue(binding.cmdsProduced.hasComplete());
		assertNull(binding.loggerCmd);
		assertNull(binding.loggerBinding);
	}

	@Test
	void testExecAsync() throws CancelFSMException {
		binding.setAsync(true);
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.cmd.candoValue = true;
		binding.fsmStops();
		assertTrue(binding.execAsync);
	}

	@Test
	void testExecNotAsync() throws CancelFSMException {
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.cmd.candoValue = true;
		binding.fsmStops();
		assertFalse(binding.execAsync);
	}

	@Test
	void testAfterExecCmdHadEffects() throws CancelFSMException {
		binding = new WidgetBindingStub(true, CmdStubUndoable::new, new InteractionStub());
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.cmd.candoValue = true;
		binding.fsmStops();
		assertEquals(1, CommandsRegistry.getInstance().getCommands().size());
		assertTrue(CommandsRegistry.getInstance().getCommands().get(0) instanceof CmdStubUndoable);
		assertTrue(binding.cmdHadEffects);
	}

	@Test
	void testAfterExecCmdHadEffectsNonePolicy() throws CancelFSMException {
		binding = new WidgetBindingStub(true, () -> new CmdStubUndoable() {
			@Override
			public RegistrationPolicy getRegistrationPolicy() {
				return RegistrationPolicy.NONE;
			}
		}, new InteractionStub());
		binding.conditionRespected = true;
		binding.fsmStarts();
		binding.cmd.candoValue = true;
		CommandsRegistry.getInstance().addCommand(binding.cmd);
		binding.fsmStops();
		assertTrue(CommandsRegistry.getInstance().getCommands().isEmpty());
		assertTrue(binding.cmdHadEffects);
	}

	static class CmdStubUndoable extends CmdStub implements Undoable {
		@Override
		public boolean hadEffect() {
			return true;
		}
		@Override
		public boolean canDo() {
			return true;
		}
		@Override
		public void undo() {
		}
		@Override
		public void redo() {
		}
		@Override
		public String getUndoName(final ResourceBundle bundle) {
			return "";
		}
	}

	static class WidgetBindingStub extends WidgetBindingImpl<CmdStub, InteractionStub, InteractionData> {
		public boolean conditionRespected;
		public boolean mustCancel;
		public boolean unbind;
		public boolean cancel;
		public boolean endOrcancel;
		public boolean then;
		public boolean ifcannotdo;
		public boolean execAsync;
		public boolean first;
		public boolean cmdHadEffects;

		WidgetBindingStub(final boolean continuous, final Supplier<CmdStub> cmdCreation, final InteractionStub interaction) {
			super(continuous, i -> cmdCreation.get(), interaction);
		}

		WidgetBindingStub(final boolean continuous, final Function<InteractionData, CmdStub> cmdCreation, final InteractionStub interaction) {
			super(continuous, cmdCreation, interaction);
			conditionRespected = false;
			mustCancel = false;
		}

		@Override
		public void first() {
			super.first();
			first = true;
		}

		@Override
		public boolean when() {
			return conditionRespected;
		}

		@Override
		public boolean isStrictStart() {
			return mustCancel;
		}

		@Override
		protected void unbindCmdAttributes() {
			unbind = true;
		}

		@Override
		protected void executeCmdAsync(final Command cmd) {
			execAsync = true;
		}

		@Override
		public void then() {
			super.then();
			then = true;
		}

		@Override
		public void ifCannotExecuteCmd() {
			super.ifCannotExecuteCmd();
			ifcannotdo = true;
		}

		@Override
		public void ifCmdHadEffects() {
			super.ifCmdHadEffects();
			cmdHadEffects = true;
		}

		@Override
		public void cancel() {
			super.cancel();
			cancel = true;
		}

		@Override
		public void endOrCancel() {
			super.endOrCancel();
			endOrcancel = true;
		}
	}
}

