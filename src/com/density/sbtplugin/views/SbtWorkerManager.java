package com.density.sbtplugin.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.part.ViewPart;

public class SbtWorkerManager {
	static protected Map<String, SbtWorker> workerMap = new HashMap<String, SbtWorker>();
	static public SbtWorker getSbtWorker(TreeParent node, ViewPart view){
		String path = node.getName();
		if(workerMap.containsKey(path) && workerMap.get(path)!=null){
			return workerMap.get(path);
		}else{
			SbtWorker sbtWorker = new SbtWorker(node.getName(), node.getContainer(), view);
			workerMap.put(path, sbtWorker);
			return sbtWorker;
		}
	}
}
