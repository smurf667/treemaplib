package de.engehausen.treemap.examples.swing;

import javax.swing.SwingUtilities;

import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.impl.BorderSquarifiedLayout;
import de.engehausen.treemap.impl.GenericTreeModel;
import de.engehausen.treemap.swing.TreeMap;
import de.engehausen.treemap.swing.impl.BorderRenderer;
import de.engehausen.treemap.swing.impl.DefaultRectangleRenderer;

/**
 * A very simple example of a tree map layout using the {@link BorderRenderer}.
 */
public class SimpleBorder extends Simple {

	private static final long serialVersionUID = 1L;

	public SimpleBorder(final TreeMap<String> aTreeMap, final String title) {
		super(aTreeMap, title);
	}

	/**
	 * Initializes the tree map with a border layout and a sample
	 * tree model.
	 */
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
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// create a tree map, overriding the rectangle find
				// method to avoid the selection of a non-leaf node
				// this is just a question of taste, but allowing non-leaf
				// selection has awkward side effects
				final TreeMap<String> treeMap = new TreeMap<String>() {
					private static final long serialVersionUID = 1L;
					@Override
					protected IRectangle<String> findRectangle(int x, int y) {
						final IRectangle<String> result = super.findRectangle(x, y);
						return rectangles.hasChildren(result)?null:result;
					}
					
				};
				final SimpleBorder main = new SimpleBorder(treeMap, "SimpleBorder.java");
				main.init();
				main.setVisible(true);
			}
		});
	}

}
