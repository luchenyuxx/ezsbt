package com.density.sbtplugin.views;

import org.eclipse.core.runtime.IAdaptable;

public class TreeObject implements IAdaptable {
	private String name;
	private String sbtCommand;
	private TreeParent parent;

	public TreeObject(String name) {
		this.name = name;
	}

	public TreeObject(String name, String sbtCommand) {
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

	public void setParent(TreeParent parent) {
		this.parent = parent;
	}

	public TreeParent getParent() {
		return parent;
	}

	public String toString() {
		return getName();
	}

	public Object getAdapter(Class key) {
		return null;
	}
}
