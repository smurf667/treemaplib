package de.engehausen.treemap.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.engehausen.treemap.IGenericWeightedTreeModel;
import de.engehausen.treemap.IIteratorSize;
import de.engehausen.treemap.NumberArithmetic;

/**
 * Sample weighted tree model implementation for generic weights. In addition to the
 * weighted tree model interface this implementation allows adding
 * nodes to the tree, which is of course needed to build the model.
 * @param <N> the type this model holds
 * @param <T> the weight type.
 */
public class GenericTreeModelEx<N, T extends Number> implements IGenericWeightedTreeModel<N, T> {

	protected final Map<N, List<N>> children;
	protected final Map<N, N> parents;
	protected final Map<N, T> weights;
	protected final NumberArithmetic<T> arithmetic;

	/**
	 * Creates an empty generic tree.
	 * @param numberArithmetic the arithmetic to use, must not be <code>null</code>.
	 */
	public GenericTreeModelEx(final NumberArithmetic<T> numberArithmetic) {
		this(numberArithmetic, new HashMap<N, List<N>>(32, 0.9f), new HashMap<N, N>(32, 0.9f), new HashMap<N, T>(32, 0.9f));
	}

	/**
	 * Creates the tree from the given information.
	 * @param numberArithmetic the arithmetic to use, must not be <code>null</code>.
	 * @param childMap mappings "parent to child list", must not be <code>null</code>.
	 * @param parentMap mappings "child to parent", must not be <code>null</code>.
	 * @param weightMap weights per node, must not be <code>null</code>.
	 */
	public GenericTreeModelEx(final NumberArithmetic<T> numberArithmetic, Map<N, List<N>> childMap, final Map<N, N> parentMap, final Map<N, T> weightMap) {
		arithmetic = numberArithmetic;
		children = childMap;
		parents = parentMap;
		weights = weightMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NumberArithmetic<T> getArithmetic() {
		return arithmetic;
	}

	/**
	 * Adds the given node to the parent and propagates the nodes'
	 * weight upwards to the root.
	 * @param node the node to add, must not be <code>null</code>.
	 * @param weight the weight of the node
	 * @param parent the parent of the node; if the parent is <code>null</code>
	 * the given node will be the root node of the model.
	 */
	public void add(final N node, final T weight, final N parent) {
		add(node, weight, parent, true);
	}

	/**
	 * Adds the given node to the parent and propagates the nodes'
	 * weight upwards to the root.
	 * @param node the node to add, must not be <code>null</code>.
	 * @param weight the weight of the node
	 * @param parent the parent of the node; if the parent is <code>null</code>
	 * the given node will be the root node of the model.
	 * @param propagateWeights <code>true</code> to propagate the weight of
	 * the given node upwards to its parents, <code>false</code> otherwise.
	 */
	public void add(final N node, final T weight, final N parent, final boolean propagateWeights) {
		if (parent != null) {
			parents.put(node, parent);
			List<N> list = children.get(parent);
			if (list == null) {
				list = new ArrayList<N>();
				children.put(parent, list);
			}
			list.add(node);
			if (propagateWeights) {
				N runner = getParent(node);
				while (runner != null) {
					weights.put(runner, arithmetic.add(weights.get(runner), weight));
					runner = getParent(runner);
				}
			}
		} else {
			// this is the root node
			parents.put(null, node);
		}
		weights.put(node, weight);
	}

	@Override
	public T getWeight(final N node) {
		final T result = weights.get(node);
		return (result != null)?result:arithmetic.zero();
	}

	@Override
	public Iterator<N> getChildren(final N node) {
		final List<N> result = children.get(node);
		if (result != null && !result.isEmpty()) {
			return new NodeIterator<N>(result);
		} else {
			return Collections.<N>emptyList().iterator();
		}
	}

	@Override
	public N getParent(final N node) {
		return parents.get(node);
	}

	@Override
	public N getRoot() {
		return parents.get(null);
	}

	@Override
	public boolean hasChildren(final N node) {
		final List<N> result = children.get(node);
		if (result != null) {
			return !result.isEmpty();
		} else {
			return false;
		}
	}

	private static class NodeIterator<N> implements IIteratorSize<N> {

		protected final List<N> nodes;
		protected int pos;

		protected NodeIterator(final List<N> nodeList) {
			nodes = nodeList;
		}

		@Override
		public int size() {
			return nodes.size();
		}

		@Override
		public boolean hasNext() {
			return pos < nodes.size();
		}

		@Override
		public N next() {
			return nodes.get(pos++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
