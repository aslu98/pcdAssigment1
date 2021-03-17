import org.apache.commons.math3.analysis.function.Sin;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GlobalMap {

	private Map<String, Integer> wordCount = new HashMap<>();

	public void updateWordCount(final String w){
		if (wordCount.containsKey(w)){
			wordCount.put(w, wordCount.get(w)+1);
		} else {
			wordCount.put(w, 1);
		}
	}

	public Map<String, Integer> getMap() {
		return wordCount;
	}
}

