package de.engehausen.treemap.swing.impl;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Map;
import java.util.WeakHashMap;

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
public class CushionRectangleRenderer<N> implements IRectangleRenderer<N, Graphics2D, Color> {

	private static Boolean fastRenderingSupported = Boolean.FALSE;
	protected final Map<Color, int[]> colorMappingRGB;
	protected final Map<Color, Color> colorMappingColor;
	protected final int len;

	/**
	 * TODO puh, maybe don't offer the size option
	 * @param colorRangeSize ...
	 */
	public CushionRectangleRenderer(final int colorRangeSize) {
		colorMappingRGB = new WeakHashMap<Color, int[]>(32, 0.9f);
		colorMappingColor = new WeakHashMap<Color, Color>(16, 0.9f);
		len = colorRangeSize;
	}

	@Override
	public void render(final Graphics2D graphics, final ITreeModel<IRectangle<N>> model,
			final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider,
			final ILabelProvider<N> labelProvider) {
		if (!model.hasChildren(rectangle)) {
			paintCushion(graphics, colorProvider.getColor(model, rectangle), rectangle);
		} else if (rectangle.equals(model.getRoot())) {
			// paint the whole background black
			graphics.setColor(Color.BLACK);
			graphics.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());			
		}
	}

	@Override
	public void highlight(final Graphics2D graphics, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider, final ILabelProvider<N> labelProvider) {
		final Composite oldComposite = graphics.getComposite();
		try {
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));			
			final int[] colors = getColorRange(colorProvider.getColor(model, rectangle));
			graphics.setColor(new Color(colors[0]));
			graphics.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
		} finally {
			graphics.setComposite(oldComposite);
		}
		highlightParents(graphics, model, rectangle, colorProvider, labelProvider);
	}
	
	protected void highlightParents(final Graphics2D graphics, final ITreeModel<IRectangle<N>> model, final IRectangle<N> rectangle, final IColorProvider<N, Color> colorProvider, final ILabelProvider<N> labelProvider) {
	}

	protected void paintCushion(final Graphics2D graphics, final Color color, final IRectangle<N> rectangle) {
		final int w = rectangle.getWidth();
		final int h = rectangle.getHeight();
		if (w*h > 1) {
			graphics.drawImage(createCushion(rectangle, getColorRange(color), w, h), rectangle.getX(), rectangle.getY(), null);
		} else {
			// single pixel, so don't go through the normal cushion process
			final int x = rectangle.getX();
			final int y = rectangle.getY();
			if ((x+y)%2==0) {
				graphics.setColor(color);
			} else {
				graphics.setColor(getDarker(color));
			}
			graphics.drawLine(x, y, x, y);
		}
	}

	protected BufferedImage createCushion(final IRectangle<N> r, final int[] range, final int w, final int h) {
		final BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		if (fastRenderingSupported == null) {
			fastRenderingSupported = Boolean.valueOf(result.getRaster().getDataBuffer() instanceof DataBufferInt);
		}
		if (fastRenderingSupported.booleanValue()) {
			final double m = w>h?w:h;
			final int hw = w>>1; // that's the center
			final int hh = h>>1; 
			final int[] pixels = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();
			for (int y = 0; y < h; y++) {
				final double dy = (hh-y)*(hh-y);
				int pos = y*w;
				for (int x = 0; x < w; x++) {
					final int idx = (int) (range.length * Math.sqrt((hw-x)*(hw-x)+dy)/m);
					pixels[pos+x] = range[idx];
				}
			}			
		} else {
			renderSlow(result, w, h, range);
		}
		return result;
	}
	
	protected void renderSlow(final BufferedImage image, final int w, final int h, final int[] range) {
		final double m = w>h?w:h;
		final int hw = w>>1;
		final int hh = h>>1;
		for (int y = 0; y < h; y++) {
			final double dy = (hh-y)*(hh-y);
			for (int x = 0; x < w; x++) {
				final int idx = (int) (range.length * Math.sqrt((hw-x)*(hw-x)+dy)/m);
				image.setRGB(x, y, range[idx]);
			}
		}		
	}
	
	protected int[] getColorRange(final Color c) {
		int[] result = colorMappingRGB.get(c);
		if (result == null) {
			result = createColorRange(c);
			colorMappingRGB.put(c, result);
		}
		return result;
	}
	
	protected Color getDarker(final Color in) {
		Color out = colorMappingColor.get(in);
		if (out == null) {
			out = in.darker();
			colorMappingColor.put(in, out);
		}
		return out;
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
