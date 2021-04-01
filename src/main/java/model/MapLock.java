package model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MapLock {

	private final List<String> updating;
	private final Lock mutex;
	private final Map<String, Condition> isAvailUpdate;

	public MapLock(){
		this.updating = new LinkedList<>();
		this.mutex = new ReentrantLock();
		this.isAvailUpdate = new HashMap<>();
	}

	private void addConditionVariabile(String w){
		if (!isAvailUpdate.containsKey(w)){
			this.isAvailUpdate.put(w, mutex.newCondition());
		}
	}

	public void request_update(String w) {
		try {
			mutex.lock();
			addConditionVariabile(w);
			while (updating.contains(w)) {
				try {
					isAvailUpdate.get(w).await();
				} catch (InterruptedException e){}
			}
			updating.add(w);
		} finally {
			mutex.unlock();
		}
	}

	public void release_update(String w) {
		try {
			mutex.lock();
			updating.remove(w);
			addConditionVariabile(w);
			isAvailUpdate.get(w).signalAll();
		} finally {
			mutex.unlock();
		}
	}
}

