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
package org.malai.javafx.binding;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import org.malai.action.ActionImpl;
import org.malai.javafx.instrument.JfxInstrument;
import org.malai.javafx.interaction.library.TabSelected;

/**
 * The binding builder to create bindings between a tab interaction and a given action.
 * @param <A> The type of the action to produce.
 * @author Arnaud Blouin
 */
public class TabBinder<A extends ActionImpl> extends Binder<TabPane, A, TabSelected> {
	public TabBinder(final Class<A> action, final JfxInstrument instrument) {
		super(action, new TabSelected(), instrument);
	}

	@Override
	public TabBinder<A> on(final TabPane... widget) {
		super.on(widget);
		return this;
	}

	@Override
	public TabBinder<A> on(final ObservableList<Node> widgets) {
		super.on(widgets);
		return this;
	}

	@Override
	public TabBinder<A> map(final Function<TabSelected, A> actionFunction) {
		actionProducer = actionFunction;
		return this;
	}

	@Override
	public TabBinder<A> first(final Consumer<A> initActionFct) {
		super.first(initActionFct);
		return this;
	}

	@Override
	public Binder<TabPane, A, TabSelected> first(final BiConsumer<A, TabSelected> initActionFct) {
		super.first(initActionFct);
		return this;
	}

	@Override
	public TabBinder<A> when(final Predicate<TabSelected> checkAction) {
		super.when(checkAction);
		return this;
	}

	@Override
	public TabBinder<A> when(final BooleanSupplier checkAction) {
		super.when(checkAction);
		return this;
	}

	@Override
	public TabBinder<A> async() {
		super.async();
		return this;
	}

	@Override
	public TabBinder<A> end(final BiConsumer<A, TabSelected> onEndFct) {
		super.end(onEndFct);
		return this;
	}

	@Override
	public JfXWidgetBinding<A, TabSelected, ?> bind() throws IllegalAccessException, InstantiationException {
		final JFxAnonNodeBinding<A, TabSelected, JfxInstrument> binding = new JFxAnonNodeBinding<>(instrument, false, actionClass, interaction,
			initAction, null, checkConditions, onEnd, actionProducer, null, null,
			widgets.stream().map(w -> (Node) w).collect(Collectors.toList()), additionalWidgets, async);
		instrument.addBinding(binding);
		return binding;
	}
}
