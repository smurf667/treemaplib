package de.engehausen.treemap.swt.impl;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;

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
public class DefaultColorProvider<N> implements IColorProvider<N, Color> {

	protected static final RGB[] DEFAULT;

	static {
		DEFAULT = new RGB[] {
				new RGB(0x96, 0x69, 0xFE),
				new RGB(0xA2, 0x7A, 0xFE),
				new RGB(0xAD, 0x8B, 0xFE),
				new RGB(0xB8, 0x9A, 0xFE),
				new RGB(0xC4, 0xAB, 0xFE),
				new RGB(0xD0, 0xBC, 0xFE),
				new RGB(0xDD, 0xCE, 0xFF),
				new RGB(0xE6, 0xDB, 0xFF)
		};
	}

	protected final Color[] colors;

	/**
	 * Create a color provider with blue pastel colors.
	 * @param device the device the provider uses for creating colors
	 */
	public DefaultColorProvider(final Device device) {
		colors = createColors(device, DEFAULT);
	}

	/**
	 * Creates the color provider with the given RGB colors.
	 * @param device the device the provider uses for creating colors
	 * @param colorArray the colors to use, must not be <code>null</code>.
	 */
	public DefaultColorProvider(final Device device, final RGB[] colorArray) {
		colors = createColors(device, colorArray);
	}

	/**
	 * Disposes the colors held by the provider.
	 */
	public void dispose() {
		for (int i = colors.length-1; i>=0; i--) {
			colors[i].dispose();
		}
	}

	protected Color[] createColors(final Device d, final RGB[] arr) {
		final Color[] result = new Color[arr.length];
		for (int i = arr.length-1; i>=0; i--) {
			result[i] = new Color(d, arr[i]);
		}
		return result;
	}

	@Override
	public Color getColor(final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle) {
		return colors[Math.abs(rectangle.getNode().hashCode()%colors.length)];
	}

}
