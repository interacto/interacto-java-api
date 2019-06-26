/*
 * Interacto
 * Copyright (C) 2019 Arnaud Blouin
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
package io.interacto.instrument;

import io.interacto.properties.Modifiable;
import io.interacto.properties.Preferenciable;
import io.interacto.properties.Reinitialisable;
import java.util.List;
import io.interacto.command.CmdHandler;
import io.interacto.binding.WidgetBinding;

/**
 * The concept of instrument and its related services.
 * @author Arnaud BLOUIN
 */
public interface Instrument<T extends WidgetBinding> extends Preferenciable, Modifiable, Reinitialisable, CmdHandler {
	/**
	 * @return The number of widget bindings that compose the instrument.
	 */
	int getNbWidgetBindings();

	/**
	 * @return True: the instrument has at least one widget binding. False otherwise.
	 */
	boolean hasWidgetBindings();

	/**
	 * @return The widget bindings that compose the instrument. Cannot be null.
	 */
	List<T> getWidgetBindings();

	/**
	 * Stops the interactions of the instrument and clears all its events waiting for a process.
	 */
	void clearEvents();

	/**
	 * @return True if the instrument is activated.
	 */
	boolean isActivated();

	/**
	 * Activates or deactivates the instrument.
	 * @param activated True = activation.
	 */
	void setActivated(final boolean activated);

	/**
	 * Reinitialises the interim feedback of the instrument.
	 * Must be overridden.
	 */
	void interimFeedback();

	void uninstallBindings();
}
