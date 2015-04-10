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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.part.ViewPart;

public class SbtWorkerManager {
	static protected Map<ProjectNode, SbtWorker> workerMap = new HashMap<ProjectNode, SbtWorker>();

	static public SbtWorker getSbtWorker(ProjectNode node, ViewPart view) {
		SbtWorker worker = workerMap.get(node);
		if (worker == null) {
			worker = new SbtWorker(node, view);
			workerMap.put(node, worker);
		}
		return worker;
	}

	/** only for get sbtWorker, won't create new worker. may get null. */
	static public SbtWorker getSbtWorker(ProjectNode node) {
		return workerMap.get(node);
	}

	static public void closeAllSbtWorker() {
		for(SbtWorker worker: workerMap.values()){
			worker.stopSbt();
		};
		workerMap.clear();
	}
	
	static public void closeSbtWorkerWithPath(String projectPath){
		for(ProjectNode node: workerMap.keySet()){
			if(projectPath.equals(node.getName())) {
				workerMap.get(node).stopSbt();
				workerMap.remove(node);
			}
		}
	}

	static public void closeSbtWorker(ProjectNode node) {
		if (workerMap.get(node) != null){
			workerMap.get(node).stopSbt();
			workerMap.remove(node);
		}
	}
}
