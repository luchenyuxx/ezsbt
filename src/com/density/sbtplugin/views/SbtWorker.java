package com.density.sbtplugin.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import com.density.sbtplugin.util.PluginConstants;

public class SbtWorker {
	protected ViewPart view;
	protected String projectPath;
	protected IContainer container;
	protected TreeParent node;
	protected ProcessBuilder processBuilder;
	protected ConsolePrinter consolePrinter;

	protected IConsoleView consoleView;
	protected Action consoleViewStopAction;

	protected Process sbtProcess;
	protected Thread printThread;
	protected PrintWriter processWriter;
	protected volatile Scanner scanner;

	public final static String SCANNER_DELIMITER = "\\n|\\r\\n|\\(i\\)gnore\\?";

	public SbtWorker(TreeParent node, ViewPart view) {
		this.view = view;
		this.node = node;
		this.projectPath = node.getName();
		this.container = node.getContainer();
		consolePrinter = ConsolePrinterManager.getPrinter(findConsole(
				projectPath, container));
		processBuilder = new ProcessBuilder("");

		consoleViewStopAction = new Action() {
			public void run() {
				stopSbt();
				this.setEnabled(false);
			};
		};
		consoleViewStopAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_STOP));
		consoleViewStopAction.setDisabledImageDescriptor(PlatformUI
				.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_STOP_DISABLED));
		consoleViewStopAction.setToolTipText("Stop SBT process");
	}

	protected void startSbt() {
		try {
			processBuilder.command(getLaunchCommand()).directory(
					new File(projectPath));
			processBuilder.redirectErrorStream(true);
			processBuilder.environment().put("JAVA_HOME", node.getJavaHome());
			sbtProcess = processBuilder.start();
			linkInputStream(sbtProcess);
			linkOutputStream(sbtProcess);
			startPrintThread();
			startMonitorThread();
			consoleViewStopAction.setEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void makeEnvironment(ProcessBuilder processBuilder) {
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			processBuilder.environment().put("PATH", node.getJavaHome()+"\bin");
		} else {
			processBuilder.environment().put("JAVA_HOME", node.getJavaHome());
		}
	}

	// if running, close, if not, do nothing
	public void stopSbt() {
		if (!isProcessTerminated()) {
			processWriter.close();
			sbtProcess = null;
			printThread = null;
			processWriter = null;
			scanner = null;
		}
	}

	public void restartSbt() {
		stopSbt();
		startSbt();
	}

	public void write(String input) {
		MessageConsole console = findConsole(projectPath, container);
		console.clearConsole();
		if (isProcessTerminated())
			startSbt();
		processWriter.println(input);
		revealConsoleView(console);
	}

	protected void startMonitorThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					sbtProcess.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					consoleViewStopAction.setEnabled(false);
				}
			}
		}, "SbtMoniterThread").start();
	}

	protected void startPrintThread() {
		printThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Thread thisThread = Thread.currentThread();
				while (thisThread == printThread) {
					consolePrinter.println(scanner.next());
				}
			}
		}, "SbtPrintThread");
		printThread.start();
	}

	protected void linkInputStream(Process process) {
		consolePrinter.println("Starting...");
		scanner = new Scanner(new BufferedReader(new InputStreamReader(
				process.getInputStream()))).useDelimiter(SCANNER_DELIMITER);
	}

	protected void linkOutputStream(Process process) {
		processWriter = new PrintWriter(process.getOutputStream(), true);
	}

	protected String[] getLaunchCommand() {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add("java");
		commands.addAll(node.getJavaOptions());
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			commands.add("-Djline.terminal=jline.UnsupportedTerminal");
			commands.add("-cp");
			commands.add(getSbtLaunchPath());
			commands.add("xsbt.boot.Boot");
		} else {
			commands.add("-jar");
			commands.add(getSbtLaunchPath());
		}
		return commands.toArray(new String[0]);
	}

	protected String getSbtLaunchPath() {
		Bundle bundle = Platform.getBundle(PluginConstants.BUNDLE_NAME);
		URL url = bundle.getEntry(PluginConstants.SBT_JAR_PATH);
		String result = null;
		try {
			result = FileLocator.resolve(url).getPath();
			if (System.getProperty("os.name").toLowerCase().contains("win")) {
				result = result.substring(1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private synchronized MessageConsole findConsole(String name,
			IContainer container) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager consoleManager = plugin.getConsoleManager();
		IConsole[] existing = consoleManager.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		return createConsole(name, container, consoleManager);
	}

	private synchronized MessageConsole createConsole(String name,
			IContainer container, IConsoleManager consoleManager) {
		MessageConsole myConsole = new MessageConsole(name, null);
		consoleManager.addConsoles(new IConsole[] { myConsole });
		myConsole.addPatternMatchListener(new FileLinkPatternMatchListener(
				container));
		myConsole.addPatternMatchListener(new ActionPatternMatchListener(
				"\\(r\\)etry", "r"));
		myConsole.addPatternMatchListener(new ActionPatternMatchListener(
				"\\(i\\)gnore", "i"));
		myConsole.addPatternMatchListener(new ActionPatternMatchListener(
				"\\(q\\)uit", "q"));
		myConsole.addPatternMatchListener(new ActionPatternMatchListener(
				"\\(l\\)ast", "l"));
		return myConsole;
	}

	protected void revealConsoleView(IConsole console) {
		try {
			if (consoleView == null)
				createAndRevealConsoleView(console);
			view.getSite().getPage().activate(consoleView);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	protected void createAndRevealConsoleView(IConsole console)
			throws PartInitException {
		IWorkbenchPage page = view.getSite().getPage();
		consoleView = (IConsoleView) page.showView(
				IConsoleConstants.ID_CONSOLE_VIEW, projectPath,
				IWorkbenchPage.VIEW_CREATE);
		consoleView.getViewSite().getActionBars().getToolBarManager()
				.add(consoleViewStopAction);
		consoleView.display(console);
	}

	public boolean isWorking() {
		return !isProcessTerminated();
	}

	protected boolean isProcessTerminated() {
		try {
			sbtProcess.exitValue();
			return true;
		} catch (IllegalThreadStateException e) {
			return false;
		} catch (NullPointerException e) {
			return true;
		}
	}
}
