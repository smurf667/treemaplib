package de.engehausen.treemap.examples.swt;

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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import de.engehausen.treemap.IColorProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.examples.FileInfo;
import de.engehausen.treemap.examples.Messages;

/**
 * Color dialog used in {@link FileViewer} which allows to map colors to file names and
 * directory names based on regular expressions (see {@link Pattern}).
 * The dialog directly acts as a {@link IColorProvider} to colorButton
 * nodes of the tree map.
 * PRETTY UGLY
 */
public class FilterDialog implements IColorProvider<FileInfo, Color>, SelectionListener, KeyListener, Serializable {

	private static final long serialVersionUID = 1L;

	private static String TMP_DIR = "java.io.tmpdir";
	private static String SETTINGS = File.separatorChar+"FileViewer_swt.dat";

	protected final Shell shell;
	protected final FileViewer viewer;
	protected final Table filters;
	protected final Button ok;
	protected final Button add;
	protected final Button colorButton;
	protected final Label colorLabel;
	protected final Text input;
	protected final List<ModelEntry> entries;

	/**
	 * Creates the dialog for the given file viewer.
	 * @param parent the file viewer, must not be {@code null}.
	 * @param title the title of the dialog, must not be {@code null}
	 */
	public FilterDialog(final FileViewer parent, final String title) {
		viewer = parent;

		shell = new Shell(parent.display, SWT.DIALOG_TRIM|SWT.APPLICATION_MODAL);
		shell.setText(title);
		shell.setLayout(new FormLayout());

		filters = new Table(shell, SWT.MULTI|SWT.BORDER|SWT.FULL_SELECTION);
		filters.setLinesVisible(true);
		filters.setHeaderVisible(true);
		filters.addKeyListener(this);
		FormData data = new FormData();
		data.left = new FormAttachment(0,0);
		filters.setLayoutData(data);
		TableColumn tc = new TableColumn(filters, SWT.NONE);
		tc.setText(Messages.getString("fv.filter"));
		tc.setWidth(120);
		tc = new TableColumn(filters, SWT.NONE);
		tc.setText(Messages.getString("fv.color"));
		tc.setWidth(70);
		entries = createTableEntries(filters, viewer.display);

		input = new Text(shell, SWT.BORDER);
		data = new FormData();
		data.top = new FormAttachment(filters, 5);
		input.setLayoutData(data);

		colorLabel = new Label(shell, SWT.NULL);
		colorLabel.setText("   ");
		data = new FormData();
		data.top = new FormAttachment(filters, 5);
		data.left = new FormAttachment(input, 5);
		colorLabel.setLayoutData(data);

		colorButton = new Button(shell, SWT.NULL);
		colorButton.addSelectionListener(this);
		colorButton.setText("color...");
		final Color c = parent.display.getSystemColor(SWT.COLOR_BLACK);
		colorLabel.setBackground(c);
		data = new FormData();
		data.top = new FormAttachment(filters, 5);
		data.left = new FormAttachment(colorLabel, 5);
		colorButton.setLayoutData(data);

		add = new Button(shell, SWT.NULL);
		add.setText(Messages.getString("fv.add"));
		add.addSelectionListener(this);
		data = new FormData();
		data.top = new FormAttachment(filters, 5);
		data.left = new FormAttachment(colorButton, 5);
		add.setLayoutData(data);

		ok = new Button(shell, SWT.NULL);
		ok.setText(Messages.getString("fv.ok"));
		ok.addSelectionListener(this);
		data = new FormData();
		data.top = new FormAttachment(filters, 5);
		data.left = new FormAttachment(add, 15);
		ok.setLayoutData(data);

		shell.pack();
	}

	public void setVisible(final boolean flag) {
		shell.setVisible(flag);
	}

	protected List<ModelEntry> createTableEntries(final Table t, final Display d) {
		final String tmpDir = System.getProperty(TMP_DIR);
		final List<ModelEntry> result;
		if (tmpDir != null) {
			result = loadEntries(tmpDir+SETTINGS);
		} else {
			result = defaultEntries();
		}
		for (int i = 0; i < result.size(); i++) {
			final ModelEntry entry = result.get(i);
			createTableItem(t, entry.expression, entry.getColor(d));
		}
		return result;
	}

	protected TableItem createTableItem(final Table table, final String expression, final Color color) {
		final TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, expression);
		item.setBackground(1, color);
		item.setForeground(1, color);
		return item;
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// ignore
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.widget.equals(ok)) {
			shell.setVisible(false);
		} else if (event.widget.equals(colorButton)) {
	        final ColorDialog cd = new ColorDialog(shell);
	        cd.setText(Messages.getString("fv.filter"));
	        cd.setRGB(colorLabel.getBackground().getRGB());
	        final RGB newColor = cd.open();
	        if (newColor != null) {
	        	final Color col = new Color(viewer.display, newColor);
				colorLabel.setBackground(col);
				col.dispose();
	        }
		} else if (event.widget.equals(add)) {
			final Color c = colorLabel.getBackground();
			final int rgb = c.getRed()<<16 | c.getGreen()<<8 | c.getBlue();
			final ModelEntry newEntry = new ModelEntry(input.getText(), rgb);
			if (newEntry.isValid()) {
				entries.add(newEntry);
				createTableItem(filters, newEntry.expression, newEntry.getColor(viewer.display));
			}
		}

	}


	@Override
	public void keyPressed(final KeyEvent keyevent) {
		// ignore
	}

	@Override
	public void keyReleased(final KeyEvent keyevent) {
		if (keyevent.keyCode == SWT.DEL) {
			final int[] idx = filters.getSelectionIndices();
			if (idx != null && idx.length>0) {
				Arrays.sort(idx);
				filters.remove(idx);
				for (int i = idx.length-1; i>=0; i--) {
					final ModelEntry e = entries.remove(idx[i]);
					e.dispose();
				}
			}
		}
	}

	@Override
	public Color getColor(final ITreeModel<IRectangle<FileInfo>> treeModel, final IRectangle<FileInfo> rectangle) {
		Color result = viewer.display.getSystemColor(SWT.COLOR_GRAY);
		final List<ModelEntry> list = entries;
		final int max = list.size();
		final String name = rectangle
		.getNode()
		.getName();
		for (int i = 0; i < max; i++) {
			final ModelEntry entry = list.get(i);
			final Matcher m = entry.matcher;
			m.reset(name);
			if (m.matches()) {
				result = entry.getColor(viewer.display);
				break;
			}
		}
		return result;
	}

	/**
	 * Disposes the resources held by the dialog.
	 */
	public void dispose() {
		for (ModelEntry e : entries) {
			e.dispose();
		}
		shell.dispose();
	}

	/**
	 * Load a list of model entries from the given file. If reading from
	 * the file is not possible, a default list is returned.
	 * @param fileName the file to load from; must not be {@code null}.
	 * @return a list of model entries, never {@code null}.
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
	 * Returns the default entries for the colorButton provider.
	 * @return the default entries for the colorButton provider, never {@code null}.
	 */
	protected List<ModelEntry> defaultEntries() {
		final List<ModelEntry> result = new ArrayList<ModelEntry>();
		result.add(new ModelEntry(".*png", 0x2F74D0));
		result.add(new ModelEntry(".*gif", 0xBAD0EF));
		result.add(new ModelEntry(".*bmp", 0x8CD1E6));
		result.add(new ModelEntry(".*jpg", 0xC9EAF3));

		result.add(new ModelEntry(".*txt", 0xDFE32D));
		result.add(new ModelEntry(".*doc", 0xDFDF00));
		result.add(new ModelEntry(".*log", 0xEDEF85));
		result.add(new ModelEntry(".*pdf", 0xDEEF8A));

		result.add(new ModelEntry(".*wav", 0xFF86C2));
		result.add(new ModelEntry(".*mp3", 0xFE8BF0));
		result.add(new ModelEntry(".*ogg", 0xF5CAFF));

		result.add(new ModelEntry(".*avi", 0xFF8A8A));
		result.add(new ModelEntry(".*mkv", 0xFFACEC));
		result.add(new ModelEntry(".*mp4", 0xFF97CB));
		result.add(new ModelEntry(".*mov", 0xFFBBF7));

		result.add(new ModelEntry(".*zip", 0x36F200));
		result.add(new ModelEntry(".*rar", 0x95FF4F));
		result.add(new ModelEntry(".*cab", 0xC9DECB));

		result.add(new ModelEntry(".*exe", 0xBDF4CB));
		return result;
	}

	private static class ModelEntry implements Serializable {
		private static final long serialVersionUID = 1L;
		protected final String expression;
		protected final int color;
		protected transient Matcher matcher;
		protected transient Color swtColor;
		public ModelEntry(final String regexp, final int rgbColor) {
			expression = regexp;
			color = rgbColor;
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
		protected Color getColor(final Display d) {
			if (swtColor != null) {
				return swtColor;
			} else {
				swtColor = new Color(d, (color>>16&0xff), (color>>8&0xff), color&0xff);
				return swtColor;
			}
		}
		protected void dispose() {
			if (swtColor != null) {
				swtColor.dispose();
				swtColor = null;
			}
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

}
