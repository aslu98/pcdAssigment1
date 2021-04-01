package model;

import java.util.*;
import java.util.stream.Collectors;

public class GlobalMap {

	private final Map<String, Integer> wordCount;
	private final Map<String, Integer> result;
	private final MapLock mapLock;
	private List<Map.Entry<String, Integer>> entryList;

	public GlobalMap (){
		this.wordCount = new HashMap<>();
		this.result = new LinkedHashMap<>();
		this.mapLock = new MapLock();
	}

	public void computeWord(final String w){
		if (!wordCount.containsKey(w)){
			wordCount.put(w, 0);
		}

		mapLock.request_update(w);
		wordCount.put(w, wordCount.get(w)+1);
		mapLock.release_update(w);
	}

	public Map<String, Integer> getSortedMap() {
		entryList = new LinkedList<>(wordCount.entrySet());
		entryList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
		for (Map.Entry<String, Integer> entry : entryList) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}

