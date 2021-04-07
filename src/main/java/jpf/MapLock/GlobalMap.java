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

	public void computeWord(final String w, final String thread){
		mapLock.request_update(w, thread);
		wordCount.put(w, wordCount.containsKey(w) ? wordCount.get(w) + 1 : 1);
		mapLock.release_update(w, thread);
	}

	public Map<String, Integer> getMap() {
		mapLock.request_get();
		Map<String, Integer> result = new LinkedHashMap<>(wordCount);
		mapLock.release_get();
		return result;
	}
}

