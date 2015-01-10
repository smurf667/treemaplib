package de.engehausen.treemap;

/**
 * A tree model which provides a "weight" for each of its node.
 * For use with a tree map, the returned weight of a node <b>must</b>
 * be larger or the same as the sum of all its children.
 *
 * @param <N> the type of node returned by model.
 */
public interface IWeightedTreeModel<N> extends ITreeModel<N> {

	/**
	 * Returns the weight of the node.
	 * @param node the node for which to return its weight; must not be <code>null</code>
	 * @return the weight of the node.
	 */
	long getWeight(N node);

}
