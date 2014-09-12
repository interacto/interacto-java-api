package org.malai.swing.instrument.library;

import java.awt.Dimension;

import javax.swing.Icon;

import org.malai.action.library.Zoom;
import org.malai.error.ErrorCatcher;
import org.malai.instrument.Interactor;
import org.malai.properties.Zoomable;
import org.malai.swing.interaction.library.ButtonPressed;
import org.malai.swing.interaction.library.SpinnerModified;
import org.malai.swing.widget.MButton;
import org.malai.swing.widget.MSpinner;

/**
 * This instrument allows to zoom on the canvas.<br>
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
 * 2012-05-25<br>
 * @author Arnaud BLOUIN
 * @version 0.2
 */
public class WidgetZoomer extends BasicZoomer {
	/** The spinner that helps to change the zoom. */
	protected MSpinner zoomSpinner;

	/** This button allows to set the default zoom level. */
	protected MButton zoomDefaultButton;


	/**
	 * Creates the instrument.
	 * @param zoomable The object that will be zoomed.
	 * @param withDefaultZoomButton True: a button to set to the default zoom value will be created.
	 * @param withZoomSpinner True: a spinner changing the zoom level will be created.
	 * @param withKeys True: the keys + and - will be used to zoom in and out.
	 * @param defaultButIcon The icon of the default button. Can be null.
	 * @param defaultButToolTip The tool tip text of the default zoom button. Can be null.
	 * @param maxDimSpinner The maximal dimension of the zoom spinner. Can be null.
	 * @param spinnerToolTip The tool tip text of the zoom spinner. Can be null.
	 */
	public WidgetZoomer(final Zoomable zoomable, final boolean withDefaultZoomButton, final boolean withZoomSpinner,
						final Icon defaultButIcon, final String defaultButToolTip, final Dimension maxDimSpinner,
						final String spinnerToolTip, final boolean withKeys) {
		super(zoomable, withKeys);

		if(withDefaultZoomButton) {
			zoomDefaultButton = new MButton(defaultButIcon);
			zoomDefaultButton.setToolTipText(defaultButToolTip);
		}

		if(withZoomSpinner) {
			zoomSpinner = new MSpinner(new MSpinner.MSpinnerNumberModel(zoomable.getZoom()*100., zoomable.getMinZoom()*100.,
																		zoomable.getMaxZoom()*100., zoomable.getZoomIncrement()*100.), null);
			if(maxDimSpinner!=null)
				zoomSpinner.setMaximumSize(maxDimSpinner);
			zoomSpinner.setToolTipText(spinnerToolTip);
		}
	}


	@Override
	public void reinit() {
		if(zoomSpinner!=null)
			zoomSpinner.setValueSafely(zoomable.getZoom()*100);
	}


	@Override
	protected void initialiseInteractors() {
		super.initialiseInteractors();
		try{
			addInteractor(new Spinner2Zoom(this));
			addInteractor(new Button2Zoom(this));
		}catch(final InstantiationException | IllegalAccessException e){
			ErrorCatcher.INSTANCE.reportError(e);
		}
	}


	@Override
	public void interimFeedback() {
		if(zoomSpinner!=null)
			zoomSpinner.setValueSafely(zoomable.getZoom()*100);
	}


	/**
	 * @return The button that sets the zoom to its default value.
	 * @since 0.2
	 */
	public MButton getZoomDefaultButton() {
		return zoomDefaultButton;
	}


	/**
	 * @return The spinner that modifies the zoom level.
	 * @since 0.2
	 */
	public MSpinner getZoomSpinner() {
		return zoomSpinner;
	}


	@Override
	public void setActivated(final boolean activated) {
		super.setActivated(activated);
		if(zoomDefaultButton!=null)
			zoomDefaultButton.setVisible(activated);

		if(zoomSpinner!=null)
			zoomSpinner.setVisible(activated);
	}




	/**
	 * This link maps a button that changes the zoom to a button-pressed interaction.
	 */
	protected static class Button2Zoom extends Interactor<Zoom, ButtonPressed, WidgetZoomer> {
		/**
		 * Initialises the link.
		 * @param ins The zoomer.
		 */
		protected Button2Zoom(final WidgetZoomer ins) throws InstantiationException, IllegalAccessException {
			super(ins, false, Zoom.class, ButtonPressed.class);
		}


		@Override
		public void initAction() {
			action.setZoomable(instrument.zoomable);
			action.setZoomLevel(1.);
		}


		@Override
		public boolean isConditionRespected() {
			return instrument.zoomDefaultButton==interaction.getButton();
		}
	}



	/**
	 * The links maps the zoom spinner to the zoom action.
	 */
	protected static class Spinner2Zoom extends Interactor<Zoom, SpinnerModified, WidgetZoomer> {
		/**
		 * Initialises the link.
		 * @param ins The zoomer.
		 */
		protected Spinner2Zoom(final WidgetZoomer ins) throws InstantiationException, IllegalAccessException {
			super(ins, true, Zoom.class, SpinnerModified.class);
		}


		@Override
		public void initAction() {
			action.setZoomable(instrument.zoomable);

		}


		@Override
		public void updateAction() {
			action.setZoomLevel(Double.valueOf(interaction.getSpinner().getValue().toString())/100.);
		}


		@Override
		public boolean isConditionRespected() {
			return instrument.zoomSpinner==interaction.getSpinner();
		}
	}
}
