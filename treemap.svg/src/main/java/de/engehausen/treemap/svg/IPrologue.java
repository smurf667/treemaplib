package de.engehausen.treemap.svg;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Appends XML elements before rendering the rectangles.
 * Optional interface for a rectangle renderer.
 * If implemented, the renderer can insert SVG elements before the
 * rectangles are rendered.
 */
public interface IPrologue {

	/**
	 * Outputs SVG elements before the rectangles.
	 * @param writer the writer to write to, never {@code null}.
	 * @throws XMLStreamException in case of error
	 */
	void prologue(final XMLStreamWriter writer) throws XMLStreamException;

}
