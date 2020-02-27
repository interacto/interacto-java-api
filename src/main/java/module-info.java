module interacto.java.api {
	requires java.logging;
	requires java.desktop;
	requires io.reactivex.rxjava2;

	exports io.github.interacto.binding;
	exports io.github.interacto.command;
	exports io.github.interacto.command.library;
	exports io.github.interacto.error;
	exports io.github.interacto.fsm;
	exports io.github.interacto.instrument;
	exports io.github.interacto.interaction;
	exports io.github.interacto.properties;
	exports io.github.interacto.undo;
}
