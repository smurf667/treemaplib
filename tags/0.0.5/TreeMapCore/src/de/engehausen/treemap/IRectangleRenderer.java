package de.engehausen.treemap;

/**
 * Renderer for rectangles.
 *
 * @param <N> the type of node the rectangle supports.
 * @param <G> the graphics support type
 * @param <C> the color type
 */
public interface IRectangleRenderer<N, G, C> {

	/**
	 * Renders the given rectangle.
	 * @param graphics the graphics to render on, must not be <code>null</code>.
	 * @param model the model the rectangle belongs to, must not be <code>null</code>.
	 * @param rectangle the rectangle to render, must not be <code>null</code>.
	 * @param colorProvider provider for the color of the rectangle, must not be <code>null</code>.
	 * @param labelProvider provider for the label of the rectangle, may be <code>null</code>.
	 */
	void render(G graphics, ITreeModel<IRectangle<N>> model, IRectangle<N> rectangle, IColorProvider<N, C> colorProvider, ILabelProvider<N> labelProvider);

	/**
	 * Renders the given rectangle in "highlighted" mode. An implementor may
	 * safely assume that the given rectangle has already been rendered onto
	 * the graphics normally.
	 * @param graphics the graphics to render on, must not be <code>null</code>.
	 * @param model the model the rectangle belongs to, must not be <code>null</code>.
	 * @param rectangle the rectangle to render, must not be <code>null</code>.
	 * @param colorProvider provider for the color of the rectangle, must not be <code>null</code>.
	 * @param labelProvider provider for the label of the rectangle, may be <code>null</code>.
	 */
	void highlight(G graphics, ITreeModel<IRectangle<N>> model, IRectangle<N> rectangle, IColorProvider<N, C> colorProvider, ILabelProvider<N> labelProvider);

}
