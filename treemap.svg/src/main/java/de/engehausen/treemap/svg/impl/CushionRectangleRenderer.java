package de.engehausen.treemap.svg.impl;

import java.io.OutputStream;

import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.svg.IPrologue;
import de.engehausen.treemap.svg.TreeMap;

/**
 * Rectangle renderer that attempts to produce a "cushion effect" for
 * the rectangles. The renderer is thread-safe.
 *
 * @param <N> the type of node the renderer supports
 */
public class CushionRectangleRenderer<N> extends DefaultRectangleRenderer<N> implements IPrologue {

	private static final String GRADIENT_DEF = "<defs>\n" + 
		"	<radialGradient id=\"cushion\" spreadMethod=\"reflect\">\n" + 
		"		<stop offset=\"0%\" stop-color=\"white\" stop-opacity=\"0.5\" />\n" + 
		"		<stop offset=\"100%\" stop-color=\"white\" stop-opacity=\"0\" />\n" + 
		"	</radialGradient>\n" + 
		"</defs>\n";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prologue(final OutputStream out) {
		TreeMap.write(out, GRADIENT_DEF);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void rect(final OutputStream out, final ITreeModel<IRectangle<N>> model, final IRectangle<N> node, final String color) {
		super.rect(out, model, node, color);
		TreeMap.write(out, String.format("\t<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" fill=\"url('#cushion')\"/>\n", node.getX(), node.getY(), node.getWidth(), node.getHeight()));
	}

}
