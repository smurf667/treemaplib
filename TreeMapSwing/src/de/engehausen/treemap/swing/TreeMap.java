package de.engehausen.treemap.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

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
import de.engehausen.treemap.swing.impl.DefaultColorProvider;
import de.engehausen.treemap.swing.impl.DefaultRectangleRenderer;

/**
 * Tree map UI widget. It displays information represented in a {@link IWeightedTreeModel}
 * as a tree map and supports navigation inside of the model.
 * 
 * @param <N> the type of node the backing weighted tree model uses.
 */
public class TreeMap<N> extends JPanel implements ComponentListener, MouseMotionListener, MouseListener {
	
	private static final long serialVersionUID = 1L;
	
	protected IWeightedTreeModel<N> model;
	protected ITreeMapLayout<N> layout;
	protected ITreeModel<IRectangle<N>> rectangles;
	protected IRectangle<N> selected;
	protected N currentRoot;
	protected BufferedImage image;
	protected BuildControl buildControl;
	protected IRectangleRenderer<N, Graphics2D, Color> renderer = DefaultRectangleRenderer.defaultInstance();
	protected ILabelProvider<N> labelProvider;
	protected IColorProvider<N, Color> colorProvider;
	protected List<ISelectionChangeListener<N>> listeners;
	protected final GraphicsConfiguration gc;

	/**
	 * Create the tree map (supporting) navigation).
	 */
	public TreeMap() {
		this(true);
	}

	/**
	 * Creates the tree map.
	 * 
	 * @param supportNavigation <code>true</code> if navigation through
	 * left/right mouse clicks is supported, <code>false</code> otherwise.
	 */
	public TreeMap(final boolean supportNavigation) {
		super();
		addComponentListener(this);
		addMouseMotionListener(this);
		if (supportNavigation) {
			addMouseListener(this);			
		}
		final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsDevice gs = ge.getDefaultScreenDevice();
		gc = gs.getDefaultConfiguration();
	}
	
	/**
	 * Sets the rectangle renderer the tree map will use. If no renderer
	 * is set, a default will be used.
	 * @param aRenderer the rectangle renderer, must not be <code>null</code>.
	 */
	public void setRectangleRenderer(final IRectangleRenderer<N, Graphics2D, Color> aRenderer) {
		renderer = aRenderer;
	}

	/**
	 * Returns the rectangle renderer used by the tree map.
	 * @return the rectangle renderer used by the tree map.
	 */
	public IRectangleRenderer<N, Graphics2D, Color> getRectangleRenderer() {
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

	/**
	 * Force a refresh of the paint buffer; this may be needed
	 * if the colors provided by the {@link IColorProvider} have
	 * changed. This does <i>not</i> recompute the rectangles.
	 */
	public void refresh() {
		image = rebuildImage(getWidth(), getHeight(), rectangles);
		repaint();
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
		image = null;
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
	
	@Override
	public void paintComponent(final Graphics gr) {
		final Graphics2D g = (Graphics2D) gr;
		if (image != null) {
			final int w = getWidth();
			final int h = getHeight();
			// tree map image available; render it, regardless
			// of potential sizing issues
			g.drawImage(image, 0, 0, w, h, null);
			if (selected != null) {
				final int imgw = image.getWidth();
				final int imgh = image.getHeight();
				if (imgw != w || imgh != h) {
					final AffineTransform transform = new AffineTransform();
					transform.scale(w/(double) imgw, h/(double) imgh);
					g.setTransform(transform);
				}
				renderer.highlight(g, rectangles, selected, colorProvider, labelProvider);
			}
		} else {
			drawBusy(g);
		}
	}
	
	/**
	 * Called when the tree map is busy during rendering.
	 * @param gr the graphics object used for rendering.
	 */
	protected void drawBusy(final Graphics2D gr) {
		// work in progress...
		gr.setColor(getBackground());
		gr.fillRect(0, 0, getWidth(), getHeight());		
	}
	
	/**
	 * Renders the rectangles of the tree map.
	 * @param g the graphics to draw on
	 * @param rects the rectangles to render
	 */
	protected void render(final Graphics2D g, final ITreeModel<IRectangle<N>> rects) {
		final IRectangle<N> root = rects.getRoot();
		if (root != null) {
			if (colorProvider == null) {
				colorProvider = new DefaultColorProvider<N>();
			}
			final FIFO<IRectangle<N>> queue = new FIFO<IRectangle<N>>();
			queue.push(rects.getRoot());
			while (queue.notEmpty()) {
				final IRectangle<N> node = queue.pull();
				render(g, rects, node);
				if (rects.hasChildren(node)) {
					for (Iterator<IRectangle<N>> i = rects.getChildren(node); i.hasNext(); ) {
						queue.push(i.next());
					}
				}
			}
		}
	}
	
	protected void render(final Graphics2D g, final ITreeModel<IRectangle<N>> rects, final IRectangle<N> rect) {
		renderer.render(g, rects, rect, colorProvider, labelProvider);
	}

	@Override
	public void componentResized(final ComponentEvent componentevent) {
		recalculate();
	}

	@Override
	public void mouseReleased(final MouseEvent mouseevent) {
		if (model != null && currentRoot != null) {
			switch (mouseevent.getButton()) {
				case MouseEvent.BUTTON1:
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
				case MouseEvent.BUTTON3:
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

	@Override
	public void mouseMoved(final MouseEvent event) {
		final boolean notBuilding;
		synchronized (this) {
			notBuilding = buildControl == null;
		}
		if (notBuilding) {
			final int x = event.getX();
			final int y = event.getY();
			if (selected == null || !selected.contains(x, y)) {
				selected = findRectangle(x, y);
				repaint();
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

	@Override
	public void componentHidden(final ComponentEvent componentevent) {
		// don't care
	}

	@Override
	public void componentMoved(final ComponentEvent componentevent) {
		// don't care
	}

	@Override
	public void componentShown(final ComponentEvent componentevent) {
		// don't care
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
	 * Recalculates the rectangle tree model and internal image
	 * in a separated thread.
	 */
	protected synchronized void recalculate() {
		if (model != null) {
			if (buildControl != null) {
				// stop the ongoing layout computation
				buildControl.cancel();
				buildControl = null;
			}
			final BuildControl ctrl = new BuildControl();
			final SwingWorker<ITreeModel<IRectangle<N>>, Object> worker = new Worker<N>(this, ctrl);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			worker.execute();
			buildControl = ctrl;
		}
	}
	
	/**
	 * Rebuilds the image buffer used for rendering the tree map quickly.
	 * @param width the new width
	 * @param height the new height
	 * @param rects the rectangle model to use for rendering into the buffer
	 * @return the render result.
	 */
	protected BufferedImage rebuildImage(final int width, final int height, final ITreeModel<IRectangle<N>> rects) {
		if (width*height > 0) {
			final BufferedImage result = gc.createCompatibleImage(width, height);
			final Graphics2D g = result.createGraphics();
			try {
				render(g, rects);
			} finally {
				g.dispose();
			}
			return result;			
		} else {
			return null;
		}
	}

	/**
	 * Threaded worker to recompute the layout.
	 * @param <N> the type of node the models use
	 */
	private static class Worker<N> extends SwingWorker<ITreeModel<IRectangle<N>>, Object> {
		
		private final BuildControl buildControl;
		private final TreeMap<N> treeMap;
		private final int width, height;
		private BufferedImage image;		
		
		public Worker(final TreeMap<N> aMap, final BuildControl aControl) {
			super();
			treeMap = aMap;
			buildControl = aControl;
			width = aMap.getWidth();
			height = aMap.getHeight();
		}

		@Override
		protected ITreeModel<IRectangle<N>> doInBackground() throws Exception {
			final ITreeModel<IRectangle<N>> result = treeMap.layout.layout(treeMap.model, treeMap.currentRoot, width, height, buildControl);
			if (!buildControl.isCanceled()) {
				image = treeMap.rebuildImage(width, height, result);
			}
			return result;
		}

		@Override
		protected void done() {
			try {
				final ITreeModel<IRectangle<N>> newRects = get();
				if (!buildControl.isCanceled()) {
					synchronized (treeMap) {
						treeMap.rectangles = newRects;
						final IRectangle<N> root = newRects.getRoot();
						if (root != null) {
							treeMap.currentRoot = root.getNode();
						}
						treeMap.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						treeMap.image = image;
						treeMap.selected = null;
						treeMap.buildControl = null;						
					}
					treeMap.repaint();
				}
			} catch (InterruptedException e) {
				// TODO how to handle
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO how to handle
				e.printStackTrace();
			}
		}

	}

}
