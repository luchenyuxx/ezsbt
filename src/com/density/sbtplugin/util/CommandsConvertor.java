package com.density.sbtplugin.util;

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
		return commandPair.split("->")[0];
	}
	static public String valueOf(String commandPair){
		return commandPair.split("->")[1];
	}
	static public String addCommandPair(String commandPairs,String commandName,String command){
		if(commandPairs.equals("")){
			return "["+commandName+"->"+command+"]";
		}
		String adjustCommandPairs = commandPairs.replaceAll("\\]", "");
		return adjustCommandPairs+ ", "+commandName+"->"+command+"]";
	}
	static public String[] pairToArray(String commandPair){
		return commandPair.split("->");
	}
}
