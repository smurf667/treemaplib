package de.engehausen.treemap.svg.impl;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.svg.IPrologue;

/**
 * Rectangle renderer that attempts to produce a "cushion effect" for
 * the rectangles. The renderer is thread-safe.
 *
 * @param <N> the type of node the renderer supports
 */
public class CushionRectangleRenderer<N> extends DefaultRectangleRenderer<N> implements IPrologue {

	public static String GRADIENT_ID = "cushion";
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prologue(final XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement(XMLConstants.ELEMENT_DEFS);
		writer.writeStartElement(XMLConstants.ELEMENT_RADIAL_GRADIENT);
		writer.writeAttribute(XMLConstants.ATTR_ID, GRADIENT_ID);
		writer.writeAttribute(XMLConstants.ATTR_SPREAD_METHOD, XMLConstants.VALUE_REFLECT);
		stop(writer, "0%", "0");
		stop(writer, "100%", "0.5");
		writer.writeEndElement();
		writer.writeEndElement();
	}

	protected void stop(final XMLStreamWriter writer, final String offset, final String opacity) throws XMLStreamException {
		writer.writeStartElement(XMLConstants.ELEMENT_STOP);
		writer.writeAttribute(XMLConstants.ATTR_OFFSET, offset);
		writer.writeAttribute(XMLConstants.ATTR_STOP_OPACITY, opacity);
		writer.writeEndElement();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void rect(final XMLStreamWriter writer, final ITreeModel<IRectangle<N>> model, final IRectangle<N> node, final String color) {
		try {
			super.rect(writer, model, node, color);
			writer.writeStartElement("rect");
			writer.writeAttribute(XMLConstants.ATTR_X, Integer.toString(node.getX()));
			writer.writeAttribute(XMLConstants.ATTR_Y, Integer.toString(node.getY()));
			writer.writeAttribute(XMLConstants.ATTR_WIDTH, Integer.toString(node.getWidth()));
			writer.writeAttribute(XMLConstants.ATTR_HEIGHT, Integer.toString(node.getHeight()));
			writer.writeAttribute(XMLConstants.ATTR_FILL, "url('#cushion')");
			writer.writeEndElement();
		} catch (XMLStreamException e) {
			throw new IllegalStateException(e);
		}
	}

}
