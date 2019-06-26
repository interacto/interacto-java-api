module interacto.java.api {
	requires java.logging;
	requires java.desktop;

	exports io.interacto.binding;
	exports io.interacto.command;
	exports io.interacto.command.library;
	exports io.interacto.error;
	exports io.interacto.fsm;
	exports io.interacto.instrument;
	exports io.interacto.interaction;
	exports io.interacto.logging;
	exports io.interacto.properties;
	exports io.interacto.undo;
	exports io.interacto.utils;
}
