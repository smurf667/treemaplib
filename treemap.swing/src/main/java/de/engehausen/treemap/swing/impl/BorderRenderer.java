package de.engehausen.treemap.swing.impl;

import java.awt.Color;
import java.awt.Graphics2D;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.IRectangleRenderer;
import de.engehausen.treemap.ITreeModel;

/**
 * A rectangle renderer which only renders non-leaf rectangles.
 * The leafs are rendered by delegation to a renderer supporting
 * rendering leafs.
 *
 * @param <N> the type of node the renderer supports.
 */
public class BorderRenderer<N> implements IRectangleRenderer<N, Graphics2D, Color> {

	protected final IRectangleRenderer<N, Graphics2D, Color> leafRenderer;

	public BorderRenderer(final IRectangleRenderer<N, Graphics2D, Color> aLeafRenderer) {
		leafRenderer = aLeafRenderer;
	}

	@Override
	public void highlight(final Graphics2D graphics, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider, final ILabelProvider<N> labelProvider) {
		if (model.hasChildren(rectangle)) {
			final Color c = colorProvider.getColor(model, rectangle);
			graphics.setColor(c.brighter());
			graphics.drawRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
		} else {
			leafRenderer.highlight(graphics, model, rectangle, colorProvider, labelProvider);
		}
	}

	@Override
	public void render(final Graphics2D graphics, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider, final ILabelProvider<N> labelProvider) {
		if (model.hasChildren(rectangle)) {
			final Color c = colorProvider.getColor(model, rectangle);
			graphics.setColor(c);
			graphics.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
		} else {
			leafRenderer.render(graphics, model, rectangle, colorProvider, labelProvider);
		}
	}

}
