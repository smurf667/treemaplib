package de.engehausen.treemap.svg;

import java.io.OutputStream;

/**
 * Optional interface for a rectangle renderer.
 * If implemented, the renderer can insert SVG elements after the
 * rectangles are rendered.
 */
public interface IPrologue {

	/**
	 * Outputs SVG elements after the rectangles.
	 * @param out the output stream, never {@code null}.
	 */
	void prologue(final OutputStream out);

}
