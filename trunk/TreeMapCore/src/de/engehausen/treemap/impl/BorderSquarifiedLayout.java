package de.engehausen.treemap.impl;

/**
 * Squarified layout which uses a border for each built rectangle.
 * Each rectangle that is built will be shrunk according to a given
 * shrink size.
 * @param <N> the type of node the tree model works on
 */
public class BorderSquarifiedLayout<N> extends SquarifiedLayout<N> {

	private static final long serialVersionUID = 1L;

	protected final int shrink;

	/**
	 * Creates the layout.
	 * @param nestingDepth defines how deep the layout should go into
	 * the tree model for layout purposes.
	 * @param borderShrink defines how much each rectangle is to be
	 * shrunk.
	 */
	public BorderSquarifiedLayout(final int nestingDepth, final int borderShrink) {
		super(nestingDepth);
		shrink = borderShrink;
	}

	@Override
	protected RectangleImpl<N> createRectangle(final N n, final int x, final int y, final int w, final int h) {
		final int nw = w-2*shrink;
		final int nh = h-2*shrink;
		if (nw > 0 && nh > 0) {
			return new RectangleImpl<N>(n, x+shrink, y+shrink, nw, nh);
		} else {
			return null;
		}
	}

}
