/* Copyright 2015 Density Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.density.ezsbt.preference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.density.ezsbt.util.AbstractCommandDialog;

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
