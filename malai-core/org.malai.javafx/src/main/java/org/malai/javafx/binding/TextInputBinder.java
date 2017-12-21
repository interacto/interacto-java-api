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
import javafx.scene.control.TextInputControl;
import org.malai.action.ActionImpl;
import org.malai.javafx.instrument.JfxInstrument;
import org.malai.javafx.interaction.library.TextChanged;

/**
 * The binding builder to create bindings between a text input interaction and a given action.
 * @param <A> The type of the action to produce.
 * @author Arnaud Blouin
 */
public class TextInputBinder<A extends ActionImpl, W extends TextInputControl> extends UpdateBinder<W, A, TextChanged> {
	public TextInputBinder(final Class<A> action, final JfxInstrument instrument) {
		super(action, new TextChanged(), instrument);
	}

	@Override
	public TextInputBinder<A, W> then(final BiConsumer<A, TextChanged> update) {
		super.then(update);
		return this;
	}

	@Override
	public TextInputBinder<A, W> then(final Consumer<A> update) {
		super.then(update);
		return this;
	}

	@Override
	public TextInputBinder<A, W> abort(final Runnable abort) {
		super.abort(abort);
		return this;
	}

	@Override
	public TextInputBinder<A, W> feedback(final Runnable feedback) {
		super.feedback(feedback);
		return this;
	}

	@Override
	public TextInputBinder<A, W> exec() {
		super.exec();
		return this;
	}

	@Override
	public TextInputBinder<A, W> on(final W... widget) {
		super.on(widget);
		return this;
	}

	@Override
	public TextInputBinder<A, W> on(final ObservableList<Node> widgets) {
		super.on(widgets);
		return this;
	}

	@Override
	public TextInputBinder<A, W> map(final Function<TextChanged, A> actionFunction) {
		actionProducer = actionFunction;
		return this;
	}

	@Override
	public TextInputBinder<A, W> first(final Consumer<A> initActionFct) {
		super.first(initActionFct);
		return this;
	}

	@Override
	public TextInputBinder<A, W> first(final BiConsumer<A, TextChanged> initActionFct) {
		super.first(initActionFct);
		return this;
	}

	@Override
	public TextInputBinder<A, W> when(final Predicate<TextChanged> checkAction) {
		super.when(checkAction);
		return this;
	}

	@Override
	public TextInputBinder<A, W> when(final BooleanSupplier checkAction) {
		super.when(checkAction);
		return this;
	}

	@Override
	public TextInputBinder<A, W> async() {
		super.async();
		return this;
	}

	@Override
	public TextInputBinder<A, W> end(final BiConsumer<A, TextChanged> onEndFct) {
		super.end(onEndFct);
		return this;
	}

	@Override
	public JfXWidgetBinding<A, TextChanged, ?> bind() throws IllegalAccessException, InstantiationException {
		final JFxAnonNodeBinding<A, TextChanged, JfxInstrument> binding = new JFxAnonNodeBinding<>(instrument, execOnChanges, actionClass, interaction,
			initAction, updateFct, checkConditions, onEnd, actionProducer, abortFct, feedbackFct,
			widgets.stream().map(w -> (Node) w).collect(Collectors.toList()), additionalWidgets, async);
		instrument.addBinding(binding);
		return binding;
	}
}
