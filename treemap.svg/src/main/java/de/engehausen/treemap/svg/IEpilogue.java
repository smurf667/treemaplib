package de.engehausen.treemap.svg;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Appends XML elements after rendering the rectangles.
 * Optional interface for a rectangle renderer.
 * If implemented, the renderer can insert SVG elements after the
 * rectangles are rendered.
 */
public interface IEpilogue {

	/**
	 * Outputs SVG elements after the rectangles.
	 * @param writer the writer to write to, never {@code null}.
	 * @throws XMLStreamException in case of error
	 */
	void epilogue(final XMLStreamWriter writer) throws XMLStreamException;

}
