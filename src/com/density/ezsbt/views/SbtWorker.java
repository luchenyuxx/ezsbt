/* Copyright 2015 Density Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.density.ezsbt.views;

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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import com.density.ezsbt.util.PluginConstants;

public class SbtWorker {
	protected ViewPart view;
	protected String projectPath;
	protected IContainer container;
	protected ProjectNode node;
	protected ProcessBuilder processBuilder;
	protected ConsolePrinter consolePrinter;

	protected Process sbtProcess;
	protected Thread printThread;
	protected PrintWriter processWriter;
	protected volatile Scanner scanner;

	public final static String SCANNER_DELIMITER = "\\n|\\r\\n|\\(i\\)gnore\\?";

	public SbtWorker(ProjectNode node, ViewPart view) {
		this.view = view;
		this.node = node;
		this.projectPath = node.getName();
		this.container = node.getContainer();
		consolePrinter = ConsolePrinterManager.getPrinter(findConsole(
				projectPath, container));
		processBuilder = new ProcessBuilder("");
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
			scanner.close();
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
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			commands.add(getJavaExe());
			commands.addAll(node.getJavaOptions());
			commands.add("-Djline.terminal=jline.UnsupportedTerminal");
			commands.add("-cp");
			commands.add(getSbtLaunchPath());
			commands.add("xsbt.boot.Boot");
		} else {
			commands.add("java");
			commands.addAll(node.getJavaOptions());
			commands.add("-jar");
			commands.add(getSbtLaunchPath());
		}
		return commands.toArray(new String[0]);
	}
	
	protected String getJavaExe(){
		return node.getJavaHome() + "\\bin\\java.exe";
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
		myConsole.setConsoleWidth(200);
		myConsole.addPatternMatchListener(new FileLinkPatternMatchListener(
				container));
		myConsole.addPatternMatchListener(new ActionPatternMatchListener(
				"\\(r\\)etry", "r", node));
		myConsole.addPatternMatchListener(new ActionPatternMatchListener(
				"\\(i\\)gnore", "i", node));
		myConsole.addPatternMatchListener(new ActionPatternMatchListener(
				"\\(q\\)uit", "q", node));
		myConsole.addPatternMatchListener(new ActionPatternMatchListener(
				"\\(l\\)ast", "l",node));
		return myConsole;
	}

	protected void revealConsoleView(IConsole console) {
		try {
			IWorkbenchPage page = view.getSite().getPage();
			IConsoleView consoleView = (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
			consoleView.display(console);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
