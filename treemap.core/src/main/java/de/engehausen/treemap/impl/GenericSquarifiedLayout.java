package de.engehausen.treemap.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.engehausen.treemap.ICancelable;
import de.engehausen.treemap.IGenericTreeMapLayout;
import de.engehausen.treemap.IGenericWeightedTreeModel;
import de.engehausen.treemap.IIteratorSize;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.IWeightedTreeModel;
import de.engehausen.treemap.NumberArithmetic;

/**
 * Squarified tree map layout, used by various implementations such as the SWT and the Swing versions.
 * This tree map layout supports weighted tree models using any kind of number-based weight.
 * @param <N> the type of node the layout operates on.
 * @param <T> the weight type.
 * @see SquarifiedLayout
 */
public class GenericSquarifiedLayout<N, T extends Number> implements IGenericTreeMapLayout<N, T>, ICancelable, Serializable {

	private static final long serialVersionUID = 1L;

	protected final int maxDepth;

	/**
	 * Creates the layout engine for the given model.
	 *
	 * @param nestingDepth the maximum nesting depth
	 */
	public GenericSquarifiedLayout(final int nestingDepth) {
		maxDepth = nestingDepth;
	}

	@Override
	public ITreeModel<IRectangle<N>> layout(final IGenericWeightedTreeModel<N, T> model, final N startNode, final int width, final int height) {
		return layout(model, startNode, width, height, this);
	}

	@Override
	public ITreeModel<IRectangle<N>> layout(final IGenericWeightedTreeModel<N, T> model, final N startNode, final int width, final int height, final ICancelable cancelable) {
		final RectangleImpl<N> root = new RectangleImpl<N>(startNode, 0, 0, width, height);
		final RectangleModelImpl<N> result = new RectangleModelImpl<N>(root);
		squarify(result, root, new ComparatorImpl<N, T>(model), 0, cancelable);
		if (cancelable.isCanceled()) {
			return RectangleModelImpl.emptyModel();
		} else {
			return result;
		}
	}

	/**
	 * Squarifies the rectangles' model children.
	 *
	 * @param result the tree result object
	 * @param rectangle the rectangle into which to fit its children
	 * @param comparator a comparator for node weights
	 * @param depth the current traversal depth
	 * @param cancelable cancel monitor
	 */
	protected void squarify(final RectangleModelImpl<N> result, final RectangleImpl<N> rectangle, final ComparatorImpl<N, T> comparator, final int depth, final ICancelable cancelable) {
		if (depth < maxDepth && !cancelable.isCanceled()) {
			final N n = rectangle.getNode();
			final IGenericWeightedTreeModel<N, T> model = comparator.getModel();
			final NumberArithmetic<T> arithmetic = model.getArithmetic();
			if (model.hasChildren(n)) {
				T total = arithmetic.zero();
				// get children and sort by weight
				final Iterator<N> i = model.getChildren(n);
				final List<N> nodes = new ArrayList<N>(i instanceof IIteratorSize<?>?((IIteratorSize<?>) i).size():16);
				while (i.hasNext()) {
					final N c = i.next();
					nodes.add(c);
					total = arithmetic.add(total, model.getWeight(c));
				}
				final int max = nodes.size();
				if (max > 2) {
					Collections.sort(nodes, comparator);
					squarify(result, rectangle, rectangle, comparator, nodes, 0, max, total, depth, cancelable);
				} else {
					if (max == 2) {
						final N one = nodes.get(0);
						final N two = nodes.get(1);
						if (comparator.compare(one, two) > 0) {
							nodes.set(0, two);
							nodes.set(1, one);
						}
					}
					slice(result, rectangle, rectangle, comparator, nodes, 0, max, total, depth, cancelable);
				}
			}
		}
	}

	/**
	 * Fits the given nodes into the given rectangle.
	 * @param result the tree result object
	 * @param parent the parent rectangle holding the currently treated nodes
	 * @param rectangle the rectangle into which to fit the given nodes
	 * @param comparator a comparator for node weights
	 * @param nodes the nodes to fit into the rectangle
	 * @param start start offset in nodes list
	 * @param end end offset in nodes list
	 * @param weight the summed up weight of all nodes from start to end
	 * @param depth the current traversal depth
	 * @param cancelable cancel monitor
	 */
	protected void squarify(final RectangleModelImpl<N> result, final RectangleImpl<N> parent, final RectangleImpl<N> rectangle, final ComparatorImpl<N, T> comparator, final List<N> nodes, final int start, final int end, final T weight, final int depth, final ICancelable cancelable) {
		if (end-start > 2) {
			final IGenericWeightedTreeModel<N, T> model = comparator.getModel();
			final NumberArithmetic<T> arithmetic = model.getArithmetic();
			float aspectRatio = Float.MAX_VALUE, last;
			int i = start;
			T sum = arithmetic.zero();
			final int[] rect = new int[2];
			do {
				final N n = nodes.get(i++);
				final T nodeWeight = model.getWeight(n);
				sum = arithmetic.add(sum, nodeWeight);
				// TODO this must be possible in a more elegant way
				rect[0] = rectangle.w;
				rect[1] = rectangle.h;
				fit(rect, sum, weight);
				fit(rect, nodeWeight, sum);
				last = aspectRatio;
				aspectRatio = aspectRatio(rect[0],rect[1]);
				if (aspectRatio > last) {
					sum = arithmetic.sub(sum, model.getWeight(nodes.get(--i)));
					final double frac = sum.doubleValue()/weight.doubleValue();
					if (frac > 0 && frac < 1) {
						final RectangleImpl<N> r[] = rectangle.split(frac);
						squarify(result, parent, r[0], comparator, nodes, start, i, sum, depth, cancelable);
						squarify(result, parent, r[1], comparator, nodes, i, end, arithmetic.sub(weight, sum), depth, cancelable);
						return;
					} else {
						// need to slice
						break;
					}
				}
			} while (i<end);
		}
		// if we are here, the AR continually improved, so we
		// put all children into the rectangle
		// slice 'em
		slice(result, parent, rectangle, comparator, nodes, start, end, weight, depth, cancelable);
	}

	/**
	 * Slice the nodes into the given rectangle according to their weight.
	 * @param result the tree result object
	 * @param parent the parent rectangle holding the sliced rectangles
	 * @param r the rectangle into which to slice the given nodes
	 * @param comparator a comparator for node weights
	 * @param nodes the nodes to fit into the rectangle
	 * @param start start offset in nodes list
	 * @param max end offset in nodes list
	 * @param w total weight
	 * @param depth the current traversal depth
	 * @param cancelable cancel monitor
	 */
	protected void slice(final RectangleModelImpl<N> result, final RectangleImpl<N> parent, final RectangleImpl<N> r, final ComparatorImpl<N, T> comparator, final List<N> nodes, final int start, final int max, final T w, final int depth, final ICancelable cancelable) {
		if (cancelable.isCanceled()) {
			return;
		}
		final IGenericWeightedTreeModel<N, T> model = comparator.getModel();
		final double dw = w.doubleValue();
		final int last = max-1;
		if (r.w < r.h) {
			final int sx = r.x;
			int sy = r.y;
			final int maxy = r.y+r.h;
			// split horizontally
			for (int i = start; i < max && sy < maxy; i++) {
				final N c = nodes.get(i);
				final T wc = model.getWeight(c);
				// compute height according to weight, but fill anyway for last node
				final int step = (i!=last)?(int) Math.round((r.h*wc.doubleValue())/dw):r.h-(sy-r.y);
				if (step > 0) {
					final RectangleImpl<N> child = createRectangle(c, sx, sy, r.w, step);
					if (child != null) {
						result.addChild(parent, child);
						if (model.hasChildren(c)) {
							squarify(result, child, comparator, depth+1, cancelable);
						}
						sy += step;
					}
				} else {
					// too small to actually display; use up 1 pixel height,
					// dropping the rest children; they will not be displayable
					final int rest = r.h-(sy-r.y);
					if (rest > 0) {
						final RectangleImpl<N> child = createRectangle(c, sx, sy, r.w, 1);
						if (child != null) {
							result.addChild(parent, child);
							sy++;
						}
					}
				}
			}
		} else {
			int sx = r.x;
			final int sy = r.y;
			final int maxx = r.x+r.w;
			// split vertically
			for (int i = start; i < max && sx < maxx; i++) {
				final N c = nodes.get(i);
				final T wc = model.getWeight(c);
				// compute width according to weight, but fill anyway for last node
				final int step = (i!=last)?(int) Math.round((r.w*wc.doubleValue())/dw):r.w-(sx-r.x);
				if (step > 0) {
					final RectangleImpl<N> child = createRectangle(c, sx, sy, step, r.h);
					if (child != null) {
						result.addChild(parent, child);
						if (model.hasChildren(c)) {
							squarify(result, child, comparator, depth+1, cancelable);
						}
						sx += step;
					}
				} else {
					// too small to actually display; use up 1 pixel width,
					// dropping the rest children; they will not be displayable
					final int rest = r.w-(sx-r.x);
					if (rest > 0) {
						final RectangleImpl<N> child = createRectangle(c, sx, sy, 1, r.h);
						if (child != null) {
							result.addChild(parent, child);
							sx++;
						}
					}
				}
			}
		}
	}

	/**
	 * Returns the given rectangle.
	 * @param n the node of the rectangle, must not be <code>null</code>
	 * @param x starting x coordinate
	 * @param y starting y coordinate
	 * @param w width
	 * @param h height
	 * @return the given rectangle, or <code>null</code> if the rectangle
	 * cannot be built due to constraints.
	 */
	protected RectangleImpl<N> createRectangle(final N n, final int x, final int y, final int w, final int h) {
		return new RectangleImpl<N>(n, x, y, w, h);
	}

	private void fit(final int[] rect, final T weight, final T total) {
		final int s = rect[0]<rect[1]?rect[0]:rect[1];
		final int l = rect[0]<rect[1]?rect[1]:rect[0];
		rect[0] = (int) (weight.doubleValue()*l/total.doubleValue());
		rect[1] = s;
		if (rect[0] == 0) {
			// sanitize to avoid bogus aspect
			rect[0] = 1;
		}
	}

	private float aspectRatio(final int a, final int b) {
		if (a > b) {
			return a/(float) b;
		} else {
			return b/(float) a;
		}
	}

	private static class ComparatorImpl<N, T extends Number> implements Comparator<N> {

		private final IGenericWeightedTreeModel<N, T> model;

		public ComparatorImpl(final IGenericWeightedTreeModel<N, T> aModel) {
			model = aModel;
		}

		protected IGenericWeightedTreeModel<N, T> getModel() {
			return model;
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compare(final N o1, final N o2) {
			return ((Comparable<T>) model.getWeight(o2)).compareTo(model.getWeight(o1));
		}

	}

	@Override
	public final boolean isCanceled() {
		return false;
	}

	@Override
	public ITreeModel<IRectangle<N>> layout(final IWeightedTreeModel<N> treeModel, final N startingNode, final int width, final int height) {
		return layout(treeModel, startingNode, width, height, this);
	}

	@Override
	public ITreeModel<IRectangle<N>> layout(final IWeightedTreeModel<N> treeModel, final N startingNode, final int width, final int height, final ICancelable cancelable) {
		if (treeModel instanceof IGenericWeightedTreeModel) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			final IGenericWeightedTreeModel<N, T> model = (IGenericWeightedTreeModel) treeModel;
			return layout(model, startingNode, width, height, cancelable);
		} else {
			throw new IllegalArgumentException();
		}
	}

}
