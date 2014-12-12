package com.density.sbtplugin.views;

import java.io.IOException;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsolePrinter {
	private MessageConsole console;
	private MessageConsoleStream errorStream;
	private MessageConsoleStream infoStream;
	private MessageConsoleStream warningStream;
	private MessageConsoleStream successStream;
	private static final String ERROR_PATTERN = "[error]";
	private static final String INFO_PATTERN = "[info]";
	private static final String SUCCESS_PATTERN = "[success]";
	private static final String WARN_PATTERN = "[warn]";

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

	public MessageConsole getConsole() {
		return console;
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
		String newLine = line.substring(INFO_PATTERN.length());
		infoStream.println(newLine);
	}

	protected void printErrorLine(String line) {
		infoStream.print("[");
		errorStream.print("error");
		infoStream.print("]");
		String newLine = line.substring(ERROR_PATTERN.length());
		errorStream.println(newLine);
	}

	protected void printSuccessLine(String line) {
		infoStream.print("[");
		successStream.print("success");
		infoStream.print("]");
		String newLine = line.substring(SUCCESS_PATTERN.length());
		infoStream.println(newLine);
	}

	protected void printWarningLine(String line) {
		infoStream.print("[");
		warningStream.print("warn");
		infoStream.print("]");
		String newLine = line.substring(WARN_PATTERN.length());
		infoStream.println(newLine);
	}

	protected boolean isErrorLine(String line) {
		return line.startsWith(ERROR_PATTERN);
	}

	protected boolean isInfoLine(String line) {
		return line.startsWith(INFO_PATTERN);
	}

	protected boolean isSuccessLine(String line) {
		return line.startsWith(SUCCESS_PATTERN);
	}

	protected boolean isWarningLine(String line) {
		return line.startsWith(WARN_PATTERN);
	}
	public void dispose() throws IOException{
		errorStream.close();
		infoStream.close();
		successStream.close();
		warningStream.close();
	}
}
