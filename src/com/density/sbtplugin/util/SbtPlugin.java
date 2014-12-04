package com.density.sbtplugin.util;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.density.sbtplugin.views.PluginConstants;

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
	}
}
