package de.engehausen.treemap.swing.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

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
public class ScalingLabelRenderer<N> implements IRectangleRenderer<N, Graphics2D, Color> {

	protected final Color normal, highlight;
	protected final Font font;
	protected final boolean showRoot;

	/**
	 * Create the renderer using the given font and label colors. The
	 * root node is not labeled.
	 * @param aFont the font, must not be <code>null</code>.
	 * @param aNormalColor the color for painting the label when not selected, must not be <code>null</code>.
	 * @param aHighlightColor the color for painting the label when selected, must not be <code>null</code>.
	 */
	public ScalingLabelRenderer(final Font aFont, final Color aNormalColor, final Color aHighlightColor) {
		this(aFont, aNormalColor, aHighlightColor, false);
	}

	/**
	 * Create the renderer using the given font and label colors.
	 * @param aFont the font, must not be <code>null</code>.
	 * @param aNormalColor the color for painting the label when not selected, must not be <code>null</code>.
	 * @param aHighlightColor the color for painting the label when selected, must not be <code>null</code>.
	 * @param showsRoot <code>true</code> if the root node label should be shown, <code>false</code> otherwise.
	 */
	public ScalingLabelRenderer(final Font aFont, final Color aNormalColor, final Color aHighlightColor, final boolean showsRoot) {
		font = aFont;
		normal = aNormalColor;
		highlight = aHighlightColor;
		showRoot = showsRoot;
	}

	@Override
	public void render(final Graphics2D graphics,
			final ITreeModel<IRectangle<N>> model,
			final IRectangle<N> rectangle,
			final IColorProvider<N, Color> colorProvider,
			final ILabelProvider<N> labelProvider) {
		if (labelProvider != null) {
			if (showRoot || !rectangle.equals(model.getRoot()) || !model.hasChildren(rectangle)) {
				render(graphics, labelProvider.getLabel(model, rectangle), normal, rectangle);
			}
		}
	}

	@Override
	public void highlight(final Graphics2D graphics,
			final ITreeModel<IRectangle<N>> model,
			final IRectangle<N> rectangle,
			final IColorProvider<N, Color> colorProvider,
			final ILabelProvider<N> labelProvider) {
		if (labelProvider != null) {
			if (showRoot || !rectangle.equals(model.getRoot()) || !model.hasChildren(rectangle)) {
				render(graphics, labelProvider.getLabel(model, rectangle), highlight, rectangle);
			}
		}
	}

	/**
	 * Renders a label for the given rectangle. The label is fitted
	 * into the rectangle if possible.
	 * @param graphics the graphics object to work on
	 * @param text the text to render
	 * @param color the color to use
	 * @param bounds the rectangle bounds
	 */
	protected void render(final Graphics2D graphics, final String text, final Color color, final IRectangle<N> bounds) {
		if (text != null) {
			graphics.setColor(color);
			graphics.setFont(font);
			setupGraphics(graphics);
			final FontMetrics fontMetrics = graphics.getFontMetrics();
			final Rectangle2D textRect = fontMetrics.getStringBounds(text, graphics);
			final double w = textRect.getWidth()*1.2d; // make some room left and right
			final double h = (double) textRect.getHeight();
            final double scale = Math.min(bounds.getWidth()/w, bounds.getHeight()/h);
            if (scale > 0.4d) {
                final AffineTransform at = AffineTransform.getTranslateInstance(bounds.getX()+(bounds.getWidth()-w/1.2f*scale)/2, bounds.getY()+(h+(bounds.getHeight()-h)/2));
                at.scale(scale, scale);
                graphics.setFont(font.deriveFont(at));
    			graphics.drawString(text, 0, 0);
            }
		}
	}

	/**
	 * Sets up the graphics object for rendering the text.
	 * By default turns on the anti aliasing; if not wanted override
	 * this method.
	 * @param graphics the graphics to set up.
	 */
	protected void setupGraphics(final Graphics2D graphics) {
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

}
