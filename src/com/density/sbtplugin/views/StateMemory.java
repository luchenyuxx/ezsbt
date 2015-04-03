package com.density.sbtplugin.views;

import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.IMemento;

import com.density.sbtplugin.views.SbtViewContentProvider.RootNode;

public class StateMemory {
	static public String CONTAINER_TYPE = "container";
	static public String COMMAND_BUTTON_TYPE = "command";
	static public String CONTAINER_NAME_KEY = "name";
	static public String COMMAND_NAME_KEY = "name";
	static public String COMMAND_VALUE_KEY = "sbtcommand";
	static public String JAVA_HOME_KEY = "javaHome";
	static public String JAVA_OPTIONS_KEY = "javaOptions";
	static public String PROJECT_ID_KEY = "projectId";

	static public void rememberState(SbtViewContentProvider viewContentProvider,
			IMemento rootMem) {
		RootNode root = viewContentProvider.getRoot();
		for (ProjectNode projectNode : root.getProjects()) {
			IMemento projectMem = rootMem.createChild(CONTAINER_TYPE);
			projectNode.fillMemory(projectMem);
		}
	}
	
	static public String optionsListToString(List<String> optionsList) {
		Iterator<String> iterator = optionsList.iterator();
		String result = "";
		boolean first = true;
		while(iterator.hasNext()){
			if(!first) result = result + " "; else first = false;
			result = result + iterator.next();
		}
		return result;
	}

	static public void remindState(SbtViewContentProvider viewContentProvider,
			IMemento rootMem) {
		RootNode root = viewContentProvider.getRoot();
		for (IMemento projectMem : rootMem.getChildren()) {
			root.addProject(new ProjectNode(projectMem));
		}
	}
}
