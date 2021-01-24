package de.engehausen.treemap.swt.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Transform;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.IRectangleRenderer;
import de.engehausen.treemap.ITreeModel;

/**
 * Renders labels for rectangles scaled to fit the rectangles,
 * keeping proportion of the font used.
 * @param <N> the type of node the rectangles work on.
 */
public class ScalingLabelRenderer<N> implements IRectangleRenderer<N, PaintEvent, Color> {

	protected final RGB normal, highlight;
	protected final String fontName;
	protected final boolean showRoot;

	/**
	 * Create the renderer using the given font and label colors. The
	 * root node is not labeled.
	 * @param aFontName the font name, must not be {@code null}.
	 * @param aNormalColor the color for painting the label when not selected, must not be {@code null}.
	 * @param aHighlightColor the color for painting the label when selected, must not be {@code null}.
	 */
	public ScalingLabelRenderer(final String aFontName, final RGB aNormalColor, final RGB aHighlightColor) {
		this(aFontName, aNormalColor, aHighlightColor, false);
	}

	/**
	 * Create the renderer using the given font and label colors.
	 * @param aFontName the font, must not be {@code null}.
	 * @param aNormalColor the color for painting the label when not selected, must not be {@code null}.
	 * @param aHighlightColor the color for painting the label when selected, must not be {@code null}.
	 * @param showsRoot <code>true</code> if the root node label should be shown, <code>false</code> otherwise.
	 */
	public ScalingLabelRenderer(final String aFontName, final RGB aNormalColor, final RGB aHighlightColor, final boolean showsRoot) {
		fontName = aFontName;
		normal = aNormalColor;
		highlight = aHighlightColor;
		showRoot = showsRoot;
	}

	@Override
	public void render(PaintEvent graphics, ITreeModel<IRectangle<N>> model,
			IRectangle<N> rectangle, IColorProvider<N, Color> colorProvider,
			ILabelProvider<N> labelProvider) {
		if (labelProvider != null) {
			if (showRoot || !rectangle.equals(model.getRoot()) || !model.hasChildren(rectangle)) {
				render(graphics, labelProvider.getLabel(model, rectangle), normal, rectangle);
			}
		}
	}

	@Override
	public void highlight(PaintEvent graphics, ITreeModel<IRectangle<N>> model,
			IRectangle<N> rectangle, IColorProvider<N, Color> colorProvider,
			ILabelProvider<N> labelProvider) {
		if (labelProvider != null) {
			if (showRoot || !rectangle.equals(model.getRoot()) || !model.hasChildren(rectangle)) {
				render(graphics, labelProvider.getLabel(model, rectangle), highlight, rectangle);
			}
		}
	}

	/**
	 * Renders a label for the given rectangle. The label is fitted
	 * into the rectangle if possible.
	 * @param event the paint event to work with on
	 * @param text the text to render
	 * @param color the color to use
	 * @param bounds the rectangle bounds
	 */
	protected void render(final PaintEvent event, final String text, final RGB color, final IRectangle<N> bounds) {
		if (text != null) {
			// quite strange to create a new font object all the time?
			// it did not work when I tried to reuse a long-living font object!
			final Font font = new Font(event.display, fontName, 16, SWT.BOLD);
			try {
				event.gc.setFont(font);
				final Color c = new Color(event.display, color);
				try {
					event.gc.setForeground(c);
				} finally {
					c.dispose();
				}

				final Point p = event.gc.textExtent(text);
				p.x = p.x*12/10; // make some space
				final float scale = (float) Math.min(bounds.getWidth()/(double) p.x, bounds.getHeight()/(double) p.y);
				final Transform transform = new Transform(event.display);
				try {
					transform.translate((int) (bounds.getX() + bounds.getWidth()/12d), bounds.getY()+(bounds.getHeight()-scale*p.y)/2);
					transform.scale(scale, scale);
					event.gc.setTransform(transform);
					event.gc.drawString(text, 0, 0, true);
				} finally {
					transform.dispose();
				}
			} finally {
				font.dispose();
			}

		}
	}

}
