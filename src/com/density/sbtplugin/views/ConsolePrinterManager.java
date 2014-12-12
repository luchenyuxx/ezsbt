package com.density.sbtplugin.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.console.MessageConsole;

public class ConsolePrinterManager {
	static protected Map<MessageConsole, ConsolePrinter> printerMap = new HashMap<MessageConsole, ConsolePrinter>();
	
	static public ConsolePrinter getPrinter(MessageConsole messageConsole){
		if(printerMap.containsKey(messageConsole) && printerMap.get(messageConsole)!=null){
			return printerMap.get(messageConsole);
		}else{
			ConsolePrinter consolePrinter = new ConsolePrinter(messageConsole);
			printerMap.put(messageConsole, consolePrinter);
			return consolePrinter;
		}
	}
}
