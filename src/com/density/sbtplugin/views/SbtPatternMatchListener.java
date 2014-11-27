package com.density.sbtplugin.views;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class SbtPatternMatchListener implements IPatternMatchListener {
	private TextConsole console;
	private IContainer container;
	public SbtPatternMatchListener(IContainer container){
		this.container = container;
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
		try {
			int offset = event.getOffset();
			int length = event.getLength();
			String matchedString = console.getDocument().get(offset, length);
			addFileLink(matchedString,offset,length);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getPattern() {
		return "\\S+\\w+\\.(java|scala):[0-9]+";
	}

	@Override
	public int getCompilerFlags() {
		return 0;
	}

	@Override
	public String getLineQualifier() {
		return ".*:[0-9]+:.*";
	}
	protected void addFileLink(String line,int linkOffset, int linkLength) {
		String[] splitedLine = line.split(":");
		String filePath = splitedLine[0].trim();
		String relativeFilePath = filePath.replace(container.getRawLocation().toString(), "");
		int fileLineNumber = Integer.parseInt(splitedLine[1].trim());
		IPath location = new Path(relativeFilePath);
		IFile file = container.getFile(location);
		FileLink fileLink = new FileLink(file, null, -1, -1, fileLineNumber);
		try {
			console.addHyperlink(fileLink, linkOffset, linkLength);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
