package de.engehausen.treemap.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.ITreeModel;

/**
 * An implementation of a tree model holding rectangles.
 *
 * @param <N> the type of node the rectangles use.
 */
public class RectangleModelImpl<N> implements ITreeModel<IRectangle<N>> {

	private static final RectangleModelImpl<?> EMPTY = new RectangleModelImpl<Object>() {
		void addChild(final IRectangle<Object> parent, final IRectangle<Object> child) { /* NOP */}
	};

	protected final Map<N, List<IRectangle<N>>> children;
	protected final Map<N, IRectangle<N>> childToParent;

	@SuppressWarnings("unchecked")
	protected static <T> RectangleModelImpl<T> emptyModel() {
		return (RectangleModelImpl<T>) EMPTY;
	}

	/**
	 * Creates an empty rectangle model.
	 */
	public RectangleModelImpl() {
		children = new HashMap<N, List<IRectangle<N>>>(64, 1);
		childToParent = new HashMap<N, IRectangle<N>>(64, 1);
	}

	/**
	 * Creates a rectangle model with one node (the root node).
	 * @param root the root node.
	 */
	public RectangleModelImpl(final IRectangle<N> root) {
		this();
		addChild(null, root);
	}

	/**
	 * Adds the given child node to the given parent node in the model.
	 * @param parent the parent node; if <code>null</code> a call to this
	 * method will use the child node as the root node.
	 * @param child the child node, must not be <code>null</code>.
	 */
	/*package protected*/ void addChild(final IRectangle<N> parent, final IRectangle<N> child) {
		if (parent != null) {
			childToParent.put(child.getNode(), parent);
			final N key = parent.getNode();
			List<IRectangle<N>> list = children.get(key);
			if (list == null) {
				list = new ArrayList<IRectangle<N>>(5);
				children.put(key, list);
			}
			list.add(child);
		} else {
			// root node
			childToParent.put(null, child);
		}
	}

	@Override
	public Iterator<IRectangle<N>> getChildren(final IRectangle<N> node) {
		final List<IRectangle<N>> result = children.get(node.getNode());
		return result!=null?result.iterator():Collections.<IRectangle<N>>emptyList().iterator();
	}

	@Override
	public IRectangle<N> getParent(final IRectangle<N> node) {
		return childToParent.get(node.getNode());
	}

	@Override
	public IRectangle<N> getRoot() {
		return childToParent.get(null);
	}

	@Override
	public boolean hasChildren(final IRectangle<N> node) {
		return children.containsKey(node.getNode());
	}

	/**
	 * Returns all nodes of the model as a list.
	 * @return all nodes of the model as a list, never <code>null</code>.
	 */
	public List<IRectangle<N>> toList() {
		final List<IRectangle<N>> list = new ArrayList<IRectangle<N>>(16);
		final List<IRectangle<N>> stack = new LinkedList<IRectangle<N>>();
		stack.add(getRoot());
		while (!stack.isEmpty()) {
			final IRectangle<N> node = stack.remove(0);
			list.add(node);
			if (hasChildren(node)) {
				for (final Iterator<IRectangle<N>> i = getChildren(node); i.hasNext(); ) {
					stack.add(i.next());
				}
			}
		}
		return list;
	}

	public String toString() {
		return toList().toString();
	}

}
