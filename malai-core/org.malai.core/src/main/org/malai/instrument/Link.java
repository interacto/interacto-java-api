package org.malai.instrument;

import org.malai.action.Action;
import org.malai.action.ActionsRegistry;
import org.malai.error.ErrorCatcher;
import org.malai.interaction.Eventable;
import org.malai.interaction.Interaction;
import org.malai.interaction.InteractionHandler;
import org.malai.stateMachine.MustAbortStateMachineException;
import org.malai.undo.Undoable;

/**
 * In the Malai interaction model, an instrument links interactions to actions.
 * Thus, an instrument is composed of Link definitions: each Link links an interaction
 * to an action. A Link manages the life cycle of an action following the life cycle
 * of the interaction (started, aborted, etc.).<br>
 * <br>
 * This file is part of Malai.<br>
 * Copyright (c) 2009-2012 Arnaud BLOUIN<br>
 * <br>
 * Malai is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * <br>
 * Malai is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br>
 * <br>
 * 05/10/2010<br>
 * @author Arnaud BLOUIN
 * @version 0.2
 * @since 0.2
 * @param <A> The type of the action that will produce this link.
 * @param <I> The type of the interaction that will use this link.
 * @param <N> The type of the instrument that will contain this link.
 */
public abstract class Link<A extends Action, I extends Interaction, N extends Instrument> implements InteractionHandler {

	/** The source interaction. */
	protected I interaction;

	/** The target action. */
	protected A action;

	/** The instrument that contains the link. */
	protected N instrument;

	/** Specifies if the action must be execute or update
	 * on each evolution of the interaction. */
	protected boolean execute;

	protected Class<A> clazzAction;

	/**
	 * Creates a link. This constructor must initialise the interaction. The link is (de-)activated if the given
	 * instrument is (de-)activated.
	 * @param ins The instrument that contains the link.
	 * @param exec Specifies if the action must be execute or update on each evolution of the interaction.
	 * @param clazzAction The type of the action that will be created. Used to instantiate the action by reflexivity.
	 * @param clazzInteraction The type of the interaction that will be created. Used to instantiate the interaction by reflexivity.
	 * @throws IllegalAccessException If no free-parameter constructor is available.
	 * @throws InstantiationException If an error occurs during instantiation of the interaction/action.
	 * @throws IllegalArgumentException If the given interaction or instrument is null.
	 * @since 0.2
	 */
	public Link(final N ins, final boolean exec, final Class<A> clazzAction, final Class<I> clazzInteraction) throws InstantiationException, IllegalAccessException {
		super();

		if(ins==null || clazzAction==null || clazzInteraction==null)
			throw new IllegalArgumentException();

		this.clazzAction = clazzAction;
		interaction = clazzInteraction.newInstance();
		action		= null;
		instrument  = ins;
		execute		= exec;
		setActivated(ins.isActivated());
		interaction.addHandler(this);
	}



	/**
	 * Binds the interaction of the link to a Eventable object that produces
	 * events used by the interaction.
	 * @param eventable The eventable object that gathers event used by the interaction.
	 * @since 0.2
	 */
	public void addEventable(final Eventable eventable) {
		if(eventable!=null && interaction!=null)
			interaction.linkToEventable(eventable);
	}


	/**
	 * Stops the interaction and clears all its events waiting for a process.
	 * @since 0.2
	 */
	public void clearEvents() {
		interaction.reinit();
		interaction.clearEventsStillInProcess();
	}


	/**
	 * Initialises the action of the link. If the attribute 'action' is
	 * not null, nothing will be done.
	 * @since 0.2
	 */
	protected void createAction() {
		try{
			action = clazzAction.newInstance();
		}catch(final InstantiationException | IllegalAccessException e){
			ErrorCatcher.INSTANCE.reportError(e);
		}
	}


	/**
	 * After being created by method createAction, the action must be initialised
	 * by this method.
	 * @since 0.2
	 */
	public abstract void initAction();



	/**
	 * Updates the current action. To override.
	 * @since 0.2
	 */
	public void updateAction() {
		// to override.
	}


	/**
	 * @return True if the condition of the link is respected.
	 */
	public abstract boolean isConditionRespected();



	/**
	 * @return The interaction.
	 */
	public I getInteraction() {
		return interaction;
	}


	/**
	 * @return The action in progress or null.
	 */
	public A getAction() {
		return action;
	}


	/**
	 * @return True if the link is activated.
	 */
	public boolean isActivated() {
		return instrument.isActivated();
	}


//	/**
//	* Indicates if the link can be run. To be run, no link, of the instrument, that produces the
//	* same type of action must be running.
//	* @return True: The link can be run.
//	*/
//	public boolean isRunnable() {
//		for(final Link<?, ?, ?> link : instrument.links)
//			if(link!=this && link.isRunning() && link.clazzAction==clazzAction) {
//				System.out.println("Not RUNNABLE: " + this + " because of " + link);
//				return true;
//			//	return false;
//			}
//
//		return true;
//	}



	/**
	* @return True: if the link is currently used.
	* since 0.2
	*/
	public boolean isRunning() {
		return interaction.isRunning();
	}


	/**
	 * Sometimes the interaction of two different links can overlap themselves. It provokes
	 * that the first interaction can stops while the second is blocked in a intermediary state.
	 * Two solutions are possible to avoid such a problem:<br>
	 * - the use of this function that perform some tests. If the test fails, the starting interaction
	 * is aborted and the resulting action is never created;<br>
	 * - the modification of one of the interactions to avoid the overlapping.
	 * @return True: if the starting interaction must be aborted so that the action is never created.
	 * @since 0.2
	 */
	public boolean isInteractionMustBeAborted() {
		return false;
	}


	@Override
	public void interactionAborts(final Interaction inter) {
		if(action!=null && inter==interaction) {
			action.abort();

			// The instrument is notified about the aborting of the action.
			instrument.onActionAborted(action);

			if(isExecute())
				if(action instanceof Undoable)
					((Undoable)action).undo();
				else throw new MustBeUndoableActionException(action.getClass());

			action = null;
			instrument.interimFeedback();
		}
	}


	@Override
	public void interactionStarts(final Interaction inter) throws MustAbortStateMachineException {
		if(inter==interaction && isInteractionMustBeAborted())
			throw new MustAbortStateMachineException();
//isRunnable() &&
		if(action==null && inter==interaction && instrument.isActivated() && isConditionRespected()) {
			createAction();
			initAction();
			interimFeedback();
		}
	}


	@Override
	public void interactionStops(final Interaction inter) {
		if(action!=null && interaction==inter) {
			if(isConditionRespected()) {
    			if(!execute)
    				updateAction();

    			if(action.doIt()) {
    				instrument.onActionExecuted(action);
    				action.done();
    				instrument.onActionDone(action);
    			}

    			if(action.hadEffect())
					if(action.isRegisterable()) {
    					 ActionsRegistry.INSTANCE.addAction(action, instrument);
    					 instrument.onActionAdded(action);
    				}
    				else {
    					ActionsRegistry.INSTANCE.cancelActions(action);
    					instrument.onActionCancelled(action);
    				}

    			action = null;
			}
			else {
				action.abort();
				instrument.onActionAborted(action);
				action = null;
			}
			instrument.interimFeedback();
		}
	}


	@Override
	public void interactionUpdates(final Interaction inter) {
		if(inter==interaction && isConditionRespected()) {
			if(action==null) {
				createAction();
				initAction();
			}

			updateAction();

			if(execute && action.canDo()) {
				action.doIt();
				instrument.onActionExecuted(action);
			}

			interimFeedback();
		}
	}


	/**
	 * @return True if the action is executed on each evolution
	 * of the interaction.
	 */
	public boolean isExecute() {
		return execute;
	}


	/**
	 * Defines the interim feedback of the link. If overridden, the interim
	 * feedback of its instrument should be define too.
	 */
	public void interimFeedback() {
		//
	}


	/**
	 * Activates the link.
	 * @param activated True: the link is activated. Otherwise, it is desactivated.
	 * @since 3.0
	 */
	public void setActivated(final boolean activated) {
		interaction.setActivated(activated);
	}


	/**
	 * @return The instrument that contains the link.
	 * @since 0.1
	 */
	public N getInstrument() {
		return instrument;
	}
}
