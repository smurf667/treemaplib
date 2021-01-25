package de.engehausen.treemap.svg.impl;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.IRectangleRenderer;
import de.engehausen.treemap.ITreeModel;

/**
 * The default rectangle renderer simply renders the given
 * rectangle using the color provided by the color provider.
 * If a label is provided, the label is rendered.
 * The renderer is thread-safe.
 *
 * @param <N> the type of node being operated on
 */
public class DefaultRectangleRenderer<N> implements IRectangleRenderer<N, XMLStreamWriter , String> {

	private static final IRectangleRenderer<Object, XMLStreamWriter, String> DEFAULT = new DefaultRectangleRenderer<>();

	/**
	 * Returns the default instance of the renderer.
	 * @return the default instance of the renderer, never {@code null}.
	 * @param <R> the type of node being operated on
	 */
	@SuppressWarnings("unchecked")
	public static final <R> IRectangleRenderer<R, XMLStreamWriter , String> defaultInstance() {
		return (IRectangleRenderer<R, XMLStreamWriter, String>) DEFAULT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(final XMLStreamWriter writer, final ITreeModel<IRectangle<N>> rectangles, final IRectangle<N> node, final IColorProvider<N, String> colorProvider, final ILabelProvider<N> labelProvider) {
		try {
			rect(writer, rectangles, node, colorProvider.getColor(rectangles, node));
			final String label = labelProvider.getLabel(rectangles, node);
			if (label != null) {
				label(writer, rectangles, node, label);
			}
		} catch (XMLStreamException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void highlight(final XMLStreamWriter writer, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, String> colorProvider, final ILabelProvider<N> labelProvider) {
		render(writer, model, rectangle, colorProvider, (nodes, node) -> "#a0a0a0");
	}

	/**
	 * Renders the rectangle.
	 * @param writer the writer to write to to write to, must not be {@code null}.
	 * @param model the rectangle model, must not be {@code null}.
	 * @param node the node to render, must not be {@code null}.
	 * @param color the color string to use, must not be {@code null}.
	 * @throws XMLStreamException in case of error
	 */
	protected void rect(final XMLStreamWriter writer, final ITreeModel<IRectangle<N>> model, final IRectangle<N> node, final String color) throws XMLStreamException {
		writer.writeStartElement(XMLConstants.ELEMENT_RECT);
		writer.writeAttribute(XMLConstants.ATTR_X, Integer.toString(node.getX()));
		writer.writeAttribute(XMLConstants.ATTR_Y, Integer.toString(node.getY()));
		writer.writeAttribute(XMLConstants.ATTR_WIDTH, Integer.toString(node.getWidth()));
		writer.writeAttribute(XMLConstants.ATTR_HEIGHT, Integer.toString(node.getHeight()));
		writer.writeAttribute(XMLConstants.ATTR_FILL, color);
		writer.writeEndElement();
	}

	/**
	 * Renders the label for a rectangle.
	 * @param writer the writer to write to to write to, must not be {@code null}.
	 * @param model the rectangle model, must not be {@code null}.
	 * @param node the node to render, must not be {@code null}.
	 * @param label the label to render, must not be {@code null}.
	 * @throws XMLStreamException in case of error
	 */
	protected void label(final XMLStreamWriter writer, final ITreeModel<IRectangle<N>> model, final IRectangle<N> node, final String label) throws XMLStreamException {
		final int cx = node.getX() + node.getWidth() / 2;
		final int cy = node.getY() + node.getHeight() / 2;
		final String rotate = node.getWidth() / node.getHeight() < 1 ? " rotate(270)" : "";
		writer.writeStartElement(XMLConstants.ELEMENT_TEXT);
		writer.writeAttribute(XMLConstants.ATTR_TRANSFORM, String.format("translate(%d,%d)%s", cx, cy, rotate));
		writer.writeCharacters(label);
		writer.writeEndElement();
	}

}
