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

public class CommandNode {
	private String name;
	private String sbtCommand;
	private ProjectNode parent;

	public CommandNode(String name) {
		this.name = name;
	}

	public CommandNode(String name, String sbtCommand) {
		super();
		this.name = name;
		this.sbtCommand = sbtCommand;
	}

	public String getSbtCommand() {
		return sbtCommand;
	}

	public void setSbtCommand(String sbtCommand) {
		this.sbtCommand = sbtCommand;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setParent(ProjectNode parent) {
		this.parent = parent;
	}

	public ProjectNode getParent() {
		return parent;
	}

	public String toString() {
		return name.substring(name.lastIndexOf("/")+1);
	}

}
