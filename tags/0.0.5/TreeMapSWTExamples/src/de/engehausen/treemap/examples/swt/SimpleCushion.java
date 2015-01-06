package de.engehausen.treemap.examples.swt;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.IRectangleRenderer;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.swt.impl.CushionRectangleRenderer;
import de.engehausen.treemap.swt.impl.LabelRenderer;
import de.engehausen.treemap.swt.impl.MonoColorProvider;

/**
 * A very simple example of a tree map layout using the {@link CushionRectangleRenderer}.
 */
public class SimpleCushion extends Simple implements ILabelProvider<String> {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates the sample for the given display using the given title.
	 * @param display the display to run on
	 * @param title the title to show
	 */
	public SimpleCushion(final Display display, final String title) {
		super(display, title);
	}

	@Override
	protected void init() {
		super.init();
		treeMap.setLabelProvider(this); // SimpleCushion acts as a label provider...
		treeMap.setColorProvider(new MonoColorProvider<String>());

		// render with the cushion renderer, and afterwards overlay
		// with the label renderer
		final IRectangleRenderer<String, PaintEvent, Color> delegate = new LabelRenderer<String>("Arial");
		treeMap.setRectangleRenderer(new CushionRectangleRenderer<String>(128) {
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


	@Override
	public String getLabel(final ITreeModel<IRectangle<String>> model, final IRectangle<String> rectangle) {
		return rectangle.getNode();
	}

	/**
	 * Creates and runs the UI.
	 * @param args optional parameters; currently ignored
	 */
	public static void main(final String[] args) {
        final Display display = new Display();
        try {
            new SimpleCushion(display, "SimpleCushion.java").run();
        } finally {
            display.dispose();        	
        }
	}

}
