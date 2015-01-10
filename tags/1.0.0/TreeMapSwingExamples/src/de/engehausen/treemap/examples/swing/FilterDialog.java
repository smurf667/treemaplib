package de.engehausen.treemap.examples.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.examples.FileInfo;
import de.engehausen.treemap.examples.Messages;

/**
 * Color dialog used in {@link FileViewer} which allows to map colors to file names and
 * directory names based on regular expressions (see {@link Pattern}).
 * The dialog directly acts as a {@link IColorProvider} to color
 * nodes of the tree map.
 */
public class FilterDialog extends JDialog implements IColorProvider<FileInfo, Color>, ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private static String TMP_DIR = "java.io.tmpdir";
	private static String SETTINGS = File.separatorChar+"FileViewer.dat";

	protected final FileViewer viewer;
	protected final JTable filters;
	protected final JButton ok;
	protected final JButton add;
	protected final JButton color;
	protected final JTextField input;
	protected final TableModelImpl model;

	/**
	 * Creates the dialog for the given file viewer.
	 * @param parent the file viewer, must not be <code>null</code>.
	 * @param title the title of the dialog, must not be <code>null</code>
	 */
	public FilterDialog(final FileViewer parent, final String title) {
		super(parent, title, true);
		viewer = parent;
		model = createTableModel();
		filters = new JTable(model);
		filters.getColumnModel().getColumn(1).setCellRenderer(new ColorCellRenderer());
		filters.addKeyListener(this);
		ok = new JButton(Messages.getString("fv.ok"));
		ok.addActionListener(this);
		add = new JButton(Messages.getString("fv.add"));
		add.addActionListener(this);
		color = new JButton();
		color.addActionListener(this);
		color.setBackground(Color.BLACK);
		input = new JTextField(16);
		final Container root = getContentPane();
		root.setLayout(new BorderLayout());
		root.add(new JScrollPane(filters), BorderLayout.CENTER);
		root.add(createEditPanel(), BorderLayout.SOUTH);
		pack();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(final ActionEvent actionevent) {
		final Object source = actionevent.getSource();
		if (source.equals(ok)) {
			setVisible(false);
			viewer.treeMap.refresh();
		} else if (source.equals(color)) {
			final Color c = JColorChooser.showDialog(viewer, Messages.getString("fv.filter"), color.getBackground());
			if (c != null) {
				color.setBackground(c);
			}
		} else if (source.equals(add)) {
			final String exp = input.getText();
			if (exp != null && exp.length()>0) {
				final TableModelImpl tableModel = (TableModelImpl) filters.getModel();
				tableModel.addEntry(exp, color.getBackground());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyPressed(final KeyEvent keyevent) {
		/* NOP */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyReleased(final KeyEvent keyevent) {
		if (keyevent.getKeyCode() == KeyEvent.VK_DELETE) {
			final int[] idxs = filters.getSelectedRows();
			if (idxs != null && idxs.length>0) {
				final TableModelImpl tableModel = (TableModelImpl) filters.getModel();
				tableModel.removeEntries(idxs);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyTyped(final KeyEvent keyevent) {
		/* NOP */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getColor(final ITreeModel<IRectangle<FileInfo>> treeModel, final IRectangle<FileInfo> rectangle) {
		Color result = Color.GRAY;
		final List<ModelEntry> list = model.entries;
		final int max = list.size();
		final String name = rectangle.getNode().getName();
		for (int i = 0; i < max; i++) {
			final ModelEntry entry = list.get(i);
			final Matcher m = entry.matcher;
			m.reset(name);
			if (m.matches()) {
				result = entry.color;
				break;
			}
		}
		return result;
	}

	/**
	 * Creates the panel at the bottom of the dialog,
	 * which allows to add a regular expression to color
	 * mapping, as well as exiting the dialog.
	 * @return the panel to use for the bottom of the dialog
	 */
	protected JPanel createEditPanel() {
		final JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.LINE_AXIS));

		JPanel line = new JPanel();
		line.setLayout(new BoxLayout(line, BoxLayout.PAGE_AXIS));

		final JPanel temp = new JPanel(new BorderLayout());
		temp.add(input, BorderLayout.LINE_START);
		temp.add(color, BorderLayout.CENTER);
		temp.add(add, BorderLayout.LINE_END);
		final JPanel temp2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
		temp2.add(temp);
		line.add(temp2);
		result.add(line);

		line.add(Box.createGlue());
		line = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		line.add(ok);
		result.add(line);
		return result;
	}

	protected TableModelImpl createTableModel() {
		final String tmpDir = System.getProperty(TMP_DIR);
		final List<ModelEntry> entries;
		if (tmpDir != null) {
			entries = loadEntries(tmpDir+SETTINGS);
		} else {
			entries = defaultEntries();
		}
		final TableModelImpl table = new TableModelImpl(Messages.getString("fv.filter"), Messages.getString("fv.color"), entries);
		return table;
	}

	/**
	 * Load a list of model entries from the given file. If reading from
	 * the file is not possible, a default list is returned.
	 * @param fileName the file to load from; must not be <code>null</code>.
	 * @return a list of model entries, never <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	protected List<ModelEntry> loadEntries(final String fileName) {
		final List<ModelEntry> result;
		final File f = new File(fileName);
		if (f.exists()) {
			List<ModelEntry> temp;
	    	try {
				final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
				temp = (List<ModelEntry>) ois.readObject();
				ois.close();
			} catch (FileNotFoundException e) {
				// can't read... ignore
				temp = null;
			} catch (IOException e) {
				// can't read... ignore
				temp = null;
			} catch (ClassNotFoundException e) {
				// can't read... ignore
				temp = null;
			}
			if (temp != null) {
				result = temp;
			} else {
				result = defaultEntries();
			}
		} else {
			result = defaultEntries();
		}
		// add shutdown hook which writes the model entries list
		// to a temporary file for use the next time the viewer
		// is used
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	try {
					final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
					oos.writeObject(result);
					oos.close();
				} catch (FileNotFoundException e) {
					// can't write... ignore
				} catch (IOException e) {
					// can't write... ignore
				}
		    }
		});
		return result;
	}

	/**
	 * Returns the default entries for the color provider.
	 * @return the default entries for the color provider, never <code>null</code>.
	 */
	protected List<ModelEntry> defaultEntries() {
		final List<ModelEntry> result = new ArrayList<ModelEntry>();
		result.add(new ModelEntry(".*png", new Color(0x2F74D0)));
		result.add(new ModelEntry(".*gif", new Color(0xBAD0EF)));
		result.add(new ModelEntry(".*bmp", new Color(0x8CD1E6)));
		result.add(new ModelEntry(".*jpg", new Color(0xC9EAF3)));

		result.add(new ModelEntry(".*txt", new Color(0xDFE32D)));
		result.add(new ModelEntry(".*doc", new Color(0xDFDF00)));
		result.add(new ModelEntry(".*log", new Color(0xEDEF85)));
		result.add(new ModelEntry(".*pdf", new Color(0xDEEF8A)));

		result.add(new ModelEntry(".*wav", new Color(0xFF86C2)));
		result.add(new ModelEntry(".*mp3", new Color(0xFE8BF0)));
		result.add(new ModelEntry(".*ogg", new Color(0xF5CAFF)));

		result.add(new ModelEntry(".*avi", new Color(0xFF8A8A)));
		result.add(new ModelEntry(".*mkv", new Color(0xFFACEC)));
		result.add(new ModelEntry(".*mp4", new Color(0xFF97CB)));
		result.add(new ModelEntry(".*mov", new Color(0xFFBBF7)));

		result.add(new ModelEntry(".*zip", new Color(0x36F200)));
		result.add(new ModelEntry(".*rar", new Color(0x95FF4F)));
		result.add(new ModelEntry(".*cab", new Color(0xC9DECB)));

		result.add(new ModelEntry(".*exe", new Color(0xBDF4CB)));
		return result;
	}

	private static class TableModelImpl extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		protected final List<ModelEntry> entries;
		protected final String exprCol, colCol;

		public TableModelImpl(final String columnTitle1, final String columnTitle2, final List<ModelEntry> e) {
			entries = e;
			exprCol = columnTitle1;
			colCol = columnTitle2;
		}

		protected void addEntry(final String regularExpression, final Color color) {
			final ModelEntry candidate = new ModelEntry(regularExpression, color);
			if (candidate.isValid() && !entries.contains(candidate)) {
				entries.add(candidate);
				fireTableDataChanged();
			}
		}

		protected void removeEntries(final int[] indices) {
			final Set<ModelEntry> tmp = new HashSet<ModelEntry>(indices.length, 1f);
			for (int i = indices.length-1; i>=0; i--) {
				tmp.add(entries.get(i));
			}
			for (ModelEntry e : tmp) {
				entries.remove(e);
			}
			fireTableDataChanged();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getColumnName(final int i) {
			return i==0?exprCol:colCol;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getColumnCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getRowCount() {
			return entries.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getValueAt(final int i, final int j) {
			if (i < entries.size()) {
				return j==0?entries.get(i).expression:entries.get(i).color;
			} else {
				return null;
			}
		}

	}

	private static class ModelEntry implements Serializable {
		private static final long serialVersionUID = 1L;
		protected final String expression;
		protected final Color color;
		protected transient Matcher matcher;
		public ModelEntry(final String regexp, final Color c) {
			expression = regexp;
			color = c;
			matcher = getMatcher(regexp);
		}
		protected static Matcher getMatcher(final String expr) {
			Matcher m;
			try {
				m = Pattern.compile(expr).matcher("");
			} catch (PatternSyntaxException e) {
				m = null;
			}
			return m;
		}
		protected boolean isValid() {
			return matcher!=null;
		}
		public int hashCode() {
			return expression.hashCode();
		}
		public boolean equals(final Object o) {
			if (o instanceof ModelEntry) {
				return expression.equals(((ModelEntry) o).expression);
			} else {
				return false;
			}
		}
		protected Object readResolve() throws ObjectStreamException {
			matcher = getMatcher(expression);
			return this;
		}

	}

	/**
	 * Paints a simple rectangle in the given color.
	 */
	private static class ColorCellRenderer implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			final JPanel result = new JPanel();
			result.setBackground((Color) value);
			return result;
		}

	}

}
