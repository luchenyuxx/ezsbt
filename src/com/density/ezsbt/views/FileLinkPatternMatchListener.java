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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class FileLinkPatternMatchListener implements IPatternMatchListener {
	private TextConsole console;
	private IContainer container;
	private final static String PATTERN = "\\S+\\w+\\.(java|scala):[0-9]+";
	private final static String LINE_QUALIFIER = ".*:[0-9]+:.*";
	private final static String SPLIT_SEPARATOR = ":";

	public FileLinkPatternMatchListener(IContainer container) {
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
			addFileLink(matchedString, offset, length);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getPattern() {
		return PATTERN;
	}

	@Override
	public int getCompilerFlags() {
		return 0;
	}

	@Override
	public String getLineQualifier() {
		return LINE_QUALIFIER;
	}

	protected void addFileLink(String line, int linkOffset, int linkLength) {
		String filePath = line.substring(0, line.lastIndexOf(SPLIT_SEPARATOR));
		int fileLineNumber = Integer.parseInt(line.substring(line.lastIndexOf(SPLIT_SEPARATOR)+1));
		IPath location = new Path(filePath);
		IPath relativeLocation = location.makeRelativeTo(container.getLocation());
		IFile file = container.getFile(relativeLocation);
		FileLink fileLink = new FileLink(file, null, -1, -1, fileLineNumber);
		try {
			console.addHyperlink(fileLink, linkOffset, linkLength);
			fileLink.linkActivated();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
