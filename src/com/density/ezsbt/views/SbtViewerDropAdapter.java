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

import org.eclipse.core.resources.IContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

import com.density.ezsbt.util.SbtPlugin;
import com.density.ezsbt.views.SbtViewContentProvider.RootNode;

public class SbtViewerDropAdapter extends ViewerDropAdapter {

	public SbtViewerDropAdapter(Viewer viewer) {
		super(viewer);
	}

	@Override
	public boolean performDrop(Object data) {
		if (data.getClass().isAssignableFrom(TreeSelection.class)) {
			TreeSelection treeSelection = (TreeSelection) data;
			Object obj = treeSelection.getFirstElement();
			if (IJavaProject.class.isAssignableFrom(obj.getClass())) {
				IJavaProject javaProject = (IJavaProject) obj;
				return addNewSbtProject(javaProject.getProject());
			}else if (IContainer.class.isAssignableFrom(obj.getClass())) {
				return addNewSbtProject((IContainer) obj);
			} 
		}
		return false;
	}

	protected boolean addNewSbtProject(IContainer container) {
		if (container.isAccessible()) {
			SbtViewContentProvider contentProvider = (SbtViewContentProvider) ((TreeViewer) getViewer())
					.getContentProvider();
			RootNode root = contentProvider.getRoot();
			String path = getPath(container);
			if (!root.hasProject(path)) {
				addNewSbtProject(path, container.getName(), root);
				return true;
			}
		}
		return false;
	}

	protected String getPath(IContainer container) {
		String path = container.getLocationURI().getRawPath();
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			return path.substring(1);
		}
		return path;
	}

	protected void addNewSbtProject(String path, String projectId, RootNode root) {
		IPreferenceStore store = SbtPlugin.getInstance().getPreferenceStore();
		ProjectNode newSbtProject = new ProjectNode(path, projectId,store);
		root.addProject(newSbtProject);
		getViewer().refresh();
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return true;
	}

	public void defaultCommands() {
		SbtPlugin.getInstance().getPreferenceStore();
	}

}
