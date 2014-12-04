package com.density.sbtplugin.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

import com.density.sbtplugin.util.AbstractCommandDialog;

public class AddCommandDialog extends AbstractCommandDialog {
	private TreeParent target;
	private TreeViewer viewer;
	private final static String MESSAGE = "The name field should not be empty. If so, press OK buttion will do nothing.";
	private final static String TITLE = "Add a command to ";

	public AddCommandDialog(Shell parentShell, TreeParent target,
			TreeViewer viewer) {
		super(parentShell);
		this.target = target;
		this.viewer = viewer;
	}

	protected TreeObject saveInput() {
		return new TreeObject(nameInput.getText(), commandInput.getText());
	}

	@Override
	protected void okPressed() {
		if (!nameInput.getText().isEmpty()) {
			TreeObject newCommand = saveInput();
			target.addChild(newCommand);
			viewer.refresh();
		}
		super.okPressed();
	}

	@Override
	protected String getTitle() {
		return TITLE + target.getName();
	}

	@Override
	protected String getTipMessage() {
		return MESSAGE;
	}
}
