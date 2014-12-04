package com.density.sbtplugin.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class SbtView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.density.sbtplugin.views.SbtView";

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

	private HashMap<String, PrintWriter> processWriterMap = new HashMap<String, PrintWriter>();

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
				.setHelp(viewer.getControl(), "sbt-plugin.viewer");
		makeActions();
		addDragAndDrop();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
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
		MenuManager menuMgr = new MenuManager("#PopupMenu");
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
				closeAllProcess();
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
				closeAllProcess();
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
			String path = parent.getName();
			if (selectedNode.getName().equals(PluginConstants.START_SBT_NAME)) {
				startSbt(parent);
			} else if (selectedNode.getSbtCommand().equals(
					PluginConstants.EXIT_COMMAND)) {
				exitSbt(path);
			} else if (selectedNode.getSbtCommand().equals(
					PluginConstants.RESTART_COMMAND)) {
				restartSbt(path);
			} else {
				writeCommand(path, selectedNode.getSbtCommand());
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

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Sbt View", message);
	}

	protected void startSbt(TreeParent parent) {
		String path = parent.getName();
		if (processWriterMap.keySet().contains(path)) {
			showMessage("sbt for " + path + " is running");
		} else {
			try {
				ProcessBuilder processBuilder = new ProcessBuilder("java",
						"-Xms1024m", "-Xmx1024m",
						"-XX:ReservedCodeCacheSize=128m",
						"-Dsbt.log.noformat=true", "-XX:MaxPermSize=256m",
						"-jar", getSbtLaunchPath()).directory(new File(path));
				processBuilder.environment().put("JAVA_HOME", getJavaHome());
				Process sbtProcess = processBuilder.start();
				final InputStream inStream = sbtProcess.getInputStream();
				final IContainer container = parent.getContainer();
				final String consoleName = path;
				new Thread(new Runnable() {
					public void run() {
						MessageConsole myConsole = findConsole(consoleName,
								container);
						ConsolePrinter printer = new ConsolePrinter(myConsole);
						printer.println("Starting...");
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(inStream));
						Scanner scan = new Scanner(reader);
						while (scan.hasNextLine()) {
							printer.println(scan.nextLine());
						}
					}
				}).start();
				OutputStream outStream = sbtProcess.getOutputStream();
				PrintWriter pWriter = new PrintWriter(outStream);
				processWriterMap.put(path, pWriter);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void printConsoleLine(String line) {

	}

	protected String getJavaHome() {
		String java_home = null;
		if (System.getProperty("os.name").toLowerCase().contains("win"))
			java_home = new File(System.getProperty("java.home"))
					.getParentFile().getAbsolutePath();
		else
			java_home = System.getProperty("java.home");
		return java_home;
	}

	protected String getSbtLaunchPath() {
		Bundle bundle = Platform.getBundle(PluginConstants.BUNDLE_NAME);
		URL url = bundle.getEntry("resources/sbt-launch.jar");
		String result = null;
		try {
			result = FileLocator.resolve(url).getPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	protected void sbtCompile(String path) {
		writeCommand(path, PluginConstants.COMPILE_COMMAND);
	}

	protected void sbtClean(String path) {
		writeCommand(path, PluginConstants.CLEAN_COMMAND);
	}

	protected void exitSbt(String path) {
		writeCommand(path, PluginConstants.EXIT_COMMAND);
		PrintWriter writer = processWriterMap.get(path);
		if (writer != null) {
			writer.close();
			processWriterMap.remove(path);
		}
	}

	protected void sbtRun(String path) {
		writeCommand(path, PluginConstants.RUN_COMMAND);
	}

	protected void restartSbt(String path) {
		exitSbt(path);
		TreeParent treeParent = ((TreeObject) getSelectedObject()).getParent();
		startSbt(treeParent);
	}

	protected void writeCommand(String path, String command) {
		PrintWriter writer = processWriterMap.get(path);
		if (writer != null) {
			writer.println(command);
			writer.flush();
			revealConsole(findConsole(path, null));
		} else {
			TreeParent treeParent = ((TreeObject) getSelectedObject())
					.getParent();
			startSbt(treeParent);
			writeCommand(path, command);
		}
	}

	protected void closeProcess(String path) {
		PrintWriter writer = processWriterMap.get(path);
		if (writer != null) {
			writer.close();
			processWriterMap.remove(path);
		} else {
			showMessage("can't find sbt process on path " + path);
		}
	}

	protected void closeAllProcess() {
		for (String path : processWriterMap.keySet()) {
			closeProcess(path);
		}
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
		if (processWriterMap.keySet().contains(project.getName())) {
			closeProcess(project.getName());
		}
		root.removeChild(project);
		viewer.refresh();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private synchronized MessageConsole findConsole(String name,
			IContainer container) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		SbtPatternMatchListener sbtPatternMatchListener = new SbtPatternMatchListener(
				container);
		myConsole.addPatternMatchListener(sbtPatternMatchListener);
		return myConsole;
	}

	protected void revealConsole(IConsole console) {
		try {
			IWorkbenchPage page = getSite().getPage();
			IConsoleView consoleView = (IConsoleView) page
					.showView(IConsoleConstants.ID_CONSOLE_VIEW);
			consoleView.display(console);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
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
		closeAllProcess();
	}
}