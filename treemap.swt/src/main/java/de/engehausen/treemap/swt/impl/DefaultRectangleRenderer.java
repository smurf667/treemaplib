package de.engehausen.treemap.swt.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

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
public class DefaultRectangleRenderer<N> implements IRectangleRenderer<N, PaintEvent, Color> {

	private static final IRectangleRenderer<Object, PaintEvent, Color> DEFAULT = new DefaultRectangleRenderer<Object>();
	@SuppressWarnings("unchecked")
	public static final <R> IRectangleRenderer<R, PaintEvent, Color> defaultInstance() {
		return (IRectangleRenderer<R, PaintEvent, Color>) DEFAULT;
	}

	@Override
	public void render(final PaintEvent event, final ITreeModel<IRectangle<N>> model,
			final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider,
			final ILabelProvider<N> labelProvider) {
		final Color c = colorProvider.getColor(model, rectangle);
		event.gc.setBackground(c);
		event.gc.fillRectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}

	@Override
	public void highlight(final PaintEvent event, final ITreeModel<IRectangle<N>> model,
			final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider,
			final ILabelProvider<N> labelProvider) {
		event.gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		event.gc.drawRectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}

}
