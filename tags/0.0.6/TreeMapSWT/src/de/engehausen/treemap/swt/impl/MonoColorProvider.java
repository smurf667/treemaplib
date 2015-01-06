package de.engehausen.treemap.swt.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

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
		return Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
	}

}
