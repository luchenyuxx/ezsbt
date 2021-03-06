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

import org.eclipse.jface.dialogs.IMessageProvider;
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

public class SetJavaHomeDialog extends TitleAreaDialog{
	protected ProjectNode node;
	protected Text javaHome;
	protected final static String TITLE = "Set java home of ";
	protected final static String MESSAGE = "Value should not be empty. If so, press OK buttion will do nothing.\n"
			+ "Set java home makes effect the next time SBT starts.\n"
			+ "Set java home sets the environment variable JAVA_HOME of SBT process.";
	
	public SetJavaHomeDialog(Shell parentShell, ProjectNode node) {
		super(parentShell);
		this.node = node;
	}
	@Override
	public void create() {
		super.create();
		setTitle(TITLE + node.getName());
		setMessage(MESSAGE, IMessageProvider.INFORMATION);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		container.setLayout(new GridLayout(2, false));
		createJavaHomeInput(container);
		Button defaultButton = new Button(container, SWT.DEFAULT);
		defaultButton.setText("default");
		defaultButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IPreferenceStore store = SbtPlugin.getInstance().getPreferenceStore();
				javaHome.setText(store.getString(PluginConstants.JAVA_HOME_KEY));
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return super.createDialogArea(parent);
	}
	
	protected void createJavaHomeInput(Composite container){
		Label label = new Label(container, SWT.NONE);
		label.setText("java home:");
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		javaHome = new Text(container, SWT.BORDER);
		javaHome.setLayoutData(gridData);
		javaHome.setText(node.getJavaHome());
	}
	
	@Override
	protected void okPressed() {
		if(!javaHome.getText().isEmpty()){
			node.setJavaHome(javaHome.getText());
		}
		super.okPressed();
	}
}
