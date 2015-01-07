package de.engehausen.treemap.swt.impl;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;

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
 * @param <N>
 */
public class BorderRenderer<N> implements IRectangleRenderer<N, PaintEvent, Color> {

	protected final IRectangleRenderer<N, PaintEvent, Color> leafRenderer;

	public BorderRenderer(final IRectangleRenderer<N, PaintEvent, Color> aLeafRenderer) {
		leafRenderer = aLeafRenderer;
	}

	@Override
	public void highlight(final PaintEvent event, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider, final ILabelProvider<N> labelProvider) {
		if (model.hasChildren(rectangle)) {
			final Color c = brighter(event.display, colorProvider.getColor(model, rectangle));
			event.gc.setForeground(c);
			event.gc.drawRectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
			c.dispose();
		} else {
			leafRenderer.highlight(event, model, rectangle, colorProvider, labelProvider);
		}
	}

	@Override
	public void render(final PaintEvent event, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider, final ILabelProvider<N> labelProvider) {
		if (model.hasChildren(rectangle)) {
			final Color c = colorProvider.getColor(model, rectangle);
			event.gc.setBackground(c);
			event.gc.fillRectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
		} else {
			leafRenderer.render(event, model, rectangle, colorProvider, labelProvider);
		}
	}

	protected Color brighter(final Device d, final Color c) {
		final int r = Math.min(3*c.getRed()/2, 255);
		final int g = Math.min(3*c.getGreen()/2, 255);
		final int b = Math.min(3*c.getBlue()/2, 255);
		final Color result = new Color(d, r, g, b);
		return result;
	}

}
