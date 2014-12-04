package com.density.sbtplugin.preference;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import com.density.sbtplugin.util.AbstractCommandDialog;

public class EditCommandDialog extends AbstractCommandDialog {
	private TableItem commandItem;
	private final static String MESSAGE = "The name field should not be empty. If so, press OK buttion will do nothing.";
	private final static String TITLE = "Edit your command";

	public EditCommandDialog(Shell parentShell, TableItem commandItem) {
		super(parentShell);
		this.commandItem = commandItem;
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Control area = super.createDialogArea(parent);
		nameInput.setText(commandItem.getText(0));
		commandInput.setText(commandItem.getText(1));
		return area;
	}

	@Override
	protected void okPressed() {
		if (!nameInput.getText().isEmpty()) {
			commandItem.setText(new String[] { nameInput.getText(),
					commandInput.getText() });
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
