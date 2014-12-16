package com.density.sbtplugin.util;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class SbtPlugin extends AbstractUIPlugin {
	static private SbtPlugin instance;
	static public SbtPlugin getInstance(){
		return instance;
	}
	public SbtPlugin(){
		super();
		instance = this;
	}
	
	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(PluginConstants.COMMANDS_NAME_KEY,
				CommandsConvertor.arrayToString(PluginConstants.DEFAULT_COMMANDS));
		store.setDefault(PluginConstants.JAVA_HOME_KEY, getJavaHome());
	}
	
	protected String getJavaHome() {
		String java_home = null;
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			java_home = System.getenv("JAVA_HOME");
		} else
			java_home = System.getProperty("java.home");
		return java_home;
	}
}
