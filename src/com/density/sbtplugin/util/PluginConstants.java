package com.density.sbtplugin.util;


public class PluginConstants {
	final public static String START_SBT_NAME = "start sbt";
	final public static String COMPILE_NAME = "compile";
	final public static String CLEAN_NAME = "clean";
	final public static String EXIT_NAME = "exit";
	final public static String COMPILE_COMMAND = "build";
	final public static String CLEAN_COMMAND = "clean";
	final public static String EXIT_COMMAND = "exit";
	final public static String RUN_NAME = "run";
	final public static String RUN_COMMAND = "run";
	final public static String RESTART_COMMAND = "restart";
	final public static String RESTART_NAME = "restart sbt";
	
	final public static String BUNDLE_NAME = "sbt-plugin";
	
	//key and value in preference store
	final public static String COMMANDS_NAME_KEY = "#commands";
	final public static String[] DEFAULT_COMMANDS = {"clean->clean","compile->build","restart sbt->restart"};
	
	final public static int DELETE_KEY_CODE = 127;
	
	final public static String CONTROL_ID = "sbt-plugin.viewer";
	
	final public static String SBT_JAR_PATH = "resources/sbt-launch.jar";
}
