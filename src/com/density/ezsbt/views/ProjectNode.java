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

import java.util.ArrayList;

import com.density.ezsbt.util.CommandsConvertor;
import com.density.ezsbt.util.PluginConstants;
import com.density.ezsbt.views.StateMemory;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IMemento;

public class ProjectNode {
	protected String name;
	protected String projectId;
	protected String javaHome;
	protected List<String> javaOptions;
	protected ArrayList<CommandNode> children;

	public ProjectNode(String name, String projectId, IPreferenceStore store) {
		this(name, projectId);
		String[] commandPairs = CommandsConvertor.stringToArray(store
				.getString(PluginConstants.COMMANDS_NAME_KEY));
		for (String commandPair : commandPairs) {
			CommandNode commandObject = new CommandNode(
					CommandsConvertor.keyOf(commandPair),
					CommandsConvertor.valueOf(commandPair));
			this.addChild(commandObject);
		}
		this.setJavaHome(store.getString(PluginConstants.JAVA_HOME_KEY));
		this.setJavaOptions(Arrays.asList(store.getString(
				PluginConstants.JAVA_OPTIONS_KEY).split(" ")));
	}

	public ProjectNode(IMemento projectMem) {
		this(projectMem.getString(StateMemory.CONTAINER_NAME_KEY), projectMem
				.getString(StateMemory.PROJECT_ID_KEY));
		this.setJavaHome(projectMem.getString(StateMemory.JAVA_HOME_KEY));
		this.setJavaOptions(Arrays.asList(projectMem.getString(
				StateMemory.JAVA_OPTIONS_KEY).split(" ")));
		for (IMemento commandMem : projectMem.getChildren()) {
			this.addChild(new CommandNode(commandMem
					.getString(StateMemory.COMMAND_NAME_KEY), commandMem
					.getString(StateMemory.COMMAND_VALUE_KEY)));
		}
	}

	public ProjectNode(String name, String projectId) {
		this.name = name;
		this.projectId = projectId;
		children = new ArrayList<CommandNode>();
	}

	public IContainer getContainer() {
		IPath location = new Path(name);
		return ResourcesPlugin.getWorkspace().getRoot()
				.getContainerForLocation(location);
	}

	public void addChild(CommandNode child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(CommandNode child) {
		children.remove(child);
		child.setParent(null);
	}

	public CommandNode getChild(String childName) {
		for (CommandNode child : children) {
			if (child.getName().equals(childName))
				return child;
		}
		return null;
	}

	public void removeChild(String childName) {
		CommandNode child = getChild(childName);
		if (child != null) {
			removeChild(child);
		}
	}

	public CommandNode[] getChildren() {
		return (CommandNode[]) children
				.toArray(new CommandNode[children.size()]);
	}

	public String getJavaHome() {
		return javaHome;
	}

	public void setJavaHome(String javaHome) {
		this.javaHome = javaHome;
	}

	public List<String> getJavaOptions() {
		return javaOptions;
	}

	public void setJavaOptions(List<String> javaOptions) {
		this.javaOptions = javaOptions;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name.substring(name.lastIndexOf("/") + 1);
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public void fillMemory(IMemento projectMem) {
		projectMem.putString(StateMemory.CONTAINER_NAME_KEY, this.getName());
		projectMem.putString(StateMemory.JAVA_HOME_KEY, this.getJavaHome());
		projectMem.putString(StateMemory.JAVA_OPTIONS_KEY,
				StateMemory.optionsListToString(this.getJavaOptions()));
		projectMem.putString(StateMemory.PROJECT_ID_KEY, this.getProjectId());
		for (CommandNode command : this.getChildren()) {
			IMemento commandMem = projectMem
					.createChild(StateMemory.COMMAND_BUTTON_TYPE);
			commandMem.putString(StateMemory.COMMAND_NAME_KEY,
					command.getName());
			commandMem.putString(StateMemory.COMMAND_VALUE_KEY,
					command.getSbtCommand());
		}
	}

	public boolean hasChild(String childName) {
		return getChild(childName) != null;
	}
}
