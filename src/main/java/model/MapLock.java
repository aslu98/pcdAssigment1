package model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MapLock {

	private List<String> permissionGiven;
	private List<String> created;
	private List<String> updating;
	private Lock mutex;
	private Map<String, Condition> isAvailCreate;
	private Map<String, Condition> isAvailUpdate;

	public MapLock(){
		this.permissionGiven = new LinkedList<>();
		this.created = new LinkedList<>();
		this.updating = new LinkedList<>();
		this.mutex = new ReentrantLock();
		this.isAvailCreate = new HashMap<>();
		this.isAvailUpdate = new HashMap<>();
	}

	public boolean request_add(String w){
		try {
			mutex.lock();
			while (!created.contains(w)) {
				if (!permissionGiven.contains(w)) {
					permissionGiven.add(w);
					isAvailCreate.put(w, mutex.newCondition());
					return true;
				} else {
					try {
						isAvailCreate.get(w).await();
					} catch (InterruptedException e){}
				}
			}
			return false;
		} finally {
			mutex.unlock();
		}
	}

	public void release_add(String w){
		try {
			mutex.lock();
			this.created.add(w);
			this.permissionGiven.remove(w);
			this.isAvailUpdate.put(w, mutex.newCondition());
			this.isAvailCreate.get(w).signalAll();
		} finally {
			mutex.unlock();
		}
	}

	public void request_update(String w) {
		try {
			mutex.lock();
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
			isAvailUpdate.get(w).signalAll();
		} finally {
			mutex.unlock();
		}
	}
}

