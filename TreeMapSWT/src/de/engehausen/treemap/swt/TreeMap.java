package de.engehausen.treemap.swt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.IRectangleRenderer;
import de.engehausen.treemap.ISelectionChangeListener;
import de.engehausen.treemap.ITreeMapLayout;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.IWeightedTreeModel;
import de.engehausen.treemap.impl.BuildControl;
import de.engehausen.treemap.impl.FIFO;
import de.engehausen.treemap.impl.SquarifiedLayout;
import de.engehausen.treemap.swt.impl.DefaultColorProvider;
import de.engehausen.treemap.swt.impl.DefaultRectangleRenderer;

/**
 * Tree map UI widget. It displays information represented in a {@link IWeightedTreeModel}
 * as a tree map and supports navigation inside of the model.
 * 
 * @param <N> the type of node the backing weighted tree model uses.
 */
public class TreeMap<N> extends Canvas implements PaintListener, ControlListener, MouseMoveListener, MouseListener {

	protected IWeightedTreeModel<N> model;
	protected ITreeMapLayout<N> layout;
	protected ITreeModel<IRectangle<N>> rectangles;
	protected IRectangle<N> selected;
	protected N currentRoot;
	protected IRectangleRenderer<N, PaintEvent, Color> renderer = DefaultRectangleRenderer.<N>defaultInstance();
	protected ILabelProvider<N> labelProvider;
	protected IColorProvider<N, Color> colorProvider;
	protected List<ISelectionChangeListener<N>> listeners;
	protected BuildControl buildControl;
	protected Image image;

	/**
	 * Create the tree map (supporting navigation) for the given composite.
	 * @param composite the parent composite, must not be <code>null</code>.
	 */
	public TreeMap(final Composite composite) {
		this(composite, true);
	}

	/**
	 * Creates the tree map.
	 * 
	 * @param composite the parent composite, must not be <code>null</code>.
	 * @param supportNavigation <code>true</code> if navigation through
	 * left/right mouse clicks is supported, <code>false</code> otherwise.
	 */
	public TreeMap(final Composite composite, final boolean supportNavigation) {
		super(composite, SWT.NO_BACKGROUND);
		addPaintListener(this);
		addControlListener(this);
		addMouseMoveListener(this);
		if (supportNavigation) {
			addMouseListener(this);			
		}
	}

	@Override
	public void dispose() {
		setImage(null);
		super.dispose();
	}

	/**
	 * Adds the given listener for selection change events.
	 * 
	 * @param aListener the listener to add, must not be <code>null</code>
	 * and must not already have been added.
	 */
	public void addSelectionChangeListener(final ISelectionChangeListener<N> aListener) {
		if (listeners == null) {
			listeners = new ArrayList<ISelectionChangeListener<N>>(2);
		}
		listeners.add(aListener);
	}

	/**
	 * Removes the given change event listener.
	 * 
	 * @param aListener the listener to remove
	 */
	public void removeSelectionChangeListener(final ISelectionChangeListener<N> aListener) {
		if (listeners != null) {
			listeners.remove(aListener);
		}
	}

	/**
	 * Sets the layout for the tree map. If this method is not called,
	 * a default squarified layout with maximum nesting level two is used
	 * @param aLayout the layout to use, must not be <code>null</code>
	 */
	public void setTreeMapLayout(final ITreeMapLayout<N> aLayout) {
		layout = aLayout;
	}

	/**
	 * Sets the rectangle renderer the tree map will use. If no renderer
	 * is set, a default will be used.
	 * @param aRenderer the rectangle renderer, must not be <code>null</code>.
	 */
	public void setRectangleRenderer(final IRectangleRenderer<N, PaintEvent, Color> aRenderer) {
		renderer = aRenderer;
	}

	/**
	 * Returns the rectangle renderer used by the tree map.
	 * @return the rectangle renderer used by the tree map.
	 */
	public IRectangleRenderer<N, PaintEvent, Color> getRectangleRenderer() {
		return renderer;
	}

	/**
	 * Sets the label provider the tree map will use during rendering.
	 * If no provider is set no labels are displayed.
	 * @param aProvider the label provider; may be <code>null</code>.
	 */
	public void setLabelProvider(final ILabelProvider<N> aProvider) {
		labelProvider = aProvider;
	}

	/**
	 * Returns the currently active label provider of this tree map.
	 * @return the currently active label provider of this tree map.
	 */
	public ILabelProvider<N> getLabelProvider() {
		return labelProvider;
	}

	/**
	 * Sets the color provider the tree map will use during rendering.
	 * If no provider is set a default will be used.
	 * @param aProvider the color provider; may be <code>null</code>.
	 */
	public void setColorProvider(final IColorProvider<N, Color> aProvider) {
		colorProvider = aProvider;
	}

	/**
	 * Returns the color provider the tree map uses.
	 * @return the color provider the tree map uses.
	 */
	public IColorProvider<N, Color> getColorProvider() {
		return colorProvider;
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
		if (model != null && currentRoot != null) {
			switch (mouseevent.button) {
				case 1:
					if (selected != null) {
						N runner = selected.getNode();
						if (!runner.equals(currentRoot)) {
							N last;
							do {
								last = runner;
								runner = model.getParent(runner);
							} while (!currentRoot.equals(runner));
							if (!currentRoot.equals(last)) {
								currentRoot = last;
								recalculate();
							}							
						}
					}
					break;
				case 3:
					final N parent = model.getParent(currentRoot);
					if (parent != null) {						
						currentRoot = parent;
						recalculate();
					}
					break;
				default:
					break;
			}			
		}
	}

	/**
	 * Sets the model to use in this tree map.
	 * @param aModel the model to use; must not be <code>null</code>.
	 */
	public void setTreeModel(final IWeightedTreeModel<N> aModel) {
		if (layout == null) {
			layout = new SquarifiedLayout<N>(2);
		}
		model = aModel;
		currentRoot = aModel.getRoot();
		selected = null;
		rectangles = null;
		recalculate();
	}

	/**
	 * Returns the model currently being used by the tree map.
	 * @return the model currently being used by the tree map, may be <code>null</code>.
	 */
	public IWeightedTreeModel<N> getTreeModel() {
		return model;
	}
	
	/**
	 * Recalculates the rectangle tree model and internal image
	 * in a separate thread.
	 */
	protected synchronized void recalculate() {
		if (model != null) {
			if (buildControl != null) {
				buildControl.cancel();
				buildControl = null;
			}
			setCursor(getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
			final BuildControl ctrl = new BuildControl();
			final Worker<N> w = new Worker<N>(this, ctrl);
			buildControl = ctrl;
			new Thread(w).start();
		}
	}

	@Override
	public void paintControl(final PaintEvent event) {
		if (image != null) {
			final Rectangle bounds = getBounds();
			final Rectangle imgBounds = image.getBounds();
			// tree map image available; render it, regardless
			// of potential sizing issues
			event.gc.drawImage(image, 0, 0, imgBounds.width, imgBounds.height, 0, 0, bounds.width, bounds.height);
			if (selected != null) {
				if (imgBounds.width != bounds.width || imgBounds.height != bounds.height) {
					final Transform transform = new Transform(getDisplay());
					transform.scale(bounds.width/(float) imgBounds.width, bounds.height/(float) imgBounds.height);
					event.gc.setTransform(transform);
					try {
						renderer.highlight(event, rectangles, selected, colorProvider, labelProvider);
					} finally {
						transform.dispose();
					}
				} else {
					renderer.highlight(event, rectangles, selected, colorProvider, labelProvider);
				}
			}
		} else {
			drawBusy(event);
		}
	}
	
	/**
	 * Called when the tree map is busy during rendering.
	 * @param event the paint event used for rendering.
	 */
	protected void drawBusy(final PaintEvent event) {
		// work in progress...
		event.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final Rectangle r = getBounds();
		event.gc.fillRectangle(0, 0, r.width, r.height);
	}

	/**
	 * Rebuilds the image buffer used for rendering the tree map quickly.
	 * @param width the new width
	 * @param height the new height
	 * @param rects the rectangle model to use for rendering into the buffer
	 * @return the render result.
	 */
	protected void rebuildImage(final int width, final int height, final ITreeModel<IRectangle<N>> rects) {
		if (width*height > 0) {
			final Display d = getDisplay();
			final Image result = new Image(d, width, height);
			final GC gc = new GC(result);
			final Event synth = new Event();
			synth.widget = this;
			synth.display = d;
			synth.gc = gc;
			synth.x = 0;
			synth.y = 0;
			synth.width = width;
			synth.height = height;
			final PaintEvent event = new PaintEvent(synth);
			try {
				render(event, rects);
			} finally {
				gc.dispose();
			}
			setImage(result);
		}
	}
	
	protected void setImage(final Image img) {
		if (image != null) {
			image.dispose();
		}
		image = img;
	}

	/**
	 * Renders the rectangles of the tree map.
	 * @param event the graphics to draw on
	 * @param rects the rectangles to render
	 */
	protected void render(final PaintEvent event, final ITreeModel<IRectangle<N>> rects) {
		final IRectangle<N> root = rects.getRoot();
		if (root != null) {
			if (colorProvider == null) {
				colorProvider = new DefaultColorProvider<N>(Display.getCurrent());
			}
			// it appears to me a bit gruesome to create a new
			// identity object just to get rid of the previous transform?!
			final FIFO<IRectangle<N>> queue = new FIFO<IRectangle<N>>();
			queue.push(rects.getRoot());
			final Transform ident = new Transform(event.display);
			try {
				while (queue.notEmpty()) {
					final IRectangle<N> node = queue.pull();
					event.gc.setTransform(ident); // reset transform before rendering
					render(event, rects, node);
					if (rects.hasChildren(node)) {
						for (Iterator<IRectangle<N>> i = rects.getChildren(node); i.hasNext(); ) {
							queue.push(i.next());
						}
					}
				}				
			} finally {
				ident.dispose();
			}
		}
	}
	
	protected void render(final PaintEvent event, final ITreeModel<IRectangle<N>> rects, final IRectangle<N> rect) {
		renderer.render(event, rects, rect, colorProvider, labelProvider);
	}

	
	@Override
	public void controlMoved(ControlEvent controlevent) {
		// ignore
	}

	@Override
	public void controlResized(ControlEvent controlevent) {
		recalculate();
	}

	@Override
	public void mouseMove(final MouseEvent event) {
		final boolean notBuilding;
		synchronized (this) {
			notBuilding = buildControl == null;
		}
		if (notBuilding) {
			if (selected == null || !selected.contains(event.x, event.y)) {
				selected = findRectangle(event.x, event.y);
				redraw();
				if (selected != null && listeners != null) {
					final String label;
					if (labelProvider != null) {
						label = labelProvider.getLabel(rectangles, selected);
					} else {
						label = null;
					}
					for (int i = listeners.size()-1; i >= 0; i--) {
						listeners.get(i).selectionChanged(rectangles, selected, label);
					}
				}
			}			
		}
	}

	/**
	 * Find the smallest rectangle in the model containing the given
	 * coordinates.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return a rectangle of the current rectangle tree, or <code>null</code>
	 * if no rectangle can be found.
	 */
	protected IRectangle<N> findRectangle(final int x, final int y) {
		IRectangle<N> result;
		if (rectangles != null) {
			result = rectangles.getRoot();
			if (result.contains(x, y)) {
				while (rectangles.hasChildren(result)) {
					boolean found = false;
					for (Iterator<IRectangle<N>> i = rectangles.getChildren(result); i.hasNext(); ) {
						final IRectangle<N> candidate = i.next();
						if (candidate.contains(x, y)) {
							result = candidate;
							found = true;
							break;
						}
					}
					if (found == false) {
						break;
					}
				}
			} else {
				result = null;
			}
		} else {
			result = null;
		}
		return result;
	}

	/**
	 * Threaded worker to recompute the layout.
	 * @param <N> the type of node the models use
	 */
	private static class Worker<N> implements Runnable {
		
		private final BuildControl buildControl;
		private final TreeMap<N> treeMap;
		private final int width, height;
		
		public Worker(final TreeMap<N> aMap, final BuildControl aControl) {
			treeMap = aMap;
			buildControl = aControl;
			final Rectangle bounds = aMap.getBounds();
			width = bounds.width;
			height = bounds.height;
		}

		@Override
		public void run() {
			final ITreeModel<IRectangle<N>> result = treeMap.layout.layout(treeMap.model, treeMap.currentRoot, width, height, buildControl);
			if (!buildControl.isCanceled()) {
				synchronized (treeMap) {
					treeMap.buildControl = null;					
					treeMap.rectangles = result;
					final IRectangle<N> rootNode = result.getRoot();
					if (rootNode != null) {
						treeMap.currentRoot = rootNode.getNode();					
					}
				}
				// repaint in UI thread async...
				treeMap.getDisplay().asyncExec(new Runnable() {					
					@Override
					public void run() {
						if (!treeMap.isDisposed()) {
							treeMap.selected = null;
							treeMap.rebuildImage(width, height, result);
							treeMap.redraw();
							treeMap.setCursor(treeMap.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
						}
					}
				});
			}
		}
	}
		
}
