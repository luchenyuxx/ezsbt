package com.density.sbtplugin.views;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.IMemento;

public class StateMemory {
	static public String CONTAINER_TYPE = "container";
	static public String COMMAND_BUTTON_TYPE = "command";
	static public String CONTAINER_NAME_KEY = "name";
	static public String COMMAND_NAME_KEY = "name";
	static public String COMMAND_VALUE_KEY = "sbtcommand";
	static public String JAVA_HOME_KEY = "javaHome";
	static public String JAVA_OPTIONS_KEY = "javaOptions";

	static public void rememberState(SbtViewContentProvider viewContentProvider,
			IMemento rootMem) {
		TreeParent root = viewContentProvider.getInvisibleRoot();
		for (TreeObject containerNode : root.getChildren()) {
			TreeParent container = (TreeParent) containerNode;
			IMemento containerMem = rootMem.createChild(CONTAINER_TYPE);
			containerMem.putString(CONTAINER_NAME_KEY, container.getName());
			containerMem.putString(JAVA_HOME_KEY, container.getJavaHome());
			containerMem.putString(JAVA_OPTIONS_KEY, optionsListToString(container.getJavaOptions()));
			for (TreeObject command : container.getChildren()) {
				IMemento commandMem = containerMem
						.createChild(COMMAND_BUTTON_TYPE);
				commandMem.putString(COMMAND_NAME_KEY, command.getName());
				commandMem
						.putString(COMMAND_VALUE_KEY, command.getSbtCommand());
			}
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
		TreeParent root = viewContentProvider.getInvisibleRoot();
		for (IMemento containerMem : rootMem.getChildren()) {
			TreeParent newContainer = new TreeParent(
					containerMem.getString(CONTAINER_NAME_KEY));
			newContainer.setJavaHome(containerMem.getString(JAVA_HOME_KEY));
			newContainer.setJavaOptions(Arrays.asList(containerMem.getString(JAVA_OPTIONS_KEY).split(" ")));
			for (IMemento commandMem : containerMem.getChildren()) {
				newContainer.addChild(new TreeObject(commandMem
						.getString(COMMAND_NAME_KEY), commandMem
						.getString(COMMAND_VALUE_KEY)));
			}
			root.addChild(newContainer);
		}
	}
}
