package com.density.sbtplugin.views;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsolePrinter {
	private MessageConsole console;
	private MessageConsoleStream errorStream;
	private MessageConsoleStream infoStream;
	private MessageConsoleStream warningStream;
	private MessageConsoleStream successStream;

	public ConsolePrinter(MessageConsole console) {
		this.console = console;
		buildConsoleStreams();
	}

	protected void buildConsoleStreams() {
		errorStream = getErrorStream(console);
		infoStream = getInfoStream(console);
		warningStream = getWarningStream(console);
		successStream = getSuccessStream(console);
	}

	protected MessageConsoleStream getErrorStream(MessageConsole console) {
		MessageConsoleStream errorStream = console.newMessageStream();
		errorStream.setColor(new Color(null, 150, 0, 0));
		return errorStream;
	}

	protected MessageConsoleStream getInfoStream(MessageConsole console) {
		MessageConsoleStream infoStream = console.newMessageStream();
		infoStream.setColor(new Color(null, 0, 0, 0));
		return infoStream;
	}

	protected MessageConsoleStream getWarningStream(MessageConsole console) {
		MessageConsoleStream warningStream = console.newMessageStream();
		warningStream.setColor(new Color(null, 150, 150, 0));
		return warningStream;
	}

	protected MessageConsoleStream getSuccessStream(MessageConsole console) {
		MessageConsoleStream successStream = console.newMessageStream();
		successStream.setColor(new Color(null, 0, 150, 0));
		return successStream;
	}

	public void println(String line) {
		if (isErrorLine(line)) {
			printErrorLine(line);
		} else if (isInfoLine(line)) {
			printInfoLine(line);
		} else if (isSuccessLine(line)) {
			printSuccessLine(line);
		} else if (isWarningLine(line)) {
			printWarningLine(line);
		} else {
			infoStream.println(line);
		}
	}

	protected void printInfoLine(String line) {
		infoStream.print("[info]");
		String newLine = line.substring("[info]".length());
		infoStream.println(newLine);
	}

	protected void printErrorLine(String line) {
		infoStream.print("[");
		errorStream.print("error");
		infoStream.print("]");
		String newLine = line.substring("[error]".length());
		errorStream.println(newLine);
	}

	protected void printSuccessLine(String line) {
		infoStream.print("[");
		successStream.print("success");
		infoStream.print("]");
		String newLine = line.substring("[success]".length());
		infoStream.println(newLine);
	}

	protected void printWarningLine(String line) {
		infoStream.print("[");
		warningStream.print("warn");
		infoStream.print("]");
		String newLine = line.substring("[warn]".length());
		infoStream.println(newLine);
	}

	protected boolean isErrorLine(String line) {
		return line.startsWith("[error]");
	}

	protected boolean isInfoLine(String line) {
		return line.startsWith("[info]");
	}

	protected boolean isSuccessLine(String line) {
		return line.startsWith("[success]");
	}

	protected boolean isWarningLine(String line) {
		return line.startsWith("[warn]");
	}

}