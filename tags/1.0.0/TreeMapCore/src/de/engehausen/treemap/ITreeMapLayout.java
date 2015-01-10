package de.engehausen.treemap;

/**
 * Tree map layout interface. The methods allow to create a layout
 * of rectangles from a given weighted tree model.
 *
 * @param <N> the type of node the layout supports.
 */
public interface ITreeMapLayout<N> {

	/**
	 * Creates a layout of rectangles, starting at the current node
	 * for the given width and height.
	 * @param treeModel the tree model to lay out
	 * @param startingNode the starting node in the tree model the
	 * layout operates on; must not be <code>null</code>.
	 * @param width the width to use for the layout.
	 * @param height the height to use for the layout.
	 * @return a tree model holding the rectangles representing the
	 * used nodes in hierarchical order, never <code>null</code>.
	 */
	ITreeModel<IRectangle<N>> layout(IWeightedTreeModel<N> treeModel, N startingNode, int width, int height);

	/**
	 * Creates a layout of rectangles, starting at the current node
	 * for the given width and height.
	 * @param treeModel the tree model to lay out
	 * @param startingNode the starting node in the tree model the
	 * layout operates on; must not be <code>null</code>.
	 * @param width the width to use for the layout.
	 * @param height the height to use for the layout.
	 * @param cancelable an indicator that can cancel the layout operation
	 * if {@link ICancelable#isCanceled()} returns <code>true</code>. The
	 * argument must not be <code>null</code>. If the operation is canceled,
	 * the result may be incomplete.
	 * @return a tree model holding the rectangles representing the
	 * used nodes in hierarchical order, never <code>null</code>.
	 */
	ITreeModel<IRectangle<N>> layout(IWeightedTreeModel<N> treeModel, N startingNode, int width, int height, ICancelable cancelable);

}
