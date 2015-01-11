package de.engehausen.treemap.examples.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.math.BigDecimal;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.examples.ScientificModel;
import de.engehausen.treemap.swing.TreeMap;
import de.engehausen.treemap.swing.impl.DefaultRectangleRenderer;
import de.engehausen.treemap.swing.impl.LabelRenderer;

/**
 * Very simple demo of the treemap widget using a model that uses {@link BigDecimal} weights.
 * This is a minimal demo, using a model of Earth's atmospheric composition.
 */
public class Scientific extends JFrame implements ILabelProvider<String> {

	private static final long serialVersionUID = 1L;

	protected final TreeMap<String> treeMap;

	/**
	 * Creates the example using the given tree map and title
	 * @param aTreeMap the tree map
	 * @param title the title
	 */
	public Scientific(final TreeMap<String> aTreeMap, final String title) {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(64, 64, 640, 400);
		treeMap = aTreeMap;
		getContentPane().add(treeMap);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel(final ITreeModel<IRectangle<String>> model, final IRectangle<String> rectangle) {
		final ITreeModel<String> weightedModel = treeMap.getCurrentTreeModel();
		if (weightedModel instanceof ScientificModel) {
			final StringBuilder sb = new StringBuilder(64);
			sb.append(rectangle.getNode())
				.append(" (")
				.append(((ScientificModel) weightedModel).getWeight(rectangle.getNode()))
				.append("%)");
			return sb.toString();
		} else {
			return rectangle.getNode();
		}
	}

	/**
	 * Initializes the treemap widget using a model of Earth's atmospheric
	 * composition and sets a rectangle renderer that shows labels.
	 */
	protected void init() {
		treeMap.setTreeModel(ScientificModel.ATMOSPHERE_COMPOSITION);
		treeMap.setLabelProvider(this);
		// augment a label renderer and the default rectangle renderer into one
		final LabelRenderer<String> labels = new LabelRenderer<String>(treeMap.getFont());
		final DefaultRectangleRenderer<String> rectangles = new DefaultRectangleRenderer<String>() {
			@Override
			public void render(final Graphics2D graphics, final ITreeModel<IRectangle<String>> model, final IRectangle<String> rectangle, final IColorProvider<String, Color> colorProvider, final ILabelProvider<String> labelProvider) {
				super.render(graphics, model, rectangle, colorProvider, labelProvider);
				labels.render(graphics, model, rectangle, colorProvider, labelProvider);
			}
			@Override
			public void highlight(final Graphics2D graphics, final ITreeModel<IRectangle<String>> model, final IRectangle<String> rectangle, final IColorProvider<String, Color> colorProvider, final ILabelProvider<String> labelProvider) {
				super.highlight(graphics, model, rectangle, colorProvider, labelProvider);
				labels.highlight(graphics, model, rectangle, colorProvider, labelProvider);
			}
		};
		treeMap.setRectangleRenderer(rectangles);
	}

	/**
	 * Creates and runs the UI.
	 * @param args optional parameters; currently ignored
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final Scientific main = new Scientific(new TreeMap<String>(), "Scientific.java");
				main.init();
				main.setVisible(true);
			}
		});
	}

}
