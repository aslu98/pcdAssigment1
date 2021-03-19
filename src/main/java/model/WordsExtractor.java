package model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WordsExtractor {

	private List<PDFDocumentReader> documentReaders = new LinkedList<>();

	public WordsExtractor(List<PDFDocumentReader> documentReaders) {
		this.documentReaders = documentReaders;
	}

	public synchronized void getWords(final String w){
		for (PDFDocumentReader pdfReader : documentReaders){

		}
	}
}

