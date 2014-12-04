package com.density.sbtplugin.preference;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class AddCommandDialog extends TitleAreaDialog {
	private Text nameInput;
	private Text commandInput;
	private Table table;
	private final static String MESSAGE = "The name field should not be empty. If so, press OK buttion will do nothing.";
	private final static String TITLE = "Add a default command";

	public AddCommandDialog(Shell parentShell, Table table) {
		super(parentShell);
		this.table = table;
	}

	@Override
	public void create() {
		super.create();
		setTitle(TITLE);
		setMessage(MESSAGE, IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);
		createNameInput(container);
		createCommandInput(container);
		return area;
	}

	private void createNameInput(Composite container) {
		Label nameLabel = new Label(container, SWT.NONE);
		nameLabel.setText("Name");

		GridData nameData = new GridData();
		nameData.grabExcessHorizontalSpace = true;
		nameData.horizontalAlignment = GridData.FILL;

		nameInput = new Text(container, SWT.BORDER);
		nameInput.setLayoutData(nameData);
	}

	private void createCommandInput(Composite container) {
		Label commandLabel = new Label(container, SWT.NONE);
		commandLabel.setText("Command");

		GridData commandData = new GridData();
		commandData.grabExcessHorizontalSpace = true;
		commandData.horizontalAlignment = GridData.FILL;
		commandInput = new Text(container, SWT.BORDER);
		commandInput.setLayoutData(commandData);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		if (!nameInput.getText().isEmpty()) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { nameInput.getText(),
					commandInput.getText() });
		}
		super.okPressed();
	}
}
