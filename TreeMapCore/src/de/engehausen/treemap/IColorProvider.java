package de.engehausen.treemap;

/**
 * Provides a color for a rectangle. Implementations may or may not
 * be stateful and may or may not be thread-safe; please consult the
 * documentation of the specific implementation.
 *
 * @param <N> the type of node the interface supports
 * @param <C> the color type, e.g for Swing {@link java.awt.Color}.
 */
public interface IColorProvider<N, C> {

	/**
	 * Returns a color for the given rectangle.
	 * @param model the model the rectangle belongs to, never <code>null</code>
	 * @param rectangle the rectangle for which to return a color, never <code>null</code>
	 * @return a color, never <code>null</code>
	 */
	C getColor(ITreeModel<IRectangle<N>> model, IRectangle<N> rectangle);

}
