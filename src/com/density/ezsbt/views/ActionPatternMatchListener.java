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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class ActionPatternMatchListener implements IPatternMatchListener {
	private TextConsole console;
	private String pattern;
	private String command;
	private ProjectNode node;
	private final static String LINE_QUALIFIER = "\\(r\\)etry, \\(q\\)uit, \\(l\\)ast, or \\(i\\)gnore\\?";

	public ActionPatternMatchListener(String pattern, String command, ProjectNode node) {
		this.pattern = pattern;
		this.command = command;
		this.node = node;
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
				SbtWorker sbtWorker = SbtWorkerManager.getSbtWorker(node);
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
