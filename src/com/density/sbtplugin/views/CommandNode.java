package com.density.sbtplugin.views;

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
