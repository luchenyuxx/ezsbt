package com.density.sbtplugin.views;

import org.eclipse.ui.IMemento;

public class StateMemory {
	static public String CONTAINER_TYPE = "container";
	static public String COMMAND_BUTTON_TYPE = "command";
	static public String CONTAINER_NAME_KEY = "name";
	static public String COMMAND_NAME_KEY = "name";
	static public String COMMAND_VALUE_KEY = "sbtcommand";

	static public void rememberState(SbtViewContentProvider viewContentProvider,
			IMemento rootMem) {
		TreeParent root = viewContentProvider.getInvisibleRoot();
		for (TreeObject containerNode : root.getChildren()) {
			TreeParent container = (TreeParent) containerNode;
			IMemento containerMem = rootMem.createChild(CONTAINER_TYPE);
			containerMem.putString(CONTAINER_NAME_KEY, container.getName());
			for (TreeObject command : container.getChildren()) {
				IMemento commandMem = containerMem
						.createChild(COMMAND_BUTTON_TYPE);
				commandMem.putString(COMMAND_NAME_KEY, command.getName());
				commandMem
						.putString(COMMAND_VALUE_KEY, command.getSbtCommand());
			}
		}
	}

	static public void remindState(SbtViewContentProvider viewContentProvider,
			IMemento rootMem) {
		TreeParent root = viewContentProvider.getInvisibleRoot();
		for (IMemento containerMem : rootMem.getChildren()) {
			TreeParent newContainer = new TreeParent(
					containerMem.getString(CONTAINER_NAME_KEY));
			for (IMemento commandMem : containerMem.getChildren()) {
				newContainer.addChild(new TreeObject(commandMem
						.getString(COMMAND_NAME_KEY), commandMem
						.getString(COMMAND_VALUE_KEY)));
			}
			root.addChild(newContainer);
		}
	}
}
