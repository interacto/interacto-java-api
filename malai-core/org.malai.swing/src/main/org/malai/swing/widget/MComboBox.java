package org.malai.swing.widget;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.malai.picking.Pickable;
import org.malai.picking.Picker;
import org.malai.swing.interaction.SwingEventManager;

/**
 * This widgets is based on a JComboBox. It allows to be used in the Malai framework for picking.<br>
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
 * 06/05/2010<br>
 * @author Arnaud BLOUIN
 * @version 0.2
 * @since 0.2
 */
public class MComboBox<E> extends JComboBox<E> implements Pickable {
	private static final long serialVersionUID = 1L;

	/** The label used to describe the use of the spinner. */
	protected JLabel label;


	/**
	 * {@link JComboBox#JComboBox()}
	 */
	public MComboBox() {
		super();
	}

	/**
	 * {@link JComboBox#JComboBox(ComboBoxModel)}
	 */
	public MComboBox(final ComboBoxModel<E> aModel, final JLabel label) {
		super(aModel);
		setLabel(label);
	}

	/**
	 * {@link JComboBox#JComboBox(E[])}
	 */
	public MComboBox(final E[] items, final JLabel label) {
		super(items);
		setLabel(label);
	}


	/**
	 * Sets the label of the spinner.
	 * @param label The new label or null.
	 */
	public void setLabel(final JLabel label) {
		//FIXME SCALA: to extract as a trait.
		if(this.label!=null)
			this.label.setLabelFor(null);
		
		this.label = label;

		if(label!=null)
			label.setLabelFor(this);
	}


	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);

		if(label!=null)
			label.setVisible(visible);
	}


	/**
	 * @return The label used to describe the use of the combobox. Can be null.
	 * @since 0.2
	 */
	public JLabel getLabel() {
		return label;
	}


	/**
	 * Idem than method setSelectedItem(value) but here the SwingEventManager listener is removed
	 * from the combobox before the setting to avoid any unexpected behaviour.
	 * The listener is then re-added.<br>
	 * The widget must be linked to a single SwingEventManager listener at max.
	 * @param anObject The item to select.
	 * @since 3.0
	 */
	public void setSelectedItemSafely(final Object anObject) {
		final ItemListener[] list = getItemListeners();
		ItemListener il = null;

		for(int i=0; i<list.length && il==null; i++)
			// Removing the listener.
			if(list[i] instanceof SwingEventManager) {
				removeItemListener(list[i]);
				il = list[i];
			}

		setSelectedItem(anObject);

		// Re-adding the listener if needed.
		if(il!=null)
			addItemListener(il);
	}


    @Override
	protected void selectedItemChanged() {
    	selectedItemReminder = dataModel.getSelectedItem();

    	if(selectedItemReminder != null)
    		fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, selectedItemReminder, ItemEvent.SELECTED));
    }


	@Override
	public boolean contains(final double x, final double y) {
		return SwingWidgetUtilities.INSTANCE.contains(this, x, y);
	}


	@Override
	public Picker getPicker() {
		return SwingWidgetUtilities.INSTANCE.getPicker(this);
	}
}
