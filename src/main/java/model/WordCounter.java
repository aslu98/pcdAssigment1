package model;

import controller.ThreadsController;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class WordCounter extends Thread {

	private final int UPDATE_EACH = 10000;
	private final GlobalMap map;
	private final ThreadsController controller;
	private final WordsExtractor wordsExtractor;
	private int totWords = 0;
	private int count = 0;

	public WordCounter(final WordsExtractor wordsExtractor, final int index, final ThreadsController controller){
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
				count++;
				if (count % UPDATE_EACH == 0) {
					log("sends update");
					controller.update(map);
				}
			}
			totWords += pdfWords.size();
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
