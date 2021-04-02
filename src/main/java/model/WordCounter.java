package model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WordCounter extends Thread {

	private final GlobalMap map;
	private final Model model;
	private final SyncWordsExtractor wordsExtractor;
	private volatile boolean running = true;

	public WordCounter(final SyncWordsExtractor wordsExtractor, final GlobalMap map, final int index, final Model model){
		super("wordCounter " + index);
		this.wordsExtractor = wordsExtractor;
		this.model = model;
		this.map = map;
	}

	public void terminate() {
		this.running = false;
	}

	public void run(){
		log("before counting");
		Optional<List<String>> pdfWordsOpt;
		while (running){
			pdfWordsOpt = wordsExtractor.getWords();
			if (pdfWordsOpt.isPresent()){
				List<String> pdfWords = pdfWordsOpt.get();
				log("new words: " + pdfWords.size());
				for (String w : pdfWords) {
					map.computeWord(w);
				}
				model.update(pdfWords.size(), map.getSortedMap());
				log("sent update");
			} else {
				break;
			}
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
