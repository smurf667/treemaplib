package de.engehausen.treemap.swing.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;

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
			final FontRenderContext frc = graphics.getFontRenderContext();
			final float sw = (float) font.getStringBounds(text, frc).getWidth();
			final LineMetrics lm = font.getLineMetrics(text, frc);
			final float sh = lm.getAscent() + lm.getDescent();
            final double scale = Math.min(bounds.getWidth()/sw, bounds.getHeight()/sh);
            final AffineTransform at = AffineTransform.getTranslateInstance(bounds.getX() + scale*(bounds.getWidth() - scale*sw)/2,
            														  		bounds.getY() + bounds.getHeight() - sh);
            at.scale(scale, scale);
            graphics.setFont(font.deriveFont(at));
            graphics.drawString(text, 0, 0);
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
