package de.engehausen.treemap.svg;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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
import de.engehausen.treemap.svg.impl.XMLConstants;

/**
 * Tree map that can render as SVG to an output stream.
 *
 * @param <N> the type of node the backing weighted tree model uses.
 */
public class TreeMap<N> {

	private static final String NOT_NULL = "%s must not be null";
	private static final String RECTANGLES_ID = "rectangles";
	private static final Map<String, String> DEFAULT_ATTRIBUTES;
	
	static {
		final Map<String, String> defaultMap = new HashMap<>();
		defaultMap.put(XMLConstants.ATTR_ID, RECTANGLES_ID);
		defaultMap.put(XMLConstants.ATTR_DOMINANT_BASELINE, XMLConstants.VALUE_MIDDLE);
		defaultMap.put(XMLConstants.ATTR_FONT_SIZE, XMLConstants.VALUE_MIDDLE);
		defaultMap.put(XMLConstants.ATTR_FILL, XMLConstants.VALUE_WHITE);
		defaultMap.put(XMLConstants.ATTR_FONT_SIZE, XMLConstants.VALUE_LARGE);
		DEFAULT_ATTRIBUTES = Collections.unmodifiableMap(defaultMap);
	}

	protected ITreeModel<N> model;
	protected ITreeMapLayout<N> layout;
	protected IRectangleRenderer<N, XMLStreamWriter, String> renderer;
	protected ILabelProvider<N> labelProvider;
	protected IColorProvider<N, String> colorProvider;
	protected Map<String, String> graphicsAttributes;

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
		this(model, layout, DEFAULT_ATTRIBUTES);
	}

	/**
	 * Creates the tree map with the given layout and attributes for the graphics
	 * section of the rectangle.
	 * @param model the model to use, must not be {@code null}.
	 * @param layout the layout to use, must not be {@code null}.
	 * @param graphicsAttributes attributes of the {@code g} element.
	 */
	public TreeMap(final ITreeModel<N> model, final ITreeMapLayout<N> layout, final Map<String, String> graphicsAttributes) {
		notNull(model, "model");
		this.model = model;
		notNull(layout, "layout");
		this.layout = layout;
		notNull(layout, "graphicsAttributes");
		this.graphicsAttributes = graphicsAttributes;
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
	public void setRectangleRenderer(final IRectangleRenderer<N, XMLStreamWriter, String> renderer) {
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

	/**
	 * Sets the attributes used when emitting the root {@code g} element for the
	 * rectangles.
	 * @param graphicsAttributes the attributes, must not be {@code null}
	 */
	public void setGraphicsAttributes(final Map<String, String> graphicsAttributes) {
		this.graphicsAttributes = graphicsAttributes;
	}

	/**
	 * Renders the tree map as SVG using the given writer and dimension.
	 * @param writer the writer to write to, must not be {@code null}
	 * @param width the image width
	 * @param height the image height
	 * @param control a control to cancel, if required, must not be {@code null}
	 * @throws XMLStreamException in case of error
	 */
	public void render(final XMLStreamWriter writer, final int width, final int height, final ICancelable control) throws XMLStreamException {
		notNull(writer, "writer");
		final ITreeModel<IRectangle<N>> rectangles = layout.layout((IWeightedTreeModel<N>) model, model.getRoot(), width, height, control);
		final IRectangle<N> root = rectangles.getRoot();
		if (root != null) {
			if (colorProvider == null) {
				colorProvider = new DefaultColorProvider<>();
			}
			writer.writeStartDocument(XMLConstants.ATTR_ENCODING, null);
			renderPrologue(writer, width, height);
			if (renderer instanceof IPrologue) {
				((IPrologue) renderer).prologue(writer);
			}
			writer.writeStartElement(XMLConstants.ELEMENT_G);
			graphicsAttributes.forEach((name, value) -> {
				try {
					writer.writeAttribute(name, value);
				} catch (XMLStreamException e) {
					throw new IllegalStateException(e);
				}
			});
			final FIFO<IRectangle<N>> queue = new FIFO<IRectangle<N>>();
			queue.push(rectangles.getRoot());
			renderer.render(writer, rectangles, rectangles.getRoot(), colorProvider, labelProvider);
			while (queue.notEmpty()) {
				final IRectangle<N> node = queue.pull();
				if (rectangles.hasChildren(node)) {
					for (Iterator<IRectangle<N>> child = rectangles.getChildren(node); child.hasNext(); ) {
						queue.push(child.next());
					}
				} else {
					renderer.render(writer, rectangles, node, colorProvider, labelProvider);
				}
			}
			writer.writeEndElement();
			if (renderer instanceof IEpilogue) {
				((IEpilogue) renderer).epilogue(writer);
			}
			renderEpilogue(writer);
			writer.writeEndDocument();
		}
	}

	/**
	 * Renders the opening SVG element.
	 * @param writer the writer to write to, must not be {@code null}.
	 * @param width the width of the image
	 * @param height the height of the image
	 * @throws XMLStreamException in case of error
	 */
	public void renderPrologue(final XMLStreamWriter writer, final int width, final int height) throws XMLStreamException {
		writer.writeStartElement(XMLConstants.ELEMENT_SVG);
		writer.writeNamespace(XMLConstants.ATTR_XMLNS, XMLConstants.VALUE_SVGNS);
		writer.writeAttribute(XMLConstants.ATTR_WIDTH, Integer.toString(width));
		writer.writeAttribute(XMLConstants.ATTR_HEIGHT, Integer.toString(height));
		writer.writeAttribute(XMLConstants.ATTR_VIEWBOX, String.format("0 0 %d %d", width, height));
	}

	/**
	 * Renders the closing SVG element.
	 * @param writer the writer to write to, must not be {@code null}.
	 * @throws XMLStreamException in case of error
	 */
	public void renderEpilogue(final XMLStreamWriter writer) throws XMLStreamException {
		writer.writeEndElement();
	}

	private static void notNull(final Object arg, final String param) {
		if (arg == null) {
			throw new IllegalArgumentException(String.format(NOT_NULL, param));
		}
	}

}