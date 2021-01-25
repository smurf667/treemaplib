package de.engehausen.treemap.swing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * This class registers mouse and mouse motion listeners with the tree map to be able
 * to handle desired mouse events (i.e mouse move for tooltips &amp; highlighting,
 * mouse released and so on).
 *
 * @author Sorinel Cristescu
 * @param <N> the type of node of the model used for the tree map
 */
public class TreeMapMouseController<N> implements MouseListener, MouseMotionListener {

	protected final TreeMap<N> treemap;

	/**
	 * Creates a controller object that will register the mouse and mouse motion
	 * listeners to the treemap.
	 * @param aTreeMap the tree map used by the mouse controller, must not be {@code null} 
	 */
	public TreeMapMouseController(final TreeMap<N> aTreeMap) {
		treemap = aTreeMap;

		treemap.addMouseMotionListener(this);
		treemap.addMouseListener(this);
	}

	@Override
	public void mouseReleased(final MouseEvent mouseevent) {
		if (treemap.model != null && treemap.currentRoot != null) {
			switch (mouseevent.getButton()) {
				case MouseEvent.BUTTON1:
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
				case MouseEvent.BUTTON3:
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

	@Override
	public void mouseMoved(final MouseEvent event) {
		final boolean notBuilding;
		synchronized (this) {
			notBuilding = treemap.buildControl == null;
		}
		if (notBuilding) {
			if (treemap.selectRectangle(event.getX(), event.getY())) {
				treemap.repaint();
			}
		}
	}

	@Override
	public void mouseDragged(final MouseEvent event) {
		// don't care
	}

	@Override
	public void mouseClicked(final MouseEvent mouseevent) {
		// don't care
	}

	@Override
	public void mouseEntered(final MouseEvent mouseevent) {
		// don't care
	}

	@Override
	public void mouseExited(final MouseEvent mouseevent) {
		// don't care
	}

	@Override
	public void mousePressed(final MouseEvent mouseevent) {
		// don't care
	}

}
