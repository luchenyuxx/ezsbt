package com.density.sbtplugin.views;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

import com.density.sbtplugin.util.SbtPlugin;
import com.density.sbtplugin.views.SbtViewContentProvider.RootNode;

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
