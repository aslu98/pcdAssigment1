package model;

import controller.ThreadsController;

import java.io.File;
import java.util.List;

public class WordCounter extends Thread {

	private final int UPDATE_EACH = 10000;
	private final List<File> pdfFiles;
	private final List<String> wordsToIgnore;
	private final GlobalMap map;
	private final ThreadsController controller;
	private int totWords = 0;

	public WordCounter(final List<File> files, final List<String> wordsToIgnore, final int index, final ThreadsController controller){
		super("wordCounter " + index);
		this.map = new GlobalMap();
		this.pdfFiles = files;
		this.wordsToIgnore = wordsToIgnore;
		this.controller = controller;
	}

	public void run(){
		log("before counting. number of files:" + pdfFiles.size());
		for (File pdfFile : pdfFiles) {
			PDFDocumentReader pdf =	new PDFDocumentReader(pdfFile, wordsToIgnore);
			List<String> pdfWords = pdf.getUsefulWords();
			log("processing reader " + pdf.toString() + " with total words: " + pdfWords.size());
			for (int i = 0; i< pdfWords.size(); i++){
				map.computeWord(pdfWords.get(i));
				/*if (i % UPDATE_EACH == 0){
					controller.update(new GlobalMap(map));
				}*/
			}
			totWords += pdfWords.size();
		};
		log("after counting");
		controller.threadCompleted(totWords, map);
	}

	private void log(String st){
		synchronized(System.out){
			System.out.println("["+this.getName()+"] "+st);
		}
	}
}
