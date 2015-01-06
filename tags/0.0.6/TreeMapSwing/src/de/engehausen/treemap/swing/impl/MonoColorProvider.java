package de.engehausen.treemap.swing.impl;

import java.awt.Color;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.ITreeModel;

/**
 * Assigns all nodes the same color (light gray).
 * 
 * @param <N> the type of node operated on
 */
public class MonoColorProvider<N> implements IColorProvider<N, Color> {

	@Override
	public Color getColor(final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle) {
		return Color.LIGHT_GRAY;
	}

}
