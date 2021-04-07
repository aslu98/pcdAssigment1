package jpf.MapLock;

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
	private final Condition canUpdate;
	private final Condition canGet;
	private boolean getAvailable;
	private Map<String, Condition> wordIsAvail;

	public MapLock(){
		this.updating = new LinkedList<>();
		this.mutex = new ReentrantLock();
		this.wordIsAvail = new HashMap<>();
		this.canGet = mutex.newCondition();
		this.canUpdate = mutex.newCondition();
		this.getAvailable = false;
	}

	private void addConditionVariabile(final String w){
		if (!wordIsAvail.containsKey(w)){
			this.wordIsAvail.put(w, mutex.newCondition());
		}
	}

	public void request_update(final String w, String thread) {
		try {
			mutex.lock();
			while (getAvailable){
				try {
					canUpdate.await();
				} catch (InterruptedException e){}
			}
			addConditionVariabile(w);
			while (updating.contains(w)) {
				try {
					wordIsAvail.get(w).await();
				} catch (InterruptedException e){}
			}
			updating.add(w);
		} finally {
			mutex.unlock();
		}
	}

	public void release_update(final String w, String thread) {
		try {
			mutex.lock();
			updating.remove(w);
			wordIsAvail.get(w).signal();
			if (updating.isEmpty()){
				canGet.signal();
			}
		} finally {
			mutex.unlock();
		}
	}

	public void request_get(){
		try {
			mutex.lock();
			while (!updating.isEmpty()){
				try {
					canGet.await();
				} catch (InterruptedException e){}
			}
			getAvailable = true;
		} finally {
			mutex.unlock();
		}
	}

	public void release_get(){
		try {
			mutex.lock();
			getAvailable = false;
			canUpdate.signalAll();
		} finally {
			mutex.unlock();
		}
	}
}

