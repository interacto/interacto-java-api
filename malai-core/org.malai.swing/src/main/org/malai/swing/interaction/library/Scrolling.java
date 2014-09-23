package org.malai.swing.interaction.library;

import org.malai.interaction.InteractionImpl;
import org.malai.interaction.TerminalState;
import org.malai.picking.Pickable;
import org.malai.stateMachine.SourceableState;
import org.malai.stateMachine.TargetableState;
import org.malai.swing.interaction.ScrollTransition;
import org.malai.swing.interaction.SwingInteraction;

/**
 * Defines an interaction based on mouse scrolling.<br>
 * <br>
 * This file is part of Malai.<br>
 * Copyright (c) 2005-2014 Arnaud BLOUIN<br>
 * <br>
 * Malai is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * <br>
 * Malai is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br>
 * <br>
 * 10/28/2010<br>
 * @author Arnaud BLOUIN
 * @version 0.2
 * @since 0.2
 */
public class Scrolling extends SwingInteraction {
	/** The object on which the scroll is performed. */
	protected Pickable scrollTarget;

	/** The X-coordinate of the scroll position. */
 	protected double px;

	/** The Y-coordinate of the scroll position. */
 	protected double py;

 	/** The total increment of the scrolling. */
 	protected int increment;


	/**
	 * Creates the interaction.
	 */
	public Scrolling() {
		super();
		initStateMachine();
	}


	@Override
	public void reinit() {
		super.reinit();

		scrollTarget = null;
		px = 0.;
		py = 0.;
		increment = 0;
	}


	@SuppressWarnings("unused")
	@Override
	protected void initStateMachine() {
		final TerminalState wheeled = new TerminalState("scrolled"); //$NON-NLS-1$

		addState(wheeled);

		new ScrollingScrollTransition(initState, wheeled);
	}



	/**
	 * @return The object on which the scroll is performed.
	 * @since 0.2
	 */
	public Pickable getScrollTarget() {
		return scrollTarget;
	}


	/**
	 * @return The X-coordinate of the scroll position.
	 * @since 0.2
	 */
	public double getPx() {
		return px;
	}


	/**
	 * @return The Y-coordinate of the scroll position.
	 * @since 0.2
	 */
	public double getPy() {
		return py;
	}


	/**
	 * @return The total increment of the scrolling.
	 * @since 0.2
	 */
	public int getIncrement() {
		return increment;
	}


	/**
	 * @param scrollTarget The object on which the scroll is performed.
	 * @since 0.2
	 */
	protected void setScrollTarget(final Pickable scrollTarget) {
		this.scrollTarget = scrollTarget;
	}


	/**
	 * @param px The X-coordinate of the scroll position.
	 * @since 0.2
	 */
	protected void setPx(final double px) {
		this.px = px;
	}


	/**
	 * @param py The Y-coordinate of the scroll position.
	 * @since 0.2
	 */
	protected void setPy(final double py) {
		this.py = py;
	}


	/**
	 * @param increment The total increment of the scrolling.
	 * @since 0.2
	 */
	protected void setIncrement(final int increment) {
		this.increment = increment;
	}


	/**
	 * This scroll transition modifies the scrolling interaction.
	 */
	public class ScrollingScrollTransition extends ScrollTransition {
		/**
		 * Creates the transition.
		 * @param inputState The input state of the transition.
		 * @param outputState The output state of the transition.
		 */
		public ScrollingScrollTransition(final SourceableState inputState, final TargetableState outputState) {
			super(inputState, outputState);
		}

		@Override
		public void action() {
			Scrolling.this.setLastHIDUsed(this.hid);
			Scrolling.this.increment  	= Scrolling.this.increment + (this.direction>0 ? -this.amount : this.amount);
			Scrolling.this.px			= this.x;
			Scrolling.this.py        	= this.y;
			Scrolling.this.scrollTarget = InteractionImpl.getPickableAt(this.x, this.y, this.source);
		}
	}
}
