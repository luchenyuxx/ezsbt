package com.density.sbtplugin.views;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddCommandDialog extends TitleAreaDialog {
	private Text nameInput;
	private Text commandInput;
	private TreeParent target;
	private TreeViewer viewer;

	public AddCommandDialog(Shell parentShell, TreeParent target,
			TreeViewer viewer) {
		super(parentShell);
		this.target = target;
		this.viewer = viewer;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add a command to " + target.getName());
		// setMessage("This is a TitleAreaDialog",
		// IMessageProvider.INFORMATION);
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

	protected TreeObject saveInput() {
		return new TreeObject(nameInput.getText(), commandInput.getText());
	}

	@Override
	protected void okPressed() {
		TreeObject newCommand = saveInput();
		target.addChild(newCommand);
		viewer.refresh();
		super.okPressed();
	}
}
