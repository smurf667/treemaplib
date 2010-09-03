package de.engehausen.treemap.swing.impl;

import java.awt.Color;
import java.awt.Graphics2D;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.IRectangleRenderer;
import de.engehausen.treemap.ITreeModel;

/**
 * The default rectangle renderer simply renders the given
 * rectangle using the color provided by the color provider.
 * The rectangle is framed white when it is highlighted.
 * The renderer does not paint any labels.
 * 
 * @param <N> the type of node being operated on
 */
public class DefaultRectangleRenderer<N> implements IRectangleRenderer<N, Graphics2D, Color> {

	@SuppressWarnings("unchecked")
	private static final IRectangleRenderer DEFAULT = new DefaultRectangleRenderer();
	@SuppressWarnings("unchecked")
	public static final <R> IRectangleRenderer<R, Graphics2D, Color> defaultInstance() {
		return DEFAULT;
	}

	@Override
	public void render(final Graphics2D graphics, final ITreeModel<IRectangle<N>> model,
			final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider,
			final ILabelProvider<N> labelProvider) {
		graphics.setColor(colorProvider.getColor(model, rectangle));
		graphics.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}

	@Override
	public void highlight(final Graphics2D graphics, final ITreeModel<IRectangle<N>> model,
			final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider,
			final ILabelProvider<N> labelProvider) {
		graphics.setColor(Color.WHITE);
		graphics.drawRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}

}
