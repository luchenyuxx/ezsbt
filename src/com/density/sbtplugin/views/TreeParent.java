package com.density.sbtplugin.views;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class TreeParent extends TreeObject {
	private String javaHome;
	private ArrayList<TreeObject> children;

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

	public IContainer getContainer() {
		IPath location = new Path(name);
		return ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(location);
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

	public String getJavaHome() {
		return javaHome;
	}
	public void setJavaHome(String javaHome) {
		this.javaHome = javaHome;
	}
	public boolean hasChildren() {
		return children.size() > 0;
	}
}
