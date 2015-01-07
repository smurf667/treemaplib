package de.engehausen.treemap.impl;

import de.engehausen.treemap.IRectangle;

/**
 * Rectangle implementation. Additionally supports splitting a
 * rectangle in two along the longer of its sides and to
 * "subtract" one rectangle from the other, given some special
 * circumstances the tree map engine provides.
 * @param <N> the type of node backing the tree map.
 */
public class RectangleImpl<N> implements IRectangle<N> {

	protected final int x, y, w, h;
	protected final N node;

	/**
	 * Creates the rectangle.
	 * @param aNode the node this rectangle represents, must not be <code>null</code>.
	 * @param x the x starting position of the rectangle.
	 * @param y the y starting position of the rectangle.
	 * @param width the width of the rectangle.
	 * @param height the height of the rectangle.
	 */
	public RectangleImpl(final N aNode, final int x, final int y, final int width, final int height) {
		node = aNode;
		this.x = x;
		this.y = y;
		w = width;
		h = height;
	}

	protected int area() {
		return w*h;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHeight() {
		return h;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getWidth() {
		return w;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getX() {
		return x;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getY() {
		return y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public N getNode() {
		return node;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(final int px, final int py) {
		final int wi = px-x;
		if (wi >= 0 && wi < w) {
			final int he = py-y;
			return he >= 0 && he < h;
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof RectangleImpl<?>) {
			final RectangleImpl<?> other = (RectangleImpl<?>) obj;
			return w == other.w && h == other.h && node.equals(other.node);
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return node.hashCode() ^ (w+h);
	}

	/**
	 * Splits the rectangle with the given proportion at its longest side.
	 * @param proportion the proportion of the split (>0..<1)
	 * @return an array of size two with the sub-rectangles; the sub-rectangles
	 * will report the same node as the original rectangle.
	 * @throws IllegalArgumentException in case of error
	 */
	@SuppressWarnings("unchecked")
	/* package protected*/ RectangleImpl<N>[] split(final double proportion) {
		if (proportion <= 0 || proportion >=1) {
			throw new IllegalArgumentException("cannot split at "+proportion);
		}
		final RectangleImpl<N>[] result = new RectangleImpl[2];
		if (w < h) {
			final int nh = (int) (h*proportion);
			result[0] = new RectangleImpl<N>(node, x, y, w, nh);
			result[1] = new RectangleImpl<N>(node, x, y+nh, w, h-nh);
		} else {
			final int nw = (int) (w*proportion);
			result[0] = new RectangleImpl<N>(node, x, y, nw, h);
			result[1] = new RectangleImpl<N>(node, x+nw, y, w-nw, h);
		}
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(32);
		sb.append('(').append(node).append(',')
		  .append(x).append(',').append(y).append(',')
		  .append(w).append(',').append(h).append(')');
		return sb.toString();
	}

}
