package model;

import java.util.*;
import java.util.stream.Collectors;

public class WordsExtractor {

	private List<PDFDocumentReader> documentReaders = new LinkedList<>();
 	private int actualIndex = 0;

	public WordsExtractor(List<PDFDocumentReader> documentReaders) {
		this.documentReaders = documentReaders;
	}

	public int size(){
		return this.documentReaders.size();
	}

	public synchronized Optional<List<String>> getWords(){
		if (actualIndex < documentReaders.size()) {
			var wordsOpt = documentReaders.get(actualIndex).extractWords();
			if (wordsOpt.isPresent()) {
				return wordsOpt;
			} else {
				actualIndex += 1;
				return this.getWords();
			}
		} else {
			return Optional.empty();
		}
	}
}

