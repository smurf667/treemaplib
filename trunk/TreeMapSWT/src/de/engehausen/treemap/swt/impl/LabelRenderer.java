package de.engehausen.treemap.swt.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.IRectangleRenderer;
import de.engehausen.treemap.ITreeModel;

/**
 * Renders labels for rectangles.
 * @param <N> the type of node the rectangles work on.
 */
public class LabelRenderer<N> implements IRectangleRenderer<N, PaintEvent, Color> {
	
	protected final String fontName;
	protected final boolean showRoot;

	/**
	 * Create the renderer using the given font.
	 * @param aFontName the name of the font, must not be <code>null</code>.
	 */
	public LabelRenderer(final String aFontName) {
		this(aFontName, false);
	}

	/**
	 * Create the renderer using the given font.
	 * @param aFontName the name of the font, must not be <code>null</code>.
	 * @param showsRoot set to <code>true</code> if the root node label
	 * should be rendered, <code>false</code> otherwise.
	 */
	public LabelRenderer(final String aFontName, final boolean showsRoot) {
		fontName = aFontName;
		showRoot = showsRoot;
	}

	@Override
	public void render(final PaintEvent event, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider, final ILabelProvider<N> labelProvider) {
		if (labelProvider != null) {
			if (showRoot || !rectangle.equals(model.getRoot()) || !model.hasChildren(rectangle)) {
//			if (showRoot || !rectangle.equals(model.getRoot())) {
				render(event, labelProvider.getLabel(model, rectangle), event.display.getSystemColor(SWT.COLOR_WHITE), rectangle);
			}
		}
	}

	@Override
	public void highlight(final PaintEvent event, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider, final ILabelProvider<N> labelProvider) {
		if (labelProvider != null) {
			if (showRoot || !rectangle.equals(model.getRoot()) || !model.hasChildren(rectangle)) {
//			if (showRoot || !rectangle.equals(model.getRoot())) {
				render(event, labelProvider.getLabel(model, rectangle), event.display.getSystemColor(SWT.COLOR_RED), rectangle);
			}
		}
	}
	
	/**
	 * Renders a label for the given rectangle if possible. The label is
	 * rotated 90 degrees if it fits "better" into the rectangle that way.
	 * @param event the paint event used for rendering
	 * @param text the text to render
	 * @param color the color to use
	 * @param bounds the rectangle bounds
	 */
	protected void render(final PaintEvent event, final String text, final Color color, final IRectangle<N> bounds) {
		if (text != null) {
			// quite strange to create a new font object all the time?
			// it did not work when I tried to reuse a long-living font object!
			final Font font = new Font(event.display, fontName, 16, SWT.BOLD);
			event.gc.setFont(font);
			event.gc.setForeground(color);
			try {
				final Point p = event.gc.textExtent(text);
				final int textw = (int) p.x;
				final int texth = (int) p.y;
				final double rectAR = aspectRatio(bounds.getWidth(), bounds.getHeight());
				final double textAR = aspectRatio(textw, texth);
				final boolean norotate = Math.abs(1d/textAR - rectAR) >= Math.abs(textAR - rectAR);

				if (norotate) {
					final int w = bounds.getWidth() - textw;
					final int h = bounds.getHeight() - texth;
					if (w > 0 && h > 0) {
						event.gc.setForeground(color);
						event.gc.drawString(text, bounds.getX()+w/2, bounds.getY()+h/2+texth/2, true);
					}				
				} else {
					// must fit rotated
					if (bounds.getWidth() - texth > 0 && bounds.getHeight() - textw > 0) {
						// rotate 90 degrees
						final Transform transform = new Transform(event.display);
						try {
							final int hw = bounds.getWidth()/2;
							final int hh = bounds.getHeight()/2;
							transform.translate(bounds.getX()+hw, bounds.getY()+hh);
							transform.rotate(-90);
							event.gc.setTransform(transform);
							event.gc.drawString(text, -textw/2, -texth/2, true);
						} finally {
							transform.dispose();
						}
						// it appears to me a bit gruesome to create a new
						// identity object just to get rid of the previous transform?!
						final Transform ident = new Transform(event.display);
						event.gc.setTransform(ident);
						ident.dispose();
					}
				}				
			} finally {
				font.dispose();
			}
		}
	}
	
	protected double aspectRatio(final int w, final int h) {
		return w/(double) h;
	}

}
