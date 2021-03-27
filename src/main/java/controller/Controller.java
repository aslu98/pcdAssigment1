package controller;


import model.Model;

public class Controller {
	
	private final Model model;
	private final Agent agent;
	public Controller(final Model model){
		this.model = model;
		this.agent = new Agent(model);
	}

	public void start(final String toIgnorePath, final String directoryPath, final int wordsNumber) {
		try {
			new Thread(() -> {
				try {
					System.out.println("[Controller] Processing start event...");
					model.initialize();
					model.mostFrequentWords(toIgnorePath, directoryPath, wordsNumber, false);
					agent.start();
					System.out.println("[Controller] Processing start event done.");
				} catch (Exception ex){
					ex.printStackTrace();
				}
			}).start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void stop() {
		try {
			new Thread(() -> {
				try {
					System.out.println("[Controller] Processing stop event...");
					model.stopThreads();
					agent.terminate();
					System.out.println("[Controller] Processing stop event done.");
				} catch (Exception ex){
					ex.printStackTrace();
				}
			}).start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
