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

import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.IMemento;

import com.density.ezsbt.views.SbtViewContentProvider.RootNode;

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
