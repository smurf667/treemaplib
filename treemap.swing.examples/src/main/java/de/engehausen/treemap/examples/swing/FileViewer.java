package de.engehausen.treemap.examples.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.ISelectionChangeListener;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.examples.FileInfo;
import de.engehausen.treemap.examples.FileModel;
import de.engehausen.treemap.examples.Messages;
import de.engehausen.treemap.impl.SquarifiedLayout;
import de.engehausen.treemap.swing.TreeMap;
import de.engehausen.treemap.swing.impl.CushionRectangleRendererEx;

/**
 * Sample application using a tree map to show file sizes starting
 * at a given directory.
 */
public class FileViewer extends JFrame implements ActionListener, ISelectionChangeListener<FileInfo>, ILabelProvider<FileInfo> {

	private static final long serialVersionUID = 1L;

	protected final JMenuItem chooseMenu;
	protected final JMenuItem colorsMenu;
	protected final JMenuItem exitMenu;
	protected final TreeMap<FileInfo> treeMap;
	protected final JLabel selectionTitle;
	protected final JFileChooser fileChooser;
	protected final FilterDialog colorDialog;

	public FileViewer() {
		super(Messages.getString("fv.title"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// menu bar and items
		final JMenu fileMenu = createJMenu("fv.file");
		chooseMenu = createJMenuItem("fv.choose");
		colorsMenu = createJMenuItem("fv.colors");
		exitMenu = createJMenuItem("fv.exit");
		fileMenu.add(chooseMenu);
		fileMenu.add(colorsMenu);
		fileMenu.add(new JSeparator());
		fileMenu.add(exitMenu);
		final JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);

		// directory chooser dialog
		fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle(Messages.getString("fv.choose"));
	    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    fileChooser.setAcceptAllFileFilterUsed(false);

	    // dialog to set regular expressions for matching to colors
	    colorDialog = new FilterDialog(this, Messages.getString("fv.colors"));

	    // the tree map
		treeMap = new TreeMap<FileInfo>();
		treeMap.setRectangleRenderer(new CushionRectangleRendererEx<FileInfo>(160));
		// the frame acts as a selection change listener; when
		// the selection (i.e. rectangle) has changed, the status
		// bar is updated to show the selected items' label
		treeMap.addSelectionChangeListener(this);
		treeMap.setLabelProvider(this);
		treeMap.setColorProvider(colorDialog);
		treeMap.setTreeMapLayout(new SquarifiedLayout<FileInfo>(16));
		final Dimension d = new Dimension(600, 400);
		treeMap.setMinimumSize(d);
		treeMap.setPreferredSize(d);
		selectionTitle = new JLabel(" ");

		final Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(treeMap, BorderLayout.CENTER);
		container.add(selectionTitle, BorderLayout.SOUTH);
		pack();
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		final Object src = event.getSource();
		if (src.equals(chooseMenu)) {
			chooseDirectory();
		} else if (src.equals(colorsMenu)) {
			colorDialog.setVisible(true);
		} else if (src.equals(exitMenu)) {
			dispose();
		}
	}

	@Override
	public void selectionChanged(final ITreeModel<IRectangle<FileInfo>> model, final IRectangle<FileInfo> rectangle, final String text) {
		if (text != null) {
			selectionTitle.setText(text);
			final FileInfo info = rectangle.getNode();
			treeMap.setToolTipText(info.getSize()>0?info.getSizeAsString():null);
		}
	}

	@Override
	public String getLabel(final ITreeModel<IRectangle<FileInfo>> model, final IRectangle<FileInfo> rectangle) {
		return rectangle.getNode().getName();
	}

	@Override
	public void setVisible(final boolean flag) {
		super.setVisible(flag);
		if (flag == true) {
			// if the frame becomes visible and the tree map is not
			// yet showing content, prompt for a starting directory
			if (treeMap.getCurrentTreeModel() == null) {
				chooseDirectory();
			}
		}
	}

	/**
	 * Shows a dialog for choosing a directory. The chosen directory
	 * will be the root node of a weighted tree model build after
	 * selection.
	 */
	protected void chooseDirectory() {
	    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	    	final File root = fileChooser.getSelectedFile();
	    	if (root != null && root.exists()) {
	    		SwingUtilities.invokeLater(new Runnable() {
	    			public void run() {
	    				treeMap.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    				// create weighted tree model starting at the given
	    				// root directory...
	    	    		treeMap.setTreeModel(FileModel.createFileModel(root.getAbsolutePath()));
	    	    		// ...then repaint the tree map view
	    	    		treeMap.repaint();
	    			}
	    		});
	    	}
	    }
	}

	/**
	 * Creates a <code>JMenu</code> object, looking up its title
	 * in the messages properties file. If the item has an <code>.mne</code>
	 * suffix defined, a mnemonic will be set.
	 * @param key the lookup key in the messages properties
	 * @return the menu object, never {@code null}.
	 */
	protected JMenu createJMenu(final String key) {
		final JMenu result = new JMenu(Messages.getString(key));
		final String str = Messages.getString(key+".mne");
		if (str != null && str.length() > 0) {
			result.setMnemonic(str.charAt(0));
		}
//		result.addActionListener(this);
		return result;
	}

	/**
	 * Creates a <code>JMenuItem</code> object, looking up its title
	 * in the messages properties file. If the item has an <code>.mne</code>
	 * suffix defined, a mnemonic will be set. The frames' action listener
	 * is added to the menu.
	 * @param key the lookup key in the messages properties
	 * @return the menu item object, never {@code null}.
	 */
	protected JMenuItem createJMenuItem(final String key) {
		final JMenuItem result = new JMenuItem(Messages.getString(key));
		final String str = Messages.getString(key+".mne");
		if (str != null && str.length() > 0) {
			result.setMnemonic(str.charAt(0));
		}
		result.addActionListener(this);
		return result;
	}

	/**
	 * Creates and runs the UI.
	 * @param args optional parameters; currently ignored
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new FileViewer().setVisible(true);
			}
		});
	}

}
