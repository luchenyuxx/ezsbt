package com.density.sbtplugin.preference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.density.sbtplugin.util.AbstractCommandDialog;

public class AddCommandDialog extends AbstractCommandDialog {
	private Table table;
	private final static String MESSAGE = "The name field should not be empty. If so, press OK buttion will do nothing.";
	private final static String TITLE = "Add a default command";

	public AddCommandDialog(Shell parentShell, Table table) {
		super(parentShell);
		this.table = table;
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

	@Override
	protected String getTitle() {
		return TITLE;
	}

	@Override
	protected String getTipMessage() {
		return MESSAGE;
	}
}
