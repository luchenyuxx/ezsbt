package com.density.sbtplugin.views;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.density.sbtplugin.util.PluginConstants;
import com.density.sbtplugin.util.SbtPlugin;

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
	private static final String SPECIAL_LINE_PATTERN = "(r)etry, (q)uit, (l)ast,";
	private static final String SPECIAL_LINE_SUFFIX = "(i)gnore?";

	private MessageConsoleStream streamInUse;

	public ConsolePrinter(MessageConsole console) {
		this.console = console;
		buildConsoleStreams();
	}

	protected void buildConsoleStreams() {
		errorStream = getErrorStream(console);
		infoStream = getInfoStream(console);
		warningStream = getWarningStream(console);
		successStream = getSuccessStream(console);
		streamInUse = infoStream;
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

	public void print(String aString) {
		if (isErrorString(aString)) {
			printErrorString();
			streamInUse = errorStream;
		} else if (isSuccessString(aString)) {
			printSuccessString();
			streamInUse = infoStream;
		} else if (isWarningString(aString)) {
			printWarningString();
			streamInUse = infoStream;
		} else if (isInfoString(aString)) {
			printInfoString();
			streamInUse = infoStream;
		} else {
			streamInUse.print(aString);
		}
		streamInUse.print(" ");
	}

	public void println(String line) {
		String newLine = checkSpecialLine(line);
		if (isErrorLine(newLine)) {
			printErrorLine(newLine);
		} else if (isInfoLine(newLine)) {
			printInfoLine(newLine);
		} else if (isSuccessLine(newLine)) {
			printSuccessLine(newLine);
		} else if (isWarningLine(newLine)) {
			printWarningLine(newLine);
		} else {
			infoStream.println(newLine);
		}
	}

	protected String checkSpecialLine(String line) {
		String result = line;
		if (line.contains(SPECIAL_LINE_PATTERN)) {
			result = line + SPECIAL_LINE_SUFFIX;
		}
		return result;
	}

	protected void printInfoLine(String line) {
		String newLine = line.substring(INFO_PATTERN.length());
		if (isHideResolveMessage() && newLine.startsWith(" Resolving")) {
		} else {
			printInfoString();
			infoStream.println(newLine);
		}
	}

	protected void printErrorLine(String line) {
		printErrorString();
		String newLine = line.substring(ERROR_PATTERN.length());
		errorStream.println(newLine);
	}

	protected void printSuccessLine(String line) {
		printSuccessString();
		String newLine = line.substring(SUCCESS_PATTERN.length());
		infoStream.println(newLine);
	}

	protected void printWarningLine(String line) {
		printWarningString();
		String newLine = line.substring(WARN_PATTERN.length());
		infoStream.println(newLine);
	}

	protected void printInfoString() {
		infoStream.print("[info]");
	}

	protected void printErrorString() {
		infoStream.print("[");
		errorStream.print("error");
		infoStream.print("]");
	}

	protected void printSuccessString() {
		infoStream.print("[");
		successStream.print("success");
		infoStream.print("]");
	}

	protected void printWarningString() {
		infoStream.print("[");
		warningStream.print("warn");
		infoStream.print("]");
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

	protected boolean isErrorString(String aString) {
		return aString.startsWith(ERROR_PATTERN);
	}

	protected boolean isInfoString(String aString) {
		return aString.startsWith(INFO_PATTERN);
	}

	protected boolean isSuccessString(String aString) {
		return aString.startsWith(SUCCESS_PATTERN);
	}

	protected boolean isWarningString(String aString) {
		return aString.startsWith(WARN_PATTERN);
	}
	
	protected boolean isHideResolveMessage(){
		return SbtPlugin.getInstance().getPreferenceStore().getBoolean(PluginConstants.HIDE_RESOLVE_KEY);
	}
}
