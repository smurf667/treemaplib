package de.engehausen.treemap.swt.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.IRectangleRenderer;
import de.engehausen.treemap.ITreeModel;

/**
 * Rectangle renderer that attempts to produce a "cushion effect" for
 * the rectangles. It does not paint any labels. The renderer is not
 * thread safe and cannot be shared between different threads.
 *
 * @param <N> the type of node the renderer supports
 */
public class CushionRectangleRenderer<N> implements IRectangleRenderer<N, PaintEvent, Color> {

	private static PaletteData RGB_PALETTE = new PaletteData(0xff0000, 0xff00, 0xff);
	protected final Map<Color, int[]> colorMappingRGB;
	protected final Map<Color, Color> colorMappingColor;
	protected final int len;

	/**
	 * TODO puh, maybe don't offer the size option
	 * @param colorRangeSize ...
	 */
	public CushionRectangleRenderer(final int colorRangeSize) {
		colorMappingRGB = new HashMap<Color, int[]>(32, 0.9f);
		colorMappingColor = new HashMap<Color, Color>(16, 0.9f);
		len = colorRangeSize;
	}

	public void dispose() {
		for (Color c : colorMappingColor.values()) {
			c.dispose();
		}
		colorMappingColor.clear();
		colorMappingColor.clear();
	}

	@Override
	public void render(final PaintEvent event, final ITreeModel<IRectangle<N>> model,
			final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider,
			final ILabelProvider<N> labelProvider) {
		if (!model.hasChildren(rectangle)) {
			paintCushion(event, colorProvider.getColor(model, rectangle), rectangle);
		} else if (rectangle.equals(model.getRoot())) {
			// paint the whole background black
			event.gc.setBackground(event.display.getSystemColor(SWT.COLOR_BLUE));
			event.gc.fillRectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
		}
	}

	@Override
	public void highlight(final PaintEvent event, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider, final ILabelProvider<N> labelProvider) {
		final int old = event.gc.getAlpha();
		event.gc.setAlpha(127);
		final int rgb = getColorRange(colorProvider.getColor(model, rectangle))[0];
		final Color c = new Color(event.display, (rgb>>16)&0xff, (rgb>>16)&0xff, rgb&0xff);
		event.gc.setBackground(c);
		event.gc.fillRectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
		c.dispose();
		event.gc.setAlpha(old);
		highlightParents(event, model, rectangle, colorProvider, labelProvider);
	}

	protected void highlightParents(final PaintEvent event, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider, final ILabelProvider<N> labelProvider) {
		/* NOP */
	}

	protected void paintCushion(final PaintEvent event, final Color color, final IRectangle<N> rectangle) {
		final int w = rectangle.getWidth();
		final int h = rectangle.getHeight();
		if (w*h > 1) {
			final Image img = createCushion(event.display, rectangle, getColorRange(color), w, h);
			event.gc.drawImage(img, rectangle.getX(), rectangle.getY());
			img.dispose();
		} else {
			// single pixel, so don't go through the normal cushion process
			final int x = rectangle.getX();
			final int y = rectangle.getY();
			if ((x+y)%2==0) {
				event.gc.setForeground(color);
				event.gc.drawPoint(x, y);
			} else {
				final Color c = darker(event.display, color);
				event.gc.setForeground(c);
				event.gc.drawPoint(x, y);
				c.dispose();
			}
		}
	}

	protected Image createCushion(final Device d, final IRectangle<N> r, final int[] range, final int w, final int h) {
		final ImageData data = new ImageData(w, h, 24, RGB_PALETTE);
		final double m = w>h?w:h;
		final int hw = w>>1;
		final int hh = h>>1;
		for (int y = 0; y < h; y++) {
			final double dy = (hh-y)*(hh-y);
			for (int x = 0; x < w; x++) {
				final int idx = (int) (range.length * Math.sqrt((hw-x)*(hw-x)+dy)/m);
				data.setPixel(x, y, range[idx]);
			}
		}
		return new Image(d, data);
	}

	protected int[] getColorRange(final Color c) {
		int[] result = colorMappingRGB.get(c);
		if (result == null) {
			result = createColorRange(c);
			colorMappingRGB.put(c, result);
		}
		return result;
	}

	protected Color darker(final Device d, final Color c) {
		final int r = 2*c.getRed()/3;
		final int g = 2*c.getGreen()/3;
		final int b = 2*c.getBlue()/3;
		final Color result = new Color(d, r, g, b);
		return result;
	}

	protected int[] createColorRange(final Color c) {
		float r = c.getRed()/255f;
		float g = c.getGreen()/255f;
		float b = c.getBlue()/255f;
		float sr = r/len;
		float sg = g/len;
		float sb = b/len;
		final int[] result = new int[len];
		for (int i = 0; i < len; i++) {
			result[i] = toRGB(r, g, b);
			r -= sr;
			g -= sg;
			b -= sb;
		}
		return result;
	}

	protected int toRGB(final float r, final float g, final float b) {
		return (int) (r*255f)<<16 | (int) (g*255f)<<8 | (int) (b*255f);
	}

}
