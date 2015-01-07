package de.engehausen.treemap;

/**
 * Listener that is to be notified when the selection changes
 * in the tree map.
 *
 * @param <N> the type of node the listener acts for
 */
public interface ISelectionChangeListener<N> {

	/**
	 * Indicates a selection change in the tree map.
	 *
	 * @param model the model the rectangle comes from; never <code>null</code>.
	 * @param rectangle the rectangle the tree map now shows as selected.
	 * @param label the label of the rectangle; may be <code>null</code>.
	 */
	void selectionChanged(ITreeModel<IRectangle<N>> model, IRectangle<N> rectangle, String label);

}
