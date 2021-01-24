package de.engehausen.treemap.svg;

import java.io.OutputStream;

/**
 * Optional interface for a rectangle renderer.
 * If implemented, the renderer can insert SVG elements before the
 * rectangles are rendered.
 */
public interface IEpilogue {

	/**
	 * Outputs SVG elements before the rectangles.
	 * @param out the output stream, never {@code null}.
	 */
	void epilogue(final OutputStream out);

}
