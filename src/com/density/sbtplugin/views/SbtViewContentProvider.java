package com.density.sbtplugin.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.part.ViewPart;

class SbtViewContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {
	private ViewPart sbtView;
	private RootNode root;

	public SbtViewContentProvider(ViewPart view){
		super();
		initialize();
		sbtView = view;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
		root = null;
		sbtView = null;
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(sbtView.getViewSite())) {
			if (root == null)
				initialize();
			return root.getProjects();
		}
		return getChildren(parent);
	}

	public Object getParent(Object child) {
		if (child instanceof CommandNode) {
			return ((CommandNode) child).getParent();
		}
		if(child instanceof ProjectNode) {
			return root;
		}
		return null;
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof ProjectNode) {
			return ((ProjectNode) parent).getChildren();
		}
		if (parent instanceof RootNode){
			return ((RootNode)parent).getProjects();
		}
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof ProjectNode)
			return ((ProjectNode) parent).hasChildren();
		if(parent instanceof RootNode)
			return ((RootNode) parent).hasProjects();
		return false;
	}

	public RootNode getRoot() {
		if (root == null)
			initialize();
		return root;
	}

	public void setRoot(RootNode root) {
		this.root = root;
	}

	/*
	 * We will set up a dummy model to initialize tree heararchy. In a real
	 * code, you will connect to a real model and expose its hierarchy.
	 */
	private void initialize() {
		root = new RootNode();
	}
	
	public static class RootNode {
		private List<ProjectNode> projects;

		public RootNode() {
			super();
			projects = new ArrayList<ProjectNode>();
		}

		public RootNode(List<ProjectNode> projects) {
			super();
			this.projects = projects;
		}

		public ProjectNode[] getProjects() {
			return projects.toArray(new ProjectNode[projects.size()]);
		}

		public void setProjects(List<ProjectNode> projects) {
			this.projects = projects;
		}
		
		public void addProject(ProjectNode project){
			projects.add(project);
		}
		
		public void removeProject(ProjectNode project){
			projects.remove(project);
		}
		
		public void removeProject(String projectName){
			ProjectNode project = getProject(projectName);
			if(project != null){
				removeProject(project);
			}
		}
		
		public ProjectNode getProject(String projectName){
			for(ProjectNode project: projects){
				if(project.getName().equals(projectName)) return project;
			}
			return null;
		}
		public boolean hasProjects(){
			return projects.size() > 0;
		}
		public boolean hasProject(String projectPath){
			return getProject(projectPath)!=null;
		}
	}
}
