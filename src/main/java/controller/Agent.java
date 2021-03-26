package controller;

import model.Model;

public class Agent extends Thread {

	private Model model;
	private volatile boolean running = true;
	
	public Agent(Model model){
		this.model = model;
	}

	public void terminate() {
		this.running = false;
	}
	
	public void run(){
		while (running){
			try {
				model.notifyObservers();
				Thread.sleep(500);
			} catch (Exception ex){
			}
		}
	}
}
