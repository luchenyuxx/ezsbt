package com.density.sbtplugin.views;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

import com.density.sbtplugin.util.CommandsConvertor;
import com.density.sbtplugin.util.PluginConstants;
import com.density.sbtplugin.util.SbtPlugin;

public class SbtViewerDropAdapter extends ViewerDropAdapter {

	public SbtViewerDropAdapter(Viewer viewer) {
		super(viewer);
	}

	@Override
	public boolean performDrop(Object data) {
		if (data.getClass().isAssignableFrom(TreeSelection.class)) {
			TreeSelection treeSelection = (TreeSelection) data;
			Object obj = treeSelection.getFirstElement();
			if (IContainer.class.isAssignableFrom(obj.getClass())) {
				return addNewSbtProject((IContainer) obj);
			} else if (IJavaProject.class.isAssignableFrom(obj.getClass())) {
				IJavaProject javaProject = (IJavaProject) obj;
				return addNewSbtProject(javaProject.getProject());
			}
		}
		return false;
	}

	protected boolean addNewSbtProject(IContainer container) {
		SbtViewContentProvider contentProvider = (SbtViewContentProvider) ((TreeViewer) getViewer())
				.getContentProvider();
		TreeParent root = contentProvider.getInvisibleRoot();
		String path = getPath(container);
		if (!root.hasChild(path)) {
			addNewSbtProject(path, root);
			return true;
		} else {
			return false;
		}
	}
	protected String getPath(IContainer container){
		String path = container.getLocationURI().getRawPath();
		if(System.getProperty("os.name").toLowerCase().contains("win")){
			return path.substring(1);
		}
		return path;
	}

	protected void addNewSbtProject(String path, TreeParent root) {
		TreeParent newSbtProject = new TreeParent(path);
		IPreferenceStore store = SbtPlugin.getInstance().getPreferenceStore();
		String[] commandPairs = CommandsConvertor.stringToArray(store
				.getString(PluginConstants.COMMANDS_NAME_KEY));
		for (String commandPair : commandPairs) {
			TreeObject commandObject = new TreeObject(
					CommandsConvertor.keyOf(commandPair),
					CommandsConvertor.valueOf(commandPair));
			newSbtProject.addChild(commandObject);
		}
		root.addChild(newSbtProject);
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
