package model;

import java.util.*;
import java.util.stream.Collectors;

public class GlobalMap {

	private Map<String, Integer> wordCount = new HashMap<>();
	private MapLock mapLock = new MapLock();

	public void computeWord(final String w){
		if (!wordCount.containsKey(w)){
			if (mapLock.request_add(w)){
				this.addWord(w);
				mapLock.release_add(w);
			}
		}

		mapLock.request_update(w);
		updateCount(w);
		mapLock.release_update(w);
	}

	private void updateCount(final String w){
		wordCount.put(w, wordCount.get(w)+1);
	}

	private void addWord(final String w){
		wordCount.put(w, 0);
	}

	public Map<String, Integer> getMap() {
		return wordCount;
	}
}

