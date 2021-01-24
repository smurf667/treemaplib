package de.engehausen.treemap.svg;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import de.engehausen.treemap.ICancelable;
import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.IRectangleRenderer;
import de.engehausen.treemap.ITreeMapLayout;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.IWeightedTreeModel;
import de.engehausen.treemap.impl.FIFO;
import de.engehausen.treemap.impl.SquarifiedLayout;
import de.engehausen.treemap.svg.impl.DefaultColorProvider;
import de.engehausen.treemap.svg.impl.DefaultRectangleRenderer;

/**
 * Tree map that can render as SVG to an output stream.
 *
 * @param <N> the type of node the backing weighted tree model uses.
 */
public class TreeMap<N> {

	private static final String NOT_NULL = "%s must not be null";

	protected ITreeModel<N> model;
	protected ITreeMapLayout<N> layout;
	protected IRectangleRenderer<N, OutputStream, String> renderer;
	protected ILabelProvider<N> labelProvider;
	protected IColorProvider<N, String> colorProvider;

	/**
	 * Writes the given string to the output stream using UTF-8.
	 * I/O exceptions will end in {@code IllegalStateException}.
	 * @param out the output stream, must not be {@code null}
	 * @param str the string to write, must not be {@code null}
	 */
	public static void write(final OutputStream out, final String str) {
		try {
			out.write(str.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Creates the tree map with a squarified layout.
	 * @param model the model to use, must not be{@code null}.
	 */
	public TreeMap(final ITreeModel<N> model) {
		this(model, new SquarifiedLayout<N>(2));
	}

	/**
	 * Creates the tree map with the given layout.
	 * @param model the model to use, must not be {@code null}.
	 * @param layout the layout to use, must not be {@code null}.
	 */
	public TreeMap(final ITreeModel<N> model, final ITreeMapLayout<N> layout) {
		notNull(model, "model");
		this.model = model;
		notNull(layout, "layout");
		this.layout = layout;
		renderer = DefaultRectangleRenderer.defaultInstance();
		labelProvider = (nodes, node) -> null;
		colorProvider = (nodes, node) -> "#00a000";
	}

	/**
	 * Sets the model to use.
	 * @param model the model to use, must not be {@code null}.
	 */
	public void setModel(final ITreeModel<N> model) {
		notNull(model, "model");
		this.model = model;
	}

	/**
	 * Sets the layout to use.
	 * @param layout the layout to use, must not be {@code null}.
	 */
	public void setLayout(final ITreeMapLayout<N> layout) {
		notNull(layout, "layout");
		this.layout = layout;
	}

	/**
	 * Sets the rectangle renderer the tree map will use. If no renderer
	 * is set, a default will be used.
	 * @param renderer the rectangle renderer, must not be {@code null}.
	 */
	public void setRectangleRenderer(final IRectangleRenderer<N, OutputStream, String> renderer) {
		notNull(renderer, "renderer");
		this.renderer = renderer;
	}

	/**
	 * Sets the label provider to use. If no provider is set,
	 * no labels will be rendered.
	 * @param labelProvider the label provider, must not be {@code null}.
	 */
	public void setLabelProvider(final ILabelProvider<N> labelProvider) {
		notNull(labelProvider, "labelProvider");
		this.labelProvider = labelProvider;
	}

	/**
	 * Sets the color provider to use. If no provider is set,
	 * a default provider will be used.
	 * @param colorProvider the color provider, must not be {@code null}.
	 */
	public void setColorProvider(final IColorProvider<N, String> colorProvider) {
		notNull(colorProvider, "colorProvider");
		this.colorProvider = colorProvider;
	}

	public void render(final OutputStream out, final int width, final int height, final ICancelable control) {
		notNull(out, "out");
		final ITreeModel<IRectangle<N>> rectangles = layout.layout((IWeightedTreeModel<N>) model, model.getRoot(), width, height, control);
		final IRectangle<N> root = rectangles.getRoot();
		if (root != null) {
			if (colorProvider == null) {
				colorProvider = new DefaultColorProvider<>();
			}
			renderPrologue(out, width, height);
			if (renderer instanceof IPrologue) {
				((IPrologue) renderer).prologue(out);
			}
			write(out, "<g id=\"rectangles\" dominant-baseline=\"middle\" text-anchor=\"middle\" fill=\"white\" font-size=\"large\">\n");
			final FIFO<IRectangle<N>> queue = new FIFO<IRectangle<N>>();
			queue.push(rectangles.getRoot());
			renderer.render(out, rectangles, rectangles.getRoot(), colorProvider, labelProvider);
			while (queue.notEmpty()) {
				final IRectangle<N> node = queue.pull();
				if (rectangles.hasChildren(node)) {
					for (Iterator<IRectangle<N>> child = rectangles.getChildren(node); child.hasNext(); ) {
						queue.push(child.next());
					}
				} else {
					renderer.render(out, rectangles, node, colorProvider, labelProvider);
				}
			}
			write(out, "</g>\n");
			if (renderer instanceof IEpilogue) {
				((IEpilogue) renderer).epilogue(out);
			}
			renderEpilogue(out);
		}
	}

	/**
	 * Renders the opening SVG element.
	 * @param out the output stream, must not be {@code null}.
	 * @param width the width of the image
	 * @param height the height of the image
	 */
	public void renderPrologue(final OutputStream out, final int width, final int height) {
		write(out, String.format("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\" viewBox=\"0 0 %d %d\">\n", width, height, width, height));
	}

	/**
	 * Renders the closing SVG element.
	 * @param out the output stream, must not be {@code null}.
	 */
	public void renderEpilogue(final OutputStream out) {
		write(out, "</svg>");
	}

	private static void notNull(final Object arg, final String param) {
		if (arg == null) {
			throw new IllegalArgumentException(String.format(NOT_NULL, param));
		}
	}

}