package org.malai.ex.draw.action;

import org.malai.ex.draw.model.MyDrawing;
import org.malai.ex.draw.model.MyShape;
import org.malai.undo.Undoable;

/*
 * Defines an Malai action that adds a shape into a drawing.
 * A Malai action must inherits from Action.
 * An action may be undoable. In such a case, it must implements
 * the interface Undoable.
 */
public class AddShape extends ShapeAction implements Undoable {
	/*
	 * The attributes of the actions are used to execute, undo, and redo them.
	 * They must be set throw setters, not using a constructor.
	 */
	private final MyDrawing drawing;

	/*
	 * A Malai action must have a constructor having NO parameter.
	 */
	public AddShape(final MyDrawing drawing, final MyShape shape) {
		super(shape);
		this.drawing = drawing;
	}

	@Override
	protected void doActionBody() {
		/*
		 * This operation must contain the execution of the action.
		 * Here, adding the shape into the drawing and setting the drawing as modified.
		 */
		drawing.addShape(shape);
	}

	@Override
	public boolean canDo() {
		/*
		 * Checking that the parameter mandatory for the execution of the action are correct.
		 * here, checking that the drawing and the shape are not null,
		 * and that the drawing does not contain the shape already.
		 */
		return super.canDo() && drawing != null && !drawing.getShapes().contains(shape);
	}


	@Override
	public void undo() {
		/* Defines what to do for undoing the action. */
		drawing.getShapes().remove(shape);
	}

	@Override
	public void redo() {
		/*
		 * Defines what to do for redoing the action.
		 * Here, doing the same job that the execution of the action.
		 */
		doActionBody();
	}

	@Override
	public String getUndoName() {
		/*
		 * This string is a message defining the goal of the action.
		 * It is used by the undo/redo manager to show a tooltip
		 * in the undo and redo widgets.
		 */
		return "Shape added";
	}
}
