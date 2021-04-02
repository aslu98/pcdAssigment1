package model;

import java.awt.desktop.PreferencesEvent;
import java.util.*;
import java.util.stream.Collectors;

public class GlobalMap {

	private final Map<String, Integer> wordCount;
	private final MapLock mapLock;

	public GlobalMap (){
		this.wordCount = new HashMap<>();
		this.mapLock = new MapLock();
	}

	public void computeWord(final String w){
		wordCount.putIfAbsent(w, 0);
		mapLock.request_update(w);
		wordCount.put(w, wordCount.get(w)+1);
		mapLock.release_update(w);
	}

	public Map<String, Integer> getSortedMap() {
		mapLock.request_get();
		Map<String, Integer> result = wordCount.entrySet().stream()
				.sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(e1, e2) -> e1, LinkedHashMap::new));
		mapLock.release_get();
		return result;
	}
}

