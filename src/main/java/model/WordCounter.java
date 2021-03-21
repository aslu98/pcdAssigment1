package model;

import controller.ThreadsController;

import java.util.List;
import java.util.Optional;

public class WordCounter extends Thread {

	private final GlobalMap map;
	private final ThreadsController controller;
	private final SyncWordsExtractor wordsExtractor;
	private int totWords = 0;

	public WordCounter(final SyncWordsExtractor wordsExtractor, final int index, final ThreadsController controller){
		super("wordCounter " + index);
		this.map = new GlobalMap();
		this.wordsExtractor = wordsExtractor;
		this.controller = controller;
	}

	public void run(){
		log("before counting");
		Optional<List<String>> pdfWordsOpt = wordsExtractor.getWords();
		while (pdfWordsOpt.isPresent()){
			List<String> pdfWords = pdfWordsOpt.get();
			log("new words: " + pdfWords.size());
			for (String w : pdfWords) {
				map.computeWord(w);
			}
			totWords += pdfWords.size();
			controller.update(map);
			pdfWordsOpt = wordsExtractor.getWords();
		}
		log("after counting");
		controller.threadCompleted(totWords, map);
	}

	private void log(String st){
		synchronized(System.out){
			System.out.println("["+this.getName()+"] "+st);
		}
	}
}
