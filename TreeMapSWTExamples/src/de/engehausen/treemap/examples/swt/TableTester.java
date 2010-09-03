package de.engehausen.treemap.examples.swt;

import java.util.HashMap;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class TableTester {
	
	private Display display;
	private Shell shell;
	private HashMap<String, Image> hashImages;
	private Table table;
	
	private Color GRAY;
	private Color WHITE;
	private int EDITABLECOLUMN;
	
	public TableTester() {
		display = new Display();
		shell = new Shell(display);
		
		init();
		createGUI();
		
		shell.open();
		
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	private void init() {
		hashImages = new HashMap<String, Image>();
		hashImages.put("help", new Image(display, "D:\\eclipse\\configuration\\org.eclipse.osgi\\bundles\\159\\1\\.cp\\icons\\full\\wizban\\newclass_wiz.png"));
		hashImages.put("about", new Image(display, "D:\\eclipse\\configuration\\org.eclipse.osgi\\bundles\\240\\1\\.cp\\icons\\wizban\\extstr_wiz.png"));
		
		GRAY = new Color(display, 220, 220, 220);
		WHITE = new Color(display, 255, 255, 255);
	}
	
	private void createGUI() {
		shell.setLayout(new FillLayout());
		shell.setText("TableTester");
		
		shell.setImage(hashImages.get("about"));
		
		table = new Table(shell, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		table.addListener (SWT.MouseDown, new Listener () {
			public void handleEvent (Event event) {
				Rectangle clientArea = table.getClientArea ();
				Point selectedPoint = new Point (event.x, event.y);
				int index = table.getTopIndex ();
				while (index < table.getItemCount ()) {
					boolean visible = false;
					TableItem item = table.getItem (index);
					for (int i=0; i < table.getColumnCount(); i++) {
						Rectangle rect = item.getBounds (i);
						if (rect.contains (selectedPoint)) {
//							System.out.println ("Item " + index + "-" + i);
							EDITABLECOLUMN = i;
						}
						if (!visible && rect.intersects (clientArea)) {
							visible = true;
						}
					}
					if (!visible) return;
					index++;
				}
			}
		});
		
		final TableEditor editor = new TableEditor(table);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Clean up any previous editor control
				Control oldEditor = editor.getEditor();
				if (oldEditor != null) oldEditor.dispose();
		
				// Identify the selected row
				TableItem item = (TableItem)e.item;
				if (item == null) {
					return;
				}

				// The control that will be the editor must be a child of the Table
				if(EDITABLECOLUMN == 0) { // boolean
					if("true".equalsIgnoreCase(item.getText())) {
						final Button newEditor = new Button(table, SWT.CHECK);
						newEditor.setText("Click to change");
						newEditor.setSelection(true);
						newEditor.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent e) {
								editor.getItem().setText("" + newEditor.getSelection());
							}
						});
						newEditor.setFocus();
						editor.setEditor(newEditor, item, EDITABLECOLUMN);
					} else {
						final Button newEditor = new Button(table, SWT.CHECK);
						newEditor.setText("Click to change");
						newEditor.setSelection(false);
						newEditor.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent e) {
								editor.getItem().setText("" + newEditor.getSelection());
							}
						});
						newEditor.setFocus();
						editor.setEditor(newEditor, item, EDITABLECOLUMN);
					}
				} else if(EDITABLECOLUMN == 3) { // password string
					Button newEditor = new Button(table, SWT.NONE);
					newEditor.setText("Generate");
					newEditor.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							String s = getRandomString(new Random(System.nanoTime()), 12);
							editor.getItem().setText(EDITABLECOLUMN, s);
						}
					});
					newEditor.setFocus();
					editor.setEditor(newEditor, item, EDITABLECOLUMN);
				} else if(EDITABLECOLUMN == 4) { // color
					ColorDialog newEditor = new ColorDialog(shell, SWT.NONE);
					String[] data = item.getText(EDITABLECOLUMN).split(",");
					newEditor.setRGB(new RGB(Integer.parseInt(data[0].trim()),
							Integer.parseInt(data[1].trim()),
							Integer.parseInt(data[2].trim())));
					RGB rgb = newEditor.open();
					item.setText(EDITABLECOLUMN, rgb.red + "," + rgb.green + "," + rgb.blue);
				} else {
					Text newEditor = new Text(table, SWT.NONE);
					newEditor.setText(item.getText(EDITABLECOLUMN));
					newEditor.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent me) {
							Text text = (Text)editor.getEditor();
							editor.getItem().setText(EDITABLECOLUMN, text.getText());
						}
					});
					newEditor.selectAll();
					newEditor.setFocus();
					editor.setEditor(newEditor, item, EDITABLECOLUMN);
				}
			}
		});
		
		fillWithData();
	}
	
	private void fillWithData() {
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText("boolean");
		col.setWidth(100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setText("Integer");
		col.setWidth(100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setText("Float");
		col.setWidth(100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setText("Password");
		col.setWidth(100);

		col = new TableColumn(table, SWT.NONE);
		col.setText("Color");
		col.setWidth(100);
		
		TableItem row;
		Random r = new Random(System.nanoTime());
		for (int i = 0; i < 1000; i++) {
			row = new TableItem(table, SWT.NONE);
			row.setText(new String[] {
					"" + r.nextBoolean(),
					"" + r.nextInt(),
					"" + r.nextFloat(),
					"" + getRandomString(r, 12),
					"" + r.nextInt(255) + "," + r.nextInt(255) + "," + r.nextInt(255)});
			row.setBackground((i % 2 == 0) ? WHITE : GRAY);
		}
	}
		
	private String getRandomString(final Random rnd, int l) {
		final StringBuilder sb = new StringBuilder(l);
		while(l-->0) {
			sb.append((char) ('a'+rnd.nextInt(26)));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		new TableTester();
	}
}
