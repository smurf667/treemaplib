package de.engehausen.treemap.svg.impl;

import java.io.Serializable;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.ITreeModel;

/**
 * The default color provider uses a set of colors mapped to nodes
 * by their hash code (this may appear a little random, but makes them
 * distinguishable a bit better). It is thread-safe and stateless.
 *
 * @param <N> type of node
 */
public class DefaultColorProvider<N> implements IColorProvider<N, String>, Serializable {

	private static final long serialVersionUID = 1L;

	protected static final String[] COLORS;

	static {
		COLORS = new String[] {
				"#9669ae",
				"#a27aae",
				"#ad8bae",
				"#b89aae",
				"#c4abae",
				"#d0bcae",
				"#ddceaf",
				"#e6dbaf"
		};
	}

	protected final String[] colors;

	/**
	 * Create a color provider with blue pastel colors.
	 */
	public DefaultColorProvider() {
		colors = COLORS;
	}

	/**
	 * Creates the color provider with the given colors.
	 * @param colorArray the colors to use, must not be {@code null}.
	 */
	public DefaultColorProvider(final String[] colorArray) {
		colors = colorArray;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColor(final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle) {
		return colors[Math.abs(rectangle.getNode().hashCode()%colors.length)];
	}

}
