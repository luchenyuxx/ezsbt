package com.density.sbtplugin.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

import com.density.sbtplugin.views.SbtView.ViewContentProvider;

public class SbtViewerDropAdapter extends ViewerDropAdapter {

	public SbtViewerDropAdapter(Viewer viewer) {
		super(viewer);
	}

	@Override
	public boolean performDrop(Object data) {
		ViewContentProvider contentProvider = (ViewContentProvider) ((TreeViewer) getViewer())
				.getContentProvider();
		TreeParent root = contentProvider.getInvisibleRoot();
		if (data.getClass().isAssignableFrom(TreeSelection.class)) {
			TreeSelection treeSelection = (TreeSelection) data;
			if (IProject.class.isAssignableFrom(treeSelection.getFirstElement().getClass())) {
				IProject project = (IProject) treeSelection.getFirstElement();
				String path = project.getRawLocation().toString();
				if (!root.hasChild(path)) {
					addNewSbtProject(path, root, project);
				}
				return true;
			}
		}
		return false;
	}

	protected void addNewSbtProject(String path, TreeParent root,
			IProject project) {
		TreeParent newSbtProject = new TreeParent(path, project);
		newSbtProject.addChild(new TreeObject(PluginConstants.START_SBT_NAME));
		newSbtProject.addChild(new TreeObject(PluginConstants.COMPILE_NAME,PluginConstants.COMPILE_COMMAND));
		newSbtProject.addChild(new TreeObject(PluginConstants.CLEAN_NAME,PluginConstants.CLEAN_COMMAND));
		newSbtProject.addChild(new TreeObject(PluginConstants.EXIT_NAME,PluginConstants.EXIT_COMMAND));
		root.addChild(newSbtProject);
		getViewer().refresh();
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return true;
	}

}
