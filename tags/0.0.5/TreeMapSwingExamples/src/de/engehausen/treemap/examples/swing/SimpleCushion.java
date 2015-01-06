package de.engehausen.treemap.examples.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.IRectangleRenderer;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.swing.TreeMap;
import de.engehausen.treemap.swing.impl.CushionRectangleRenderer;
import de.engehausen.treemap.swing.impl.LabelRenderer;
import de.engehausen.treemap.swing.impl.MonoColorProvider;

/**
 * A very simple example of a tree map layout using the {@link CushionRectangleRenderer}.
 */
public class SimpleCushion extends Simple implements ILabelProvider<String> {

    private static final long serialVersionUID = 1L;

	public SimpleCushion(final TreeMap<String> aTreeMap, final String title) {
		super(aTreeMap, title);
	}

	@Override
	protected void init() {
		super.init();
		treeMap.setLabelProvider(this); // SimpleCushion acts as a label provider...
		treeMap.setColorProvider(new MonoColorProvider<String>());

		// render with the cushion renderer, and afterwards overlay
		// with the label renderer
		final IRectangleRenderer<String, Graphics2D, Color> delegate = new LabelRenderer<String>(new Font("SansSerif", Font.BOLD, 16));
		treeMap.setRectangleRenderer(new CushionRectangleRenderer<String>(128) {
			@Override
			public void render(final Graphics2D graphics,
					final ITreeModel<IRectangle<String>> model,
					final IRectangle<String> rectangle,
					final IColorProvider<String, Color> colorProvider,
					final ILabelProvider<String> labelProvider) {
				super.render(graphics, model, rectangle, colorProvider, labelProvider);
				delegate.render(graphics, model, rectangle, colorProvider, labelProvider);
			}
			@Override
			public void highlight(final Graphics2D graphics,
					final ITreeModel<IRectangle<String>> model,
					final IRectangle<String> rectangle,
					final IColorProvider<String, Color> colorProvider,
					final ILabelProvider<String> labelProvider) {
				super.highlight(graphics, model, rectangle, colorProvider, labelProvider);
				delegate.highlight(graphics, model, rectangle, colorProvider, labelProvider);
			}			
		});
	}


	@Override
	public String getLabel(final ITreeModel<IRectangle<String>> model, final IRectangle<String> rectangle) {
		return rectangle.getNode();
	}

	/**
	 * Creates and runs the UI.
	 * @param args optional parameters; currently ignored
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final SimpleCushion main = new SimpleCushion(new TreeMap<String>(), "SimpleCushion.java");
				main.init();
				main.setVisible(true);
			}
		});
	}

}
