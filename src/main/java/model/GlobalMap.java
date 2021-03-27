package model;

import java.util.*;
import java.util.stream.Collectors;

public class GlobalMap {

	private Map<String, Integer> wordCount = new LinkedHashMap<>();
	private MapLock mapLock = new MapLock();

	public void computeWord(final String w){
		if (!wordCount.containsKey(w)){
			if (mapLock.request_add(w)){
				wordCount.put(w, 0);
				mapLock.release_add(w);
			}
		}

		mapLock.request_update(w);
		wordCount.put(w, wordCount.get(w)+1);
		mapLock.release_update(w);
	}

	public Map<String, Integer> getMap() {
		return this.wordCount;
	}
}

