package jpf.MapLock;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalMap {

	private final Map<String, Integer> wordCount;
	private final MapLock mapLock;

	public GlobalMap(){
		this.wordCount = new HashMap<>();
		this.mapLock = new MapLock();
	}

	public void computeWord(final String w){
		wordCount.putIfAbsent(w, 0);
		mapLock.request_update(w);
		wordCount.put(w, wordCount.get(w)+1);
		mapLock.release_update(w);
	}

	public Map<String, Integer> getMap() {
		mapLock.request_get();
		Map<String, Integer> result = new LinkedHashMap<>(wordCount);
		mapLock.release_get();
		return result;
	}
}

