package com.density.sbtplugin.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Scanner;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
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
	protected ProcessBuilder processBuilder;
	protected ConsolePrinter consolePrinter;

	protected Process sbtProcess;
	protected Thread printThread;
	protected PrintWriter processWriter;
	protected volatile Scanner scanner;

	protected boolean running = false;

	public SbtWorker(String projectPath, IContainer container, ViewPart view) {
		this.view = view;
		this.projectPath = projectPath;
		this.container = container;
		processBuilder = new ProcessBuilder(getLaunchCommand())
				.directory(new File(projectPath));
		processBuilder.environment().put("JAVA_HOME", getJavaHome());
		consolePrinter = ConsolePrinterManager.getPrinter(findConsole(
				projectPath, container));
	}

	protected void startSbt() {
		try {
			sbtProcess = processBuilder.start();
			linkInputStream(sbtProcess);
			linkOutputStream(sbtProcess);
			startPrintThread();
			running = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopSbt() {
		processWriter.close();
		sbtProcess = null;
		printThread = null;
		processWriter = null;
		scanner = null;
		running = false;
	}

	public void restartSbt() {
		if (running) {
			stopSbt();
		}
		startSbt();
	}

	public void write(String input) {
		if (running) {
			processWriter.println(input);
			revealConsole(findConsole(projectPath, container));
		} else {
			startSbt();
			write(input);
		}
	}
	protected void startPrintThread(){
		printThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Thread thisThread = Thread.currentThread();
				while (scanner.hasNextLine() && thisThread==printThread) {
					consolePrinter.println(scanner.nextLine());
				}
			}
		});
		printThread.start();
	}

	protected void linkInputStream(Process process) {
		consolePrinter.println("Starting...");
		scanner = new Scanner(new BufferedReader(new InputStreamReader(
				process.getInputStream())));
	}

	protected void linkOutputStream(Process process) {
		processWriter = new PrintWriter(process.getOutputStream(), true);
	}

	protected String[] getLaunchCommand() {
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			return new String[] { "java", "-Xmx512m",
					"-Djline.terminal=jline.UnsupportedTerminal",
					"-XX:ReservedCodeCacheSize=128m",
					"-Dsbt.log.noformat=true", "-XX:MaxPermSize=256m", "-cp",
					getSbtLaunchPath(), "xsbt.boot.Boot" };
		} else
			return new String[] { "java", "-Xmx512m",
					"-XX:ReservedCodeCacheSize=128m",
					"-Dsbt.log.noformat=true", "-XX:MaxPermSize=256m", "-jar",
					getSbtLaunchPath() };
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

	protected String getJavaHome() {
		String java_home = null;
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			java_home = System.getenv("JAVA_HOME");
		} else
			java_home = System.getProperty("java.home");
		return java_home;
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
			IWorkbenchPage page = view.getSite().getPage();
			IConsoleView consoleView = (IConsoleView) page
					.showView(IConsoleConstants.ID_CONSOLE_VIEW);
			consoleView.display(console);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
