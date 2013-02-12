package org.malai.kevoree.examples.drawingEditor.model.impl;


import org.malai.kevoree.examples.drawingEditor.model.interfaces.IPoint;
import org.malai.kevoree.examples.drawingEditor.model.interfaces.IShape;
import org.malai.kevoree.examples.drawingEditor.model.interfaces.IText;

/**
 * Defines a model of a text.<br>
 * <br>
 * This file is part of LaTeXDraw.<br>
 * Copyright (c) 2005-2012 Arnaud BLOUIN<br>
 * <br>
 * LaTeXDraw is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * <br>
 * LaTeXDraw is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br>
 * <br>
 * 07/05/2009<br>
 * @author Arnaud BLOUIN
 * @version 3.0
 * @since 3.0
 */
class LText extends LPositionShape implements IText {
	/** The text */
	protected String text;

	/** The text position of the text. */
	protected TextPosition textPosition;


	/**
	 * The constructor by default.
	 * @param isUniqueID True: the model will have a unique ID.
	 * @since 3.0
	 */
	protected LText(final boolean isUniqueID) {
		this(isUniqueID, new LPoint(), "text"); //$NON-NLS-1$
	}


	/**
	 * @param isUniqueID True: the model will have a unique ID.
	 * @param pt The position of the text.
	 * @param text The text.
	 * @throws IllegalArgumentException If pt is not valid.
	 * @since 3.0
	 */
	protected LText(final boolean isUniqueID, final IPoint pt, final String text) {
		super(isUniqueID, pt);

		this.text = text==null || text.length()==0 ? "text" : text; //$NON-NLS-1$
		textPosition = TextPosition.BOT_LEFT;
	}


	@Override
	public boolean isParametersEquals(final IShape sh, final boolean considerShadow) {
		boolean ok = super.isParametersEquals(sh, considerShadow);

		if(ok && sh instanceof IText) {
			final IText txt = (IText)sh;
			ok = txt.getText().equals(text) && txt.getTextPosition()==textPosition;
		}

		return ok;
	}


	@Override
	public IText duplicate() {
		final IShape sh = super.duplicate();
		return sh instanceof IText ? (IText)sh : null;
	}


	@Override
	public String getText() {
		return text;
	}


	@Override
	public void setText(final String text) {
		if(text!=null && text.length()>0)
			this.text = text;
	}


	@Override
	public void copy(final IShape s) {
		super.copy(s);

		if(s instanceof IText) {
			IText textSh 	= (IText)s;
			text 			= textSh.getText();
			textPosition	= textSh.getTextPosition();
		}
	}


	@Override
	public TextPosition getTextPosition() {
		return textPosition;
	}


	@Override
	public void setTextPosition(final TextPosition textPosition) {
		if(textPosition!=null)
			this.textPosition = textPosition;
	}
}
