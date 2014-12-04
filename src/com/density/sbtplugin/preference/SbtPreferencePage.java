package com.density.sbtplugin.preference;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.density.sbtplugin.util.CommandsConvertor;
import com.density.sbtplugin.util.SbtPlugin;
import com.density.sbtplugin.views.PluginConstants;

public class SbtPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	protected Table commandTable;
	protected Button addButton;
	protected Button editButton;
	protected Button removeButton;

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText("Default commands:");
		Composite root = makeRootComposite(parent);
		makeTable(root);
		makeButtonComposite(root);
		return root;
	}

	protected Composite makeRootComposite(Composite parent) {
		Composite rootComposite = new Composite(parent, SWT.EMBEDDED
				| SWT.HORIZONTAL);
		GridLayout gridLayout = new GridLayout(2, false);
		rootComposite.setLayout(gridLayout);
		rootComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		return rootComposite;
	}

	protected Composite makeButtonComposite(Composite parent) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayout(new FillLayout(SWT.VERTICAL));
		addButton = new Button(buttonComposite, SWT.NONE);
		addButton.setText("Add");
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				doAddCommand();
			}
		});
		editButton = new Button(buttonComposite, SWT.NONE);
		editButton.setText("Edit");
		editButton.setEnabled(false);
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				doEditCommand();
			}
		});
		removeButton = new Button(buttonComposite, SWT.NONE);
		removeButton.setText("Remove");
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				doRemoveCommand();
			}
		});
		return buttonComposite;
	}

	protected void makeTable(Composite parent) {
		TableViewer tableViewer = new TableViewer(parent, SWT.V_SCROLL|SWT.H_SCROLL|SWT.BORDER|SWT.FULL_SELECTION);
		commandTable = tableViewer.getTable();
		commandTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		commandTable.setLinesVisible(true);
		commandTable.setHeaderVisible(true);
		String[] titles = { "name", "command" };
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(commandTable, SWT.NONE);
			column.setText(titles[i]);
			column.setWidth(100);
		}
		IPreferenceStore store = getPreferenceStore();
		String[] commandPairs = CommandsConvertor.stringToArray(store
				.getString(PluginConstants.COMMANDS_NAME_KEY));
		for (String pair : commandPairs) {
			TableItem item = new TableItem(commandTable, SWT.NONE);
			item.setText(CommandsConvertor.pairToArray(pair));
		}
		commandTable.pack();
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event){
				if(commandTable.getSelectionCount()==0){
					editButton.setEnabled(false);
					removeButton.setEnabled(false);
				} else {
					editButton.setEnabled(true);
					removeButton.setEnabled(true);
				}
			}
		});
	}

	protected void doAddCommand() {
		AddCommandDialog dialog = new AddCommandDialog(this.getShell(),
				commandTable);
		dialog.create();
		dialog.open();
	}

	protected void doEditCommand() {
		TableItem selectedItem = commandTable.getItem(commandTable
				.getSelectionIndex());
		if (selectedItem != null) {
			EditCommandDialog dialog = new EditCommandDialog(this.getShell(),
					selectedItem);
			dialog.create();
			dialog.open();
		}
	}

	protected void doRemoveCommand() {
		commandTable.remove(commandTable.getSelectionIndex());
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		IPreferenceStore store = getPreferenceStore();
		commandTable.removeAll();
		String[] defaultCommands = CommandsConvertor.stringToArray(store
				.getDefaultString(PluginConstants.COMMANDS_NAME_KEY));
		for (String commandPair : defaultCommands) {
			TableItem commandItem = new TableItem(commandTable, SWT.NONE);
			commandItem.setText(CommandsConvertor.pairToArray(commandPair));
		}
		editButton.setEnabled(false);
		removeButton.setEnabled(false);
	}

	@Override
	protected void performApply() {
		super.performApply();
		IPreferenceStore store = getPreferenceStore();
		String sum = "";
		for (TableItem item : commandTable.getItems()) {
			sum = CommandsConvertor.addCommandPair(sum, item.getText(0),
					item.getText(1));
		}
		store.setValue(PluginConstants.COMMANDS_NAME_KEY, sum);
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return SbtPlugin.getInstance().getPreferenceStore();
	}
}
