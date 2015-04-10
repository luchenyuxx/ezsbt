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

import java.util.Arrays;

public class CommandsConvertor {
	static public String[] stringToArray(String commandPairs){
		String adjustCommandPairs = commandPairs.replaceAll("\\[", "");
		adjustCommandPairs = adjustCommandPairs.replaceAll("\\]", "");
		return adjustCommandPairs.split(", ");
	}
	static public String arrayToString(String[] commandPairs){
		return Arrays.toString(commandPairs);
	}
	static public String keyOf(String commandPair){
		return commandPair.split(PluginConstants.COMMANDS_SPLITOR)[0];
	}
	static public String valueOf(String commandPair){
		return commandPair.split(PluginConstants.COMMANDS_SPLITOR)[1];
	}
	static public String addCommandPair(String commandPairs,String commandName,String command){
		if(commandPairs.equals("")){
			return "["+commandName+PluginConstants.COMMANDS_SPLITOR+command+"]";
		}
		String adjustCommandPairs = commandPairs.replaceAll("\\]", "");
		return adjustCommandPairs+ ", "+commandName+PluginConstants.COMMANDS_SPLITOR+command+"]";
	}
	static public String[] pairToArray(String commandPair){
		return commandPair.split(PluginConstants.COMMANDS_SPLITOR);
	}
}
