/*
 * This file is part of Malai.
 * Copyright (c) 2005-2017 Arnaud BLOUIN
 * Malai is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * Malai is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */
package org.malai.javafx.interaction;

import javafx.scene.control.Spinner;
import org.malai.stateMachine.SourceableState;
import org.malai.stateMachine.TargetableState;

/**
 * A transition based on the onAction event of spinner.
 * @author Arnaud BLOUIN
 */
public class JfxSpinnerValueChangedTransition extends JFXWidgetTransition<Spinner<?>> {
	public JfxSpinnerValueChangedTransition(final SourceableState inputState, final TargetableState outputState) {
		super(inputState, outputState);
	}
}
