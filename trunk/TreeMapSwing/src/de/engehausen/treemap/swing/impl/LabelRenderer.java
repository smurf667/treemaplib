package de.engehausen.treemap.swing.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.IRectangleRenderer;
import de.engehausen.treemap.ITreeModel;

/**
 * Renders labels for rectangles.
 * @param <N> the type of node the rectangles work on.
 */
public class LabelRenderer<N> implements IRectangleRenderer<N, Graphics2D, Color> {
	
	protected final Font font;
	protected final boolean showRoot;

	/**
	 * Create the renderer using the given font.
	 * @param aFont the font, must not be <code>null</code>.
	 */
	public LabelRenderer(final Font aFont) {
		this(aFont, false);
	}

	/**
	 * Create the renderer using the given font.
	 * @param aFont the font, must not be <code>null</code>.
	 * @param showsRoot set to <code>true</code> if the root node label
	 * should be rendered, <code>false</code> otherwise.
	 */
	public LabelRenderer(final Font aFont, final boolean showsRoot) {
		font = aFont;
		showRoot = showsRoot;
	}

	@Override
	public void render(final Graphics2D graphics, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider, final ILabelProvider<N> labelProvider) {
		if (labelProvider != null) {
			if (showRoot || !rectangle.equals(model.getRoot()) || !model.hasChildren(rectangle)) {
				render(graphics, labelProvider.getLabel(model, rectangle), Color.WHITE, rectangle);				
			}
		}
	}

	@Override
	public void highlight(final Graphics2D graphics, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider, final ILabelProvider<N> labelProvider) {
		if (labelProvider != null) {
			if (showRoot || !rectangle.equals(model.getRoot()) || !model.hasChildren(rectangle)) {
				render(graphics, labelProvider.getLabel(model, rectangle), Color.RED, rectangle);
			}
		}
	}
	
	/**
	 * Renders a label for the given rectangle if possible. The label is
	 * rotated 90 degrees if it fits "better" into the rectangle that way.
	 * @param graphics the graphics object to work on
	 * @param text the text to render
	 * @param color the color to use
	 * @param bounds the rectangle bounds
	 */
	protected void render(final Graphics2D graphics, final String text, final Color color, final IRectangle<N> bounds) {
		if (text != null) {
			graphics.setColor(color);
			graphics.setFont(font);
			final FontMetrics fontMetrics = graphics.getFontMetrics();
			final Rectangle2D textRect = fontMetrics.getStringBounds(text, graphics);

			final int textw = (int) textRect.getWidth();
			final int texth = (int) textRect.getHeight();
			final double rectAR = aspectRatio(bounds.getWidth(), bounds.getHeight());
			final double textAR = aspectRatio(textw, texth);
			final boolean norotate = Math.abs(1d/textAR - rectAR) >= Math.abs(textAR - rectAR);

			if (norotate) {
				final int w = bounds.getWidth() - textw;
				final int h = bounds.getHeight() - texth;
				if (w > 0 && h > 0) {
					graphics.drawString(text, bounds.getX()+w/2, bounds.getY()+h/2+texth);
				}				
			} else {
				// must fit rotated
				if (bounds.getWidth() - texth > 0 && bounds.getHeight() - textw > 0) {
					// rotate 90 degrees
					final AffineTransform orig = graphics.getTransform();
					try {
						final int w = bounds.getWidth() - textw;
						final int h = bounds.getHeight() - texth;
						graphics.rotate(-Math.PI/2d, bounds.getX()+bounds.getWidth()/2, bounds.getY()+bounds.getHeight()/2);
						graphics.drawString(text, bounds.getX()+w/2, bounds.getY()+h/2+texth);
					} finally {
						graphics.setTransform(orig);
					}
				}
			}
		}
	}

	/**
	 * Compute the aspect ratio.
	 * @param w the width
	 * @param h the height
	 * @return the aspect ratio
	 */
	protected double aspectRatio(final int w, final int h) {
		return w/(double) h;
	}

}
