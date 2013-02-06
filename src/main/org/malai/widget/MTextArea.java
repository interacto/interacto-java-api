package org.malai.widget;

import javax.swing.JTextArea;
import org.malai.interaction.Eventable;
import org.malai.interaction.SwingEventManager;
import org.malai.picking.Pickable;
import org.malai.picking.Picker;

/**
 * This widgets is based on a JTextArea. It allows to be used in the Malai framework for picking.<br>
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
 * 12/12/2010<br>
 * @author Arnaud BLOUIN
 * @version 0.2
 * @since 0.2
 */
public class MTextArea extends JTextArea implements Pickable, Scrollable, Eventable {
	private static final long serialVersionUID = 1L;

	/** The event manager that listens events produced by the text area. May be null. */
	protected SwingEventManager eventManager;

	/** The possible scrollpane that contains the text area. */
	protected MScrollPane scrollpane;


	/**
	 * {@link JTextArea}
	 * @param withScrollPane True: a scrollpane will be created and will contain the text area.
	 * @param withEvtManager True: the text area will have an event manager.
	 * @since 0.2
	 */
	public MTextArea(final boolean withScrollPane, final boolean withEvtManager) {
		this(withScrollPane, withEvtManager, false);
	}


	/**
	 * {@link JTextArea}
	 * @param withScrollPane True: a scrollpane will be created and will contain the text area.
	 * @param withEvtManager True: the text area will have an event manager.
	 * @param eventOnEachModification If true: each modification
	 * of the underlying document will launch an event (DocumentEvent)
	 * that can be used by a link based on the interaction TextChanged.
	 * If false, the user has to type on the back space key to create
	 * an event.
	 * @since 0.2
	 */
	public MTextArea(final boolean withScrollPane, final boolean withEvtManager, final boolean eventOnEachModification) {
		super();

		if(withEvtManager) {
			eventManager = new SwingEventManager();
			eventManager.attachTo(this);
		}

		if(withScrollPane) {
			scrollpane = new MScrollPane();
			scrollpane.getViewport().add(this);
		}

		if(eventOnEachModification)
			getDocument().putProperty(SwingEventManager.OWNING_PROPERTY, this);
	}



	@Override
	public boolean hasEventManager() {
		return eventManager!=null;
	}


	@Override
	public SwingEventManager getEventManager() {
		return eventManager;
	}

	@Override
	public MScrollPane getScrollpane() {
		return scrollpane;
	}


	@Override
	public boolean hasScrollPane() {
		return scrollpane!=null;
	}


	@Override
	public Picker getPicker() {
		return WidgetUtilities.INSTANCE.getPicker(this);
	}

	@Override
	public boolean contains(final double x, final double y) {
		return WidgetUtilities.INSTANCE.contains(this, x, y);
	}
}
