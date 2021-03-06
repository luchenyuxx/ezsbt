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

import java.util.Arrays;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.density.ezsbt.util.PluginConstants;
import com.density.ezsbt.util.SbtPlugin;

public class SetJavaOptionsDialog extends TitleAreaDialog {
	protected ProjectNode node;
	protected Text javaOptions;
	protected final static String TITLE = "Set java options of ";

	public SetJavaOptionsDialog(Shell parentShell, ProjectNode node) {
		super(parentShell);
		this.node = node;
	}
	
	@Override
	public void create() {
		super.create();
		setTitle(TITLE + node.getName());
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		container.setLayout(new GridLayout(2, false));
		createJavaOptionsInput(container);
		Button defaultButton = new Button(container, SWT.DEFAULT);
		defaultButton.setText("default");
		defaultButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IPreferenceStore store = SbtPlugin.getInstance().getPreferenceStore();
				javaOptions.setText(store.getString(PluginConstants.JAVA_OPTIONS_KEY));
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return super.createDialogArea(parent);
	}

	protected void createJavaOptionsInput(Composite container){
		Label label = new Label(container, SWT.NONE);
		label.setText("java options:");
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		javaOptions = new Text(container, SWT.BORDER);
		javaOptions.setLayoutData(gridData);
		javaOptions.setText(StateMemory.optionsListToString(node.getJavaOptions()));
	}
	
	@Override
	protected void okPressed() {
		if(!javaOptions.getText().isEmpty()){
			node.setJavaOptions(Arrays.asList(javaOptions.getText().split(" ")));
		}
		super.okPressed();
	}
}
