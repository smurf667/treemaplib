package de.engehausen.treemap.swing.impl;

import java.awt.Color;
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
public class DefaultColorProvider<N> implements IColorProvider<N, Color>, Serializable {
		
	private static final long serialVersionUID = 1L;
	
	protected static final Color[] COLORS;
	
	static {
		COLORS = new Color[] {
				new Color(0x9669FE),
				new Color(0xA27AFE),
				new Color(0xAD8BFE),
				new Color(0xB89AFE),
				new Color(0xC4ABFE),
				new Color(0xD0BCFE),
				new Color(0xDDCEFF),
				new Color(0xE6DBFF)
		};
	}
	
	protected final Color[] colors;

	/**
	 * Create a color provider with blue pastel colors.
	 */
	public DefaultColorProvider() {
		colors = COLORS;
	}

	/**
	 * Creates the color provider with the given colors.
	 * @param colorArray the colors to use, must not be <code>null</code>.
	 */
	public DefaultColorProvider(final Color[] colorArray) {
		colors = colorArray;
	}

	@Override
	public Color getColor(final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle) {
		return colors[Math.abs(rectangle.getNode().hashCode()%colors.length)];
	}
	
}
