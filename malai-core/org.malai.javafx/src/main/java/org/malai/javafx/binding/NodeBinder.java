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
import javafx.scene.Node;
import org.malai.action.ActionImpl;
import org.malai.javafx.instrument.JfxInstrument;
import org.malai.javafx.interaction.JfxInteraction;

/**
 * The binding builder to create bindings between a given user interaction on a node and a given action.
 * @param <A> The type of the action to produce.
 * @param <I> The type of the user interaction to bind.
 * @author Arnaud Blouin
 */
public class NodeBinder<A extends ActionImpl, I extends JfxInteraction> extends UpdateBinder<Node, A, I> {
	public NodeBinder(final Class<A> action, final I interaction, final JfxInstrument instrument) {
		super(action, interaction, instrument);
	}

	@Override
	public NodeBinder<A, I> then(final BiConsumer<A, I> update) {
		super.then(update);
		return this;
	}

	@Override
	public NodeBinder<A, I> then(final Consumer<A> update) {
		super.then(update);
		return this;
	}

	@Override
	public NodeBinder<A, I> exec(final boolean execActionOnChanges) {
		super.exec(execActionOnChanges);
		return this;
	}

	@Override
	public NodeBinder<A, I> abort(final Runnable abort) {
		super.abort(abort);
		return this;
	}

	@Override
	public NodeBinder<A, I> feedback(final Runnable feedback) {
		super.feedback(feedback);
		return this;
	}

	@Override
	public NodeBinder<A, I> on(final Node... widget) {
		super.on(widget);
		return this;
	}

	@Override
	public NodeBinder<A, I> map(final Function<I, A> actionFunction) {
		actionProducer = actionFunction;
		return this;
	}

	@Override
	public NodeBinder<A, I> first(final Consumer<A> initActionFct) {
		super.first(initActionFct);
		return this;
	}

	@Override
	public NodeBinder<A, I> first(final BiConsumer<A, I> initActionFct) {
		super.first(initActionFct);
		return this;
	}

	@Override
	public NodeBinder<A, I> when(final Predicate<I> checkAction) {
		super.when(checkAction);
		return this;
	}

	@Override
	public NodeBinder<A, I> when(final BooleanSupplier checkAction) {
		super.when(checkAction);
		return this;
	}

	@Override
	public NodeBinder<A, I> async() {
		super.async();
		return this;
	}

	@Override
	public NodeBinder<A, I> end(final BiConsumer<A, I> onEndFct) {
		super.end(onEndFct);
		return this;
	}

	@Override
	public void bind() throws IllegalAccessException, InstantiationException {
		instrument.addBinding(new JFxAnonNodeBinding<>(instrument, execOnChanges, actionClass, interaction, initAction, updateFct, checkConditions,
			onEnd, actionProducer, abortFct, feedbackFct, widgets, async));
	}
}
