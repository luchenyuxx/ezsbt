package com.density.sbtplugin.views;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class ActionPatternMatchListener implements IPatternMatchListener {
	private TextConsole console;
	private String pattern;
	private String command;
	private final static String LINE_QUALIFIER = "\\(r\\)etry, \\(q\\)uit, \\(l\\)ast, or \\(i\\)gnore\\?";

	public ActionPatternMatchListener(String pattern, String command) {
		this.pattern = pattern;
		this.command = command;
	}

	@Override
	public void connect(TextConsole console) {
		this.console = console;
	}

	@Override
	public void disconnect() {
		console = null;
	}

	@Override
	public void matchFound(PatternMatchEvent event) {
		int offset = event.getOffset();
		int length = event.getLength();
		addActionLink(offset, length);
	}

	@Override
	public String getPattern() {
		return pattern;
	}

	@Override
	public int getCompilerFlags() {
		return 0;
	}

	@Override
	public String getLineQualifier() {
		return LINE_QUALIFIER;
	}

	protected void addActionLink(int linkOffset, int linkLength) {
		IHyperlink link = new IHyperlink() {
			@Override
			public void linkExited() {
			}

			@Override
			public void linkEntered() {
			}

			@Override
			public void linkActivated() {
				SbtWorker sbtWorker = SbtWorkerManager.getSbtWorker(console
						.getName());
				sbtWorker.write(command);
				if(command.equals("q")) sbtWorker.stopSbt();
			}
		};
		try {
			console.addHyperlink(link, linkOffset, linkLength);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
