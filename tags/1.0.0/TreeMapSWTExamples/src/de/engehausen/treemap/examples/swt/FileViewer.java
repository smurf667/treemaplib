package de.engehausen.treemap.examples.swt;

import java.io.File;
import java.io.Serializable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import de.engehausen.treemap.ILabelProvider;
import de.engehausen.treemap.IRectangle;
import de.engehausen.treemap.ISelectionChangeListener;
import de.engehausen.treemap.ITreeModel;
import de.engehausen.treemap.IWeightedTreeModel;
import de.engehausen.treemap.examples.FileInfo;
import de.engehausen.treemap.examples.FileModel;
import de.engehausen.treemap.examples.Messages;
import de.engehausen.treemap.impl.SquarifiedLayout;
import de.engehausen.treemap.swt.TreeMap;
import de.engehausen.treemap.swt.impl.CushionRectangleRendererEx;

/**
 * Sample application using a tree map to show file sizes starting
 * at a given directory.
 */
public class FileViewer implements Runnable, ISelectionChangeListener<FileInfo>, ILabelProvider<FileInfo>, SelectionListener, Serializable {

	private static final long serialVersionUID = 1L;

	protected final Display display;
	protected final Shell shell;

	protected final MenuItem chooseMenu;
	protected final MenuItem colorsMenu;
	protected final MenuItem exitMenu;
	protected final TreeMap<FileInfo> treeMap;
	protected final Label selectionTitle;
	protected final FilterDialog colorDialog;

	public FileViewer(final Display d) {
		display = d;
		shell = new Shell(d);
		shell.setText(Messages.getString("fv.title"));

		final GridLayout glayout = new GridLayout();
		glayout.numColumns = 1;
		glayout.marginLeft = -2;
		glayout.marginRight = -2;
		glayout.marginHeight = 0;
		glayout.marginBottom = 0;
		glayout.marginTop = 0;
		glayout.horizontalSpacing = 0;
		glayout.verticalSpacing = 0;
		shell.setLayout(glayout);

	    // dialog to set regular expressions for matching to colors
	    colorDialog = new FilterDialog(this, Messages.getString("fv.colors"));

		treeMap = new TreeMap<FileInfo>(shell);
		treeMap.setTreeMapLayout(new SquarifiedLayout<FileInfo>(16));
		treeMap.setRectangleRenderer(new CushionRectangleRendererEx<FileInfo>(160));
		treeMap.addSelectionChangeListener(this);
		treeMap.setLabelProvider(this);
		treeMap.setColorProvider(colorDialog);

		final GridData mainData = new GridData();
		mainData.grabExcessVerticalSpace = true;
		mainData.grabExcessHorizontalSpace = true;
		mainData.verticalAlignment = GridData.FILL;
		mainData.horizontalAlignment = GridData.FILL;
		treeMap.setLayoutData(mainData);
		shell.setBounds(64, 64, 600, 400);

		selectionTitle = new Label(shell, SWT.LEFT);

		// menu bar and items
		final Menu menuBar = new Menu(shell, SWT.BAR);
		final MenuItem fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		fileMenuHeader.setText(getMessageString("fv.file"));

		final Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		fileMenuHeader.setMenu(fileMenu);

		chooseMenu = new MenuItem(fileMenu, SWT.PUSH);
		chooseMenu.setText(getMessageString("fv.choose"));
		chooseMenu.addSelectionListener(this);
		colorsMenu = new MenuItem(fileMenu, SWT.PUSH);
		colorsMenu.setText(getMessageString("fv.colors"));
		colorsMenu.addSelectionListener(this);
		exitMenu = new MenuItem(fileMenu, SWT.PUSH);
		exitMenu.setText(getMessageString("fv.exit"));
		exitMenu.addSelectionListener(this);
		shell.setMenuBar(menuBar);

	}

	// inserts & for the mne when found
	protected String getMessageString(final String key) {
		final String mneKey = key+".mne";
		if (Messages.contains(mneKey)) {
			final String msg = Messages.getString(key);
			final char k = Messages.getString(mneKey).charAt(0);
			final int idx = msg.indexOf(k);
			if (idx >= 0) {
				final StringBuilder sb = new StringBuilder(msg.length()+1);
				sb.append(msg);
				sb.insert(idx, '&');
				return sb.toString();
			} else {
				return msg;
			}
		} else {
			return Messages.getString(key);
		}
	}

	public void run() {
		shell.open();
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (treeMap.getCurrentTreeModel() == null) {
					// if the window becomes visible and the tree map is not
					// yet showing content, prompt for a starting directory
					chooseDirectory();
				}
			}
		});
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		colorDialog.dispose();
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// ignore
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (chooseMenu.equals(event.widget)) {
			chooseDirectory();
		} else if (colorsMenu.equals(event.widget)) {
			colorDialog.setVisible(true);
		} else if (exitMenu.equals(event.widget)) {
			shell.dispose();
		}
	}

	@Override
	public void selectionChanged(final ITreeModel<IRectangle<FileInfo>> model, final IRectangle<FileInfo> rectangle, final String text) {
		if (text != null) {
//			final Rectangle mapBounds = treeMap.getBounds();
//			final Rectangle labelBounds = selectionTitle.getBounds();
//			if (labelBounds.width != mapBounds.width) {
//				labelBounds.width = mapBounds.width;
//				selectionTitle.setBounds(labelBounds);
//			}
			selectionTitle.setText(text);
			selectionTitle.pack();
			selectionTitle.update();
			final FileInfo info = rectangle.getNode();
			treeMap.setToolTipText(info.getSize()>0?info.getSizeAsString():null);
		}
	}

	@Override
	public String getLabel(final ITreeModel<IRectangle<FileInfo>> model, final IRectangle<FileInfo> rectangle) {
		return rectangle.getNode().getName();
	}

	/**
	 * Shows a dialog for choosing a directory. The chosen directory
	 * will be the root node of a weighted tree model build after
	 * selection.
	 */
	protected void chooseDirectory() {
		final DirectoryDialog dlg = new DirectoryDialog(shell);
		final String dir = dlg.open();
		if (dir != null) {
			final File f = new File(dir);
			if (f.exists() && f.isDirectory()) {
				treeMap.setCursor(treeMap.getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
				// create weighted tree model starting at the given
				// root directory...
				new Thread(new Runnable() {
					@Override
					public void run() {
						final IWeightedTreeModel<FileInfo> result = FileModel.createFileModel(f.getAbsolutePath());
						treeMap.getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
					    		treeMap.setTreeModel(result);
					    		// ...then repaint the tree map view
					    		treeMap.redraw();
							}
						});
					}
				}).start();
			}
		}
	}

	/**
	 * Creates and runs the UI.
	 * @param args optional parameters; currently ignored
	 */
	public static void main(final String[] args) {
        final Display display = new Display();
        try {
            new FileViewer(display).run();
        } finally {
            display.dispose();
        }
	}

}
