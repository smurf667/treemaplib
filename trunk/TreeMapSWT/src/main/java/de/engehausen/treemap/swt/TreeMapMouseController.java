package de.engehausen.treemap.swt;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;

/**
 * This class registers mouse and mouse motion listeners with the tree map to be able
 * to handle desired mouse events (i.e mouse move for tooltips &amp; highlighting,
 * mouse released and so on).
 *
 * @param <N> the type of node of the model used for the tree map
 */
public class TreeMapMouseController<N> implements MouseMoveListener, MouseListener {

	protected final TreeMap<N> treemap;

	/**
	 * Creates a controller object that will register the mouse and mouse move
	 * listeners to the treemap.
	 * @param aTreeMap the tree map used by the mouse controller, must not be <code>null</code> 
	 */
	public TreeMapMouseController(final TreeMap<N> aTreeMap) {
		treemap = aTreeMap;

		treemap.addMouseMoveListener(this);
		treemap.addMouseListener(this);
	}

	@Override
	public void mouseMove(final MouseEvent event) {
		final boolean notBuilding;
		synchronized (this) {
			notBuilding = treemap.buildControl == null;
		}
		if (notBuilding) {
			treemap.selectRectangle(event.x, event.y);
		}
	}

	@Override
	public void mouseDoubleClick(final MouseEvent mouseevent) {
		// ignore
	}

	@Override
	public void mouseDown(final MouseEvent mouseevent) {
		// ignore
	}

	@Override
	public void mouseUp(final MouseEvent mouseevent) {
		if (treemap.model != null && treemap.currentRoot != null) {
			switch (mouseevent.button) {
				case 1:
					if (treemap.selected != null) {
						N runner = treemap.selected.getNode();
						if (!runner.equals(treemap.currentRoot)) {
							N last;
							do {
								last = runner;
								runner = treemap.model.getParent(runner);
							} while (!treemap.currentRoot.equals(runner));
							if (!treemap.currentRoot.equals(last)) {
								treemap.currentRoot = last;
								treemap.recalculate();
							}
						}
					}
					break;
				case 3:
					final N parent = treemap.model.getParent(treemap.currentRoot);
					if (parent != null) {
						treemap.currentRoot = parent;
						treemap.recalculate();
					}
					break;
				default:
					break;
			}
		}
	}

}
