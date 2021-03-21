package model;

import java.util.*;
import java.util.stream.Collectors;

public class SyncWordsExtractor {

	private final List<PDFDocumentReader> documentReaders;
	private final int pagesEachSection;
 	private int actualIndex = 0;

	public SyncWordsExtractor(List<PDFDocumentReader> documentReaders, final int pagesEachSection) {
		this.documentReaders = documentReaders;
		this.pagesEachSection = pagesEachSection;
	}

	public int size(){
		return this.documentReaders.size();
	}

	public synchronized Optional<List<String>> getWords(){
		if (actualIndex < documentReaders.size()) {
			var wordsOpt = documentReaders.get(actualIndex).extractWords(pagesEachSection);
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

