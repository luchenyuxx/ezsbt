package com.density.sbtplugin.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.part.ViewPart;

class SbtViewContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {
	private TreeParent invisibleRoot;
	private ViewPart sbtView;

	public SbtViewContentProvider(ViewPart view){
		super();
		initialize();
		sbtView = view;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
		invisibleRoot = null;
		sbtView = null;
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(sbtView.getViewSite())) {
			if (invisibleRoot == null)
				initialize();
			return getChildren(invisibleRoot);
		}
		return getChildren(parent);
	}

	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject) child).getParent();
		}
		return null;
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent) parent).getChildren();
		}
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent) parent).hasChildren();
		return false;
	}

	public TreeParent getInvisibleRoot() {
		if (invisibleRoot == null)
			initialize();
		return invisibleRoot;
	}

	public void setInvisibleRoot(TreeParent invisibleRoot) {
		this.invisibleRoot = invisibleRoot;
	}

	/*
	 * We will set up a dummy model to initialize tree heararchy. In a real
	 * code, you will connect to a real model and expose its hierarchy.
	 */
	private void initialize() {
		invisibleRoot = new TreeParent("");
	}
}
