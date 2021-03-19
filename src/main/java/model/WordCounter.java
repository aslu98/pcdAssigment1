package model;

import controller.ThreadsController;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class WordCounter extends Thread {

	private final int UPDATE_EACH = 10000;
	private final List<File> pdfFiles;
	private final List<String> wordsToIgnore;
	private final GlobalMap map;
	private final ThreadsController controller;
	private int totWords = 0;
	private int count = 0;

	public WordCounter(final List<File> files, final List<String> wordsToIgnore, final int index, final ThreadsController controller){
		super("wordCounter " + index);
		this.map = new GlobalMap();
		this.pdfFiles = files;
		this.wordsToIgnore = wordsToIgnore;
		this.controller = controller;
	}

	public void run(){
		PDFDocumentReader pdf = null;
		log("before counting. number of files:" + pdfFiles.size());
		for (File pdfFile : pdfFiles) {
			log("new document to load");
			pdf = new PDFDocumentReader(pdfFile, wordsToIgnore);
			log("document loaded");
			this.processDocument(pdf);
		};
		log("after counting");
		controller.threadCompleted(totWords, map);
	}

	private void processDocument(PDFDocumentReader pdfDoc){
		Optional<List<String>> pdfWords = pdfDoc.extractWords();
		int section = 1;
		while (pdfWords.isPresent()){
			log("processing reader " + pdfDoc.getTitle() + " in section " + section + " with total words: " + pdfWords.get().size());
			for (String w : pdfWords.get()) {
				map.computeWord(w);
				count++;
				if (count % UPDATE_EACH == 0) {
					log("sends update");
					controller.update(map);
				}
			}
			totWords += pdfWords.get().size();
			section += 1;
			pdfWords = pdfDoc.extractWords();
		}
	}

	private void log(String st){
		synchronized(System.out){
			System.out.println("["+this.getName()+"] "+st);
		}
	}
}
