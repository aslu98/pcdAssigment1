package model;

public class Monitor {

	private int value;
	private boolean canGet;

	public Monitor(){
		canGet = false;
	}

	public synchronized void set(int v){
		value = v;
		canGet = true;
		notifyAll();  
	}

	public synchronized int get() {
		while (!canGet){
			try {
				wait();
			} catch (InterruptedException ex){}
		}
		canGet = false;
		return value;
	}

	public int justGet(){
		return this.value;
	}
}