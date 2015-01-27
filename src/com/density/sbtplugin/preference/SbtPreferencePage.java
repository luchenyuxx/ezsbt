package com.density.sbtplugin.preference;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.density.sbtplugin.util.CommandsConvertor;
import com.density.sbtplugin.util.PluginConstants;
import com.density.sbtplugin.util.SbtPlugin;

public class SbtPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	protected Table commandTable;
	protected Button addButton;
	protected Button editButton;
	protected Button removeButton;
	protected Text javaHomeInput;
	protected Text javaOptionsInput;
	protected Button hideResolveCheck;
	protected static final String[] TABLE_COLUMNS_TITLE = { "name", "command" };

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite root = makeRootComposite(parent);
		makeCommandsPreference(root);
		makeJavaHomePreference(root);
		makeJavaOptionsPreference(root);
		makeHideResolveMessagePreference(root);
		return root;
	}
	
	protected void makeJavaHomePreference(Composite parent){
		Label label = new Label(parent, SWT.LEFT);
		label.setText("Default javaHome:");
		javaHomeInput = new Text(parent, SWT.LEFT|SWT.BORDER);
		IPreferenceStore store = getPreferenceStore();
		javaHomeInput.setText(store.getString(PluginConstants.JAVA_HOME_KEY));
		javaHomeInput.setLayoutData(new RowData(350, SWT.DEFAULT));
	}
	
	protected void makeJavaOptionsPreference(Composite parent){
		Label label = new Label(parent, SWT.LEFT);
		label.setText("Java options:");
		javaOptionsInput = new Text(parent, SWT.LEFT|SWT.BORDER);
		IPreferenceStore store = getPreferenceStore();
		javaOptionsInput.setText(store.getString(PluginConstants.JAVA_OPTIONS_KEY));
		javaOptionsInput.setLayoutData(new RowData(350, SWT.DEFAULT));
	}
	
	protected void makeHideResolveMessagePreference(Composite parent){
		Composite checkBoxPanel = new Composite(parent, SWT.EMBEDDED);
		checkBoxPanel.setLayout(new GridLayout(2, false));
		Label label = new Label(checkBoxPanel, SWT.LEFT);
		label.setText("Hide resolving messages");
		hideResolveCheck = new Button(checkBoxPanel, SWT.CHECK);
		hideResolveCheck.setSelection(getPreferenceStore().getBoolean(PluginConstants.HIDE_RESOLVE_KEY));
	}
	
	protected Composite makeRootComposite(Composite parent){
		Composite rootComposite = new Composite(parent, SWT.EMBEDDED);
		rootComposite.setLayout(new RowLayout(SWT.VERTICAL));
		return rootComposite;
	}
	
	protected void makeCommandsPreference(Composite parent){
		Label label = new Label(parent, SWT.LEFT);
		label.setText("Default commands:");
		Composite commandsPreferenceRootComposite = new Composite(parent, SWT.EMBEDDED);
		commandsPreferenceRootComposite.setLayout(new RowLayout());
		makeTable(commandsPreferenceRootComposite);
		makeButtonComposite(commandsPreferenceRootComposite);
	}

	protected void makeButtonComposite(Composite parent) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayoutData(new RowData(80, 200));
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.fill = true;
		buttonComposite.setLayout(rowLayout);
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
	}

	protected void makeTable(Composite parent) {
		Composite tableComposite = new Composite(parent, SWT.NONE);
		tableComposite.setLayoutData(new RowData(300, 200));
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		TableViewer tableViewer = new TableViewer(tableComposite, SWT.SINGLE|SWT.V_SCROLL|SWT.H_SCROLL|SWT.BORDER|SWT.FULL_SELECTION);
		commandTable = tableViewer.getTable();
		commandTable.setLinesVisible(true);
		commandTable.setHeaderVisible(true);
		for (String title: TABLE_COLUMNS_TITLE) {
			TableColumn column = new TableColumn(commandTable, SWT.CENTER);
			column.setText(title);
			tableColumnLayout.setColumnData(column, new ColumnWeightData(50, 150, false));
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
		javaHomeInput.setText(store.getDefaultString(PluginConstants.JAVA_HOME_KEY));
		javaOptionsInput.setText(store.getDefaultString(PluginConstants.JAVA_OPTIONS_KEY));
		hideResolveCheck.setSelection(store.getDefaultBoolean(PluginConstants.HIDE_RESOLVE_KEY));
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
		store.setValue(PluginConstants.JAVA_HOME_KEY, javaHomeInput.getText());
		store.setValue(PluginConstants.JAVA_OPTIONS_KEY, javaOptionsInput.getText());
		store.setValue(PluginConstants.HIDE_RESOLVE_KEY, hideResolveCheck.getSelection());
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return SbtPlugin.getInstance().getPreferenceStore();
	}
}
