package model;

import java.util.*;

public class WordCounter extends Thread {

	private final GlobalMap map;
	private final Model model;
	private final SyncWordsExtractor wordsExtractor;
	private final List<String> wordsToIgnore;
	private final int index;
	private volatile boolean running = true;

	public WordCounter(final SyncWordsExtractor wordsExtractor, final List<String> wordsToIgnore, final GlobalMap map, final int index, final Model model){
		super("wordCounter " + index);
		this.wordsExtractor = wordsExtractor;
		this.model = model;
		this.map = map;
		this.wordsToIgnore = wordsToIgnore;
		this.index = index;
	}

	public void terminate() {
		this.running = false;
	}

	public void run(){
		log("before counting");
		Optional<List<String>> pdfWordsOpt;
		List<String> pdfWords;
		while (running){
			pdfWordsOpt = wordsExtractor.getWords();
			if (pdfWordsOpt.isPresent() && (pdfWords = pdfWordsOpt.get()).size() > 0){
				log("new words: " + pdfWords.size());
				for (String toIgnore : wordsToIgnore) {
					pdfWords.removeIf(word -> word.equals(toIgnore));
				}
				log("ok words: " + pdfWords.size());
				if (pdfWordsOpt.get().size() > 0) {
					for (String w : pdfWords) {
						map.computeWord(w);
					}
					model.update(pdfWords.size(), map.getSortedMap());
					log("sent update");
				}
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
