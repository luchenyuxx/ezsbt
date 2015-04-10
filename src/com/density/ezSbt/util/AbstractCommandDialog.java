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

package com.density.ezsbt.util;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractCommandDialog extends TitleAreaDialog {
	protected Text nameInput;
	protected Text commandInput;
	
	public AbstractCommandDialog(Shell parentShell) {
		super(parentShell);
	}
	
	protected abstract String getTitle();
	
	protected abstract String getTipMessage();

	@Override
	public void create() {
		super.create();
		setTitle(getTitle());
		setMessage(getTipMessage(), IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
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
}
