package de.engehausen.treemap.examples.swt;

import java.io.Serializable;
import java.math.BigDecimal;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.IRectangleRenderer;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.examples.ScientificModel;
import de.engehausen.treemap.swt.TreeMap;
import de.engehausen.treemap.swt.impl.DefaultRectangleRenderer;
import de.engehausen.treemap.swt.impl.LabelRenderer;

/**
 * Very simple demo of the treemap widget using a model that uses {@link BigDecimal} weights.
 * This is a minimal demo, using a model of Earth's atmospheric composition.
 */
public class Scientific implements Runnable, Serializable, ILabelProvider<String> {

	private static final long serialVersionUID = 1L;

	protected final Display display;
	protected final Shell shell;
	protected final TreeMap<String> treeMap;

	/**
	 * Creates the sample using the given display and title
	 * @param aDisplay the display to run on
	 * @param title the title to show
	 */
	public Scientific(final Display aDisplay, final String title) {
		display = aDisplay;
		shell = new Shell(aDisplay);
		shell.setText(title);
		shell.setLayout(new FillLayout());
		treeMap = new TreeMap<String>(shell);
		shell.setBounds(64, 64, 640, 400);
		init();
	}

	/**
	 * Runs the UI.
	 */
	public void run() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
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
		final IRectangleRenderer<String, PaintEvent, Color> delegate = new LabelRenderer<String>("Arial");
		treeMap.setRectangleRenderer(new DefaultRectangleRenderer<String>() {
			@Override
			public void render(final PaintEvent event,
					final ITreeModel<IRectangle<String>> model,
					final IRectangle<String> rectangle,
					final IColorProvider<String, Color> colorProvider,
					final ILabelProvider<String> labelProvider) {
				super.render(event, model, rectangle, colorProvider, labelProvider);
				delegate.render(event, model, rectangle, colorProvider, labelProvider);
			}
			@Override
			public void highlight(final PaintEvent event,
					final ITreeModel<IRectangle<String>> model,
					final IRectangle<String> rectangle,
					final IColorProvider<String, Color> colorProvider,
					final ILabelProvider<String> labelProvider) {
				super.highlight(event, model, rectangle, colorProvider, labelProvider);
				delegate.highlight(event, model, rectangle, colorProvider, labelProvider);
			}
		});

	}

	/**
	 * Creates and runs the UI.
	 * @param args optional parameters; currently ignored
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		try {
			new Scientific(display, "Scientific.java").run();
		} finally {
			display.dispose();
		}
	}

}
