package model;

import java.util.*;
import java.util.stream.Collectors;

public class GlobalMap {

	private Map<String, Monitor> wordCount = new HashMap<>();

	public GlobalMap(GlobalMap another) {
		this.wordCount = another.wordCount;
	}

	public GlobalMap(){}

	public void computeWord(final String w){
		if (wordCount.containsKey(w)){
			synchronized (w){
				wordCount.put(w, newCount(w));
			}
		} else {
			addWord(w);
		}
	}

	private Monitor newCount(final String w){
		Monitor cell = wordCount.get(w);
		cell.set(cell.get()+1);
		return cell;
	}

	private void addWord(final String w){
		synchronized (w){
			Monitor cell = new Monitor();
			cell.set(1);
			Monitor prevCell = wordCount.putIfAbsent(w, cell);
			if (prevCell != null){
				newCount(w);
			}
		}
	}

	public Map<String, Integer> getMap() {
		return wordCount.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey,
						e -> e.getValue().justGet()));
	}
}

