package com.density.sbtplugin.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.part.ViewPart;

public class SbtWorkerManager {
	static protected Map<String, SbtWorker> workerMap = new HashMap<String, SbtWorker>();

	static public SbtWorker getSbtWorker(TreeParent node, ViewPart view) {
		String path = node.getName();
		if (workerMap.containsKey(path) && workerMap.get(path) != null) {
			return workerMap.get(path);
		} else {
			SbtWorker sbtWorker = new SbtWorker(node, view);
			workerMap.put(path, sbtWorker);
			return sbtWorker;
		}
	}
	
	/** only for get sbtWorker, won't create new worker. may get null. */
	static public SbtWorker getSbtWorker(String key){
		if (workerMap.containsKey(key) && workerMap.get(key) != null) {
			return workerMap.get(key);
		}
		return null;
	}

	static public void closeAllSbtWorker() {
		for (SbtWorker sbtWorker : workerMap.values()) {
			sbtWorker.stopSbt();
		}
	}

	static public void closeSbtWorker(TreeParent node) {
		if (workerMap.containsKey(node.getName())
				&& workerMap.get(node.getName()) != null) {
			workerMap.get(node.getName()).stopSbt();
		}
	}
}
