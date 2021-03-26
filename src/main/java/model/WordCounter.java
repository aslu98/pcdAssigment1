package model;

import java.util.List;
import java.util.Optional;

public class WordCounter extends Thread {

	private final GlobalMap map;
	private final Model model;
	private final SyncWordsExtractor wordsExtractor;
	private int totWords = 0;
	private volatile boolean running = true;

	public WordCounter(final SyncWordsExtractor wordsExtractor, final int index, final Model model){
		super("wordCounter " + index);
		this.map = new GlobalMap();
		this.wordsExtractor = wordsExtractor;
		this.model = model;
	}

	public void terminate() {
		this.running = false;
	}

	public void run(){
		log("before counting");
		Optional<List<String>> pdfWordsOpt;
		while (running && (pdfWordsOpt = wordsExtractor.getWords()).isPresent()){
			List<String> pdfWords = pdfWordsOpt.get();
			log("new words: " + pdfWords.size());
			for (String w : pdfWords) {
				map.computeWord(w);
			}
			totWords += pdfWords.size();
			model.notify(totWords, map);
		}

		if (running){
			log("after counting");
			model.threadCompleted();
		} else {
			log("stopped");
			model.threadStopped();
		}
	}

	private void log(String st){
		synchronized(System.out){
			System.out.println("[Model]["+this.getName()+"] "+st);
		}
	}
}
