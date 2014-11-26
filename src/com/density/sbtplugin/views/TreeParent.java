package com.density.sbtplugin.views;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;

public class TreeParent extends TreeObject {
	private ArrayList<TreeObject> children;
	protected IProject project;

	public boolean hasChild(String childName){
		for(TreeObject child:children){
			if(child.getName().equals(childName)) return true;
		}
		return false;
	}
	public TreeParent(String name) {
		super(name);
		children = new ArrayList<TreeObject>();
	}
	public TreeParent(String name, IProject project){
		super(name);
		children = new ArrayList<TreeObject>();
		this.project = project;
	}

	public IProject getProject() {
		return project;
	}
	public void setProject(IProject project) {
		this.project = project;
	}
	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(TreeObject child) {
		children.remove(child);
		child.setParent(null);
	}
	public TreeObject getChild(String childName){
		for(TreeObject child:children){
			if(child.getName().equals(childName)) return child;
		}
		return null;
	}
	public void removeChild(String childName){
		TreeObject child = getChild(childName);
		if(child!=null){
			removeChild(child);
		}
	}

	public TreeObject[] getChildren() {
		return (TreeObject[]) children.toArray(new TreeObject[children
				.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}
}
