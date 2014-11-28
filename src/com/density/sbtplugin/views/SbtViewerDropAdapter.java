package com.density.sbtplugin.views;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jdt.core.IJavaProject;
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
		if (data.getClass().isAssignableFrom(TreeSelection.class)) {
			TreeSelection treeSelection = (TreeSelection) data;
			if (IContainer.class.isAssignableFrom(treeSelection
					.getFirstElement().getClass())) {
				return addNewSbtProject((IContainer)treeSelection.getFirstElement());
			}else if(IJavaProject.class.isAssignableFrom(treeSelection.getFirstElement().getClass())){
				IJavaProject javaProject = (IJavaProject)treeSelection.getFirstElement();
				return addNewSbtProject(javaProject.getProject());
			}
		}
		return false;
	}
	
	protected boolean addNewSbtProject(IContainer container){
		ViewContentProvider contentProvider = (ViewContentProvider) ((TreeViewer) getViewer())
				.getContentProvider();
		TreeParent root = contentProvider.getInvisibleRoot();
		String path = container.getRawLocation().toString();
		if (!root.hasChild(path)) {
			addNewSbtProject(path, root);
			return true;
		}else{
			return false;
		}
	}

	protected void addNewSbtProject(String path, TreeParent root) {
		TreeParent newSbtProject = new TreeParent(path);
		newSbtProject.addChild(new TreeObject(PluginConstants.START_SBT_NAME));
		newSbtProject.addChild(new TreeObject(PluginConstants.COMPILE_NAME,
				PluginConstants.COMPILE_COMMAND));
		newSbtProject.addChild(new TreeObject(PluginConstants.CLEAN_NAME,
				PluginConstants.CLEAN_COMMAND));
		newSbtProject.addChild(new TreeObject(PluginConstants.EXIT_NAME,
				PluginConstants.EXIT_COMMAND));
		root.addChild(newSbtProject);
		getViewer().refresh();
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return true;
	}

}
