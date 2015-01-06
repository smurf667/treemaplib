package de.engehausen.treemap.examples.swt;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.engehausen.treemap.impl.GenericTreeModel;
import de.engehausen.treemap.swt.TreeMap;

/**
 * Very simple demo of the treemap widget. This is a bare-minimum
 * demo, just setting a small tree map model.
 */
public class Simple implements Runnable {
	
	private static final long serialVersionUID = 1L;
	
	protected final Display display;
	protected final Shell shell;
	protected final TreeMap<String> treeMap;

	/**
	 * Creates the sample using the given display and title
	 * @param aDisplay the display to run on
	 * @param title the title to show
	 */
	public Simple(final Display aDisplay, final String title) {
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
	 * Creates a simple weighted tree model, after the example
	 * given in the "Squarified Treemaps" <a href="http://www.win.tue.nl/~vanwijk/stm.pdf">paper</a>
	 * and sets it to the treemap widget.
	 */
	protected void init() {
		final GenericTreeModel<String> model = new GenericTreeModel<String>();
		final String root = "root";
		// create the root node
		model.add(root, 0, null);
		// add one level of children
		model.add("node a", 6, root);
		model.add("node b", 6, root);
		model.add("node c", 4, root);
		model.add("node d", 3, root);
		model.add("node e", 2, root);
		model.add("node f", 2, root);
		model.add("node g", 1, root);
		treeMap.setTreeModel(model);
	}

	/**
	 * Creates and runs the UI.
	 * @param args optional parameters; currently ignored
	 */
	public static void main(final String[] args) {
        final Display display = new Display();
        try {
            new Simple(display, "Simple.java").run();
        } finally {
            display.dispose();        	
        }
	}

}
