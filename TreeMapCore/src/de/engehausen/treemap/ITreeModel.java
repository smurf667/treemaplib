package de.engehausen.treemap;

import java.util.Iterator;

/**
 * A tree model.
 *
 * @param <N> the type of node returned by model.
 */
public interface ITreeModel<N> {

	/**
	 * Returns the root node of the model.
	 * @return the root node; may be <code>null</code> if the
	 * model is empty.
	 */
	N getRoot();

	/**
	 * Returns the parent node for the given node.
	 *
	 * @param node a node for which to return the parent; must not be <code>null</code>
	 * @return the parent node, or <code>null</code> if <code>node</code> is the root node.
	 */
	N getParent(N node);

	/**
	 * Returns an iterator for the children of the given node.
	 * @param node the node for which to return children.
	 * @return an iterator over the children; may be empty, but never
	 * <code>null</code>.
	 */
	Iterator<N> getChildren(N node);

	/**
	 * Indicates whether the passed in node has children or not.
	 * @param node the node for which to indicate whether there are children or not.
	 * @return <code>false</code> if there are no children; please note that returning
	 * <code>true</code> always is a valid implementation.
	 */
	boolean hasChildren(N node);

}
