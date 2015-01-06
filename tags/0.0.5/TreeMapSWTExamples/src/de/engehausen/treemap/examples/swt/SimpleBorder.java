package de.engehausen.treemap.examples.swt;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.impl.BorderSquarifiedLayout;
import de.engehausen.treemap.impl.GenericTreeModel;
import de.engehausen.treemap.swt.TreeMap;
import de.engehausen.treemap.swt.impl.BorderRenderer;
import de.engehausen.treemap.swt.impl.DefaultRectangleRenderer;

/**
 * A very simple example of a tree map layout using the {@link BorderRenderer}.
 */
public class SimpleBorder {

	private static final long serialVersionUID = 1L;

	protected final Display display;
	protected final Shell shell;
	protected final TreeMap<String> treeMap;
	
	public SimpleBorder(final Display aDisplay, final String title) {
		display = aDisplay;
		shell = new Shell(aDisplay);
		shell.setText(title);
		shell.setLayout(new FillLayout());
		treeMap = new TreeMap<String>(shell) {
			// create a tree map, overriding the rectangle find
			// method to avoid the selection of a non-leaf node
			// this is just a question of taste, but allowing non-leaf
			// selection has awkward side effects
			private static final long serialVersionUID = 1L;
			@Override
			protected IRectangle<String> findRectangle(int x, int y) {
				final IRectangle<String> result = super.findRectangle(x, y);
				return rectangles.hasChildren(result) ? null : result;
			}
		};
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

	protected void init() {
		treeMap.setTreeMapLayout(new BorderSquarifiedLayout<String>(Integer.MAX_VALUE, 2));
		treeMap.setRectangleRenderer(new BorderRenderer<String>(new DefaultRectangleRenderer<String>()));
		final GenericTreeModel<String> model = new GenericTreeModel<String>();
		model.add("0", 0, null);
		// 1st level children
		model.add("0-1", 1234, "0");
		model.add("0-2", 3456, "0");
		// 2nd level children
		model.add("0-1-1", 100, "0-1");
		model.add("0-1-2", 100, "0-1");
		model.add("0-2-1", 500, "0-2");
		model.add("0-2-2", 666, "0-2");
		model.add("0-2-3", 50, "0-2");
		// 3rd level children
		model.add("0-2-2-1", 50, "0-2-2");
		treeMap.setTreeModel(model);
	}

	/**
	 * Creates and runs the UI.
	 * @param args optional parameters; currently ignored
	 */
	public static void main(final String[] args) {
        final Display display = new Display();
        try {
            new SimpleBorder(display, "SimpleBorder.java").run();
        } finally {
            display.dispose();        	
        }
	}

}
