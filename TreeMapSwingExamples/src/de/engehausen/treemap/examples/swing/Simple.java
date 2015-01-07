package de.engehausen.treemap.examples.swing;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.engehausen.treemap.impl.GenericTreeModel;
import de.engehausen.treemap.swing.TreeMap;

/**
 * Very simple demo of the treemap widget. This is a bare-minimum
 * demo, just setting a small tree map model.
 */
public class Simple extends JFrame {

	private static final long serialVersionUID = 1L;

	protected final TreeMap<String> treeMap;

	/**
	 * Creates the example using the given tree map and title
	 * @param aTreeMap the tree map
	 * @param title the title
	 */
	public Simple(final TreeMap<String> aTreeMap, final String title) {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(64, 64, 640, 400);
		treeMap = aTreeMap;
		getContentPane().add(treeMap);
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
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final Simple main = new Simple(new TreeMap<String>(), "Simple.java");
				main.init();
				main.setVisible(true);
			}
		});
	}

}
