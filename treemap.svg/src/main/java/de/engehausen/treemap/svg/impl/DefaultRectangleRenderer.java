package de.engehausen.treemap.svg.impl;

import java.io.OutputStream;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.IRectangleRenderer;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.svg.TreeMap;

/**
 * The default rectangle renderer simply renders the given
 * rectangle using the color provided by the color provider.
 * If a label is provided, the label is rendered.
 * The renderer is thread-safe.
 *
 * @param <N> the type of node being operated on
 */
public class DefaultRectangleRenderer<N> implements IRectangleRenderer<N, OutputStream, String> {

	private static final IRectangleRenderer<Object, OutputStream, String> DEFAULT = new DefaultRectangleRenderer<>();

	/**
	 * Returns the default instance of the renderer.
	 * @return the default instance of the renderer, never {@code null}.
	 * @param <R> the type of node being operated on
	 */
	@SuppressWarnings("unchecked")
	public static final <R> IRectangleRenderer<R, OutputStream, String> defaultInstance() {
		return (IRectangleRenderer<R, OutputStream, String>) DEFAULT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(final OutputStream out, final ITreeModel<IRectangle<N>> rectangles, final IRectangle<N> node, final IColorProvider<N, String> colorProvider, final ILabelProvider<N> labelProvider) {
		rect(out, rectangles, node, colorProvider.getColor(rectangles, node));
		final String label = labelProvider.getLabel(rectangles, node);
		if (label != null) {
			label(out, rectangles, node, label);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void highlight(final OutputStream out, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, String> colorProvider, final ILabelProvider<N> labelProvider) {
		render(out, model, rectangle, colorProvider, (nodes, node) -> "#a0a0a0");
	}

	/**
	 * Renders the rectangle.
	 * @param out the output stream to write to, must not be {@code null}.
	 * @param model the rectangle model, must not be {@code null}.
	 * @param node the node to render, must not be {@code null}.
	 * @param color the color string to use, must not be {@code null}.
	 */
	protected void rect(final OutputStream out, final ITreeModel<IRectangle<N>> model, final IRectangle<N> node, final String color) {
		TreeMap.write(out, String.format("\t<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" fill=\"%s\"/>\n", node.getX(), node.getY(), node.getWidth(), node.getHeight(), color));
	}

	/**
	 * Renders the label for a rectangle.
	 * @param out the output stream to write to, must not be {@code null}.
	 * @param model the rectangle model, must not be {@code null}.
	 * @param node the node to render, must not be {@code null}.
	 * @param label the label to render, must not be {@code null}.
	 */
	protected void label(final OutputStream out, final ITreeModel<IRectangle<N>> model, final IRectangle<N> node, final String label) {
		final int cx = node.getX() + node.getWidth() / 2;
		final int cy = node.getY() + node.getHeight() / 2;
		final String rotate = node.getWidth() / node.getHeight() < 1 ? " rotate(270)" : "";
		TreeMap.write(out, String.format("\t<text transform=\"translate(%d,%d)%s\">%s</text>\n", cx, cy, rotate, label));
	}

}
