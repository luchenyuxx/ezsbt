package com.density.sbtplugin.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import com.density.sbtplugin.util.PluginConstants;


public class SbtView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.density.sbtplugin.views.SbtView";
	private static final String MENU_TEXT = "#PopupMenu";

	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private SbtViewContentProvider viewContentProvider = new SbtViewContentProvider(
			this);
	private Action removeAllAction;
	private Action stopAllAction;
	private Action removeProjectAction;
	private Action doubleClickAction;
	private Action editCommandAction;
	private Action addCommandAction;
	private Action removeCommandAction;
	private Action restartSbtAction;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			return obj.toString();
		}

		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof TreeParent)
				imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(imageKey);
		}
	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public SbtView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(viewContentProvider);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(viewer.getControl(), PluginConstants.CONTROL_ID);
		makeActions();
		addKeyListener();
		addDragAndDrop();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	protected void addKeyListener() {
		viewer.getTree().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == PluginConstants.DELETE_KEY_CODE) {
					Object selectedObject = getSelectedObject();
					if (selectedObject.getClass().equals(TreeParent.class)) {
						remove((TreeParent) selectedObject);
					} else if (selectedObject.getClass().equals(
							TreeObject.class)) {
						TreeObject treeObject = (TreeObject) selectedObject;
						treeObject.getParent().removeChild(treeObject);
						viewer.refresh();
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
	}

	protected void addDragAndDrop() {
		int ops = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transfers = { FileTransfer.getInstance(),
				TextTransfer.getInstance(),
				LocalSelectionTransfer.getTransfer() };
		viewer.addDropSupport(ops | DND.DROP_DEFAULT, transfers,
				new SbtViewerDropAdapter(viewer));
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager(MENU_TEXT);
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SbtView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(removeAllAction);
		manager.add(new Separator());
		manager.add(stopAllAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		Object obj = getSelectedObject();
		if (obj.getClass().equals(TreeParent.class)) {
			manager.add(removeProjectAction);
			manager.add(addCommandAction);
			manager.add(restartSbtAction);
		}
		if (obj.getClass().equals(TreeObject.class)) {
			TreeObject target = (TreeObject) obj;
			if (!target.getName().equals(PluginConstants.START_SBT_NAME)) {
				manager.add(editCommandAction);
				manager.add(removeCommandAction);
			}
		}
		// manager.add(removeAllAction);
		// manager.add(stopAllAction);
		// manager.add(new Separator());
		// drillDownAdapter.addNavigationActions(manager);
		// manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(removeAllAction);
		manager.add(stopAllAction);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		makeRemoveAllAction();
		makeStopAllAction();
		makeDoubleClickAction();
		makeRemoveProjectAction();
		makeEditCommandAction();
		makeAddCommandAction();
		makeRemoveCommandAction();
		makeRestartSbtAction();
	}
	protected void makeRestartSbtAction(){
		restartSbtAction = new Action() {
			public void run(){
				doRestartSbtAction();
			}
		};
		restartSbtAction.setText("Restart Sbt");
	}

	protected void makeAddCommandAction() {
		addCommandAction = new Action() {
			public void run() {
				doAddCommandAction();
			}
		};
		addCommandAction.setText("Add command");
		addCommandAction.setToolTipText("Add command button");
		addCommandAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
	}

	protected void makeRemoveCommandAction() {
		removeCommandAction = new Action() {
			public void run() {
				doRemoveCommandAction();
			}
		};
		removeCommandAction.setText("Remove command");
		removeCommandAction.setToolTipText("Remove command button");
		removeCommandAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));
	}

	protected void makeEditCommandAction() {
		editCommandAction = new Action() {
			public void run() {
				doEditCommandAction();
			}
		};
		editCommandAction.setText("Edit");
		editCommandAction.setToolTipText("edit command button");
	}

	protected void makeRemoveAllAction() {
		removeAllAction = new Action() {
			public void run() {
				closeAllSbt();
				cleanView();
			}
		};
		removeAllAction.setText("Remove all");
		removeAllAction
				.setToolTipText("close all sbt processes and clear view");
		removeAllAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVEALL));
	}

	protected void makeStopAllAction() {
		stopAllAction = new Action() {
			public void run() {
				closeAllSbt();
			}
		};
		stopAllAction.setText("Stop all");
		stopAllAction.setToolTipText("close all sbt processes");
		stopAllAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_STOP));
	}

	protected void makeDoubleClickAction() {
		doubleClickAction = new Action() {
			public void run() {
				doDoubleClickAction();
			}
		};
	}

	protected void makeRemoveProjectAction() {
		removeProjectAction = new Action() {
			public void run() {
				doRemoveProjectAction();
			}
		};
		removeProjectAction.setText("Remove");
		removeProjectAction
				.setToolTipText("close project's sbt and remove from view");
		removeProjectAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));
	}
	protected void doRestartSbtAction(){
		TreeParent container = (TreeParent) getSelectedObject();
		restartSbt(container);
	}

	protected void doRemoveCommandAction() {
		TreeObject command = (TreeObject) getSelectedObject();
		command.getParent().removeChild(command);
		viewer.refresh();
	}

	protected void doAddCommandAction() {
		TreeParent container = (TreeParent) getSelectedObject();
		AddCommandDialog dialog = new AddCommandDialog(viewer.getControl()
				.getShell(), container, viewer);
		dialog.create();
		dialog.open();
	}

	protected void doEditCommandAction() {
		Object obj = getSelectedObject();
		TreeObject target = (TreeObject) obj;
		EditCommandDialog dialog = new EditCommandDialog(viewer.getControl()
				.getShell(), target, viewer);
		dialog.create();
		dialog.open();
	}

	protected void doRemoveProjectAction() {
		Object obj = getSelectedObject();
		if (obj.getClass().equals(TreeParent.class)) {
			TreeParent selectedNode = (TreeParent) obj;
			remove(selectedNode);
		}
	}

	protected void doDoubleClickAction() {
		Object obj = getSelectedObject();
		if (obj.getClass().equals(TreeObject.class)) {
			TreeObject selectedNode = (TreeObject) obj;
			TreeParent parent = selectedNode.getParent();
			if (selectedNode.getSbtCommand().equals(
					PluginConstants.EXIT_COMMAND)) {
				exitSbt(parent);
			}else {
				writeCommand(parent, selectedNode.getSbtCommand());
			}
		}
	}

	protected Object getSelectedObject() {
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		return obj;
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	protected void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Sbt View", message);
	}

	protected void exitSbt(TreeParent node) {
		SbtWorkerManager.getSbtWorker(node, this).stopSbt();
	}

	protected void restartSbt(TreeParent node) {
		SbtWorkerManager.getSbtWorker(node, this).restartSbt();
	}

	protected void writeCommand(TreeParent node, String command) {
		SbtWorkerManager.getSbtWorker(node, this).write(command);
	}

	protected void closeAllSbt() {
		SbtWorkerManager.closeAllSbtWorker();
	}

	protected void closeSbt(TreeParent project) {
		SbtWorkerManager.closeSbtWorker(project);
	}

	protected void cleanView() {
		SbtViewContentProvider contentProvider = (SbtViewContentProvider) viewer
				.getContentProvider();
		contentProvider.setInvisibleRoot(new TreeParent(""));
		viewer.refresh();
	}

	protected void remove(TreeParent project) {
		TreeParent root = ((SbtViewContentProvider) viewer.getContentProvider())
				.getInvisibleRoot();
		closeSbt(project);
		root.removeChild(project);
		viewer.refresh();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		StateMemory.rememberState(viewContentProvider, memento);
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		try {
			StateMemory.remindState(viewContentProvider, memento);
		} catch (Exception e) {
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		closeAllSbt();
	}
}