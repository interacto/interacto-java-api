package org.malai.swing.widget;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.malai.interaction.Eventable;
import org.malai.picking.Pickable;
import org.malai.picking.Picker;
import org.malai.swing.interaction.SwingEventManager;

/**
 * Extends the Java JPanel to conform malai requirements.<br>
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
 * 05/10/2010<br>
 * @author Arnaud BLOUIN
 * @version 0.1
 * @since 0.1
 */
public class MPanel extends JPanel implements Picker, Eventable, ScrollableWidget {
	private static final long	serialVersionUID	= 1L;

	/** The possible scrollpane that contains the panel. */
	protected MScrollPane scrollpane;

	/** The event manager that listens events produced by the panel. May be null. */
	protected SwingEventManager eventManager;



	/**
	 * Creates and initialises a panel.
	 * @param withScrollPane True: a scrollpane will be created and will contain the panel.
	 * @param withEvtManager True: the panel will have an event manager.
	 * @since 0.1
	 */
	public MPanel(final boolean withScrollPane, final boolean withEvtManager) {
		super();

		if(withScrollPane) {
			scrollpane = new MScrollPane();
			scrollpane.getViewport().add(this);
		}

		if(withEvtManager) {
			eventManager = new SwingEventManager();
			eventManager.attachTo(this);
		}
	}


	@Override
	public Pickable getPickableAt(final double x, final double y) {
		return SwingWidgetUtilities.INSTANCE.getPickableAt(this, getComponents(), x, y);
	}


	@Override
	public Picker getPickerAt(final double x, final double y) {
		return SwingWidgetUtilities.INSTANCE.getPickerAt(this, getComponents(), x, y);
	}


	@Override
	public boolean contains(final Object obj) {
		return SwingWidgetUtilities.INSTANCE.contains(getComponents(), obj);
	}


	@Override
	public boolean hasScrollPane() {
		return scrollpane!=null;
	}


	@Override
	public MScrollPane getScrollpane() {
		return scrollpane;
	}


	@Override
	public boolean isHorizontalScrollbarVisible() {
		return getScrollbar(true).isVisible();
	}


	@Override
	public boolean isVerticalScrollbarVisible() {
		return getScrollbar(true).isVisible();
	}



	/**
	 * @param vertical True: the vertical scrollbar is returned. Otherwise, the horizontal scroll bar.
	 * @return The required scroll bar or null if the panel has no scrollpane.
	 * @since 0.1
	 */
	protected JScrollBar getScrollbar(final boolean vertical) {
		if(hasScrollPane())
			return vertical ? getScrollpane().getVerticalScrollBar() : getScrollpane().getHorizontalScrollBar();

		return null;
	}


	@Override
	public void scrollHorizontally(final int increment) {
		scroll(increment, false);
	}


	@Override
	public void scrollVertically(final int increment) {
		scroll(increment, true);
	}



	/**
	 * Scroll the vertical or horizontal scroll bar, if possible, using the given increment.
	 * @param increment The increment to apply on the vertical scroll bar.
	 * @param vertical True: the vertical scroll bar is
	 * @since 0.1
	 */
	protected void scroll(final int increment, final boolean vertical) {
		JScrollBar scrollbar = getScrollbar(vertical);

		if(scrollbar!=null && scrollbar.isVisible())
			scrollbar.setValue(scrollbar.getValue() - increment);
	}



	@Override
	public SwingEventManager getEventManager() {
		return eventManager;
	}


	@Override
	public Component add(final Component comp, final int index) {
		SwingWidgetUtilities.INSTANCE.attachAddedComponent(eventManager, comp);
		return super.add(comp, index);
	}


	@Override
	public void add(final Component comp, final Object constraints, final int index) {
		SwingWidgetUtilities.INSTANCE.attachAddedComponent(eventManager, comp);
		super.add(comp, constraints, index);
	}


	@Override
	public void add(final Component comp, final Object constraints) {
		SwingWidgetUtilities.INSTANCE.attachAddedComponent(eventManager, comp);
		super.add(comp, constraints);
	}



	@Override
	public Component add(final Component comp) {
		SwingWidgetUtilities.INSTANCE.attachAddedComponent(eventManager, comp);
		return super.add(comp);
	}



	@Override
	public Component add(final String name, final Component comp) {
		SwingWidgetUtilities.INSTANCE.attachAddedComponent(eventManager, comp);
		return super.add(name, comp);
	}


	@Override
	public boolean hasEventManager() {
		return eventManager!=null;
	}
}
