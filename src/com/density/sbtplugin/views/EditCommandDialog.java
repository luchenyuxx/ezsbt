package com.density.sbtplugin.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.density.sbtplugin.util.AbstractCommandDialog;

public class EditCommandDialog extends AbstractCommandDialog {
	private TreeObject target;
	private TreeViewer viewer;
	private final static String MESSAGE = "The name field should not be empty. If so, press OK buttion will do nothing.";
	private final static String TITLE = "Edit your command";

	public EditCommandDialog(Shell parentShell, TreeObject target,
			TreeViewer viewer) {
		super(parentShell);
		this.target = target;
		this.viewer = viewer;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control area = super.createDialogArea(parent);
		nameInput.setText(target.getName());
		commandInput.setText(target.getSbtCommand());
		return area;
	}

	protected void saveInput() {
		target.setName(nameInput.getText());
		target.setSbtCommand(commandInput.getText());
	}

	@Override
	protected void okPressed() {
		if (!nameInput.getText().isEmpty()) {
			saveInput();
			viewer.refresh();
		}
		super.okPressed();
	}

	@Override
	protected String getTitle() {
		return TITLE;
	}

	@Override
	protected String getTipMessage() {
		return MESSAGE;
	}

}
