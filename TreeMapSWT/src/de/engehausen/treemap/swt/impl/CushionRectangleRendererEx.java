package de.engehausen.treemap.swt.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.ITreeModel;

/**
 * Rectangle renderer that attempts to produce a "cushion effect" for
 * the rectangles. It does not paint any labels. The highlight method
 * highlights the selected node and frames it in red. The topmost parent
 * under the root node is framed in yellow.
 *
 * @param <N> the type of node the renderer supports
 */
public class CushionRectangleRendererEx<N> extends CushionRectangleRenderer<N> {

	/**
	 * @param colorRangeSize the number of colors to use for the cushion;
	 * must be > 1
	 */
	public CushionRectangleRendererEx(final int colorRangeSize) {
		super(colorRangeSize);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void highlightParents(final PaintEvent event, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider, final ILabelProvider<N> labelProvider) {
		event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_RED));
		event.gc.drawRectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth()-1, rectangle.getHeight()-1);
		final IRectangle<N> root = model.getRoot();
		IRectangle<N> runner = rectangle, last;
		do {
			last = runner;
			runner = model.getParent(runner);
		} while (runner != root && runner != null);
		if (last != root) {
			event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_YELLOW));
			event.gc.drawRectangle(last.getX(), last.getY(), last.getWidth()-1, last.getHeight()-1);
		}
	}

}
