package de.engehausen.treemap;

/**
 * Provides a label for a rectangle.
 *
 * @param <N> the type of node the interface supports
 */
public interface ILabelProvider<N> {

	/**
	 * Returns a label for the given rectangle.
	 * @param model the model the rectangle belongs to, never <code>null</code>
	 * @param rectangle the rectangle for which to return a color, never <code>null</code>
	 * @return a label, may be <code>null</code> if no label is to be displayed
	 */
	String getLabel(ITreeModel<IRectangle<N>> model, IRectangle<N> rectangle);

}
