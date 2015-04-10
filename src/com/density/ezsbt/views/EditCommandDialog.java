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

package com.density.ezsbt.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.density.ezsbt.util.AbstractCommandDialog;

public class EditCommandDialog extends AbstractCommandDialog {
	private CommandNode target;
	private TreeViewer viewer;
	private final static String MESSAGE = "The name field should not be empty. If so, press OK buttion will do nothing.";
	private final static String TITLE = "Edit your command";

	public EditCommandDialog(Shell parentShell, CommandNode target,
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
