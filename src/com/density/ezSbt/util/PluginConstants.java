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

package com.density.ezsbt.util;


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
	
	final public static String BUNDLE_NAME = "EzSbt";
	
	//key and value in preference store
	final public static String COMMANDS_SPLITOR = "->";
	final public static String COMMANDS_NAME_KEY = "#commands";
	final public static String[] DEFAULT_COMMANDS = {"clean->clean","update->update","compile->compile","reload->reload","exit->exit"};
	final public static String JAVA_HOME_KEY = "#javaHome";
	final public static String JAVA_OPTIONS_KEY = "#javaOptions";
	final public static String DEFAULT_JAVA_OPTIONS = "-XX:PermSize=256m -Xmx512m -Dsbt.log.noformat=true";
	final public static String HIDE_RESOLVE_KEY = "#hideResolve";
	final public static boolean DEFAULT_HIDE_RESOLVE = true;
	
	final public static int DELETE_KEY_CODE = 127;
	
	final public static String CONTROL_ID = "ezsbt.viewer";
	
	final public static String SBT_JAR_PATH = "resources/sbt-launch.jar";
	
}
